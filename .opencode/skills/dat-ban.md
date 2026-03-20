# Skill: Dat Ban

## Muc dich

Dung khi yeu cau lien quan den dat ban, lich hen, khu vuc ban, so khach, ghi chu, yeu cau phuc vu va lich su xu ly cac yeu cau nay.

## Pham vi nghiep vu

- Tao dat ban
- Validate ngay gio dat ban
- Validate so khach
- Chon khu vuc
- Huy dat ban
- Tao yeu cau phuc vu
- Xem lich su dat ban va yeu cau

## File thuong lien quan

- `app/src/main/java/com/example/quanlynhahang/RequestsFragment.java`
- `app/src/main/java/com/example/quanlynhahang/EmployeeActivity.java`
- `app/src/main/java/com/example/quanlynhahang/data/DatabaseHelper.java`
- Adapter cua reservation va service request

## Quy tac nghiep vu bat buoc

- Thoi gian dat ban khong duoc nam trong qua khu.
- So khach phai nam trong gioi han he thong.
- Dat ban moi bat dau o `PENDING_APPROVAL`.
- Yeu cau phuc vu moi bat dau o `PROCESSING`.
- Noi dung yeu cau phuc vu khong duoc rong.
- Huy dat ban phai ton trong quy tac trang thai dang co.

## Cach lam viec de xuat

1. Doc `flows/reservation-and-service-flow.md` truoc khi sua.
2. Xac dinh thay doi la o dat ban, yeu cau phuc vu hay ca hai.
3. Kiem tra validation UI va validation tang du lieu co con dong bo khong.
4. Neu doi danh sach khu vuc, kiem tra nhung noi dang hardcode.
5. Neu doi trang thai, cap nhat ca khach hang lan nhan vien.

## Danh sach canh giac

- Chon thoi gian khong hop le nhung van luu duoc
- So khach vuot gioi han
- Empty state bi sai khi huy dat ban hoac them request moi
- Danh sach nhan vien khong dong bo voi lich su cua khach
- Lech nghia giua `PROCESSING` va `DONE`

## Verify toi thieu

- Tao dat ban hop le thanh cong
- Dat ban qua khu bi chan
- Tao yeu cau phuc vu hop le thanh cong
- Huy dat ban cap nhat dung trang thai
- Nhan vien nhin thay va xu ly duoc doi tuong vua tao
