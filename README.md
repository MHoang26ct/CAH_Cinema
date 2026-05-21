# 🎬 CAH Cinema — Ứng dụng Đặt Vé Xem Phim Android

<p align="center">
  <img src="app/src/main/res/mipmap-xxxhdpi/ic_launcher.png" alt="CAH Cinema Logo" width="100"/>
</p>

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

**CAH Cinema** là ứng dụng Android cho phép người dùng tìm kiếm phim, xem lịch chiếu, đặt vé, chọn ghế và thanh toán trực tuyến qua mã QR chuyển khoản ngân hàng. Ứng dụng còn tích hợp hệ thống quản trị (Admin Panel) đầy đủ cho phép quản lý phim, rạp chiếu, suất chiếu, đồ ăn, voucher và xem báo cáo doanh thu.

Dự án được xây dựng theo mô hình **Thin Client** — toàn bộ logic nghiệp vụ (tính giá, điểm tích lũy, hạng thành viên) được xử lý hoàn toàn ở phía Backend. Frontend chỉ thu thập lựa chọn của người dùng và hiển thị kết quả từ API.

---

## ✨ Tính năng chính

### 👤 Người dùng
- **Xác thực**: Đăng ký, đăng nhập bằng email/mật khẩu hoặc **Google Sign-In**
- **Quên mật khẩu**: Xác minh OTP qua email, đặt lại mật khẩu
- **Trang chủ**: Danh sách phim đang chiếu, phim sắp chiếu, khuyến mãi nổi bật
- **Chi tiết phim**: Thông tin đầy đủ (mô tả, đạo diễn, diễn viên, thể loại, trailer), lịch chiếu theo ngày
- **Danh sách rạp**: Xem tất cả rạp chiếu, lịch chiếu theo rạp
- **Đặt vé theo luồng**:
  1. Chọn suất chiếu → Chọn số lượng vé (thường / đôi)
  2. Chọn ghế trên sơ đồ phòng chiếu tương tác (REGULAR / VIP / COUPLE / AISLE)
  3. Thêm đồ ăn & thức uống (bắp rang, nước ngọt, combo...)
  4. Áp dụng voucher giảm giá
  5. Thanh toán bằng mã QR chuyển khoản ngân hàng
- **Lịch sử đặt vé**: Xem toàn bộ hóa đơn, chi tiết từng vé kèm mã QR
- **Hồ sơ cá nhân**: Điểm tích lũy, hạng thành viên (Bạc / Vàng / Kim cương), chỉnh sửa thông tin, đổi mật khẩu
- **Voucher**: Xem và áp dụng mã giảm giá cá nhân

### 🛠️ Quản trị viên (Admin)
- **Dashboard**: Tổng quan doanh thu, số vé bán, số đặt chỗ, phim đang hoạt động
- **Quản lý phim**: Thêm, sửa, xóa phim; upload poster lên Cloudinary
- **Quản lý rạp & phòng**: CRUD rạp chiếu và phòng chiếu
- **Quản lý suất chiếu**: Tạo, cập nhật, xóa suất chiếu (2D / 3D / IMAX)
- **Quản lý ghế**: Thiết lập sơ đồ ghế cho từng phòng
- **Quản lý đồ ăn**: CRUD menu đồ ăn & thức uống
- **Quản lý voucher**: Tạo và quản lý mã giảm giá (FIXED_AMOUNT / PERCENT)
- **Báo cáo doanh thu**: Theo ngày, theo phim, theo rạp
- **Cài đặt hệ thống**: Cấu hình hệ số giá (ngày thường / cuối tuần / lễ, buổi sáng / chiều / tối, định dạng 2D/3D/IMAX), quản lý ngày lễ

---

## 🏛️ Kiến trúc

Dự án tuân theo **MVVM + Clean Architecture** với triết lý **Thin Client (Untrusted Client)**:

```
┌─────────────────────────────────────────────────────┐
│                  Presentation Layer                  │
│   Jetpack Compose Screens  ←→  ViewModels (MVVM)    │
│         StateFlow / UiState / Events                 │
└──────────────────────┬──────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────┐
│                   Domain Layer                       │
│   Repository Interfaces  |  Domain Models            │
│   Use Cases (GetSeatsUseCase, GetConcessionsUseCase) │
└──────────────────────┬──────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────┐
│                    Data Layer                        │
│   RepositoryImpl  |  ApiService (Retrofit)           │
│   RetrofitClient  |  DTOs / Data Models              │
│   PreferenceManager (JWT Token)                      │
└─────────────────────────────────────────────────────┘
```

