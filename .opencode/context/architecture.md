# Ghi Chu Kien Truc

## Cau truc tong the

- `AndroidManifest.xml` khai bao nhieu Activity va dung `LauncherActivity` lam launcher.
- `MainActivity` la vo man hinh danh cho khach hang, dung bottom navigation va fragment.
- Chuc nang khach hang chu yeu nam trong Fragment.
- Man nhan vien va admin duoc tach thanh Activity rieng.
- Luu tru local tap trung trong `DatabaseHelper`.
- Session dang nhap tap trung trong `SessionManager`.

## Vi tri code quan trong

- Model vai tro: `app/src/main/java/com/example/quanlynhahang/model/UserRole.java`
- Model nguoi dung: `app/src/main/java/com/example/quanlynhahang/model/User.java`
- Schema va seed du lieu: `app/src/main/java/com/example/quanlynhahang/data/DatabaseHelper.java`
- Session va dang nhap: `app/src/main/java/com/example/quanlynhahang/data/SessionManager.java`
- Vo man khach hang: `app/src/main/java/com/example/quanlynhahang/MainActivity.java`
- Gio hang va dat mon: `app/src/main/java/com/example/quanlynhahang/CartActivity.java`
- Lich su don hang: `app/src/main/java/com/example/quanlynhahang/OrderFragment.java`
- Dat ban va yeu cau phuc vu: `app/src/main/java/com/example/quanlynhahang/RequestsFragment.java`
- Man nhan vien: `app/src/main/java/com/example/quanlynhahang/EmployeeActivity.java`
- Man admin: `app/src/main/java/com/example/quanlynhahang/AdminActivity.java`

## Ranh gioi du lieu

- `DatabaseHelper` la noi so huu viec tao bang, nang cap schema, seed du lieu va thao tac du lieu local.
- Tang UI nen uu tien goi helper hien co thay vi tu suy luan truc tiep ve schema.
- Neu can them cot hoac bang moi, phai dam bao app nang cap an toan tren may da cai truoc do.

## Mo hinh phan vai va dieu huong

- Dang nhap xac thuc tren DB local.
- Session luu `current_user_id` va `current_user_role`.
- `MainActivity` khong giu nguoi dung dang nhap voi vai tro nhan vien/admin.
- `EmployeeActivity` va `AdminActivity` deu co role guard khi vao man.

## Mau van hanh hien tai

- Khach hang tao du lieu van hanh nhu don hang, dat ban, yeu cau phuc vu.
- Nhan vien xu ly cac doi tuong van hanh do.
- Admin quan ly mon an, tai khoan va thong ke.

## Rang buoc kien truc

- Khong tao them nguon su that thu hai cho session.
- Khong nhan ban logic DB ra nhieu Activity/Fragment.
- Khong bo qua role guard.
- Uu tien mo rong nho gon tren kien truc hien tai thay vi viet lai lon.
