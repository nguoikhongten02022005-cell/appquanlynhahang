# Ke Hoach Viet Hoa Enum Va Schema

## Muc tieu

Tai lieu nay mo ta cach Viet hoa enum trang thai va schema DB ma khong lam hong du lieu local dang co.

## Danh gia rui ro

- Enum trang thai dang duoc luu truc tiep vao DB bang `name()`.
- Gia tri nhu `PENDING_CONFIRMATION`, `CONFIRMED`, `PROCESSING` xuat hien trong:
  - model
  - adapter
  - activity/fragment
  - `DatabaseHelper`
  - default schema values
  - logic parse va transition
- Ten bang/cot SQL dang la hop dong du lieu thuc su. Doi ten se can migration va copy/rename du lieu.

## Thu tu de xuat

### Giai doan 1 - Alias enum an toan

- Giu nguyen enum cu.
- Them helper tieng Viet cho moi trang thai, vi du:
  - `laChoXacNhan()`
  - `laDaXacNhan()`
  - `laDangXuLy()`
- Chuyen code UI va nghiep vu sang doc helper tieng Viet truoc.

### Giai doan 2 - Tach gia tri luu tru khoi ten enum

- Khong dung `name()` truc tiep nua.
- Them truong ma luu tru on dinh cho enum, vi du:
  - `maDb = "PENDING_CONFIRMATION"`
- Them ham parse tu ma DB.
- Toan bo doc/ghi DB phai di qua ham chuyen doi nay.

### Giai doan 3 - Neu muon Viet hoa ten enum trong code

- Chi thuc hien sau khi Giai doan 2 hoan tat.
- Khi do co the doi ten enum trong code ma khong doi gia tri dang luu trong DB.
- Vi du co the doi:
  - `PENDING_CONFIRMATION` -> `CHO_XAC_NHAN`
  - `CONFIRMED` -> `DA_XAC_NHAN`
  - `COMPLETED` -> `HOAN_TAT`
  - `CANCELED` -> `DA_HUY`
  - `PROCESSING` -> `DANG_XU_LY`
  - `DONE` -> `DA_XONG`

### Giai doan 4 - Schema DB

- Chi lam neu that su can thiet.
- Khong nen doi ten bang/cot truc tiep neu app da co nguoi dung that.
- Neu bat buoc doi:
  1. Tao bang moi voi ten cot moi
  2. Copy du lieu tu bang cu sang bang moi
  3. Kiem tra mapping day du
  4. Chi xoa bang cu sau khi xac nhan migration thanh cong

## Cac bang va cot rui ro cao

- Bang:
  - `users`
  - `dishes`
  - `orders`
  - `order_items`
  - `reservations`
  - `service_requests`
- Cot:
  - `status`
  - `user_id`
  - `dish_name`
  - `dish_price`
  - `table_number`
  - `guest_count`
  - `sent_time`

## Khuyen nghi hien tai

- Nen dung o muc alias/helper tieng Viet va giu schema hien tai.
- Neu muon thu Viet hoa sau hon, nen bat dau tu enum truoc, schema de cuoi.
