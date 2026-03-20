# Luong Dat Ban Va Yeu Cau Phuc Vu

## Muc tieu

Cho phep khach tao dat ban va gui yeu cau phuc vu de nhan vien xu ly.

## Luong dat ban

1. Khach mo khu requests.
2. Khach chon ngay va gio.
3. Khach chon khu vuc mong muon.
4. Khach nhap so khach va ghi chu neu can.
5. App validate thoi gian va so luong khach.
6. Dat ban duoc tao voi trang thai `PENDING_APPROVAL`.
7. Khach co the xem lich su va huy neu duoc phep.
8. Nhan vien se xac nhan, hoan tat hoac huy sau do.

## Luong yeu cau phuc vu

1. Khach mo khu requests.
2. Khach gui noi dung yeu cau.
3. Yeu cau duoc luu voi trang thai `PROCESSING`.
4. Nhan vien xem danh sach yeu cau dang mo.
5. Nhan vien danh dau `DONE` sau khi xu ly xong.

## Quy tac nghiep vu

- Thoi gian dat ban khong duoc nam trong qua khu.
- So khach phai nam trong gioi han app dang dinh nghia.
- UI va DB phai thong nhat voi nhau ve cac trang thai hop le.
- Noi dung yeu cau phuc vu khong duoc rong.

## File lien quan

- `app/src/main/java/com/example/quanlynhahang/RequestsFragment.java`
- `app/src/main/java/com/example/quanlynhahang/EmployeeActivity.java`
- `app/src/main/java/com/example/quanlynhahang/data/DatabaseHelper.java`

## Huong dan khi sua

- Giu validation dat ban dong bo giua UI va tang du lieu.
- Neu khu vuc dat ban tro thanh du lieu dong, can go hardcode mot cach can than.
- Neu them thong bao sau nay, hay kich hoat tai diem chuyen trang thai.
