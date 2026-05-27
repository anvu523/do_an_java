# Báo cáo test

Môi trường hiện tại có JDK 21. Đã cài MySQL Server 8.4.9 bằng winget và start user-mode tại `127.0.0.1:3306`; Maven vẫn chưa có trong PATH.

## Đã chạy trong môi trường hiện tại

| STT | Chức năng | Trường hợp test | Kết quả mong đợi | Kết quả thực tế | Trạng thái |
|---:|---|---|---|---|---|
| 1 | Source code | Biên dịch toàn bộ Java source bằng `javac -encoding UTF-8` | Không lỗi compile | Không lỗi compile | PASS |
| 2 | Cấu trúc project | Kiểm tra thư mục MVC/DAO/service/database/factory/util/main | Có đủ thư mục và class chính | Có đủ | PASS |
| 3 | SQL script | Import `database/drink_store.sql` vào MySQL 8.4.9 | Tạo database, bảng, khóa chính, khóa ngoại, dữ liệu mẫu | Tạo đủ 6 bảng, dữ liệu mẫu insert thành công | PASS |
| 4 | Config | Kiểm tra `config/database.properties` | Có URL, username, password, driver | Có đủ | PASS |
| 5 | MySQL server | `mysqladmin ping` tại `127.0.0.1:3306` | Server phản hồi | `mysqld is alive`, version 8.4.9 | PASS |
| 6 | Dữ liệu mẫu | Đếm dòng 6 bảng sau import | users=2, employees=2, categories=5, products=8, orders=2, order_details=3 | Đúng số lượng | PASS |
| 7 | Chạy source | `powershell -ExecutionPolicy Bypass -File scripts/run-javac.ps1` | Compile, start MySQL, có driver JDBC, mở Swing app | Tạo process `javaw.exe` chạy app | PASS |

## Cần chạy sau khi có MySQL

| STT | Chức năng | Trường hợp test | Kết quả mong đợi | Kết quả thực tế | Trạng thái |
|---:|---|---|---|---|---|
| 1 | Database | Chạy `scripts/import-database.ps1` | Tạo đủ 6 bảng và insert dữ liệu mẫu | PASS, xem phần đã chạy | PASS |
| 2 | Kết nối MySQL | MySQL CLI kết nối `drink_store` bằng `root` không mật khẩu | Kết nối được database `drink_store` | PASS bằng `mysql.exe` đường dẫn tuyệt đối | PASS |
| 3 | Đăng nhập | Admin `admin/admin123` | Vào màn hình chính, hiện đủ menu quản trị | Chưa chạy | BLOCKED |
| 4 | Đăng nhập | Nhân viên `nhanvien/nv123` | Vào màn hình chính, khóa menu quản trị | Chưa chạy | BLOCKED |
| 5 | Đăng nhập | Sai mật khẩu | Hiện lỗi mật khẩu | Chưa chạy | BLOCKED |
| 6 | Đăng nhập | Tài khoản không tồn tại | Hiện lỗi tài khoản không tồn tại | Chưa chạy | BLOCKED |
| 7 | CRUD sản phẩm | Thêm, sửa, xóa, tìm kiếm, lọc loại, tải lại JTable | Dữ liệu thay đổi đúng và JTable cập nhật | Chưa chạy | BLOCKED |
| 8 | CRUD loại sản phẩm | Thêm, sửa, xóa, tìm kiếm, tải lại JTable | Dữ liệu thay đổi đúng và JTable cập nhật | Chưa chạy | BLOCKED |
| 9 | CRUD nhân viên | Thêm, sửa, xóa, tìm kiếm, tải lại JTable | Cập nhật đồng bộ `users` và `employees` | Chưa chạy | BLOCKED |
| 10 | Bán hàng | Thêm/xóa giỏ hàng, tính tổng tiền | Tổng tiền đúng | Chưa chạy | BLOCKED |
| 11 | Bán hàng | Lưu hóa đơn | Có bản ghi `orders` và `order_details` | Chưa chạy | BLOCKED |
| 12 | Bán hàng | Sau khi bán trừ tồn kho | `products.stock_quantity` giảm đúng | Chưa chạy | BLOCKED |
| 13 | Bán hàng | Bán vượt tồn kho | Bị chặn bằng thông báo lỗi | Chưa chạy | BLOCKED |
| 14 | Hóa đơn | Xem danh sách, chi tiết, tìm mã, lọc ngày, lọc nhân viên | Hiển thị đúng dữ liệu | Chưa chạy | BLOCKED |
| 15 | Thống kê | Doanh thu ngày/tháng, số hóa đơn, sản phẩm bán chạy | Hiển thị đúng dữ liệu tổng hợp | Chưa chạy | BLOCKED |

## Tự review

| Mục | Kết quả |
|---|---|
| Hoàn thành | Source Java Swing theo MVC, DAO, service, Singleton connection, Factory, SQL MySQL và dữ liệu mẫu. |
| Hạn chế | Chưa có kiểm thử tự động vì đề tài Swing/MySQL và môi trường hiện tại thiếu MySQL/Maven. |
| Mở rộng | Thêm in hóa đơn PDF, biểu đồ doanh thu, phân quyền chi tiết hơn, audit log, import/export Excel, hash mật khẩu bằng BCrypt. |
