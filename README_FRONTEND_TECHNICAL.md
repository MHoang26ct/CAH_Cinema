# 📱 CAH Cinema - Tài liệu Kỹ thuật dành cho Backend Integration (Refactored for Thin Client)

Tài liệu này mô tả chi tiết cấu trúc Frontend và các điểm cần tích hợp API. Frontend tuân thủ triết lý **Thin Client**: Chỉ hiển thị dữ liệu và gửi yêu cầu, không chứa quy tắc nghiệp vụ (Business Rules).

---

## 🏛️ 1. Triết lý Kiến trúc (Architectural Philosophy)

Dự án tuân thủ triết lý **Untrusted Client**:
1.  **Frontend (FE)**:
    *   **Nhận thông tin**: Chỉ nhận dữ liệu đã được tính toán sẵn từ Backend để hiển thị.
    *   **Gửi thông tin**: Thu thập lựa chọn từ người dùng, validate cơ bản (không để trống, đúng định dạng) và gửi ID lên Backend.
    *   **Không tính toán**: FE tuyệt đối không tự tính giá tiền, không tính điểm tích lũy, không tự xác định hạng thành viên.
2.  **Backend (BE)**:
    *   **Source of Truth**: Là nguồn duy nhất chứa quy tắc nghiệp vụ.
    *   **Tính toán**: Thực hiện mọi phép tính (Tổng tiền, phụ phí VIP, khuyến mãi, điểm thưởng).
    *   **Re-validation**: Luôn validate lại toàn bộ dữ liệu nhận được từ FE (không tin tưởng giá tiền hay số lượng FE gửi lên).

---

## 🚀 2. Danh mục API & Phân chia Nhiệm vụ

### 🎬 Movies & Cinema
*   `GET /api/movies`: Trả về danh sách phim. BE quyết định phim nào là `isFeatured` hay `isUpcoming`.
*   `GET /api/movies/{id}`: Trả về chi tiết phim. Nếu phim sắp chiếu, BE trả về list lịch chiếu rỗng.

### 🎟️ Booking & Payment (QR Transfer)
*   **Chọn ghế**: FE gửi danh sách Seat IDs. BE trả về `extraPrice` và `totalPrice`. FE không tự cộng 30k/50k cho ghế VIP/Đôi.
*   **Bắp nước**: FE gửi danh sách Item IDs + Quantity. BE trả về tổng tiền.
*   **Thanh toán QR**: FE hiển thị mã QR dựa trên chuỗi thông tin BE trả về (STK, Tên TK, Nội dung chuyển khoản duy nhất).

### 👤 User & Profile
*   **Điểm tích lũy**: BE lưu trữ `total_spent` và trả về `loyalty_points` dựa trên quy tắc (hiện tại là 40k = 1đ). Nếu BE đổi quy tắc thành 50k = 1đ, FE không cần thay đổi code.
*   **Hạng thành viên**: Trả về trực tiếp chuỗi "Hạng vàng", "Hạng bạc" từ API.

---

## 🛠️ 3. Cấu trúc Layer hiện tại

*   **`domain/`**: Định nghĩa Model và Interface. Đây là "Hợp đồng" giữa FE và BE.
*   **`data/`**: Chứa `RepositoryImpl`. Backend sẽ triển khai gọi API thực tế tại đây.
*   **`presentation/`**: Giao diện người dùng. Đã được "làm mỏng" tối đa, chỉ quan sát `State` và gọi `Event`.

---
**Frontend đã sẵn sàng!** Mọi logic tính toán đã được loại bỏ để nhường chỗ cho Backend xử lý.