### Nguyên tắc cốt lõi
- **FE là Thin Client**: Không tự tính giá vé, phụ phí VIP/Đôi, điểm tích lũy hay hạng thành viên
- **BE là Source of Truth**: Mọi con số hiển thị đều bắt nguồn từ kết quả API trả về
- **Untrusted Input**: Backend luôn tính toán lại dựa trên ID gửi lên, không tin giá tiền từ client

---

## 📁 Cấu trúc thư mục

```
CAH_Cinema/
├── app/
│   ├── build.gradle.kts
│   └── src/main/
│       ├── AndroidManifest.xml
│       └── java/com/example/cah_cinema/
│           ├── CAHCinemaApplication.kt
│           ├── MainActivity.kt                  # NavHost, BottomBar, AdminSidebar
│           │
│           ├── data/
│           │   ├── model/                       # DTOs / Data Transfer Objects
│           │   │   ├── AdminModels.kt
│           │   │   ├── AuthModels.kt
│           │   │   ├── BaseResponse.kt
│           │   │   ├── BookingModels.kt
│           │   │   ├── CinemaModels.kt
│           │   │   ├── MovieModels.kt
│           │   │   ├── ProfileModels.kt
│           │   │   └── VoucherModels.kt
│           │   ├── remote/
│           │   │   ├── ApiService.kt            # Tất cả Retrofit endpoints
│           │   │   └── RetrofitClient.kt        # OkHttp + JWT interceptor
│           │   └── repository/
│           │       ├── AdminRepositoryImpl.kt
│           │       └── ConcessionRepositoryImpl.kt
│           │
│           ├── domain/
│           │   ├── model/                       # Domain models thuần Kotlin
│           │   │   ├── Concession.kt
│           │   │   ├── Movie.kt
│           │   │   ├── Promotion.kt
│           │   │   ├── Seat.kt
│           │   │   └── TicketType.kt
│           │   ├── repository/                  # Repository interfaces (contracts)
│           │   │   ├── AdminRepository.kt
│           │   │   ├── BookingRepository.kt
│           │   │   └── ConcessionRepository.kt
│           │   └── usecase/
│           │       ├── GetConcessionsUseCase.kt
│           │       └── GetSeatsUseCase.kt
│           │
│           ├── presentation/
│           │   ├── admin/
│           │   │   ├── cinema/                  # AdminCinemaManagementScreen + ViewModel
│           │   │   ├── components/              # AdminComponents, AdminSidebar
│           │   │   ├── dashboard/               # AdminDashboardScreen + ViewModel
│           │   │   ├── food/                    # AdminFoodManagementScreen + ViewModel
│           │   │   ├── movies/                  # AdminMovieManagementScreen + ViewModel
│           │   │   ├── report/                  # AdminReportScreen + ViewModel
│           │   │   ├── seats/                   # AdminSeatManagementScreen + ViewModel
│           │   │   ├── settings/                # AdminSettingsScreen + ViewModel
│           │   │   ├── showtime/                # AdminShowtimeScreen + ViewModel
│           │   │   └── voucher/                 # AdminVoucherScreen + ViewModel
│           │   ├── component/
│           │   │   ├── AuthComponents.kt
│           │   │   ├── LoadingComponent.kt
│           │   │   └── MovieComponents.kt
│           │   ├── main/
│           │   │   └── MainViewModel.kt         # Kiểm tra token, xác định startDestination
│           │   ├── navigation/
│           │   │   ├── BottomNavigationBar.kt
│           │   │   ├── NotificationScreen.kt
│           │   │   └── Screen.kt                # Sealed class định nghĩa tất cả routes
│           │   └── user/
│           │       ├── auth/
│           │       │   ├── ForgotPassword/      # ForgotPassword, OtpVerification, ResetPassword
│           │       │   ├── login/               # LoginScreen + LoginViewModel
│           │       │   └── register/            # RegisterScreen + RegisterViewModel
│           │       ├── booking/
│           │       │   ├── TicketSelectionScreen + ViewModel
│           │       │   ├── SeatSelectionScreen + ViewModel
│           │       │   ├── ConcessionScreen + ViewModel
│           │       │   ├── VoucherScreen + VoucherViewModel
│           │       │   ├── PaymentScreen + PaymentViewModel
│           │       │   └── PaymentLoadingScreen
│           │       ├── cinema/
│           │       │   ├── CinemaScreen + CinemaViewModel
│           │       │   └── CinemaDetailScreen + CinemaDetailViewModel
│           │       ├── detail/                  # MovieDetailScreen + MovieDetailViewModel
│           │       ├── home/                    # HomeScreen + HomeViewModel, UpcomingMoviesScreen
│           │       ├── profile/
│           │       │   ├── ProfileScreen + ProfileViewModel
│           │       │   ├── BookingHistoryScreen
│           │       │   ├── ChangePasswordScreen
│           │       │   ├── EditProfileScreen
│           │       │   └── TicketDetailScreen
│           │       ├── promotion/               # PromotionDetailScreen + ViewModels
│           │       └── splash/                  # SplashScreen
│           │
│           ├── ui/theme/
│           │   ├── Color.kt
│           │   ├── Theme.kt
│           │   └── Type.kt
│           └── util/
│               ├── CloudinaryUploader.kt
│               ├── DateTimeUtils.kt
│               ├── ImageUrls.kt
│               ├── PreferenceManager.kt
│               └── QrCodeGenerator.kt
│
├── gradle/
│   └── libs.versions.toml                       # Version catalog
├── build.gradle.kts
├── settings.gradle.kts
├── README.md
├── README_FRONTEND_TECHNICAL.md
└── FRONTEND_BACKEND_INTEGRATION.md
```

