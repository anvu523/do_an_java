# Báo cáo test BrewPoint POS

## Kết quả đã chạy ngày 2026-06-12

| ID | Lệnh/kịch bản | Kết quả |
|---|---|---|
| BLD-01 | `powershell -ExecutionPolicy Bypass -File scripts/compile-javac.ps1` | PASS, compile `--release 17` vào `out` |
| BLD-02 | Grep Java source cho feature cấm | PASS, không có hit trong `src/main/java` |
| DB-01 | `powershell -ExecutionPolicy Bypass -File scripts/import-database.ps1` | PASS, tạo `brewpoint_pos` và 9 bảng |
| DB-02 | Seed count | PASS: users=2, employees=2, categories=5, products=12, product_sizes=21, toppings=7 |
| DEC-01 | Smoke service: `OLONG-SUA` size L + trân châu đen + pudding trứng, số lượng 2 | PASS: unit=67000, line=134000 |
| CHK-01 | Smoke checkout tiền mặt 200000 cho total 134000 | PASS: tạo order `BP20260612120744151`, change=66000, stock OLONG-SUA còn 63 |
| CHK-02 | Smoke checkout tiền mặt thiếu 1000 | PASS: reject và order count vẫn là 1 |
| MVN-01 | `mvn clean package` | BLOCKED: máy không có Maven trong PATH; tải Maven tạm từ Apache archive bị lỗi kết nối mạng |

## Checklist yêu cầu

| ID | Test | Kỳ vọng |
|---|---|---|
| BLD-01 | `mvn clean package` bằng JDK 17 | BUILD SUCCESS |
| BLD-02 | Compile trực tiếp bằng `scripts/compile-javac.ps1` | Không lỗi Java source |
| BLD-03 | Grep forbidden Java feature | Không có `record`, sealed, pattern matching, text block, `var`, `List.of`, `Map.of`, `Stream.toList` |
| DB-01 | Import `database/drink_store.sql` | Tạo database `brewpoint_pos` với utf8mb4 |
| DB-02 | Đếm seed | users=2, employees=2, categories=5, products=12, product_sizes=21, toppings=7 |
| IMG-01 | Chọn JPG/PNG hợp lệ | Copy vào `data/product-images/` và lưu relative path |
| IMG-02 | Ảnh thiếu/hỏng | Hiển thị placeholder, không crash |
| MON-01 | Format `39000` | `39.000 ₫` |
| DEC-01 | Base 49k + topping 10k + 8k | Đơn giá `67.000 ₫`, số lượng 2 là `134.000 ₫` |
| CHK-01 | Checkout tiền mặt đủ | Lưu order, item, topping snapshot và trừ stock |
| CHK-02 | Tiền mặt thiếu | Không lưu order |
| CHK-03 | Chuyển khoản chưa xác nhận | Không lưu order |
| CHK-04 | Stock thay đổi trong checkout | Rollback toàn bộ |
| AUTH-01 | Cashier xem hóa đơn | Chỉ thấy hóa đơn của chính mình |
| AUTH-02 | Admin | Thấy quản trị, thống kê và tất cả hóa đơn |

## Ghi chú môi trường

- Maven và JDK phải có trong PATH hoặc dùng script `scripts/compile-javac.ps1` khi đã có `javac`.
- MySQL local mặc định: `127.0.0.1:3306`, user `root`, password rỗng.
- UI test Swing cần chạy thủ công sau khi import database.
