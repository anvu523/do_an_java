package com.brewpoint.pos.service;

import com.brewpoint.pos.dao.ToppingDAO;
import com.brewpoint.pos.model.Topping;
import com.brewpoint.pos.util.ValidationException;
import com.brewpoint.pos.util.ValidationUtils;

import java.sql.SQLException;
import java.util.List;

public class ToppingService {
    private final ToppingDAO toppingDAO ;

    public ToppingService(ToppingDAO toppingDAO) {
        this.toppingDAO = toppingDAO;
    }

    public List<Topping> findAll(boolean activeOnly) throws SQLException {
        return toppingDAO.findAll(activeOnly);
    }

    public int create(Topping topping) throws SQLException {
        validate(topping);
        return toppingDAO.insert(topping);
    }

    public void update(Topping topping) throws SQLException {
        if (topping.getToppingId() <= 0) {
            throw new ValidationException("Chọn topping cần sửa.");
        }
        validate(topping);
        toppingDAO.update(topping);
    }

    public void deactivate(int toppingId) throws SQLException {
        if (toppingId <= 0) {
            throw new ValidationException("Chọn topping cần ngừng bán.");
        }
        toppingDAO.deactivate(toppingId);
    }

    private void validate(Topping topping) {
        topping.setToppingCode(ValidationUtils.requireText(topping.getToppingCode(), "Mã topping").toUpperCase());
        topping.setName(ValidationUtils.requireText(topping.getName(), "Tên topping"));
        if (topping.getExtraPrice() == null || topping.getExtraPrice().signum() <= 0) {
            throw new ValidationException("Giá topping phải lớn hơn 0.");
        }
    }
}
