package com.brewpoint.pos;

import com.brewpoint.pos.controller.*;
import com.brewpoint.pos.dao.*;
import com.brewpoint.pos.report.exporter.*;
import com.brewpoint.pos.report.service.*;
import com.brewpoint.pos.service.*;

public class DependencyContainer {
    private static DependencyContainer instance;

    // DAOs
    private final CategoryDAO categoryDAO;
    private final EmployeeDAO employeeDAO;
    private final OrderDAO orderDAO;
    private final ProductDAO productDAO;
    private final ProductSizeDAO productSizeDAO;
    private final StatisticDAO statisticDAO;
    private final ToppingDAO toppingDAO;
    private final UserDAO userDAO;

    // Services
    private final AuthService authService;
    private final CategoryService categoryService;
    private final CheckoutService checkoutService;
    private final EmployeeService employeeService;
    private final OrderService orderService;
    private final ProductService productService;
    private final StatisticService statisticService;
    private final ToppingService toppingService;
    
    // Report Services & Exporters
    private final ReportServiceFacade reportServiceFacade;
    private final JasperReportExporter jasperReportExporter;
    private final ReportExportService reportExportService;
    private final ReportExportStrategy pdfStrategy;
    private final ReportExportStrategy xlsxStrategy;
    private final ReportExportStrategy docxStrategy;

    // Controllers
    private final AuthController authController;
    private final CatalogController catalogController;
    private final CheckoutController checkoutController;
    private final EmployeeController employeeController;
    private final OrderController orderController;
    private final ReportController reportController;
    private final StatisticController statisticController;

    private DependencyContainer() {
        // Init DAOs
        categoryDAO = new CategoryDAO();
        employeeDAO = new EmployeeDAO();
        orderDAO = new OrderDAO();
        productDAO = new ProductDAO();
        productSizeDAO = new ProductSizeDAO();
        statisticDAO = new StatisticDAO();
        toppingDAO = new ToppingDAO();
        userDAO = new UserDAO();

        // Init Services
        authService = new AuthService(userDAO, employeeDAO);
        categoryService = new CategoryService(categoryDAO);
        checkoutService = new CheckoutService(employeeDAO, productDAO, productSizeDAO, toppingDAO, orderDAO);
        employeeService = new EmployeeService(employeeDAO, userDAO);
        orderService = new OrderService(orderDAO);
        productService = new ProductService(productDAO, productSizeDAO);
        statisticService = new StatisticService(statisticDAO);
        toppingService = new ToppingService(toppingDAO);
        
        // Init Report related
        ReceiptReportService receiptReportService = new ReceiptReportService();
        DailyRevenueReportService dailyRevenueReportService = new DailyRevenueReportService();
        MonthlyRevenueReportService monthlyRevenueReportService = new MonthlyRevenueReportService();
        BestSellingProductsReportService bestSellingProductsReportService = new BestSellingProductsReportService();
        CashierPerformanceReportService cashierPerformanceReportService = new CashierPerformanceReportService();
        
        reportServiceFacade = new ReportServiceFacade(receiptReportService, dailyRevenueReportService, monthlyRevenueReportService, bestSellingProductsReportService, cashierPerformanceReportService);
        jasperReportExporter = new JasperReportExporter();
        reportExportService = new ReportExportService();
        pdfStrategy = new PdfReportExportStrategy();
        xlsxStrategy = new XlsxReportExportStrategy();
        docxStrategy = new DocxReportExportStrategy();

        // Init Controllers
        authController = new AuthController(authService);
        catalogController = new CatalogController(categoryService, productService, toppingService);
        checkoutController = new CheckoutController(checkoutService);
        employeeController = new EmployeeController(employeeService);
        orderController = new OrderController(orderService);
        reportController = new ReportController(reportServiceFacade, jasperReportExporter, reportExportService, pdfStrategy, xlsxStrategy, docxStrategy);
        statisticController = new StatisticController(statisticService);
    }

    public static synchronized DependencyContainer getInstance() {
        if (instance == null) {
            instance = new DependencyContainer();
        }
        return instance;
    }

    public AuthController getAuthController() {
        return authController;
    }

    public CatalogController getCatalogController() {
        return catalogController;
    }

    public CheckoutController getCheckoutController() {
        return checkoutController;
    }

    public EmployeeController getEmployeeController() {
        return employeeController;
    }

    public OrderController getOrderController() {
        return orderController;
    }

    public ReportController getReportController() {
        return reportController;
    }

    public StatisticController getStatisticController() {
        return statisticController;
    }
}
