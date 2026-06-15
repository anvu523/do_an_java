# CHƯƠNG 5. KẾT LUẬN

## 5.1. Kết quả đạt được và Đánh giá mức độ hoàn thành
Trải qua một chặng đường dài tìm hiểu, phân tích, thiết kế và cặm cụi gõ từng dòng mã nguồn, nhóm chúng em vô cùng tự hào khi đã đưa đồ án "Hệ thống Quản lý Điểm Bán Hàng (POS)" từ những dòng ý tưởng trên giấy trở thành một sản phẩm phần mềm hiện hữu và hoạt động một cách cực kỳ mạnh mẽ. Nhìn chung, đồ án đã hoàn thành xuất sắc các mục tiêu đề ra ban đầu cả về mặt nghiệp vụ kinh doanh (Business Logic) lẫn yêu cầu khắt khe về mặt thiết kế kỹ thuật (Technical Design). 

Những thành quả cốt lõi mà dự án đã đạt được có thể được tóm lược qua ba khía cạnh lớn:
1. **Hoàn thiện chuỗi nghiệp vụ quy trình khép kín:** Sản phẩm không phải là một "vỏ bọc giao diện" rỗng tuếch, mà nó thực sự xử lý gọn gàng vòng đời của một điểm bán lẻ. Từ lúc nhập thông tin, cấu hình danh mục hàng hóa linh hoạt qua Kéo-Thả (Drag & Drop), đến việc thao tác nghiệp vụ tính tiền (Checkout) với tốc độ cao, hỗ trợ tính tiền thừa tự động, cho đến bước cuối cùng là in Hóa đơn và tổng kết báo cáo (Report). 
2. **Kiến trúc bền vững và Dễ bảo trì:** Không sa đà vào việc viết mã nguồn "mì ăn liền" (spaghetti code), nhóm đã cam kết duy trì chuẩn mực cao nhất bằng việc trung thành tuyệt đối với thiết kế MVC và DAO. Cấu trúc ứng dụng được tổ chức phân rã theo gói tính năng (Package by Feature) giúp phần mềm trở nên rành mạch, rõ ràng. Việc triển khai Dependency Injection (DI) với IoC Container tự chế là minh chứng rõ nhất cho việc loại bỏ sự gắn kết cứng nhắc (Tight coupling) giữa các lớp đối tượng.
3. **Áp dụng thành thạo và sáng tạo các Design Patterns:** Đỉnh cao của dự án nằm ở tầng kết nối cơ sở dữ liệu. Thay vì đi theo lối mòn tải các thư viện ORM cồng kềnh, nhóm tự tay xây dựng hệ thống Connection Pool kết hợp với kỹ thuật Dynamic Proxy để đánh chặn và tái sử dụng kết nối. Đi cùng với đó là việc áp dụng Singleton cho các bộ quản lý và Strategy Pattern để hỗ trợ in báo cáo đa định dạng (PDF, Excel, Word) tùy chỉnh thời gian thực. Tất cả đã mang lại một sức mạnh đáng nể, giúp chương trình tiêu thụ bộ nhớ rất thấp và đạt tỷ lệ vượt qua Unit Test là 100%.

## 5.2. Khó khăn gặp phải và Bài học kinh nghiệm
Bên cạnh những thành công, quá trình phát triển dự án cũng để lại cho nhóm nhiều bài học xương máu:
- Khó khăn lớn nhất nằm ở giai đoạn đầu khi phải thiết kế Cơ sở dữ liệu cho một hệ thống có Topping và Size. Nhóm đã phải liên tục sửa đổi ERD để tối ưu hóa, đảm bảo việc mở rộng sau này không gây vỡ cấu trúc.
- Việc áp dụng các mẫu thiết kế (Design Patterns) đòi hỏi nền tảng tư duy trừu tượng rất cao, thời gian đầu tiến độ dự án có vẻ bị đình trệ vì phải "vẽ ra quá nhiều Interface". Tuy nhiên, nhóm đã nhận ra giá trị vô giá của nó ở các giai đoạn sau: khi có yêu cầu thêm tính năng, hệ thống mới không hề phá vỡ hệ thống cũ, mọi thứ được "lắp ráp" rất trơn tru.

## 5.3. Hướng phát triển và Ứng dụng thực tiễn tương lai
Mặc dù hệ thống đã đáp ứng đủ các tiêu chí đồ án cấp đại học, nhưng để có thể thương mại hóa và ứng dụng vào môi trường doanh nghiệp quy mô lớn, sản phẩm cần được xem xét phát triển thêm theo các hướng sau:
- **Nâng cấp nền tảng:** Di chuyển và nâng cấp từ ứng dụng cục bộ Desktop (Java Swing) thành nền tảng ứng dụng Web (Sử dụng Spring Boot kết hợp với giao diện ReactJS hoặc VueJS). Điều này giúp nhân viên có thể sử dụng phần mềm linh hoạt trên mọi trình duyệt, máy tính bảng hay thậm chí là điện thoại di động thông minh (Mobile POS).
- **Tích hợp thanh toán số (Digital Payment):** Bổ sung kết nối API qua các ví điện tử (Momo, ZaloPay) và quét mã QR Code chuyển khoản tự động (VietQR). Đây là một xu thế bắt buộc trong thời đại chuyển đổi số hiện nay.
- **Xây dựng hệ thống Cloud Synchronization:** Áp dụng hệ thống đồng bộ dữ liệu đám mây (Cloud Sync) để có thể triển khai hệ thống cho chuỗi (Chain) nhà hàng/quán cafe nhiều chi nhánh, quản lý tập trung toàn bộ dữ liệu kinh doanh tại một trụ sở duy nhất.
