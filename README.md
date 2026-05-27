# Phần mềm quản lý cửa hàng bán đồ uống

Project desktop Java Swing + MySQL theo MVC, DAO, Singleton database connection và Factory đơn giản.

## Công nghệ

- Java 17+
- Java Swing
- MySQL 8+
- Maven
- MySQL Connector/J: khai báo trong `pom.xml`

## Cấu trúc

```text
src/main/java/com/drinkstore/
├── model/
├── view/
├── controller/
├── dao/
├── service/
├── database/
├── factory/
├── util/
└── main/
```

## Cấu hình database

1. Tạo database và dữ liệu mẫu:

```powershell
mysql -u root -p < database/drink_store.sql
```

Nếu terminal chưa nhận PATH sau khi cài MySQL bằng winget, dùng script có sẵn:

```powershell
powershell -ExecutionPolicy Bypass -File scripts/import-database.ps1
```

Script này tự start MySQL user-mode tại `127.0.0.1:3306` với `root` không mật khẩu, đúng với cấu hình demo trong `config/database.properties`.

Dừng MySQL user-mode:

```powershell
powershell -ExecutionPolicy Bypass -File scripts/stop-mysql.ps1
```

2. Sửa cấu hình nếu cần tại:

```text
config/database.properties
```

Mặc định:

```properties
db.url=jdbc:mysql://localhost:3306/drink_store?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Ho_Chi_Minh&useUnicode=true&characterEncoding=UTF-8
db.username=root
db.password=
```

## Chạy chương trình

Cách chạy trực tiếp bằng JDK, không cần Maven:

```powershell
powershell -ExecutionPolicy Bypass -File scripts/run-javac.ps1
```

Script này tự start MySQL, tải MySQL Connector/J vào `lib/`, compile source vào `out/`, rồi mở giao diện Swing.

Cách khuyến nghị:

```powershell
mvn clean package
java -jar target/drink-store-manager-1.0.0.jar
```

Hoặc chạy script:

```powershell
powershell -ExecutionPolicy Bypass -File scripts/run-maven.ps1
```

Nếu chỉ cần kiểm tra compile bằng JDK:

```powershell
powershell -ExecutionPolicy Bypass -File scripts/compile-javac.ps1
```

Lưu ý: chạy app thực tế cần MySQL server đang chạy và MySQL Connector/J trong classpath; Maven tự xử lý phần Connector/J.

## Tài khoản mẫu

| Vai trò | Tên đăng nhập | Mật khẩu |
|---|---|---|
| Admin | `admin` | `admin123` |
| Nhân viên | `nhanvien` | `nv123` |

## Chức năng chính

- Đăng nhập và phân quyền Admin/Nhân viên.
- CRUD sản phẩm, loại sản phẩm, nhân viên.
- Bán hàng, giỏ hàng, tính tổng tiền, lưu hóa đơn, trừ tồn kho trong transaction.
- Xem hóa đơn, xem chi tiết, lọc theo mã, ngày, nhân viên.
- Thống kê doanh thu ngày, tháng, số hóa đơn, sản phẩm bán chạy.

## Design Patterns

Chi tiết nằm tại `docs/DESIGN_PATTERNS.md`.

## Báo cáo test

Báo cáo nằm tại `docs/TEST_REPORT.md`.
