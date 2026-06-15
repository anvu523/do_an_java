# BrewPoint POS — Hướng dẫn demo

Tài liệu ngắn gọn để trình diễn đồ án. JasperReports **6.21.3** (không nâng v7).

---

## 1. Chuẩn bị database

### Windows (PowerShell) — khuyến nghị

Chạy **một script** (tự `down -v`, `up -d`, chờ MySQL, kiểm tra UTF-8):

```powershell
cd C:\Users\nngocquang\Documents\pos\do_an_java
.\scripts\reset-db-podman.ps1
```

Script trên sẽ:

1. `podman compose down -v` — xóa volume cũ (dữ liệu lỗi encoding cũng mất)
2. `podman compose up -d` — MySQL tự import **cả hai** file SQL trong container (không qua PowerShell pipe)
3. Chờ MySQL `healthy` (lần đầu import seed ~1–3 phút)
4. Chạy `verify-db-utf8.ps1` — phải thấy `UTF-8 OK`

**Tại sao không pipe `Get-Content | mysql`?**  
Trên Windows, pipe SQL từ PowerShell vào MySQL thường **hỏng tiếng Việt** (`Tr? s?a`, `B?c x?u`). Dấu `?` trong app = dữ liệu DB đã lưu sai, không phải lỗi font Swing.

**Cách import an toàn (đã cấu hình trong `docker-compose.yml`):**

| File trong container | Nội dung |
|---|---|
| `01_drink_store.sql` | Schema, sản phẩm, topping, tài khoản |
| `02_seed_demo_orders.sql` | 1.649 đơn demo (05–06/2026) |

Sau `down -v` + `up -d` **không cần** import seed thủ công nữa.

### Thủ công (nếu không dùng script)

```powershell
cd C:\Users\nngocquang\Documents\pos\do_an_java
podman compose down -v
podman compose up -d
```

Đợi đến khi healthy (xem `podman ps` — cột STATUS `healthy`), rồi kiểm tra:

```powershell
.\scripts\verify-db-utf8.ps1
```

Kết quả mong đợi: có dòng `Trà sữa trân châu đường đen` và `UTF-8 OK`.

### Chỉ import lại seed (không xóa volume)

**Không dùng** `Get-Content ... | podman exec`. Dùng:

```powershell
.\scripts\import-seed-podman.ps1
```

(Script dùng `podman cp` + `mysql < file` trong container.)

### Linux / macOS

```bash
podman compose down -v && podman compose up -d
# Đợi MySQL healthy, rồi:
podman exec drink-store-mysql mysql -uroot --default-character-set=utf8mb4 -e \
  "SELECT name FROM brewpoint_pos.products LIMIT 3;"
```

| Script | Nội dung |
|---|---|
| `database/drink_store.sql` | Schema, sản phẩm, topping, 3 thu ngân |
| `database/seed_demo_orders.sql` | **1.649 đơn** từ 01/05/2026 → 15/06/2026 |

Tái tạo file seed (tùy chọn):

```bash
python database/generate_demo_orders.py
```

> **Lưu ý:** Nếu không có seed, báo cáo / Tổng quan ngày sẽ trống hoặc ít dữ liệu.

### Xử lý lỗi tiếng Việt (`?`, `B???c`)

1. Chạy lại `.\scripts\reset-db-podman.ps1`
2. Phải thấy `UTF-8 OK` trước khi mở app
3. `mvn clean package` rồi chạy lại JAR
4. Vào **Tổng quan ngày** — cột Sản phẩm phải hiện *Trà sữa trân châu đường đen*, không phải `Tr? s?a`

(MySQL Podman: user `root`, **mật khẩu trống**.)

---

## 2. Chạy ứng dụng

```bash
mvn clean package
java -jar target/brewpoint-pos-1.0.0.jar
```

Kiểm tra unit test (báo cáo):

```bash
mvn test
```

---

## 3. Tài khoản demo

| Vai trò | Username | Mật khẩu |
|---|---|---|
| Admin | `admin` | `admin123` |
| Thu ngân 1 | `cashier01` | `cashier123` |
| Thu ngân 2 | `cashier02` | `cashier123` |
| Thu ngân 3 | `cashier03` | `cashier123` |

| Thu ngân | Họ tên |
|---|---|
| cashier01 | Nguyễn Thị Lan |
| cashier02 | Trần Văn Minh |
| cashier03 | Lê Hoàng An |

---

## 4. Kịch bản demo (~10 phút)

### A. Báo cáo (admin)

