# 🎬 CAH Cinema — Ứng dụng Đặt Vé Xem Phim Android

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-green?logo=android" />
  <img src="https://img.shields.io/badge/Language-Kotlin-blueviolet?logo=kotlin" />
  <img src="https://img.shields.io/badge/UI-Jetpack%20Compose-blue?logo=jetpackcompose" />
  <img src="https://img.shields.io/badge/Architecture-MVVM%20%2B%20Clean-orange" />
  <img src="https://img.shields.io/badge/Min%20SDK-24-lightgrey" />
  <img src="https://img.shields.io/badge/Target%20SDK-36-lightgrey" />
</p>

---

## 📖 Giới thiệu

**CAH Cinema** là ứng dụng Android cho phép người dùng tìm kiếm phim, xem lịch chiếu, đặt vé, chọn ghế và thanh toán qua mã QR chuyển khoản ngân hàng. Tích hợp đầy đủ Admin Panel và Staff Panel để quản lý vận hành rạp phim.

Dự án theo mô hình **Thin Client** — toàn bộ logic nghiệp vụ (tính giá, điểm tích lũy, hạng thành viên) xử lý ở Backend. Frontend chỉ thu thập lựa chọn và hiển thị kết quả từ API.

---

## ✨ Tính năng chính

**Người dùng**
- Đăng ký / đăng nhập email hoặc Google Sign-In, quên mật khẩu qua OTP
- Xem phim đang chiếu, sắp chiếu, chi tiết phim (trailer, đạo diễn, diễn viên, bình luận)
- Đặt vé: chọn suất → chọn ghế (REGULAR / VIP / COUPLE) → thêm đồ ăn → áp voucher → thanh toán QR
- Xem khuyến mãi, lịch sử đặt vé, chi tiết vé kèm mã QR, hồ sơ cá nhân (điểm tích lũy, hạng thành viên)
- Chỉnh sửa thông tin cá nhân, đổi mật khẩu

**Quản trị viên (Admin)**
- Dashboard tổng quan: doanh thu, vé bán, đơn hàng — biểu đồ cột và biểu đồ tròn
- CRUD phim (upload poster qua Cloudinary), rạp & phòng chiếu, suất chiếu (2D/3D/IMAX)
- Quản lý đồ ăn & nước, voucher, khuyến mãi
- Thiết lập sơ đồ ghế, cấu hình hệ số giá (ngày thường/lễ/cuối tuần, buổi sáng/chiều/tối), quản lý ngày lễ
- Báo cáo doanh thu theo phim, theo rạp, tổng quan kinh doanh

**Nhân viên (Staff)**
- Dashboard nhân viên: xem danh sách suất chiếu trong ngày
- Bán vé tại quầy: chọn suất → chọn ghế → thanh toán
- Check-in vé: quét mã QR bằng camera để xác nhận vào rạp

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
├── MainActivity.kt                  # NavHost, BottomBar, AdminSidebar
├── CAHCinemaApplication.kt
├── data/
│   ├── model/                       # DTOs: Auth, Movie, Booking, Cinema, Admin, Voucher, Comment, Profile, Staff...
│   ├── remote/                      # ApiService.kt, RetrofitClient.kt
│   └── repository/                  # AdminRepositoryImpl, ConcessionRepositoryImpl, StaffRepositoryImpl
├── domain/
│   ├── model/                       # Domain models: Movie, Seat, Concession, Promotion, TicketType
│   ├── repository/                  # Interfaces: AdminRepository, BookingRepository, ConcessionRepository, StaffRepository
│   └── usecase/                     # GetSeatsUseCase, GetConcessionsUseCase
├── presentation/
│   ├── admin/                       # Dashboard, Movies, Cinemas, Showtimes, Food, Voucher, Promotion, Report, Settings, Seats
│   │   └── components/              # AdminScaffold, AdminSidebar, AdminStatCard, AdminChartCard, SimpleBarChart, SimplePieChart
│   ├── staff/                       # Dashboard, CheckIn (QR scan), Sell (bán vé tại quầy)
│   ├── component/                   # AuthComponents, MovieComponents, LoadingComponent
│   ├── navigation/                  # Screen.kt, BottomNavigationBar, NotificationScreen
│   └── user/                        # auth/, booking/, cinema/, detail/, home/, profile/, promotion/, splash/
├── ui/theme/                        # Color.kt, Theme.kt, Type.kt
└── util/                            # CloudinaryUploader, QrCodeGenerator, PreferenceManager, DateTimeUtils
```

---

## 🗺️ Luồng điều hướng

```
App Start → SplashScreen → MainViewModel (kiểm tra JWT + role)
    ├── role = USER   → Home
    ├── role = ADMIN  → AdminDashboard (Sidebar)
    ├── role = STAFF  → StaffDashboard
    └── Không có token → Login → Register | ForgotPassword → OTP → ResetPassword

Home (Bottom Nav: Home / Cinema / Notification / Profile)
    └── Movie Detail → TicketSelection → SeatSelection → Concession → [Voucher] → Payment → PaymentLoading → TicketDetail
    └── Profile → BookingHistory | EditProfile | ChangePassword

Admin (Sidebar: Dashboard / Phim / Rạp / Lịch chiếu / Đồ ăn / Khuyến mãi / Voucher / Báo cáo / Cài đặt)

Staff (Dashboard → Bán vé tại quầy | Check-in QR)
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
| Admin | CRUD `/admin/movies\|cinemas\|showtime\|vouchers\|food\|seats\|price-config\|holiday\|reports\|promotions` |
| Staff | `GET /staff/showtimes` · `POST /staff/checkin` · `POST /staff/sell` |

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
| CameraX | 1.4.2 | Quét QR (Staff check-in) |
| Accompanist Permissions | 0.36.0 | Xin quyền camera runtime |
| Core Splashscreen | 1.2.0 | Splash screen |
| Credentials + GoogleId | 1.3.0 / 1.1.1 | Google Sign-In |

---

## ⚙️ Cài đặt & Chạy

**Yêu cầu**: Android Studio Hedgehog+, JDK 11+, Android SDK 24–36

```bash
git clone <repository-url>
cd CAH_Cinema
```

Tạo file `local.properties` (tham khảo `local.properties.example`):
```properties
CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_UPLOAD_PRESET=your_upload_preset
GOOGLE_WEB_CLIENT_ID=your_google_web_client_id
```

Cập nhật Base URL trong `RetrofitClient.kt`:
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
