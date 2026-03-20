# Tong Quan Du An

## San pham

`quanlynhahang` la ung dung Android quan ly nha hang, hien dang gom 3 nhom nguoi dung:

- Khach hang (`KHACH_HANG`)
- Nhan vien (`NHAN_VIEN`)
- Admin (`ADMIN`)

## Chuc nang chinh

- Dang ky va dang nhap tai khoan
- Xem menu mon an
- Them mon vao gio hang
- Dat mon tao don hang
- Xem lich su don hang
- Dat ban
- Gui yeu cau phuc vu
- Nhan vien xu ly don hang, dat ban, yeu cau phuc vu
- Admin quan ly mon an, tai khoan va thong ke tong quan

## Nen ky thuat hien tai

- Mot module Android duy nhat la `app`
- Java cho code Activity, Fragment, Adapter, Model
- SQLite local duoc quan ly trong `DatabaseHelper`
- Session dang nhap luu trong `SessionManager`
- Chua thay lop backend server rieng trong repo

## Cach hieu dung repo nay

- Day khong phai mo hinh tach rieng `mobile app + API server`.
- Phan giao dien nam trong Activity, Fragment, Adapter, XML.
- Phan nghiep vu va du lieu local nam trong `DatabaseHelper`, `SessionManager`, model va logic xu ly trang thai.
- Khi sua code, can tu hoi minh dang sua `UI` hay `Data-Business` hay ca hai.

## Doi tuong du lieu chinh

- Nguoi dung
- Mon an
- Don hang
- Chi tiet don hang
- Dat ban
- Yeu cau phuc vu

## Nguyen tac lam viec cho OpenCode

Khi nhan mot yeu cau, hay map no vao mot trong cac nhom sau truoc khi sua:

1. Dang nhap, dang ky, session, phan quyen
2. Luong khach hang dat mon
3. Luong dat ban va yeu cau phuc vu
4. Luong nhan vien xu ly cong viec
5. Luong admin quan ly he thong
6. Thay doi schema, du lieu, tinh toan nghiep vu

Sau do doc file trong `flows/` va `rules/` tuong ung roi moi implement.
