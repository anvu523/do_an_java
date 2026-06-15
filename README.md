# Điểm Bán Hàng (POS) - Đồ Án Công Nghệ Java

Chào mừng bạn đến với dự án "Hệ thống Quản lý Điểm Bán Hàng (POS)" - một phần mềm quản lý bán lẻ toàn diện được phát triển bằng ngôn ngữ Java 17. Đồ án được thiết kế nghiêm ngặt theo các tiêu chuẩn kiến trúc phần mềm chuyên nghiệp với sự hiện diện của MVC, DAO, và đặc biệt là hệ thống tự thiết kế IoC Container cho Dependency Injection.

## Tính năng nổi bật (Features)

* **Phân hệ Bán hàng (Point of Sale):** Giao diện cực nhanh hỗ trợ thu ngân lập hóa đơn, tính tổng tiền, bổ sung các kích cỡ (Size) và lựa chọn đồ ăn kèm (Topping). Hệ thống tự tính tiền thừa và xuất hóa đơn định dạng PDF sắc nét.
* **Quản lý Danh mục với Drag & Drop:** Cải thiện tối đa trải nghiệm người dùng bằng cách áp dụng cơ chế Kéo và Thả trên `JTable` để sắp xếp thứ tự danh mục. Ứng dụng sử dụng Batch Updates của JDBC để đồng bộ thẳng xuống Database một cách mượt mà và trong suốt (không cần bấm Save).
* **Báo cáo và Thống kê đa dạng:** Sử dụng thư viện JasperReports hỗ trợ xuất báo cáo doanh thu theo nhiều định dạng (Strategy Pattern) như PDF, Excel, Word.

## Kiến trúc Hệ thống (Architecture & Patterns)

Đây là điểm nhấn tạo nên giá trị học thuật cho dự án:
* **MVC & DAO:** Phân rã mã nguồn thành 4 tầng rõ rệt: View, Controller, Service, DAO.
* **Dependency Injection (DI):** Áp dụng DI qua Constructor, sử dụng `DependencyContainer` tự xây dựng làm trung tâm IoC (Inversion of Control) thay vì dùng từ khóa `new` tùy tiện.
* **Object Pool & Proxy Pattern:** Giải quyết hoàn toàn bài toán nghẽn cổ chai mạng bằng cách tạo một Pool Connection (`SimpleConnectionPool`). Ứng dụng `Dynamic Proxy` được sử dụng để chặn các lời gọi `.close()`, tự động trả các kết nối đang mở về hàng đợi thay vì đóng chúng vĩnh viễn.

---

## Cách cài đặt và Vận hành (Getting Started)

### 1. Chuẩn bị cơ sở dữ liệu (MySQL qua Podman / Docker)

Dự án cung cấp sẵn các script tự động để khởi tạo Database với hơn 1.600 đơn hàng demo. Khuyến nghị sử dụng **Podman** (hoặc Docker).

**Trên Windows (PowerShell):**
Bạn chỉ cần chạy script có sẵn, script này sẽ tự động dọn dẹp volume cũ, tạo MySQL container và nạp dữ liệu (seed) vào:
```powershell
cd C:\Users\nngocquang\Documents\pos\do_an_java
.\scripts\reset-db-podman.ps1
```

Sau khi chạy xong, hãy kiểm tra xem dữ liệu tiếng Việt có bị lỗi font (hiển thị dấu `?`) hay không bằng script sau:
```powershell
.\scripts\verify-db-utf8.ps1
```
*Kết quả mong đợi: hiển thị đúng chữ "Trà sữa trân châu đường đen" và "UTF-8 OK".*

**Thủ công (Linux / macOS):**
```bash
podman compose down -v
podman compose up -d
# Đợi vài phút để MySQL khởi động và nạp tự động file 01_drink_store.sql và 02_seed_demo_orders.sql
```

### 2. Biên dịch và Chạy ứng dụng

Yêu cầu máy tính cài đặt **Java JDK 17** và **Apache Maven**.

Mở terminal tại thư mục gốc của project và chạy lệnh biên dịch tự động để đóng gói thành Fat JAR (Uber-Jar):
```bash
mvn clean package
```

Sau khi build thành công, chạy ứng dụng từ file `.jar` đã đóng gói nằm trong thư mục `target/`:
```bash
java -jar target/brewpoint-pos-1.0.0.jar
```

> **Ghi chú về file JAR:** Maven Shade Plugin đã được cấu hình để tự động ghi đè tệp `brewpoint-pos-1.0.0.jar` mặc định thành Fat JAR (chứa toàn bộ thư viện). Tệp JAR không chứa thư viện (thin jar) sẽ được đổi tên thành `original-brewpoint-pos-1.0.0.jar`. Do đó, bạn chạy lệnh `java -jar target/brewpoint-pos-1.0.0.jar` là hoàn toàn chính xác!

### 3. Mã nguồn mở & Kiểm thử tự động (Unit Testing)
Dự án được bảo vệ toàn vẹn bằng bộ **32 Unit Tests** độc lập (tỷ lệ Pass Rate 100%).
Để chạy toàn bộ bài kiểm tra nhằm xác minh độ an toàn của thuật toán tính tiền:
```bash
mvn test
```

---

## Tài khoản Demo

| Vai trò | Username | Mật khẩu | Họ tên nhân viên |
|---|---|---|---|
| **Quản lý (Admin)** | `admin` | `admin123` | Hệ thống quản trị |
| **Thu ngân 1** | `cashier01` | `cashier123` | Nguyễn Thị Lan |
| **Thu ngân 2** | `cashier02` | `cashier123` | Trần Văn Minh |
| **Thu ngân 3** | `cashier03` | `cashier123` | Lê Hoàng An |

---

> 🚀 **Mọi chi tiết kỹ thuật chuyên sâu và các sơ đồ UML (ERD, Sequence Diagram, Class Diagram), vui lòng xem tại thư mục `academic_report`.**
