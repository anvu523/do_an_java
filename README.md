# BrewPoint POS

Ứng dụng POS desktop cho quán cà phê và trà sữa tại Việt Nam, viết bằng Java Swing, JDBC, MySQL và Maven.

## Quyết định kỹ thuật

| Hạng mục | Giá trị |
|---|---|
| Artifact | `brewpoint-pos` |
| Package | `com.brewpoint.pos` |
| JDK | 17 |
| Phong cách source | Java Core 8 style |
| UI | Java Swing, mở maximized |
| Database | MySQL 8.x, `brewpoint_pos` |
| Tiền tệ | VND, `BigDecimal`, hiển thị `39.000 ₫` |

Không dùng `record`, sealed class, pattern matching, text block, `var`, module system, `List.of`, `Map.of` hoặc `Stream.toList`.

## Khởi tạo database

```powershell
mysql -u root -p < database/drink_store.sql
```

File SQL tạo database `brewpoint_pos`, schema size/topping/order snapshot và seed menu tiếng Việt.

## Chạy chương trình

```powershell
mvn clean package
java -jar target/brewpoint-pos-1.0.0.jar
```

Nếu không có Maven trong PATH, có thể compile trực tiếp bằng script:

```powershell
powershell -ExecutionPolicy Bypass -File scripts/compile-javac.ps1
```

## Tài khoản mẫu

| Vai trò | Tên đăng nhập | Mật khẩu |
|---|---|---|
| Admin | `admin` | `admin123` |
| Cashier | `cashier` | `cashier123` |

## Chức năng đã triển khai

- Đăng nhập, đăng xuất và phân quyền Admin/Cashier.
- CRUD danh mục, sản phẩm có ảnh, size, topping, nhân viên/tài khoản.
- POS toàn màn hình với filter danh mục, tìm kiếm, product card có ảnh/placeholder.
- Chọn size, nhiều topping, số lượng, ghi chú và gộp giỏ theo cấu hình.
- Decorator Pattern tính đơn giá topping bằng một `ToppingDecorator` data-driven.
- Thanh toán tiền mặt hoặc chuyển khoản xác nhận thủ công bằng Strategy/Factory.
- Checkout tính lại giá từ database, lưu snapshot và trừ tồn kho trong transaction.
- Cashier chỉ xem hóa đơn của mình; Admin xem tất cả hóa đơn và thống kê.

## Tài liệu

- `docs/DESIGN_PATTERNS.md`: giải thích MVC, DAO, Singleton, Strategy, Factory, Decorator.
- `docs/TEST_REPORT.md`: checklist kiểm thử build, DB, ảnh, VND, Decorator, checkout và phân quyền.
