# Luong Khach Hang Dat Mon

## Muc tieu

Cho phep khach hang xem mon, them vao gio, dat mon va xem lai lich su don mot cach an toan.

## Luong hien tai

1. Khach hang xem menu trong vo `MainActivity`.
2. Khach them mon vao gio qua `CartManager`.
3. Man gio hang hien danh sach mon, tong tien va nut dat hang.
4. Neu chua dang nhap, luc dat hang se mo login va quay lai sau khi dang nhap thanh cong.
5. Dat hang tao ma don, thoi gian, tong tien va danh sach mon.
6. Don moi duoc luu voi trang thai `PENDING_CONFIRMATION`.
7. Gio hang duoc xoa sau khi dat thanh cong.
8. Khach xem lich su don trong `OrderFragment`.
9. Khach co the huy don neu tang du lieu cho phep.

## Quy tac nghiep vu

- Gio hang rong khong duoc dat.
- Dat hang bat buoc co nguoi dung dang nhap hop le.
- Tong tien phai tinh tu du lieu gio hang thuc te.
- Chi tiet don phai luu du thong tin can de hien thi ve sau.
- Luong huy don phai ton trong gioi han trang thai o tang du lieu.

## File lien quan

- `app/src/main/java/com/example/quanlynhahang/CartActivity.java`
- `app/src/main/java/com/example/quanlynhahang/OrderFragment.java`
- `app/src/main/java/com/example/quanlynhahang/data/CartManager.java`
- `app/src/main/java/com/example/quanlynhahang/data/DatabaseHelper.java`

## Huong dan khi sua

- Phai giu dung hanh vi login xong quay lai tiep tuc dat hang.
- Khong hardcode tong tien ben ngoai luong tinh gio hang/don hang.
- Neu doi quy tac trang thai don, phai doi dong thoi phan khach hang va nhan vien.
- Neu them thanh toan sau nay, phai giu trai nghiem tao don la mot luong tron ven.

## Trang thai chinh

- `PENDING_CONFIRMATION`
- `CONFIRMED`
- `COMPLETED`
- `CANCELED`
