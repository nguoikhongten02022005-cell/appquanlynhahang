# Skill: Quan Ly Admin

## Muc dich

Dung khi sua dashboard admin, quan ly mon an, vai tro nguoi dung, trang thai hoat dong va thong ke tong quan.

## Pham vi nghiep vu

- Xem thong ke tong quan
- Them sua xoa mon an
- Bat/tat trang thai phuc vu cua mon
- Tim kiem mon
- Xem danh sach nguoi dung
- Loc nguoi dung theo vai tro
- Doi vai tro nguoi dung
- Bat/tat trang thai hoat dong nguoi dung

## File thuong lien quan

- `app/src/main/java/com/example/quanlynhahang/AdminActivity.java`
- `app/src/main/java/com/example/quanlynhahang/data/DatabaseHelper.java`
- Adapter cua admin dish va admin user

## Quy tac nghiep vu bat buoc

- Chi `ADMIN` moi duoc vao man admin.
- Thay doi mon an khong duoc lam hong menu, gio hang va lich su don.
- Thay doi role nguoi dung khong duoc lam sai luong dieu huong va session.
- Vo hieu hoa nguoi dung khong duoc pha du lieu lich su.
- Neu xoa mon, phai can than voi du lieu da dat truoc do.

## Cach lam viec de xuat

1. Doc `flows/admin-operations-flow.md` truoc khi sua.
2. Xac dinh dang sua khu mon an, khu nguoi dung hay khu thong ke.
3. Neu thay doi schema mon an, doc them `skills/db-safe-change.md`.
4. Neu doi role hoac active user, kiem tra ca login va dieu huong.
5. Neu sua thong ke, doi lai logic truy van va man hinh cung luc.

## Danh sach canh giac

- Sua mon an lam vo du lieu don cu
- Doi role nhung session cu van sai huong
- Counter thong ke sai sau khi doi query
- Loc user theo role bi lech enum
- Xoa mon hoac tat phuc vu lam UI khach hang loi

## Verify toi thieu

- Admin vao man duoc, role khac bi chan
- Them hoac sua mon an thanh cong
- Bat/tat trang thai mon cap nhat dung
- Doi role nguoi dung cap nhat dung
- Thong ke va danh sach van khop nhau sau khi sua
