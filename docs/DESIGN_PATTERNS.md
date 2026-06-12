# Design Patterns trong BrewPoint POS

## MVC / Layered MVC

- `view`: Swing frame/panel/dialog, chỉ hiển thị và thu input.
- `controller`: API mỏng cho view gọi.
- `service`: validation, phân quyền nghiệp vụ, pricing và transaction.
- `dao`: SQL, `PreparedStatement`, mapping `ResultSet`.
- `model`: entity, enum và DTO.

Luồng chính:

```text
Swing View -> Controller -> Service -> DAO -> JDBC/MySQL
```

## DAO

Các DAO chính:

- `ProductDAO`, `ProductSizeDAO`, `ToppingDAO`.
- `OrderDAO` lưu order header, item snapshot và topping snapshot.
- `EmployeeDAO`, `UserDAO`, `CategoryDAO`, `StatisticDAO`.

DAO không hiển thị UI và không tự quyết định nghiệp vụ; service truyền `Connection` cho các thao tác cần transaction.

## Singleton

`DatabaseManager.getInstance()` là singleton quản lý cấu hình database và tạo connection mới cho mỗi operation.

Điểm quan trọng: singleton không giữ một `Connection` global, tránh lỗi connection bị đóng hoặc bị dùng chung sai transaction.

## Strategy + Factory

`PaymentStrategy` có hai triển khai:

- `CashPaymentStrategy`: kiểm tra tiền khách đưa đủ và tính tiền thừa.
- `ManualBankTransferStrategy`: bắt buộc xác nhận đã nhận tiền.

`PaymentStrategyFactory` chọn strategy từ `PaymentMethod`.

## Decorator

Decorator được dùng cho topping:

```text
BaseDrink: Ô long sữa size L 49.000 ₫
ToppingDecorator: Trân châu đen +10.000 ₫
ToppingDecorator: Pudding trứng +8.000 ₫
Đơn giá: 67.000 ₫
Số lượng 2: 134.000 ₫
```

Thiết kế dùng một `ToppingDecorator` data-driven, không tạo class riêng như `PearlDecorator` hay `PuddingDecorator`.

Checkout không tin giá từ UI. `CheckoutService` reload product, size, topping từ database, dựng lại Decorator chain, tính tổng, chạy payment strategy và lưu snapshot trong một transaction.
