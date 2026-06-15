package com.brewpoint.pos.service;

import com.brewpoint.pos.dao.EmployeeDAO;
import com.brewpoint.pos.dao.OrderDAO;
import com.brewpoint.pos.dao.ProductDAO;
import com.brewpoint.pos.dao.ProductSizeDAO;
import com.brewpoint.pos.dao.ToppingDAO;
import com.brewpoint.pos.database.DatabaseManager;
import com.brewpoint.pos.pricing.BaseDrink;
import com.brewpoint.pos.pricing.DrinkComponent;
import com.brewpoint.pos.pricing.ToppingDecorator;
import com.brewpoint.pos.payment.PaymentStrategyFactory;
import com.brewpoint.pos.model.CartLine;
import com.brewpoint.pos.model.CartLineRequest;
import com.brewpoint.pos.model.CheckoutRequest;
import com.brewpoint.pos.model.CheckoutResult;
import com.brewpoint.pos.model.Employee;
import com.brewpoint.pos.model.PaymentMethod;
import com.brewpoint.pos.model.Product;
import com.brewpoint.pos.model.ProductSize;
import com.brewpoint.pos.model.Topping;
import com.brewpoint.pos.model.ToppingSnapshot;
import com.brewpoint.pos.payment.PaymentResult;
import com.brewpoint.pos.payment.PaymentStrategy;
import com.brewpoint.pos.util.ValidationException;
import com.brewpoint.pos.util.ValidationUtils;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CheckoutService {
    private final EmployeeDAO employeeDAO ;
    private final ProductDAO productDAO ;
    private final ProductSizeDAO sizeDAO ;
    private final ToppingDAO toppingDAO ;
    private final OrderDAO orderDAO ;

    public CheckoutService(EmployeeDAO employeeDAO, ProductDAO productDAO, ProductSizeDAO sizeDAO, ToppingDAO toppingDAO, OrderDAO orderDAO) {
        this.employeeDAO = employeeDAO;
        this.productDAO = productDAO;
        this.sizeDAO = sizeDAO;
        this.toppingDAO = toppingDAO;
        this.orderDAO = orderDAO;
    }

    public CartLine previewLine(CartLineRequest request) throws SQLException {
        try (Connection connection = DatabaseManager.getInstance().getConnection()) {
            return buildTrustedLine(connection, request);
        }
    }

    public CheckoutResult checkout(CheckoutRequest request) throws SQLException {
        if (request == null || request.getLines() == null || request.getLines().isEmpty()) {
            throw new ValidationException("Giỏ hàng đang trống.");
        }
        if (request.getPaymentInput() == null || request.getPaymentInput().getMethod() == null) {
            throw new ValidationException("Chọn phương thức thanh toán.");
        }
        try (Connection connection = DatabaseManager.getInstance().getConnection()) {
            boolean oldAutoCommit = connection.getAutoCommit();
            try {
                connection.setAutoCommit(false);
                Employee employee = employeeDAO.findActiveById(connection, request.getEmployeeId());
                if (employee == null) {
                    throw new ValidationException("Không xác định được thu ngân hợp lệ.");
                }
                List<CartLine> trustedLines = new ArrayList<CartLine>();
                BigDecimal total = BigDecimal.ZERO;
                for (CartLineRequest lineRequest : request.getLines()) {
                    CartLine trustedLine = buildTrustedLine(connection, lineRequest);
                    trustedLines.add(trustedLine);
                    total = total.add(trustedLine.getLineTotal());
                }
                PaymentMethod method = request.getPaymentInput().getMethod();
                PaymentStrategy strategy = PaymentStrategyFactory.create(method);
                PaymentResult payment = strategy.validate(total, request.getPaymentInput());
                String orderCode = generateOrderCode();
                long orderId = orderDAO.insertHeader(
                        connection,
                        orderCode,
                        employee.getEmployeeId(),
                        method,
                        total,
                        total,
                        payment.getReceivedAmount(),
                        payment.getChangeAmount()
                );
                for (CartLine trustedLine : trustedLines) {
                    boolean stockUpdated = productDAO.decreaseStock(
                            connection,
                            trustedLine.getRequest().getProductId(),
                            trustedLine.getRequest().getQuantity()
                    );
                    if (!stockUpdated) {
                        throw new ValidationException("Sản phẩm vừa hết hàng. Vui lòng kiểm tra lại giỏ.");
                    }
                    long itemId = orderDAO.insertItem(connection, orderId, trustedLine);
                    for (ToppingSnapshot topping : trustedLine.getToppings()) {
                        orderDAO.insertTopping(connection, itemId, topping);
                    }
                }
                connection.commit();
                return new CheckoutResult(orderId, orderCode, total, payment.getChangeAmount());
            } catch (SQLException | RuntimeException ex) {
                connection.rollback();
                throw ex;
            } finally {
                connection.setAutoCommit(oldAutoCommit);
            }
        }
    }

    private CartLine buildTrustedLine(Connection connection, CartLineRequest request) throws SQLException {
        if (request == null) {
            throw new ValidationException("Dòng giỏ hàng không hợp lệ.");
        }
        if (request.getQuantity() <= 0 || request.getQuantity() > 99) {
            throw new ValidationException("Số lượng phải từ 1 đến 99.");
        }
        request.setNote(ValidationUtils.normalizeNote(request.getNote()));
        Product product = productDAO.findById(connection, request.getProductId(), true);
        if (product == null) {
            throw new ValidationException("Sản phẩm không còn bán.");
        }
        ProductSize size = sizeDAO.findById(connection, request.getProductSizeId(), true);
        if (size == null || size.getProductId() != product.getProductId()) {
            throw new ValidationException("Cỡ ly không còn bán. Vui lòng chọn lại.");
        }
        List<Integer> toppingIds = uniqueToppingIds(request.getToppingIds());
        request.setToppingIds(toppingIds);
        List<Topping> toppings = toppingDAO.findByIds(connection, toppingIds, true);
        if (toppings.size() != toppingIds.size()) {
            throw new ValidationException("Topping không hợp lệ hoặc đã ngừng bán.");
        }
        DrinkComponent drink = new BaseDrink(product.getName(), size.getSizeName(), size.getSalePrice());
        List<ToppingSnapshot> snapshots = new ArrayList<ToppingSnapshot>();
        for (Topping topping : toppings) {
            drink = new ToppingDecorator(drink, topping.getName(), topping.getExtraPrice());
            snapshots.add(new ToppingSnapshot(
                    topping.getToppingId(),
                    topping.getToppingCode(),
                    topping.getName(),
                    topping.getExtraPrice()
            ));
        }
        CartLine line = new CartLine();
        line.setRequest(request);
        line.setProductCode(product.getProductCode());
        line.setProductName(product.getName());
        line.setSizeCode(size.getSizeCode());
        line.setSizeName(size.getSizeName());
        line.setBasePrice(size.getSalePrice());
        line.setToppings(snapshots);
        line.setUnitPrice(drink.getPrice());
        line.setLineTotal(drink.getPrice().multiply(BigDecimal.valueOf(request.getQuantity())));
        return line;
    }

    private List<Integer> uniqueToppingIds(List<Integer> toppingIds) {
        List<Integer> result = new ArrayList<Integer>();
        Set<Integer> seen = new HashSet<Integer>();
        if (toppingIds == null) {
            return result;
        }
        for (Integer id : toppingIds) {
            if (id != null && id.intValue() > 0 && seen.add(id)) {
                result.add(id);
            }
        }
        return result;
    }

    private String generateOrderCode() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
        return "BP" + LocalDateTime.now().format(formatter);
    }
}
