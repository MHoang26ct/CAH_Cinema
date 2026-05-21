#!/bin/bash

BASE_URL="http://100.89.144.114:8080/api/v1"
ADMIN_EMAIL="admin@cah.com"
ADMIN_PASS="admin123"

echo "🚀 Bắt đầu quá trình nạp dữ liệu mẫu qua API..."

# 1. Đăng nhập lấy Token
LOGIN_RES=$(curl -s -X POST "$BASE_URL/auth/login" \
     -H "Content-Type: application/json" \
     -d "{\"email\":\"$ADMIN_EMAIL\", \"password\":\"$ADMIN_PASS\"}")

TOKEN=$(echo $LOGIN_RES | grep -o '"accessToken":"[^"]*' | cut -d'"' -f4)

if [ -z "$TOKEN" ]; then
    echo "❌ Đăng nhập thất bại. Phản hồi: $LOGIN_RES"
    exit 1
fi

echo "✅ Đăng nhập Admin thành công."

# 2. Tạo Rạp chiếu
echo -e "\n--- Tạo Rạp ---"
declare -a CINEMA_NAMES=("Cinestar Quốc Thanh" "Cinestar Hai Bà Trưng" "Cinestar Sinh Viên")
declare -a CINEMA_ADDRS=("271 Nguyễn Trãi, Q.1, TP.HCM" "233 Hai Bà Trưng, Q.3, TP.HCM" "Nhà văn hóa Sinh viên ĐHQG, Thủ Đức")

for i in "${!CINEMA_NAMES[@]}"; do
    NAME="${CINEMA_NAMES[$i]}"
    ADDR="${CINEMA_ADDRS[$i]}"

    res=$(curl -s -X POST "$BASE_URL/admin/cinemas" \
         -H "Authorization: $TOKEN" \
         -H "Content-Type: application/json" \
         -d "{\"name\":\"$NAME\", \"address\":\"$ADDR\", \"hotline\":\"028 7300\"}")

    CID=$(echo $res | grep -o '"cinemaId":[0-9]*' | cut -d':' -f2)
    if [ ! -z "$CID" ]; then
        echo "✅ Đã tạo rạp: $NAME (ID: $CID)"
        # Tạo phòng
        curl -s -X POST "$BASE_URL/admin/cinemas/$CID/rooms" \
             -H "Authorization: $TOKEN" \
             -H "Content-Type: application/json" \
             -d "{\"cinemaId\":$CID, \"roomName\":\"Phòng 01\"}" > /dev/null
        echo "   - Đã tạo Phòng 01"
    else
        echo "⚠️ Rạp $NAME có thể đã tồn tại."
    fi
done

# 3. Tạo Phim
echo -e "\n--- Tạo Phim ---"
curl -s -X POST "$BASE_URL/admin/movies/create" \
     -H "Authorization: $TOKEN" \
     -H "Content-Type: application/json" \
     -d '{"title":"HẸN EM NGÀY NHẬT THỰC","duration":118,"releaseDate":"2026-05-16","ageRating":"T16","posterUrl":"https://api.chieu.online/images/poster1.jpg","genreIdList":[1,4]}' > /dev/null
echo "✅ Đã tạo phim: HẸN EM NGÀY NHẬT THỰC"

curl -s -X POST "$BASE_URL/admin/movies/create" \
     -H "Authorization: $TOKEN" \
     -H "Content-Type: application/json" \
     -d '{"title":"KUNG FU PANDA 4","duration":94,"releaseDate":"2026-03-08","ageRating":"P","posterUrl":"https://api.chieu.online/images/poster2.jpg","genreIdList":[3,5]}' > /dev/null
echo "✅ Đã tạo phim: KUNG FU PANDA 4"

# 4. Tạo Voucher
echo -e "\n--- Tạo Voucher ---"
curl -s -X POST "$BASE_URL/admin/vouchers/create" \
     -H "Authorization: $TOKEN" \
     -H "Content-Type: application/json" \
     -d '{"code":"WELCOME10","type":"FIXED_AMOUNT","value":10000,"quantity":1000,"startAt":"2025-01-01T00:00:00","expiredAt":"2026-12-31T23:59:59"}' > /dev/null
echo "✅ Đã tạo voucher: WELCOME10"

echo -e "\n✨ Hoàn tất nạp dữ liệu! Bạn có thể mở App để kiểm tra."
