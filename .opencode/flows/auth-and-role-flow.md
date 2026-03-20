# Luong Dang Nhap Va Phan Vai

## Muc tieu

Giu cho dang ky, dang nhap, session va dieu huong theo vai tro luon nhat quan.

## Luong hien tai

1. App mo DB qua `DatabaseHelper.chuanBiCoSoDuLieu()`.
2. App chay migration auth cu qua `SessionManager.migrateLegacyAuthIfNeeded(...)`.
3. Dang ky tao tai khoan moi voi vai tro mac dinh `KHACH_HANG`.
4. Dang nhap chap nhan email hoac so dien thoai kem mat khau.
5. Session luu `userId` va `role`.
6. Nguoi dung duoc dieu huong theo vai tro qua helper dieu huong hoac role guard.

## Quy tac nghiep vu

- Dang ky mac dinh la khach hang tru khi co yeu cau quan tri ro rang.
- Dang nhap khong duoc tu y doi vai tro cua tai khoan.
- Session phai tro thanh khong hop le neu `userId` khong con ton tai trong DB.
- `MainActivity` khong duoc giu session cua nhan vien hoac admin.
- `EmployeeActivity` va `AdminActivity` phai chan truy cap sai vai tro.

## Checklist khi sua

- Neu doi field auth, phai nho luong migration du lieu cu.
- Neu doi gia tri vai tro, phai cap nhat toan bo guard va redirect lien quan.
- Neu doi login flow, phai giu `EXTRA_RETURN_TO_CALLER` cho cac luong bi chan boi login.
- Neu doi cau truc session, phai kiem tra lai logout va stale session.

## Tinh huong de loi

- Co co dang nhap nhung `current_user_id` khong hop le.
- Session ton tai nhung bi thieu role.
- Du lieu dang nhap cu ton tai tu phien ban truoc.
- Nguoi dung dang nhap tu gio hang hoac don hang can quay lai man goi.
