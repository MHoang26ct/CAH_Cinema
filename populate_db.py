import requests
import json
from datetime import datetime, timedelta

BASE_URL = "http://100.89.144.114:8080/api/v1"
ADMIN_EMAIL = "admin@cah.com"
ADMIN_PASS = "admin123"

def populate():
    print("🚀 Bắt đầu quá trình nạp dữ liệu mẫu qua API...")

    # 1. Đăng nhập lấy Token
    login_url = f"{BASE_URL}/auth/login"
    login_data = {"email": ADMIN_EMAIL, "password": ADMIN_PASS}

    try:
        response = requests.post(login_url, json=login_data)
        if response.status_code != 200:
            print(f"❌ Đăng nhập thất bại: {response.text}")
            return

        token = response.json().get("data", {}).get("accessToken")
        if not token:
            print("❌ Không tìm thấy token trong phản hồi.")
            return

        print("✅ Đăng nhập Admin thành công.")
        headers = {"Authorization": f"{token}"} # Backend configuration: no Bearer prefix

        # 2. Tạo Rạp chiếu
        print("\n--- Tạo Rạp & Phòng ---")
        cinemas = [
            {"name": "Cinestar Quốc Thanh", "address": "271 Nguyễn Trãi, Q.1, TP.HCM", "hotline": "028 7300 8881"},
            {"name": "Cinestar Hai Bà Trưng", "address": "233 Hai Bà Trưng, Q.3, TP.HCM", "hotline": "028 7300 7279"},
            {"name": "Cinestar Sinh Viên", "address": "Nhà văn hóa Sinh viên ĐHQG, Thủ Đức", "hotline": "028 7300 1122"}
        ]

        cinema_ids = []
        for c in cinemas:
            res = requests.post(f"{BASE_URL}/admin/cinemas", json=c, headers=headers)
            if res.status_code in [200, 201]:
                c_id = res.json()["data"]["cinemaId"]
                cinema_ids.append(c_id)
                print(f"✅ Đã tạo rạp: {c['name']} (ID: {c_id})")

                # Tạo 2 phòng cho mỗi rạp
                for i in range(1, 3):
                    room_data = {"cinemaId": c_id, "roomName": f"Phòng {i:02d}"}
                    requests.post(f"{BASE_URL}/admin/cinemas/{c_id}/rooms", json=room_data, headers=headers)
            else:
                print(f"⚠️ Rạp {c['name']} có thể đã tồn tại hoặc lỗi: {res.status_code}")

        # 3. Tạo Phim (Yêu cầu có Thể loại - Giả định ID 1, 2, 3 tồn tại)
        print("\n--- Tạo Phim ---")
        movies = [
            {
                "title": "HẸN EM NGÀY NHẬT THỰC",
                "description": "Phim tình cảm lãng mạn.",
                "duration": 118,
                "releaseDate": "2026-05-16",
                "ageRating": "T16",
                "posterUrl": "https://api.chieu.online/images/poster1.jpg",
                "trailerUrl": "https://youtube.com/watch?v=1",
                "directorName": "Đạo diễn A",
                "actorList": "Diễn viên X",
                "genreIdList": [1, 4]
            },
            {
                "title": "KUNG FU PANDA 4",
                "description": "Gấu Po trở lại.",
                "duration": 94,
                "releaseDate": "2026-03-08",
                "ageRating": "P",
                "posterUrl": "https://api.chieu.online/images/poster2.jpg",
                "trailerUrl": "https://youtube.com/watch?v=2",
                "directorName": "Đạo diễn B",
                "actorList": "Jack Black",
                "genreIdList": [3, 5]
            }
        ]

        movie_ids = []
        for m in movies:
            res = requests.post(f"{BASE_URL}/admin/movies/create", json=m, headers=headers)
            if res.status_code in [200, 201]:
                m_id = res.json()["data"]["movieId"]
                movie_ids.append(m_id)
                print(f"✅ Đã tạo phim: {m['title']} (ID: {m_id})")
            else:
                print(f"⚠️ Phim {m['title']} lỗi: {res.status_code}")

        # 4. Tạo Voucher
        print("\n--- Tạo Voucher ---")
        vouchers = [
            {
                "code": "WELCOME10",
                "type": "FIXED_AMOUNT",
                "value": 10000.0,
                "quantity": 1000,
                "startAt": "2025-01-01T00:00:00",
                "expiredAt": "2026-12-31T23:59:59"
            },
            {
                "code": "CINE50",
                "type": "PERCENT",
                "value": 50.0,
                "quantity": 100,
                "startAt": "2026-01-01T00:00:00",
                "expiredAt": "2026-12-31T23:59:59"
            }
        ]
        for v in vouchers:
            requests.post(f"{BASE_URL}/admin/vouchers/create", json=v, headers=headers)
            print(f"✅ Đã tạo voucher: {v['code']}")

        print("\n✨ Hoàn tất nạp dữ liệu! Bạn có thể mở App để kiểm tra.")

    except Exception as e:
        print(f"💥 Lỗi nghiêm trọng trong quá trình chạy script: {str(e)}")

if __name__ == "__main__":
    populate()
