# Bo Ngu Canh OpenCode

Thu muc nay giup OpenCode hieu on dinh va nhat quan du an `quanlynhahang`.

Can hieu dung mo hinh hien tai cua repo:

- Day la app Android.
- Chua thay backend server rieng trong repo.
- Du an dang gom `giao dien + nghiep vu + du lieu local` trong cung mot ung dung.
- Vi vay khong nen hieu `backend` theo nghia API server, ma nen hieu la `du lieu va nghiep vu trong app`.

## Cach doc thu muc nay

- `context/` mo ta du an, kien truc, thuat ngu, ranh gioi he thong.
- `flows/` mo ta luong nghiep vu va chuyen trang thai quan trong.
- `rules/` mo ta quy tac sua code va gioi han an toan.
- `agents/` mo ta vai tro lam viec phu hop voi repo nay.
- `skills/` mo ta cach xu ly cac nhom tac vu lap lai.

## Thu tu uu tien khi sua doi

1. Giu dung luong nghiep vu trong `flows/`.
2. Giu dung cau truc va ranh gioi trong `context/`.
3. Tuan thu quy tac an toan trong `rules/`.
4. Chon dung agent va skill trong `agents/` va `skills/`.

## Thong tin du an rut ra tu code hien tai

- Module Android: `app`
- He thong build: Gradle Kotlin DSL
- Ngon ngu app: Java
- Du lieu: SQLite local qua `DatabaseHelper`
- Dang nhap va session: `SharedPreferences` qua `SessionManager`
- Vai tro: `KHACH_HANG`, `NHAN_VIEN`, `ADMIN`
- Diem vao launcher: `LauncherActivity`

## Cach chia khu vuc de agent hieu dung

- `UI/FE`: Activity, Fragment, Adapter, XML layout, string resource, dieu huong man hinh
- `Data-Business`: DatabaseHelper, SessionManager, model, status, validation nghiep vu, role routing

## Diem vao nhanh

- Vo app khach hang: `app/src/main/java/com/example/quanlynhahang/MainActivity.java`
- Dang nhap: `app/src/main/java/com/example/quanlynhahang/LoginActivity.java`
- Dang ky: `app/src/main/java/com/example/quanlynhahang/RegisterActivity.java`
- Gio hang va dat mon: `app/src/main/java/com/example/quanlynhahang/CartActivity.java`
- Don hang cua khach: `app/src/main/java/com/example/quanlynhahang/OrderFragment.java`
- Dat ban va yeu cau phuc vu: `app/src/main/java/com/example/quanlynhahang/RequestsFragment.java`
- Man nhan vien: `app/src/main/java/com/example/quanlynhahang/EmployeeActivity.java`
- Man admin: `app/src/main/java/com/example/quanlynhahang/AdminActivity.java`
- Tang du lieu: `app/src/main/java/com/example/quanlynhahang/data/DatabaseHelper.java`
- Tang session: `app/src/main/java/com/example/quanlynhahang/data/SessionManager.java`
