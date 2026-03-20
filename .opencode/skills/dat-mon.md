# Skill: Dat Mon

## Muc dich

Dung khi yeu cau lien quan den xem menu, them vao gio, cap nhat gio, dat hang, lich su don hang, huy don, hoac tinh tong tien.

## Pham vi nghiep vu

- Khach hang xem mon
- Them mon vao gio
- Tang/giam/xoa mon trong gio
- Dat hang tao don moi
- Xem danh sach don da tao
- Huy don neu du dieu kien

## File thuong lien quan

- `app/src/main/java/com/example/quanlynhahang/MenuFragment.java`
- `app/src/main/java/com/example/quanlynhahang/CartActivity.java`
- `app/src/main/java/com/example/quanlynhahang/OrderFragment.java`
- `app/src/main/java/com/example/quanlynhahang/data/CartManager.java`
- `app/src/main/java/com/example/quanlynhahang/data/DatabaseHelper.java`
- Cac adapter hien thi mon va don hang

## Quy tac nghiep vu bat buoc

- Gio hang rong thi khong duoc dat.
- Dat hang phai co nguoi dung dang nhap hop le.
- Tong tien phai tinh tu danh sach mon thuc te trong gio.
- Don moi phai bat dau o trang thai `PENDING_CONFIRMATION` neu chua co yeu cau khac.
- Chi tiet don phai giu du lieu can thiet de hien thi ve sau, ke ca khi mon an trong danh muc thay doi.
- Huy don phai ton trong quy tac trang thai dang co trong DB.

## Cach lam viec de xuat

1. Xac dinh thay doi nam o UI, Data-Business hay ca hai.
2. Doc `flows/customer-order-flow.md` truoc khi sua.
3. Neu doi cach dat hang, kiem tra ca login resume flow.
4. Neu doi tong tien hoac chi tiet don, kiem tra toan bo noi doc/ghi don hang.
5. Neu doi trang thai don, cap nhat dong bo khach hang va nhan vien.

## Danh sach canh giac

- Mat luong login xong quay lai dat mon
- Tong tien lech do parse chuoi gia
- Xoa gio hang sai thoi diem
- Huy don o trang thai khong hop le
- Lech du lieu lich su don khi mon goc da sua hoac tat phuc vu

## Verify toi thieu

- Them mon vao gio va thay tong tien doi dung
- Dat hang thanh cong tao duoc don moi
- Khach chua dang nhap van duoc dua den login dung cach
- Dang nhap xong tiep tuc dat mon duoc
- Lich su don hien thi dung va thao tac huy don dung quy tac
