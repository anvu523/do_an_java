# 🌟 DEMO: HỆ THỐNG POS CHUYÊN NGHIỆP 🌟

Tài liệu này được tạo ra nhằm tóm tắt nhanh những trải nghiệm nổi bật nhất (Showcase) mà hệ thống mang lại cho người dùng, cũng như phô diễn sức mạnh kỹ thuật ngầm đằng sau giao diện.

---

## 1. Trải nghiệm Điểm Bán Hàng (POS Checkout)
Hệ thống POS được sinh ra với mục tiêu: **Nhanh - Gọn - Chính xác**.

- **Giao diện trực quan:** Thu ngân có thể thấy ngay danh sách đồ uống phân theo loại.
- **Tính toán Real-time:** Khi thêm sản phẩm `Trà Sữa Oolong` (Size L) và `Topping Trân Châu`, tổng tiền lập tức nảy số mà không cần tải lại trang.
- **Tính Tiền Thừa Thông Minh:** Khách hàng đưa 100.000 VNĐ, nhập nhanh vào ô, phần mềm lập tức báo cần thối lại bao nhiêu tiền.
- **Kết xuất PDF Tốc độ cao:** Nhấn "Thanh toán", một biên lai (Receipt) được render cực nét ra định dạng PDF thông qua động cơ `JasperReports` chỉ trong chớp mắt.

---

## 2. Kéo Thả Xếp Hạng (Drag & Drop Category Reordering)
Sự đột phá về mặt UX/UI! Quên đi những cách làm thủ công cũ kỹ như gõ số 1, 2, 3 vào ô nhập liệu.

- Bạn muốn nhóm món ăn "Best Seller" lên đầu tiên? Chỉ việc dùng chuột **Nhấn, Kéo và Thả** dòng dữ liệu lên đầu bảng.
- **Phép Màu Sau Hậu Trường:**
   - Hệ thống tự đánh số lại toàn bộ thứ tự mượt mà.
   - Kích hoạt `Batch Update` bằng JDBC, gửi một gói dữ liệu duy nhất xuống MySQL để cập nhật vị trí của hàng chục danh mục cùng một lúc. Mọi thứ diễn ra trong vòng 10 mili-giây, hoàn toàn trong suốt với người dùng.

---

## 3. Kiến Trúc "Đẳng Cấp Enterprise"

Đây không chỉ là một phần mềm giao diện, đây là một hệ thống được thiết kế hoàn chỉnh.

- **Dependency Injection (DI) & IoC Container:** Bạn sẽ không tìm thấy bất kỳ một lệnh `new Service()` nào nằm rải rác trong Controller. Toàn bộ được "tiêm" (Inject) vào từ lớp `DependencyContainer` trung tâm. Điều này giúp mã nguồn lỏng lẻo (loose coupling) và có thể viết Unit Test cực kỳ dễ dàng.
- **Sự Tuyệt Hảo Của Object Pool & Dynamic Proxy:** 
   Để tránh tình trạng giật lag khi mở/đóng kết nối Database, nhóm tự code một `SimpleConnectionPool` chuyên dụng.
   Hệ thống còn khôn khéo bọc đối tượng Connection bằng một `Dynamic Proxy`. Mọi hàm `close()` đều bị "đánh chặn" (Intercept), không cho phép hủy kết nối vật lý mà ép trả nó về Pool chờ dùng tiếp. Một kỹ thuật mang đậm chất chuyên gia!
- **100% Pass Rate Unit Tests:** Mọi nghiệp vụ tính giá tiền đều được bảo vệ bởi 32 lớp kiểm thử độc lập (JUnit).

---

> 🚀 **Mọi chi tiết kỹ thuật chuyên sâu và các sơ đồ UML, vui lòng xem tại thư mục `academic_report`.**
