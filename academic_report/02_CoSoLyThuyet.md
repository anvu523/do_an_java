# CHƯƠNG 2. CƠ SỞ LÝ THUYẾT

## 2.1. Nền tảng Công nghệ

### 2.1.1. Java Standard Edition (Java SE 17)
Dự án phần mềm POS này được nhóm quyết định phát triển hoàn toàn dựa trên nền tảng Java Standard Edition (Java SE) phiên bản 17 (Long-Term Support). Lựa chọn Java thay vì các ngôn ngữ kịch bản khác mang lại rất nhiều lợi thế cho một hệ thống mang tính chất "Enterprise" (quy mô doanh nghiệp):
- **Khả năng Đa nền tảng (Platform Independence):** Sức mạnh lớn nhất của Java là tính năng "Write Once, Run Anywhere". Mã nguồn chỉ cần được biên dịch một lần thành bytecode (.class) và sau đó có thể được đóng gói thành tệp thực thi (.jar) để chạy mượt mà trên bất kỳ hệ điều hành nào (Windows, macOS, Linux) miễn là máy đó có cài đặt máy ảo Java (JVM). Đối với một cửa hàng bán lẻ, hệ thống máy tính cấu hình rất đa dạng, do đó Java là giải pháp an toàn và linh hoạt nhất.
- **Tính Hướng Đối Tượng (Object-Oriented Programming - OOP):** Hệ thống bán hàng là một tập hợp phức tạp của nhiều đối tượng (Đơn hàng, Sản phẩm, Nhân viên, Hóa đơn). Java Core cho phép áp dụng triệt để 4 tính chất OOP: 
   - *Đóng gói (Encapsulation):* Che giấu trạng thái dữ liệu qua các hàm Getter/Setter.
   - *Kế thừa (Inheritance):* Tái sử dụng mã nguồn cho các loại báo cáo khác nhau.
   - *Đa hình (Polymorphism):* Ứng dụng để linh hoạt trong việc xử lý các giao diện (interface) tính toán giá trị.
   - *Trừu tượng (Abstraction):* Giúp định nghĩa các Service một cách khái quát.
- **Quản lý Bộ Nhớ Tự Động (Garbage Collection):** Trong quá trình thao tác điểm bán hàng, hàng trăm đối tượng giao dịch được tạo ra mỗi giờ. Trình dọn rác tự động của Java (Garbage Collector) đảm bảo hệ thống không gặp tình trạng rò rỉ bộ nhớ (Memory Leak), điều mà ngôn ngữ C/C++ thường xuyên gặp phải nếu lập trình viên không xử lý cẩn thận.

### 2.1.2. Java Database Connectivity (JDBC)
JDBC là tiêu chuẩn công nghiệp (API) do Sun Microsystems (nay là Oracle) phát triển nhằm hỗ trợ các ứng dụng Java kết nối và tương tác với mọi Hệ quản trị cơ sở dữ liệu quan hệ (RDBMS) trên thị trường.
Mặc dù ngày nay có sự xuất hiện của các thư viện ánh xạ đối tượng-quan hệ (ORM framework) đình đám như Hibernate hay JPA, nhưng nhóm đã quyết tâm sử dụng **JDBC thuần túy (Raw JDBC)** kết hợp cấu trúc tự xây dựng vì những lý do mang tính học thuật và hiệu năng:
- **Khả năng Kiểm soát Hoàn toàn (Full Control):** JDBC cho phép lập trình viên tối ưu từng dòng lệnh SQL `SELECT`, `INSERT`, `UPDATE` nhằm đạt tốc độ thực thi nhanh nhất. Nhóm có thể dễ dàng đánh chỉ mục (Index) trên cơ sở dữ liệu và viết SQL phức tạp để gộp (JOIN) bảng một cách chính xác mà không bị hệ thống ORM tự động phát sinh các câu lệnh dư thừa (vấn đề N+1 queries).
- **Tính năng Cập nhật Hàng Loạt (Batch Updates):** Khi người dùng kéo thả để thay đổi vị trí 50 danh mục cùng lúc, tính năng `addBatch()` và `executeBatch()` của `PreparedStatement` trong JDBC giúp gộp 50 lệnh UPDATE thành duy nhất 1 lần kết nối xuống Database. Điều này giảm thiểu tối đa độ trễ mạng (Network Latency), đem lại phản hồi tức thì cho người dùng UI.
- **An toàn Bảo mật (SQL Injection Prevention):** Việc bắt buộc sử dụng `PreparedStatement` thay cho `Statement` thường giúp vô hiệu hóa hoàn toàn các nguy cơ tấn công qua lỗ hổng chèn mã SQL trái phép. Các chuỗi input độc hại sẽ được JDBC tự động mã hóa an toàn trước khi đẩy xuống cơ sở dữ liệu.

## 2.2. Các Mô hình Kiến trúc Phần mềm Nền Tảng

