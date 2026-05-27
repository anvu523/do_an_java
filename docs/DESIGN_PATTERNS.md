# Design Patterns đã sử dụng

| Pattern | Vị trí | Cách áp dụng |
|---|---|---|
| MVC Pattern | `model/`, `view/`, `controller/` | Entity nằm trong `model`, Swing UI nằm trong `view`, controller nhận yêu cầu từ UI và gọi service. |
| DAO Pattern | `dao/ProductDAO.java`, `CategoryDAO.java`, `EmployeeDAO.java`, `OrderDAO.java`, `StatisticDAO.java`, `UserDAO.java` | Mỗi bảng hoặc nhóm nghiệp vụ database có DAO riêng, dùng `PreparedStatement` để CRUD/search/filter. |
| Singleton Pattern | `database/DatabaseConnection.java` | Cung cấp `DatabaseConnection.getInstance().getConnection()` để quản lý cấu hình và kết nối database tập trung. |
| Factory Pattern | `factory/UserFactory.java` | Tạo `User` theo role, username, password hash và trạng thái khi thêm nhân viên. |

## Phân tách tầng

- `view`: chỉ dựng giao diện, đọc input, hiển thị thông báo.
- `controller`: API mỏng cho Swing gọi.
- `service`: validation và nghiệp vụ như login, checkout, kiểm tra giỏ hàng.
- `dao`: SQL, transaction, mapping `ResultSet` sang model.
- `database`: cấu hình/kết nối MySQL.

## Điểm nghiệp vụ quan trọng

- `OrderDAO.insert()` lưu `orders`, `order_details` và trừ tồn kho trong cùng transaction.
- `SalePanel` chặn bán vượt tồn kho ngay trên giao diện, `OrderDAO` vẫn kiểm tra lại bằng SQL để tránh sai lệch dữ liệu.
- `EmployeeDAO` tạo/cập nhật `users` và `employees` trong cùng transaction.