1. Đăng nhập `admin` / `admin123`
2. Menu **Báo cáo in/PDF**
3. Tháng **06/2026** (hoặc ngày **15/06/2026** cho báo cáo ngày)
4. Thử lần lượt:
   - Doanh thu theo ngày → **Xem báo cáo** / **Xuất PDF**
   - Doanh thu theo tháng
   - Sản phẩm bán chạy *(kỳ vọng: Trà sữa trân châu đường đen, Trà đào cam sả, Bạc xỉu đứng đầu)*
   - Doanh thu từng thu ngân *(3 thu ngân chia đều ~33% mỗi người)*

### B. Bán hàng + hóa đơn (thu ngân)

1. Đăng nhập `cashier01` / `cashier123`
2. Chọn món (ví dụ Trà sữa trân châu đường đen) + topping
3. **Thanh toán** → tiền mặt → nhập **100.000 ₫**
4. Dialog **THANH TOÁN THÀNH CÔNG**:
   - **Xem hóa đơn** — Jasper preview (tiếng Việt)
   - **Xuất PDF** — lưu file `Invoice_HD....pdf`
   - **In hóa đơn** — nếu có máy in (không bắt buộc khi demo)
   - **Đơn mới** — bắt đầu đơn tiếp theo

---

## 5. Đối chiếu số liệu (tùy chọn)

```sql
-- Doanh thu một ngày
SELECT COUNT(*), SUM(total_amount) FROM orders
WHERE status = 'COMPLETED' AND DATE(order_time) = '2026-06-15';

-- Doanh thu tháng 6/2026
SELECT SUM(total_amount) FROM orders
WHERE status = 'COMPLETED'
  AND order_time >= '2026-06-01' AND order_time < '2026-07-01';

-- Top sản phẩm tháng 6
SELECT product_name_snapshot, SUM(quantity) AS qty, SUM(line_total) AS revenue
FROM order_items oi
JOIN orders o ON o.order_id = oi.order_id
WHERE o.status = 'COMPLETED'
  AND o.order_time >= '2026-06-01' AND o.order_time < '2026-07-01'
GROUP BY product_name_snapshot
ORDER BY qty DESC
LIMIT 5;

-- Doanh thu từng thu ngân tháng 6
SELECT e.full_name, COUNT(*) AS orders, SUM(o.total_amount) AS revenue
FROM orders o
JOIN employees e ON e.employee_id = o.employee_id
WHERE o.status = 'COMPLETED'
  AND o.order_time >= '2026-06-01' AND o.order_time < '2026-07-01'
GROUP BY e.employee_id, e.full_name
ORDER BY revenue DESC;
```

So sánh kết quả SQL với báo cáo trên màn hình.

---

## 6. Dữ liệu seed — đặc điểm

- Mỗi ngày trong khoảng 01/05–15/06 đều có đơn
- **Ngày thường:** ~24–34 đơn/ngày | **Cuối tuần:** ~44–54 đơn/ngày
- **Giờ cao điểm:** 11:00–13:30 và 18:00–21:00
- **Thanh toán:** ~72% tiền mặt, ~28% chuyển khoản
- **Topping:** ~66% đơn không topping; ~34% có 1–2 topping

---

## 7. Screenshot gợi ý cho báo cáo đồ án

1. Đăng nhập thu ngân
2. Giỏ hàng + dialog thanh toán (tiền thừa)
3. Dialog thanh toán thành công
4. Jasper preview hóa đơn nhiệt 58mm
5. File PDF hóa đơn
6. Báo cáo doanh thu ngày (preview)
7. Báo cáo doanh thu tháng
8. Báo cáo sản phẩm bán chạy
9. Báo cáo doanh thu từng thu ngân
10. Query MySQL đối chiếu tổng tháng

---

## 8. Lưu ý khi demo

- **Xem hóa đơn** — dialog thanh toán tạm ẩn; đóng preview bằng **Đóng** hoặc **X** → dialog thanh toán hiện lại
- **In hóa đơn** — cần có ít nhất một máy in trong Windows. Nếu báo *No print service found*:
  1. **Cài đặt** → **Bluetooth và thiết bị** → **Máy in và máy quét**
  2. **Thêm máy in hoặc máy quét** → **Microsoft Print to PDF**
  3. Khởi động lại app → **In hóa đơn** lại
- Không có máy in → dùng **Xuất PDF** (an toàn cho demo)
- Lỗi font Jasper lần đầu → đã xử lý bằng `JasperFontBootstrap` + `jasperreports-fonts`; chạy lại app sau `mvn clean package`
- Fat JAR: `target/brewpoint-pos-1.0.0.jar`
