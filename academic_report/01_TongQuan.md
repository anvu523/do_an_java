# CHƯƠNG 1. TỔNG QUAN

## 1.1. Bối cảnh và Bài toán nghiệp vụ

### 1.1.1. Thực trạng ngành dịch vụ F&B và Nhu cầu quản lý bán hàng tại quầy (POS)
Ngành dịch vụ ăn uống (Food and Beverage - F&B) tại Việt Nam đang chứng kiến sự bùng nổ mạnh mẽ với sự xuất hiện của hàng loạt các mô hình kinh doanh từ chuỗi nhà hàng lớn đến các quán cafe, trà sữa quy mô vừa và nhỏ. Đi kèm với sự phát triển đó là sức ép rất lớn về mặt vận hành. Trong giờ cao điểm, thu ngân phải tiếp nhận hàng chục, hàng trăm yêu cầu đặt món liên tục. Các phương pháp ghi chép thủ công qua sổ sách hoặc bảng tính Excel cơ bản không còn đáp ứng đủ tốc độ xử lý, rất dễ dẫn đến sai sót trong việc tính toán tiền lẻ, nhầm lẫn món ăn của khách, hoặc làm thất thoát doanh thu nghiêm trọng. 

Bên cạnh đó, việc quản lý kho hàng, theo dõi lịch sử bán hàng và tính toán hiệu suất làm việc của từng nhân viên cũng trở nên bất khả thi nếu thiếu sự hỗ trợ của công nghệ. Một phần mềm quản lý điểm bán hàng (Point of Sale - POS) chuyên nghiệp ra đời chính là lời giải thiết thực nhất nhằm số hóa toàn bộ quy trình này. Hệ thống POS không chỉ đơn thuần là công cụ tính tiền thay thế máy tính bỏ túi, mà còn đóng vai trò như một bộ não trung tâm giúp kiểm soát toàn bộ hoạt động kinh doanh, mang lại sự chuyên nghiệp, nâng cao trải nghiệm khách hàng và tối ưu hóa lợi nhuận.

### 1.1.2. Mục tiêu của đồ án
Nhận thức được nhu cầu cấp bách trên thị trường, nhóm chúng em đã quyết định xây dựng "Hệ thống Quản lý Điểm Bán Hàng (POS)" bằng ngôn ngữ Java. Đồ án này được phát triển với mục tiêu cung cấp một giải pháp quản lý trọn vẹn, đáp ứng quy trình vận hành khép kín:
- **Tự động hóa quy trình bán hàng (Checkout):** Cung cấp giao diện trực quan cho thu ngân để chọn sản phẩm, thêm các tùy chọn mở rộng (Topping, Kích cỡ), và tự động tính toán tổng tiền. Hỗ trợ tính tiền thừa tự động khi khách hàng thanh toán bằng tiền mặt, đồng thời kết xuất hóa đơn bản in (Receipt) định dạng PDF ngay lập tức với tốc độ cực cao.
- **Quản lý danh mục hàng hóa chuyên sâu:** Cho phép người quản lý định nghĩa các danh mục sản phẩm. Tích hợp tính năng kéo thả (Drag and Drop) hiện đại để cấu hình thứ tự hiển thị của các danh mục một cách nhanh chóng, trực quan.
- **Phân quyền và bảo mật nhân sự:** Hệ thống xây dựng tính năng đăng nhập an toàn, quản lý tài khoản nhân viên. Mỗi hóa đơn được xuất ra đều được gắn trực tiếp với mã nhân viên đang thực hiện giao dịch, giúp chống gian lận và đánh giá hiệu suất.
- **Báo cáo và thống kê theo thời gian thực:** Cung cấp biểu đồ trực quan (Dashboard) cập nhật tức thời số lượng hóa đơn, tổng doanh thu theo ngày/tháng, và thống kê các sản phẩm bán chạy nhất để chủ cửa hàng có chiến lược kinh doanh phù hợp.

## 1.2. Phạm vi và Đối tượng sử dụng hệ thống
Hệ thống được thiết kế hướng tới mô hình kinh doanh bán lẻ, đặc biệt là các quán cafe, quán trà sữa hoặc tiệm bánh ngọt. Đối tượng người dùng chính được chia làm hai vai trò:
- **Nhân viên thu ngân (Cashier):** Sử dụng phân hệ Point of Sale để xử lý các giao dịch mua hàng của khách, kiểm tra danh mục đồ uống hiện có, áp dụng kích cỡ, topping tương ứng và in hóa đơn thanh toán.
- **Quản lý / Chủ cửa hàng (Manager):** Truy cập toàn quyền vào phần mềm để cấu hình các thiết lập, quản lý danh sách sản phẩm, tùy chỉnh giá bán, theo dõi tình hình kinh doanh qua phân hệ Thống kê, và cấp phát quyền cho nhân viên mới.

## 1.3. Phân công công việc nhóm

Dự án được thực hiện bởi nhóm 5 thành viên. Để đảm bảo tiến độ và chất lượng mã nguồn, nhóm đã áp dụng mô hình phát triển phần mềm lặp (Iterative) kết hợp phân chia công việc theo các tầng kiến trúc (Layers) cũng như theo từng chức năng nghiệp vụ (Feature-based). Cụ thể như sau:

1. **[Tên sinh viên 1] - [MSSV 1]**: **(Trưởng nhóm / Kiến trúc sư Hệ thống)** Chịu trách nhiệm thiết kế cấu trúc Cơ sở dữ liệu (ERD), chuẩn hóa các thực thể (Entity) để tránh dư thừa dữ liệu. Xây dựng nền tảng kết nối Database (JDBC) và thiết kế hệ thống Mẫu thiết kế (Design Patterns) như Object Pool, Singleton.
2. **[Tên sinh viên 2] - [MSSV 2]**: **(Kỹ sư Backend & Nghiệp vụ)** Phát triển toàn bộ logic của tầng Dịch vụ (Service Layer) và Truy xuất dữ liệu (DAO Layer) phục vụ tính năng Bán hàng (Checkout) cốt lõi. Chịu trách nhiệm xử lý các bài toán tính toán giá phức tạp, rollback giao dịch khi có lỗi xảy ra.
3. **[Tên sinh viên 3] - [MSSV 3]**: **(Kỹ sư Frontend & Quản lý Danh mục)** Đảm nhiệm việc phát triển phân hệ Quản lý Danh mục (Product, Category, Topping). Đặc biệt nghiên cứu và lập trình thành công thao tác Kéo thả (Drag and Drop) trên bảng dữ liệu để tái sắp xếp hiển thị.
4. **[Tên sinh viên 4] - [MSSV 4]**: **(Kỹ sư Tích hợp & Quản lý Nhân sự)** Đảm nhận việc tích hợp luồng Xác thực (Authentication), quản lý phiên đăng nhập (Session) và xử lý luồng thao tác của Nhân viên/Quản lý. Nghiên cứu triển khai kiến trúc Dependency Injection (IoC Container).
5. **[Tên sinh viên 5] - [MSSV 5]**: **(Kỹ sư Đảm bảo Chất lượng & Báo cáo)** Chịu trách nhiệm thiết kế các bảng Thống kê (Dashboard), vẽ đồ thị (Chart) và thiết kế mẫu hóa đơn in ấn (Invoice/Receipt) bằng JasperReports. Xây dựng trọn bộ Unit Test để đảm bảo độ tin cậy của mã nguồn trước khi đóng gói phát hành.
