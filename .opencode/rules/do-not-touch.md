# Khu Vuc Khong Nen Dung Neu Chua Can

## Vung nhay cam

- Logic schema trong `app/src/main/java/com/example/quanlynhahang/data/DatabaseHelper.java`
- Key auth va session trong `app/src/main/java/com/example/quanlynhahang/data/SessionManager.java`
- Logic dieu huong va chan truy cap theo vai tro

## Gioi han

- Khong doi ten enum role neu chua cap nhat het moi noi phu thuoc.
- Khong doi ten bang/cot DB mot cach tuy y.
- Khong xoa migration auth cu neu chua thay the an toan.
- Khong lam hong luong `EXTRA_RETURN_TO_CALLER`.
- Khong xoa seed/tai khoan test neu nguoi dung chua yeu cau ro.

## File can rat can than khi sua

- `app/src/main/java/com/example/quanlynhahang/model/UserRole.java`
- `app/src/main/java/com/example/quanlynhahang/LoginActivity.java`
- `app/src/main/java/com/example/quanlynhahang/MainActivity.java`
- `app/src/main/java/com/example/quanlynhahang/EmployeeActivity.java`
- `app/src/main/java/com/example/quanlynhahang/AdminActivity.java`