Sự thất bại của phần lớn các dự án sinh viên là do "viết code không có cấu trúc", nhồi nhét giao diện (UI) và truy vấn dữ liệu (SQL) vào cùng một file. Hệ quả là mã nguồn trở thành đống tơ vò (Spaghetti Code), không thể bảo trì, sửa một lỗi sẽ đẻ ra mười lỗi khác. Để khắc phục điều đó, đồ án này tự hào áp dụng nghiêm ngặt các mẫu cấu trúc chuyên nghiệp.

### 2.2.1. Kiến trúc Model - View - Controller (MVC)
Hệ thống lấy mẫu thiết kế kiến trúc MVC làm cốt lõi để phân tách rạch ròi các vùng trách nhiệm (Separation of Concerns):
- **Tầng Model (Mô hình):** Chứa các class biểu diễn thực thể nghiệp vụ (Entities) như `Category.java`, `Product.java`. Tầng này chỉ chứa thuộc tính dữ liệu nội bộ và các logic kiểm tra tính hợp lệ cơ bản. Model hoàn toàn không biết gì về cơ sở dữ liệu hay giao diện.
- **Tầng View (Giao diện hiển thị):** Được xây dựng dựa trên nền tảng Java Swing. View chỉ chịu trách nhiệm lắng nghe sự kiện của chuột/bàn phím từ người dùng (như click nút "Thanh toán", kéo thả các bảng) và lấy thông tin văn bản từ các TextBox. Sau khi thu thập dữ liệu, View "ném" công việc xử lý xuống cho Controller. View tuyệt đối không chứa logic tính toán tiền bạc.
- **Tầng Controller (Trạm điều khiển):** Đóng vai trò là "Tổng đài viên". Controller nhận yêu cầu từ View, dịch yêu cầu đó thành các hàm xử lý tương ứng ở tầng Service, sau khi Service chạy xong và trả về kết quả (dưới dạng đối tượng Model), Controller sẽ ra lệnh cho View cập nhật lại màn hình (Hiển thị thông báo Thành công, hoặc hiển thị Popup lỗi).

Sự phân tách ba lớp hoàn hảo này cho phép các kỹ sư Frontend có thể thoải mái sửa đổi giao diện đồ họa mà không sợ làm sụp đổ các thuật toán ở Backend.

### 2.2.2. Tầng Data Access Object (DAO)
Data Access Object (DAO) là một lớp trung gian nằm giữa hệ thống phần mềm và cơ sở dữ liệu. DAO trừu tượng hóa (Abstract) toàn bộ các chi tiết lộn xộn của việc kết nối DB (chuỗi URL kết nối, lệnh SQL, cấu hình mapping từ ResultSet ra Java Object). 
Nếu một ngày hệ thống muốn chuyển từ việc sử dụng MySQL sang Oracle Database hoặc PostgreSQL, lập trình viên chỉ cần tạo ra các class DAO mới tương ứng với ngôn ngữ SQL của cơ sở dữ liệu đó. Các tầng ở trên (Service, Controller, View) vẫn sẽ hoạt động ổn định mà không cần chỉnh sửa bất kỳ dòng mã nào. Đây chính là minh chứng sống động cho nguyên lý Open/Closed (Mở để mở rộng, Đóng để sửa đổi) trong nguyên lý thiết kế SOLID.

### 2.2.3. Nguyên lý Đảo ngược Điều khiển (IoC) & Dependency Injection (DI)
Trong các phần mềm truyền thống, mỗi khi một Class A muốn sử dụng Class B, Class A thường sử dụng từ khóa `new` để tạo ra Class B ngay bên trong mã nguồn của nó (Ví dụ: bên trong `OrderController` gọi `OrderService service = new OrderService()`). Điều này tạo ra một "Ràng buộc cứng" (Tight Coupling). Hậu quả là nếu hàm tạo (constructor) của Class B thay đổi, ta phải đi tìm toàn bộ mã nguồn để sửa lại các lệnh `new`. Thêm vào đó, việc viết mã kiểm thử (Unit Test) cho Class A là vô cùng khó khăn vì ta không thể "giả mạo" (Mock) đối tượng Class B được tạo bên trong nó.

Để nâng tầm chuyên nghiệp, nhóm đã triển khai kiến trúc **Dependency Injection (Tiêm phụ thuộc)** thông qua phương pháp **Constructor Injection**.
- Các Service/Controller sẽ không bao giờ sử dụng từ khóa `new`. Thay vào đó, chúng khai báo các thuộc tính phụ thuộc dưới dạng tham số của hàm khởi tạo (Constructor).
- Hệ thống tự xây dựng một **Inversion of Control (IoC) Container** (lớp `DependencyContainer.java`). Tại thời điểm ứng dụng khởi động, IoC Container sẽ đóng vai trò người kiến tạo: Tự động khởi tạo toàn bộ DAO, sau đó "tiêm" DAO vào các Service, và tiếp tục "tiêm" Service vào các Controller, giống hệt cách framework khổng lồ Spring Boot hoạt động ở đằng sau hậu trường.

Kiến trúc này giúp dự án hoàn toàn giải quyết được sự phụ thuộc chéo, dễ dàng kiểm thử và đảm bảo tính mạch lạc tuyệt đối trong dòng chảy dữ liệu.