---

## 🗺️ Luồng điều hướng (Navigation Flow)

```
App Start
    │
    ▼
MainViewModel (kiểm tra JWT token)
    ├── Có token  ──► Home / AdminDashboard (theo role)
    └── Không có  ──► Login
                          │
              ┌───────────┼───────────┐
              ▼           ▼           ▼
           Register  ForgotPassword  Google
                          │
                    OtpVerification
                          │
                    ResetPassword ──► Login

Home (Bottom Nav)
    ├── Home ──► MovieDetail ──► TicketSelection
    │                                  │
    │                            SeatSelection
    │                                  │
    │                            Concession ──► Voucher (tùy chọn)
    │                                  │
    │                             Payment ──► PaymentLoading ──► TicketDetail
    │
    ├── Cinema ──► CinemaDetail ──► TicketSelection ──► (booking funnel)
    │
    ├── Notification ──► PromotionDetail
    │
    └── Profile ──► EditProfile / ChangePassword / BookingHistory ──► TicketDetail
                └── (Admin) ──► AdminDashboard (Sidebar Nav)
                                    ├── Movies
                                    ├── Cinemas ──► SeatManagement
                                    ├── Showtimes
                                    ├── Food
                                    ├── Vouchers
                                    ├── Reports
                                    └── Settings
```

---

## 📡 API & Kết nối Backend

**Base URL**: `http://<server-ip>:8080/`  
**Xác thực**: JWT Bearer Token — tự động đính kèm vào mọi request qua OkHttp Interceptor  
**Lưu trữ token**: `SharedPreferences` thông qua `PreferenceManager`

### Danh sách Endpoints chính

| Nhóm | Method | Endpoint | Mô tả |
|------|--------|----------|-------|
| **Auth** | POST | `/api/v1/auth/login` | Đăng nhập |
| | POST | `/api/v1/auth/register` | Đăng ký |
| | POST | `/api/v1/auth/google` | Đăng nhập Google |
| | POST | `/api/v1/auth/send-otp` | Gửi OTP |
| | POST | `/api/v1/auth/fp-verify-otp` | Xác minh OTP quên mật khẩu |
| | POST | `/api/v1/auth/fp-change-password` | Đặt lại mật khẩu |
| | POST | `/api/v1/auth/change-password` | Đổi mật khẩu |
| **Phim** | GET | `/api/v1/public/movies/featured` | Phim nổi bật (đang chiếu + sắp chiếu) |
| | GET | `/api/v1/public/movies` | Danh sách phim (phân trang, lọc) |
| | GET | `/api/v1/public/movies/{id}` | Chi tiết phim |
| | GET | `/api/v1/public/genres/all` | Tất cả thể loại |
| **Rạp & Suất chiếu** | GET | `/api/v1/public/cinemas` | Danh sách rạp |
| | GET | `/api/v1/public/showtimes/movies/{movieId}` | Lịch chiếu theo phim |
| | GET | `/api/v1/public/showtimes/cinemas/{cinemaId}` | Lịch chiếu theo rạp |
| **Ghế & Đặt vé** | GET | `/api/v1/public/seats?showtimeId=` | Sơ đồ ghế |
| | POST | `/api/v1/seats/pre-lock` | Khóa tạm ghế |
| | POST | `/api/v1/bookings` | Tạo đơn đặt vé |
| | POST | `/api/v1/bookings/{id}/confirm-payment` | Xác nhận thanh toán |
| **Người dùng** | GET | `/api/v1/users/me` | Hồ sơ + lịch sử đặt vé |
| | PATCH | `/api/v1/users/me` | Cập nhật hồ sơ |
| | GET | `/api/v1/user/vouchers` | Voucher của tôi |
| | GET | `/api/v1/user/food` | Danh sách đồ ăn |
| **Admin** | GET | `/api/v1/admin/reports/overview` | Tổng quan doanh thu |
| | CRUD | `/api/v1/admin/movies/...` | Quản lý phim |
| | CRUD | `/api/v1/admin/cinemas/...` | Quản lý rạp & phòng |
| | CRUD | `/api/v1/admin/showtime/...` | Quản lý suất chiếu |
| | CRUD | `/api/v1/admin/vouchers/...` | Quản lý voucher |
| | CRUD | `/api/v1/admin/food/...` | Quản lý đồ ăn |
| | CRUD | `/api/v1/admin/seats/...` | Quản lý ghế |
| | GET/POST | `/api/v1/admin/price-config/...` | Cấu hình giá |
| | CRUD | `/api/v1/admin/holiday/...` | Quản lý ngày lễ |

