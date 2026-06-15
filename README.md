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

## Cách cài đặt (Getting Started)

1. Cài đặt **Java JDK 17** và **Apache Maven**.
2. Cài đặt **MySQL 8.0**, tạo Database và chạy script `.sql` (nếu có trong kho lưu trữ) để nạp dữ liệu mồi.
3. Chỉnh sửa thông tin kết nối Database trong file `src/main/resources/application.properties`.
4. Mở terminal tại thư mục gốc của project và chạy lệnh biên dịch tự động:
   ```bash
   mvn clean package
   ```
5. Chạy ứng dụng từ file `.jar` đã đóng gói nằm trong thư mục `target/`:
   ```bash
   java -jar target/brewpoint-pos-1.0.0-shaded.jar
   ```

## Mã nguồn mở & Unit Testing
Dự án được bảo vệ toàn vẹn bằng bộ **32 Unit Tests** (tỷ lệ Pass Rate 100%).
Tham khảo thư mục `academic_report/` để đọc toàn bộ báo cáo phân tích thiết kế chi tiết (ERD, Sequence Diagram, Class Diagram) được minh họa bằng PlantUML.
