# 🎞️ Tài liệu Hướng dẫn Tích hợp Frontend - Backend (CAH Cinema)

Tài liệu này dành cho đội ngũ Backend để hiểu cấu trúc dự án Android (Clean Architecture) và cách triển khai các API tương ứng để tích hợp với giao diện đã hoàn thiện.

---

## 🏗️ 1. Cấu trúc Project (Clean Architecture)

Dự án tuân thủ nghiêm ngặt mô hình 3 lớp:

1.  **Domain Layer** (`com.example.cah_cinema.domain`):
    *   **Model**: Các thực thể dữ liệu thuần túy (Movie, Cinema, Ticket...).
    *   **Repository (Interfaces)**: Khai báo các hàm cần thiết (ví dụ: `getMovies()`). Backend sẽ triển khai thực tế các hàm này ở lớp Data.
    *   **UseCase**: Chứa logic nghiệp vụ đơn lẻ (ví dụ: `GetUpcomingMoviesUseCase`).
2.  **Presentation Layer** (`com.example.cah_cinema.presentation`):
    *   **UI/Screen**: Giao diện Jetpack Compose.
    *   **ViewModel**: Quản lý trạng thái UI (`State`) và xử lý sự kiện (`Event`). **Đây là nơi Backend sẽ gọi UseCase để lấy dữ liệu.**
3.  **Data Layer** (⚠️ *Cần Backend triển khai*):
    *   Triển khai `RepositoryImpl`, gọi Retrofit API và chuyển đổi dữ liệu (Mapper) sang Domain Model.

---

## 🚀 2. Luồng Nghiệp vụ Chính (Business Flow)

### A. Luồng Đặt vé & Thanh toán (QR Transfer)
1.  **Chọn vé -> Chọn ghế -> Chọn Bắp nước.**
2.  **Thanh toán**: Người dùng thực hiện **Chuyển khoản QR**.
    *   *Backend*: Cần API tạo đơn hàng (`booking_id`) và trả về thông tin tài khoản ngân hàng.
3.  **Kiểm tra**: Khi nhấn "THANH TOÁN", ứng dụng sẽ gọi API check trạng thái.
    *   *Frontend*: Đang giả lập `isPaymentSuccessful = true`.
    *   *Backend*: Cần check DB xem đơn hàng đã khớp tiền chưa.
4.  **Hoàn tất**: Sau khi thanh toán, ứng dụng hiện màn hình "Đang tạo mã vé..." (3 giây) và chuyển sang trang **Mã vé (QR)**.

### B. Quy tắc Điểm tích lũy (Loyalty Points)
*   **Quy định**: **40.000 VNĐ = 1 điểm**.
*   **Backend**: Lưu trữ `total_spent` (tổng tiền đã chi).
*   **Hạng thành viên**:
    *   Hạng Bạc: 0 - 500 điểm.
    *   Hạng Vàng: 501 - 1500 điểm.
    *   Hạng Kim cương: > 1500 điểm.

---

## 📡 3. Danh sách API cần triển khai

### 🎬 Movies & Cinema
*   `GET /api/movies`: Trả về danh sách phim.
    *   `isFeatured: true`: Hiện ở banner đầu trang.
    *   `isUpcoming: true`: Hiện ở mục Sắp chiếu (chỉ hiện thông tin, không cho đặt vé).
*   `GET /api/cinemas`: Danh sách các rạp chiếu phim (hỗ trợ search theo tên/địa chỉ).
*   `GET /api/showtimes?movieId={id}&date={date}`: Lấy lịch chiếu của một phim theo ngày.

### 🎁 Promotions (Ưu đãi)
*   `GET /api/promotions`: Lấy danh sách các chương trình khuyến mãi hiện có để hiển thị banner trang chủ và tab Ưu đãi.

### 👤 User & Profile
*   `POST /api/auth/login`: Trả về Token và thông tin User cơ bản.
*   `GET /api/user/profile`: Thông tin chi tiết gồm: `avatar_url`, `loyalty_points`, `rank`, `email`, `phone`.
*   `PUT /api/user/profile`: Cập nhật tên, số điện thoại.
*   `PUT /api/user/change-password`: Đổi mật khẩu (cần mật khẩu cũ).

---

## 🖼️ 4. Quản lý Hình ảnh
Tất cả các URL hình ảnh mẫu đang được tập trung tại:
`com.example.cah_cinema.util.ImageUrls`

**Lưu ý cho Backend**: 
*   Các bạn nên trả về URL ảnh đầy đủ (ví dụ: `https://domain.com/images/poster.jpg`).
*   Frontend sử dụng thư viện **Coil** để tự động cache và hiển thị ảnh.

---

## 🛠️ 5. Hướng dẫn Tích hợp (Cho Backend Developer)

1.  **Bước 1**: Tạo package `com.example.cah_cinema.data`.
2.  **Bước 2**: Định nghĩa `ApiService` bằng Retrofit.
3.  **Bước 3**: Viết các `RepositoryImpl` kế thừa từ `domain/repository`.
4.  **Bước 4**: Tại các ViewModel (ví dụ `HomeViewModel`), thay thế dữ liệu Mock bằng cách gọi UseCase.
5.  **Bước 5**: Quản lý trạng thái Loading:
    ```kotlin
    // Trong ViewModel
    _state.update { it.copy(isLoading = true) }
    val result = getMoviesUseCase()
    _state.update { it.copy(isLoading = false, movies = result) }
    ```

---
**Ngày cập nhật**: 20/05/2024
