# ĐỒ ÁN MÔN HỌC: CÔNG NGHỆ JAVA

**Tên đề tài:** XÂY DỰNG PHẦN MỀM QUẢN LÝ BÁN HÀNG VÀ THANH TOÁN CHO QUÁN CÀ PHÊ & TRÀ SỮA

**Giảng viên hướng dẫn:** THS. SỬ NHẬT HẠ

**Sinh viên thực hiện:**
- 24730103	TRẦN ĐÌNH HUY
- 24730135	NGUYỄN NGỌC QUANG
- 24730150	LÊ THANH TRÚC VI
- 24730155	VŨ HOÀNG THIÊN ÂN
- 24730156	DƯƠNG PHƯƠNG ANH

**Lớp:** CN1.K2024.2 - IE303.F21.CN1.CNTT

---

## Hướng dẫn chạy phần mềm

Để chạy dự án, bạn cần khởi tạo Cơ sở dữ liệu (Database) trước, sau đó mới chạy ứng dụng Java.

### Bước 1: Khởi tạo Database (MySQL) bằng Docker hoặc Podman
Dự án đã cấu hình sẵn file `docker-compose.yml` để dựng MySQL và phpMyAdmin. Quá trình này sẽ **tự động khởi tạo và import** cấu trúc bảng cùng dữ liệu mẫu từ các file SQL nằm trong thư mục `database/` (bao gồm `database/drink_store.sql` và `database/seed_demo_orders.sql`).

Mở terminal tại thư mục gốc của dự án và chạy một trong các lệnh sau:

**Nếu dùng Docker:**
```bash
docker-compose up -d
```

**Nếu dùng Podman:**
```bash
podman-compose up -d
```
*(Lưu ý: phpMyAdmin sẽ chạy ở địa chỉ `http://localhost:8080` để bạn tiện quản lý Database nếu cần)*

### Bước 2: Chạy ứng dụng Java
Sau khi Database đã được khởi chạy thành công, bạn tiến hành mở phần mềm bán hàng bằng file `.jar` đã được build sẵn nằm trong thư mục `target/`.

Mở terminal tại thư mục gốc của dự án và chạy:
```bash
java -jar target/brewpoint-pos-1.0.0.jar
```
*(Yêu cầu: Máy tính cần cài đặt sẵn Java 17+)*

---

## Hướng dẫn tự Build file `.jar` (Tùy chọn)

Nếu muốn tự biên dịch (build) lại dự án từ mã nguồn, bạn cần chuẩn bị môi trường với các công cụ sau:

**Yêu cầu hệ thống:**
- **Java:** Cài đặt JDK 17
- **Maven:** Cài đặt Apache Maven (phiên bản 3.6+ hoặc 3.8+ được khuyến nghị)

**Lệnh thực hiện:**
1. Mở terminal tại thư mục gốc của dự án (nơi chứa file `pom.xml`).
2. Chạy lệnh sau để build mã nguồn:
```bash
mvn clean package
```
3. Sau khi chạy xong, kết quả build sẽ là file `target/brewpoint-pos-1.0.0.jar`.
