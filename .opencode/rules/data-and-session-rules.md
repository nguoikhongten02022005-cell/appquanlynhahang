# Quy Tac Du Lieu Va Session

## An toan co so du lieu

- `DatabaseHelper` la nguon su that cho du lieu local.
- Moi thay doi schema phai an toan voi may da cai phien ban cu.
- Cot moi nen co default an toan neu co the.
- Seed data phai idempotent, chay nhieu lan van on.
- Cac du lieu lich su nhu don hang phai van doc duoc sau khi doi danh muc mon.

## An toan session

- `SessionManager` la noi duy nhat so huu trang thai dang nhap.
- Khong rai auth key ra nhieu class khong lien quan.
- Phai giu nhat quan giua `current_user_id` va `current_user_role`.
- Neu session khong hop le, hay xoa session thay vi sua doan.

## An toan phan quyen

- Man hinh dac quyen moi phai kiem tra role khi vao.
- Moi luong dieu huong theo role phai ro rang va xac dinh.
- Khong duoc mac dinh nguoi dang nhap san la khach hang neu ho la nhan vien/admin.

## He qua cua mo hinh local-first

- Du an hien tai dang nghieng ve local-first/offline.
- Chua thay lop dong bo voi server trong repo.
- Khi doi du lieu, hay coi DB tren may la nguon su that hien tai.
