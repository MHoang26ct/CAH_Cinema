# 📱 CAH Cinema - Tài liệu Kỹ thuật dành cho Backend Integration

---

## 🏗️ 1. Kiến trúc Hệ thống (Clean Architecture)

Dự án được tổ chức theo 3 lớp chuẩn để đảm bảo tính độc lập:

1.  **Presentation Layer** (Frontend quản lý): Chứa UI (Compose) và ViewModel. 
    *   *Nhiệm vụ*: Quan sát trạng thái (`State`) và hiển thị lên màn hình.
2.  **Domain Layer** (Lớp trung gian): Chứa `Model` nghiệp vụ, `UseCase` và `Repository Interfaces`.
    *   *Nhiệm vụ*: Định nghĩa "Hợp đồng" mà Backend phải tuân thủ.
3.  **Data Layer** (⚠️ **Backend/Data Dev thực hiện**): 
    *   *Nhiệm vụ*: Gọi API (Retrofit), lưu Database local và **Map** dữ liệu từ API (DTO) sang Domain Model.

---

## 🚀 2. Danh mục API cần triển khai (Endpoints)

Dưới đây là danh sách các màn hình hiện có và API tương ứng cần thiết:

### 🎬 Module: Phim & Rạp (Movies & Cinemas)
| Màn hình | API Endpoint | Phương thức | Dữ liệu trả về tiêu biểu |
| :--- | :--- | :--- | :--- |
| **Trang chủ (Home)** | `/api/movies` | `GET` | List Phim kèm `isFeatured`, `isUpcoming`. |
| **Trang rạp (Cinema)** | `/api/cinemas` | `GET` | Tên rạp, địa chỉ, hình ảnh rạp. |
| **Chi tiết phim** | `/api/movies/{id}` | `GET` | Nội dung phim, Đạo diễn, Diễn viên, Link Banner. |
| **Lịch chiếu** | `/api/showtimes` | `GET` | Query theo `movieId` và `date`. Trả về list Rạp + Giờ chiếu. |

### 👤 Module: Người dùng (User & Authentication)
| Màn hình | API Endpoint | Phương thức | Ghi chú |
| :--- | :--- | :--- | :--- |
| **Đăng nhập** | `/api/auth/login` | `POST` | Trả về `accessToken` và `userId`. |
| **Profile** | `/api/user/profile` | `GET` | `avatar_url`, `total_spent`, `rank`, `recent_ticket`. |
| **Chỉnh sửa hồ sơ** | `/api/user/profile` | `PUT` | Cập nhật `name`, `email`, `phone`. |
| **Đổi mật khẩu** | `/api/user/password` | `PUT` | Gửi `old_password` và `new_password`. |

### 🎟️ Module: Đặt vé & Thanh toán (Booking)
| Màn hình | API Endpoint | Phương thức | Ghi chú |
| :--- | :--- | :--- | :--- |
| **Chọn ghế** | `/api/seats/{showtimeId}` | `GET` | Trả về trạng thái ghế: `AVAILABLE`, `TAKEN`, `VIP`. |
| **Bắp nước** | `/api/concessions` | `GET` | List đồ ăn kèm giá và hình ảnh. |
| **Tạo đơn hàng** | `/api/booking/create` | `POST` | Trả về `booking_id` để Frontend tạo mã QR. |
| **Check Thanh toán**| `/api/booking/status/{id}` | `GET` | Trả về `PAID` hoặc `PENDING`. |

---

## 💡 3. Các Logic nghiệp vụ quan trọng

### 💎 Quy tắc Tính điểm (Loyalty Points)
Frontend đã cài đặt logic hiển thị: **40.000 VNĐ = 1 điểm tích lũy.**
- Backend cần lưu trữ `total_spent` (tổng tiền người dùng đã thanh toán thành công).
- Công thức tính hạng:
  - **Hạng Bạc**: 0 - 500 điểm.
  - **Hạng Vàng**: 501 - 1500 điểm.
  - **Hạng Kim cương**: > 1500 điểm.

### 💳 Quy trình Thanh toán QR (QR Transfer)
1. Frontend sẽ tạo mã QR dựa trên thông tin Backend cung cấp (Số tài khoản, Số tiền, Nội dung).
2. Nội dung chuyển khoản nên có định dạng: `CAH <booking_id>`.
3. Backend cần sử dụng Webhook hoặc kiểm tra lịch sử giao dịch để cập nhật trạng thái đơn hàng.

### 🕒 Màn hình Chờ (Loading State)
Tất cả các ViewModel của Frontend đều đã có biến `isLoading: Boolean` trong `State`.
- Khi các bạn gọi API, hãy gán `isLoading = true`.
- Khi có kết quả (Success/Fail), gán `isLoading = false`. UI sẽ tự động ẩn vòng xoay loading.

---

## 🖼️ 4. Quản lý Tài nguyên
- **Hình ảnh**: Tất cả URL mockup đang nằm tại `com.example.cah_cinema.util.ImageUrls`. Backend hãy thay thế bằng link thực tế từ CDN/Server.
- **Mã vé**: Sau khi thanh toán, Backend cần trả về một chuỗi `ticket_code` để Frontend tạo mã QR tại màn hình chi tiết vé.

---
