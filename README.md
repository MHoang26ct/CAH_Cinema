# 🎬 CAH Cinema — Ứng dụng Đặt Vé Xem Phim Android

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-green?logo=android" />
  <img src="https://img.shields.io/badge/Language-Kotlin-blueviolet?logo=kotlin" />
  <img src="https://img.shields.io/badge/UI-Jetpack%20Compose-blue?logo=jetpackcompose" />
  <img src="https://img.shields.io/badge/Architecture-MVVM%20%2B%20Clean-orange" />
  <img src="https://img.shields.io/badge/Min%20SDK-24-lightgrey" />
</p>

---

## 📖 Giới thiệu

**CAH Cinema** là ứng dụng Android cho phép người dùng tìm kiếm phim, xem lịch chiếu, đặt vé, chọn ghế và thanh toán qua mã QR chuyển khoản ngân hàng. Tích hợp Admin Panel đầy đủ để quản lý phim, rạp, suất chiếu, đồ ăn, voucher và báo cáo doanh thu.

Dự án theo mô hình **Thin Client** — toàn bộ logic nghiệp vụ (tính giá, điểm tích lũy, hạng thành viên) xử lý ở Backend. Frontend chỉ thu thập lựa chọn và hiển thị kết quả từ API.

---

## ✨ Tính năng chính

**Người dùng**
- Đăng ký / đăng nhập email hoặc Google Sign-In, quên mật khẩu qua OTP
- Xem phim đang chiếu, sắp chiếu, chi tiết phim (trailer, đạo diễn, diễn viên)
- Đặt vé: chọn suất → chọn ghế (REGULAR / VIP / COUPLE) → thêm đồ ăn → áp voucher → thanh toán QR
- Lịch sử đặt vé, chi tiết hóa đơn kèm mã QR, hồ sơ cá nhân (điểm tích lũy, hạng thành viên)

**Quản trị viên**
- Dashboard tổng quan doanh thu
- CRUD phim (upload poster Cloudinary), rạp & phòng chiếu, suất chiếu (2D/3D/IMAX), đồ ăn, voucher
- Thiết lập sơ đồ ghế, cấu hình hệ số giá (ngày thường/lễ/cuối tuần, buổi sáng/chiều/tối), quản lý ngày lễ
- Báo cáo doanh thu theo ngày, theo phim, theo rạp

---

## 🏛️ Kiến trúc

**MVVM + Clean Architecture (3 layer)**

```
Presentation  →  Jetpack Compose Screens + ViewModels (StateFlow)
Domain        →  Repository Interfaces + Domain Models + Use Cases
Data          →  RepositoryImpl + ApiService (Retrofit) + PreferenceManager (JWT)
```

---

## 📁 Cấu trúc thư mục

```
CAH_Cinema/app/src/main/java/com/example/cah_cinema/
├── MainActivity.kt              # NavHost, BottomBar, AdminSidebar
├── data/
│   ├── model/                   # DTOs (Auth, Movie, Booking, Cinema, Voucher, Admin...)
│   ├── remote/                  # ApiService.kt, RetrofitClient.kt
│   └── repository/              # RepositoryImpl
├── domain/
│   ├── model/                   # Domain models thuần Kotlin
│   ├── repository/              # Repository interfaces
│   └── usecase/                 # GetSeatsUseCase, GetConcessionsUseCase
├── presentation/
│   ├── admin/                   # Dashboard, Movies, Cinemas, Showtimes, Food, Voucher, Report, Settings, Seats
│   ├── navigation/              # Screen.kt (sealed class), BottomNavigationBar
│   └── user/                    # auth/, booking/, cinema/, detail/, home/, profile/, promotion/
├── ui/theme/                    # Color.kt, Theme.kt, Type.kt
└── util/                        # CloudinaryUploader, QrCodeGenerator, PreferenceManager, DateTimeUtils
```

---

## 🗺️ Luồng điều hướng

