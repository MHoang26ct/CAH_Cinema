# 🎞️ Tài liệu Hướng dẫn Tích hợp Frontend - Backend (Thin Client Model)

Tài liệu này nhấn mạnh vào việc chuyển giao toàn bộ logic nghiệp vụ về phía Server. Frontend chỉ đóng vai trò hiển thị và thu thập thông tin.

---

## 🏗️ 1. Nguyên tắc cốt lõi (Core Principles)

1.  **FE là "Thin Client"**: Tuyệt đối không chứa công thức tính toán giá tiền, điểm thưởng hay logic hạng thành viên.
2.  **BE là "Source of Truth"**: Mọi con số hiển thị trên App phải bắt nguồn từ kết quả trả về của API.
3.  **Untrusted Input**: Backend phải tính toán lại mọi thứ dựa trên ID (Seat ID, Product ID) gửi từ Client, không tin cậy vào giá tiền Client gửi lên.

---

## 🚀 2. Chi tiết các màn hình (Refactored)

### A. Màn hình Profile
- **FE**: Chỉ hiển thị chuỗi `rank` và con số `loyaltyPoints` nhận từ API.
- **BE**: Chịu trách nhiệm thực hiện phép tính `total_spent / 40000` và xác định hạng thành viên (Bạc/Vàng/Kim cương).

### B. Màn hình Chọn ghế (Seat Selection)
- **FE**: Người dùng click chọn ghế -> FE lưu danh sách ID ghế -> Gửi lên BE.
- **BE**: Kiểm tra danh sách ghế, tính phụ phí VIP/Đôi và trả về `total_price` cuối cùng. FE không tự cộng 30k hay 50k vào giá vé.

### C. Màn hình Thanh toán (QR Payment)
- **FE**: Hiển thị thông tin chuyển khoản (STK, Nội dung, Số tiền) do BE trả về.
- **BE**: Cấp phát `booking_id` duy nhất và quản lý trạng thái thanh toán.

---

## 📡 3. Quy trình tích hợp API

1.  Backend tạo các Endpoint trả về JSON theo đúng các Domain Model tại `domain/model`.
2.  Backend triển khai lớp `data/repository` để thực thi việc gọi API.
3.  Khi có thay đổi về quy tắc (ví dụ: tăng giá vé VIP), Backend chỉ cần cập nhật logic trên Server, App sẽ tự động hiển thị giá mới mà không cần cập nhật code Android.

---
**Frontend Team** đã hoàn thành việc refactor để loại bỏ mọi logic nghiệp vụ. Sẵn sàng cho việc tích hợp API!
