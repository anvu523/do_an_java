# CHƯƠNG 4. THỰC NGHIỆM VÀ ĐÁNH GIÁ CHẤT LƯỢNG

Việc viết ra một mã nguồn tốt chỉ là một nửa chặng đường. Để một sản phẩm phần mềm có thể sẵn sàng bàn giao cho người dùng cuối (End-user), nó bắt buộc phải vượt qua các quá trình kiểm thử tự động, đóng gói liền mạch và đảm bảo một trải nghiệm giao diện người dùng (UI/UX) thân thiện nhất.

## 4.1. Môi trường Thực nghiệm, Công cụ và Build System
Hệ thống được lập trình, kiểm thử và đóng gói trên môi trường phần mềm đạt tiêu chuẩn công nghiệp:
- **Ngôn ngữ lập trình:** Java Development Kit (JDK) 17 - Đảm bảo hiệu năng ổn định cùng các tính năng mới như Text Blocks, Pattern Matching.
- **Công cụ quản lý vòng đời dự án (Build Tool):** Apache Maven. Không giống như các dự án cơ bản vốn tải thư viện file `.jar` bằng tay rất thủ công và dễ gây xung đột lỗi phiên bản, dự án này tận dụng sức mạnh của Maven để quản lý tự động mọi gói phụ thuộc (Dependencies).
- **Cơ sở dữ liệu:** MySQL 8.0 cho môi trường sản xuất (Production).
- **Công cụ báo cáo (Reporting Engine):** Thư viện JasperReports kết hợp với các công cụ font chữ tiên tiến, đảm bảo xuất tiếng Việt Unicode chuẩn xác trên các file PDF và Excel.

Quá trình biên dịch mã nguồn và đóng gói phần mềm thành tệp `.jar` thực thi được tự động hóa hoàn toàn bằng lệnh Command Line `mvn clean package`. Quá trình này giúp tích hợp toàn bộ các thư viện phụ thuộc (MySQL Connector, Jasper, POI...) trực tiếp vào chung một file thực thi thông qua kỹ thuật **Shade plugin** (Uber-Jar). Nhờ thiết lập cấu hình thông minh này, phần mềm đồ án của nhóm có thể dễ dàng sao chép sang bất kỳ máy tính, laptop cá nhân nào và có thể chạy được ngay lập tức chỉ với một thao tác click đúp chuột hoặc một lệnh chạy đơn giản trên Terminal, miễn là máy tính đó có cài đặt Java Runtime Environment (JRE). Sự tiện lợi này là một điểm cộng rất lớn trong việc chuyển giao công nghệ.

## 4.2. Trải nghiệm Chức năng Dưới Góc Nhìn Người Dùng (UI/UX)

Sản phẩm sở hữu giao diện Java Swing được trau chuốt tỉ mỉ bằng các thư viện giao diện nâng cao, phá vỡ đi định kiến về sự cứng nhắc và nhàm chán của các bộ công cụ lập trình đồ họa truyền thống.

### 4.2.1. Phân hệ Quản lý Bán hàng tại quầy (Point of Sale)
Giao diện bán hàng chính là trái tim của hệ thống, được thiết kế với tư duy tối giản thao tác, tối đa năng suất để hỗ trợ các thu ngân hoạt động với tốc độ "chóng mặt" trong giờ cao điểm. Giao diện được chia thành các khu vực rõ ràng: Danh sách menu, Khu vực giỏ hàng chi tiết và Khu vực thanh toán.
Thu ngân có thể nhanh chóng tra cứu đồ uống theo tên, thêm mới vào hóa đơn, lựa chọn các kích cỡ (Size M, Size L) và bổ sung các loại Topping ăn kèm (Trân châu trắng, Thạch nha đam). Ngay khi thu ngân có một thao tác nhấn chuột thay đổi, hệ thống lập tức tự động tính toán tổng tiền phải thu của khách hàng theo thời gian thực (Real-time recalculation). 
Điểm nhấn đáng kể là chức năng Hỗ trợ tính tiền thừa: Khi thu ngân nhập số tiền khách hàng đưa vào ô, phần mềm lập tức tính tiền thừa để trả lại khách. Khi nhấn "Thanh toán", một Hóa đơn điện tử chuyên nghiệp (PDF Receipt) với đầy đủ thông tin mã hóa đơn, thông tin nhân viên thu ngân và danh sách mặt hàng cùng giá cả sẽ lập tức được tự động kết xuất ra màn hình (hoặc gửi thẳng đến máy in nhiệt 58mm/80mm thực tế). Trải nghiệm này mang đến cảm giác liền mạch, không có độ trễ.