---

## 🎨 Giao diện & Theme

Ứng dụng sử dụng **dark theme** xuyên suốt với bảng màu đặc trưng:

| Tên màu | Hex | Dùng cho |
|---------|-----|----------|
| Background | `#13131A` | Nền toàn app |
| CyanBlue | `#00E5FF` | Màu nhấn chính, nút, icon active |
| SelectedRed | `#920707` | Ghế đã chọn |
| VipOrange | `#E38D62` | Ghế VIP |
| CouplePurple | `#CF10C5` | Ghế đôi |
| StandardGreen | `#0D970F` | Ghế thường còn trống |
| NotAvailableGray | `#6F7277` | Ghế không khả dụng |
| SelectingSeat | `#00E5FF` | Ghế đang chọn |
| AgeIconYellow | `#FFAA00` | Icon giới hạn độ tuổi |
| TextGray | `#9E9EA7` | Văn bản phụ |

**Hỗ trợ Dynamic Color** (Android 12+): Tự động lấy màu từ hình nền thiết bị.

---

## 📦 Thư viện & Công nghệ

| Thư viện | Phiên bản | Mục đích |
|----------|-----------|----------|
| **Kotlin** | 2.0.21 | Ngôn ngữ lập trình |
| **Jetpack Compose BOM** | 2024.12.01 | UI framework (Material3) |
| **Navigation Compose** | 2.8.5 | Điều hướng trong app |
| **Lifecycle Runtime KTX** | 2.8.7 | ViewModel, StateFlow, Coroutines |
| **Retrofit** | 2.11.0 | HTTP client, gọi REST API |
| **OkHttp** | 4.12.0 | HTTP layer + Logging Interceptor |
| **Gson** | 2.11.0 | Serialize/Deserialize JSON |
| **Coil Compose** | 2.7.0 | Tải và hiển thị ảnh bất đồng bộ |
| **ZXing Core** | 3.5.3 | Tạo mã QR thanh toán |
| **Core Splashscreen** | 1.2.0 | Splash screen API (Android 12+) |
| **Credentials** | 1.3.0 | Google Sign-In (Credential Manager) |
| **GoogleId** | 1.1.1 | Google Identity library |
| **AppCompat** | 1.7.0 | Tương thích ngược |

---

## ⚙️ Cài đặt & Chạy dự án

### Yêu cầu
- **Android Studio** Hedgehog (2023.1.1) trở lên
- **JDK** 11+
- **Android SDK** API 24 – 36
- **Gradle** 8.x

### Các bước

**1. Clone repository**
```bash
git clone <repository-url>
cd CAH_Cinema
```

**2. Cấu hình `local.properties`**

Tạo hoặc chỉnh sửa file `local.properties` ở thư mục gốc:
```properties
# Cloudinary (upload ảnh cho Admin)
CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_UPLOAD_PRESET=your_upload_preset

# Google Sign-In
GOOGLE_WEB_CLIENT_ID=your_google_web_client_id
```

**3. Cấu hình Backend URL**

Mở `app/src/main/java/com/example/cah_cinema/data/remote/RetrofitClient.kt` và cập nhật:
```kotlin
private const val BASE_URL = "http://<your-backend-ip>:8080/"
```

**4. Build & Run**
```bash
./gradlew assembleDebug
```
Hoặc nhấn **Run ▶** trong Android Studio.