```
App Start → MainViewModel (kiểm tra JWT)
    ├── Có token  → Home (user) / AdminDashboard (admin)
    └── Không có  → Login → Register | ForgotPassword → OTP → ResetPassword

Home (Bottom Nav: Home / Cinema / Notification / Profile)
    Movie/Cinema Detail → TicketSelection → SeatSelection → Concession → [Voucher] → Payment → PaymentLoading → TicketDetail
    Profile → BookingHistory | EditProfile | ChangePassword | (Admin) → AdminDashboard (Sidebar)
```

---

## 📡 API & Backend

**Base URL**: `http://<server-ip>:8080/`  
**Auth**: JWT Bearer Token tự động đính kèm qua OkHttp Interceptor

| Nhóm | Endpoints |
|------|-----------|
| Auth | `POST /api/v1/auth/login\|register\|google\|send-otp\|fp-verify-otp\|fp-change-password\|change-password` |
| Phim | `GET /api/v1/public/movies/featured\|movies\|movies/{id}\|genres/all` |
| Rạp & Suất chiếu | `GET /api/v1/public/cinemas\|showtimes/movies/{id}\|showtimes/cinemas/{id}` |
| Ghế & Đặt vé | `GET /public/seats` · `POST /seats/pre-lock` · `POST /bookings` · `POST /bookings/{id}/confirm-payment` |
| Người dùng | `GET/PATCH /api/v1/users/me` · `GET /user/vouchers\|food` |
| Admin | CRUD `/admin/movies\|cinemas\|showtime\|vouchers\|food\|seats\|price-config\|holiday\|reports` |

---

## 📦 Thư viện chính

| Thư viện | Phiên bản | Mục đích |
|----------|-----------|----------|
| Kotlin | 2.0.21 | Ngôn ngữ |
| Jetpack Compose BOM | 2024.12.01 | UI (Material3) |
| Navigation Compose | 2.8.5 | Điều hướng |
| Retrofit + OkHttp | 2.11.0 / 4.12.0 | HTTP client |
| Gson | 2.11.0 | JSON |
| Coil Compose | 2.7.0 | Load ảnh |
| ZXing Core | 3.5.3 | Tạo mã QR |
| Core Splashscreen | 1.2.0 | Splash screen |
| Credentials + GoogleId | 1.3.0 / 1.1.1 | Google Sign-In |

---

## ⚙️ Cài đặt

**Yêu cầu**: Android Studio Hedgehog+, JDK 11+, Android SDK 24–36

```bash
git clone <repository-url>
cd CAH_Cinema
```

Tạo `local.properties`:
```properties
CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_UPLOAD_PRESET=your_upload_preset
GOOGLE_WEB_CLIENT_ID=your_google_web_client_id
```

Cập nhật `RetrofitClient.kt`:
```kotlin
private const val BASE_URL = "http://<your-backend-ip>:8080/"
```

```bash
./gradlew assembleDebug
```

---

## 🎨 Theme & Màu sắc

Dark theme (`#13131A`) xuyên suốt. Màu nhấn chính: **CyanBlue `#00E5FF`**.  
Màu ghế: Xanh lá (thường) · Cam (VIP) · Tím (đôi) · Xám (không khả dụng).  
Hỗ trợ Dynamic Color trên Android 12+.

---

## 🔗 Liên kết

- **Backend**: [Backend_CAH_Cinema](../Backend_CAH_Cinema)
- **Tài liệu kỹ thuật**: [README_FRONTEND_TECHNICAL.md](README_FRONTEND_TECHNICAL.md)
- **Tích hợp FE-BE**: [FRONTEND_BACKEND_INTEGRATION.md](FRONTEND_BACKEND_INTEGRATION.md)

---

## 👥 Tác giả

| Họ và tên | Vai trò |
|-----------|---------|
| **Lê Văn An** | Thành viên nhóm |
| **Nguyễn Cao Cường** | Thành viên nhóm |
| **Mai Minh Hoàng** | Thành viên nhóm |

---

<p align="center">Được xây dựng với ❤️ bằng Kotlin & Jetpack Compose</p>