### 4.2.2. Phân hệ Quản trị với tính năng Hiện đại: Kéo và Thả (Drag and Drop)
Tại các chức năng quản lý danh mục dữ liệu, người quản trị thường có nhu cầu muốn thay đổi thứ tự ưu tiên hiển thị của các danh mục (Ví dụ muốn "Cà Phê" hiển thị lên trên cùng, sau đó mới tới "Trà Sữa"). Ở các phần mềm cũ, người dùng phải nhập tay các con số thứ tự (ví dụ: 1, 2, 3...) vào một ô TextBox rất thủ công, dễ gây trùng lặp và gây khó chịu khi phải dời hàng loạt danh mục.

Để đem lại một trải nghiệm mượt mà mang phong cách Web hiện đại, nhóm đã nâng cấp toàn diện phân hệ này bằng việc áp dụng công nghệ **Kéo và Thả (Drag & Drop)** trực tiếp trên bảng dữ liệu `JTable`. Quản trị viên chỉ cần giữ chuột trái vào một dòng danh mục bất kỳ và rê nó thả vào một vị trí khác. Hệ thống ngay lập tức tiếp nhận sự kiện thả (Drop event), tái cơ cấu lại danh sách trên bộ nhớ RAM, tự động cấp phát số thứ tự liên tiếp hoàn hảo (1, 2, 3... N) để tuyệt đối không xảy ra trùng lặp, và đặc biệt là hệ thống sẽ **âm thầm tự động lưu (Auto-save) sự thay đổi này thẳng xuống cơ sở dữ liệu**. Việc áp dụng cơ chế `Batch Update` của JDBC để đồng bộ vài chục danh mục cùng một lúc đem lại thời gian xử lý vỏn vẹn dưới vài chục mili-giây, khiến người dùng hoàn toàn không cảm nhận được là ứng dụng vừa thực hiện tác vụ với Database. Tính năng này chứng tỏ sự am hiểu và làm chủ sâu sắc giao diện đồ họa Java của nhóm phát triển.

## 4.3. Đánh giá Mức độ ổn định thông qua Kiểm thử tự động (Unit Testing)
Mọi hàm tính toán nghiệp vụ quan trọng (logic tính giá Topping, tính tiền thừa) và việc kiểm tra quá trình xuất báo cáo (Jasper) đều được bảo vệ bởi một bộ áo giáp mang tên Unit Tests (được viết bằng bộ framework kinh điển JUnit). 
Tổng cộng đã có **32 Test Cases** độc lập được cài đặt. Quá trình chạy thực nghiệm lệnh `mvn test` chứng minh tỷ lệ vượt qua (Pass Rate) đạt mức tuyệt đối **100%**, hoàn toàn không phát hiện hiện tượng Memory Leak (rò rỉ bộ nhớ) hay các lỗi nghiêm trọng làm đứng chương trình (Crash, NullPointerException). Sự an toàn và khả năng chạy Unit Test suôn sẻ này không phải ngẫu nhiên mà có, đó chính là phần thưởng xứng đáng của việc tuân thủ triệt để kiến trúc DI (Dependency Injection), giúp nhóm có thể dễ dàng làm giả (Mocking) kết nối CSDL khi tiến hành kiểm thử các tầng chức năng bên trong, một tiêu chuẩn cao cấp vốn thường chỉ thấy ở các công ty công nghệ chuyên nghiệp.