---

## 🔐 Bảo mật & Quyền truy cập

```xml
<!-- AndroidManifest.xml -->
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"
    android:maxSdkVersion="32" />
```

- JWT token được lưu trong `SharedPreferences` và tự động đính kèm vào header `Authorization: Bearer <token>`
- `network_security_config.xml` cho phép cleartext traffic đến IP backend trong môi trường phát triển
- Phân quyền theo role: `ROLE_USER` → giao diện người dùng, `ROLE_ADMIN` → Admin Panel

---

## 🗄️ Mô hình dữ liệu chính

### Luồng đặt vé
```
User chọn Showtime
    → Gửi SeatIds lên BE → BE trả về totalPrice
    → Gửi FoodItems lên BE → BE tính foodSubtotal
    → Áp dụng VoucherId → BE tính discountAmount
    → POST /bookings → BE tạo BookingData { bookingId, totalAmount, status }
    → Hiển thị QR (STK + nội dung chuyển khoản từ BE)
    → POST /confirm-payment → BE xác nhận → Booking PAID
```

### Loại ghế
| Loại | Màu hiển thị | Ghi chú |
|------|-------------|---------|
| REGULAR | Xanh lá | Ghế thường |
| VIP | Cam | Phụ phí theo hệ số BE |
| COUPLE | Tím | Ghế đôi, chọn theo cặp |
| AISLE | — | Lối đi, không thể chọn |

### Hạng thành viên (do BE tính toán)
| Hạng | Điều kiện |
|------|-----------|
| Thành viên | Mặc định |
| Hạng bạc | Theo quy tắc BE |
| Hạng vàng | Theo quy tắc BE |
| Kim cương | Theo quy tắc BE |

---

## 📱 Màn hình ứng dụng

### Người dùng
| Màn hình | Mô tả |
|----------|-------|
| Splash | Kiểm tra token, chuyển hướng tự động |
| Login | Đăng nhập email + Google |
| Register | Tạo tài khoản mới |
| Forgot Password | Gửi OTP → Xác minh → Đặt lại mật khẩu |
| Home | Phim nổi bật, sắp chiếu, khuyến mãi |
| Movie Detail | Thông tin phim + lịch chiếu theo ngày |
| Cinema | Danh sách rạp chiếu |
| Cinema Detail | Thông tin rạp + lịch chiếu |
| Ticket Selection | Chọn số lượng vé thường / đôi |
| Seat Selection | Sơ đồ ghế tương tác |
| Concession | Chọn đồ ăn & thức uống |
| Voucher | Áp dụng mã giảm giá |
| Payment | Thanh toán QR chuyển khoản |
| Payment Loading | Chờ xác nhận thanh toán |
| Ticket Detail | Chi tiết vé + mã QR |
| Booking History | Lịch sử tất cả đơn đặt |
| Profile | Thông tin cá nhân, điểm, hạng |
| Edit Profile | Chỉnh sửa tên, SĐT, avatar |
| Change Password | Đổi mật khẩu |
| Notification | Danh sách khuyến mãi |
| Promotion Detail | Chi tiết chương trình khuyến mãi |

### Quản trị viên
| Màn hình | Mô tả |
|----------|-------|
| Admin Dashboard | Tổng quan hệ thống |
| Movie Management | CRUD phim + upload poster |
| Cinema Management | CRUD rạp & phòng chiếu |
| Showtime Management | CRUD suất chiếu |
| Seat Management | Thiết lập sơ đồ ghế |
| Food Management | CRUD menu đồ ăn |
| Voucher Management | CRUD mã giảm giá |
| Report | Báo cáo doanh thu |
| Settings | Cấu hình giá & ngày lễ |

---

## 🔗 Liên kết liên quan

- **Backend Repository**: [Backend_CAH_Cinema](../Backend_CAH_Cinema)
- **Tài liệu kỹ thuật Frontend**: [README_FRONTEND_TECHNICAL.md](README_FRONTEND_TECHNICAL.md)
- **Hướng dẫn tích hợp FE-BE**: [FRONTEND_BACKEND_INTEGRATION.md](FRONTEND_BACKEND_INTEGRATION.md)

---

## 👥 Tác giả

| Họ và tên | Vai trò |
|-----------|---------|
| **Lê Văn An** | Thành viên nhóm |
| **Nguyễn Cao Cường** | Thành viên nhóm |
| **Mai Minh Hoàng** | Thành viên nhóm |

---

<p align="center">
  Được xây dựng với ❤️ bằng Kotlin & Jetpack Compose
</p>
