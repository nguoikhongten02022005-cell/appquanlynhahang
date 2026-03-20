# Skill: Xu Ly Nhan Vien

## Muc dich

Dung khi sua dashboard nhan vien, thao tac xac nhan/hoan tat/huy va cac so lieu tong hop danh cho nhan vien.

## Pham vi nghiep vu

- Xem danh sach don hang
- Xem danh sach dat ban
- Xem danh sach yeu cau phuc vu
- Xac nhan don/dat ban
- Hoan tat don/dat ban
- Huy don/dat ban
- Danh dau yeu cau phuc vu da xong
- Hien counter cong viec can xu ly

## File thuong lien quan

- `app/src/main/java/com/example/quanlynhahang/EmployeeActivity.java`
- `app/src/main/java/com/example/quanlynhahang/data/DatabaseHelper.java`
- Adapter cua employee order/reservation/request

## Quy tac nghiep vu bat buoc

- Chi `NHAN_VIEN` moi duoc vao man nay.
- Moi thao tac doi trang thai phai qua tang du lieu.
- Sau khi doi trang thai thanh cong phai refresh lai danh sach va thong ke.
- Neu them trang thai moi, phai cap nhat toan bo noi hien thi va xu ly lien quan.

## Cach lam viec de xuat

1. Doc `flows/staff-operations-flow.md` truoc khi sua.
2. Xac dinh tac dong tren don, dat ban hay request.
3. Tim ham update status trong `DatabaseHelper` truoc.
4. Sau do doi adapter va UI neu can.
5. Kiem tra role guard va redirect sai vai tro.

## Danh sach canh giac

- Counter khong doi sau khi update
- Danh sach da doi nhung so lieu tong quan chua doi
- Trang thai moi khong duoc adapter ho tro
- Nhan vien khong dung role van vao duoc man
- Huy/xac nhan/hoan tat khong dong bo voi phia khach hang

## Verify toi thieu

- Nhan vien vao man duoc, role khac bi chan
- Xac nhan don cap nhat dung
- Hoan tat dat ban cap nhat dung
- Danh dau request da xong cap nhat dung
- Counter va empty state thay doi dung theo du lieu moi
