# Quy Chuan Verify

Tai lieu nay quy dinh cach OpenCode nen kiem tra thay doi trong du an `quanlynhahang` sau khi sua code.

## Muc tieu

- Giam nguy co sua xong nhung vo flow cu
- Chon muc verify phu hop voi loai thay doi
- Tao cach lam nhat quan cho cac task sau nay

## Nguyen tac chung

- Khong phai thay doi nao cung can chay tat ca moi thu.
- Uu tien verify theo muc do anh huong.
- Neu sua `UI`, can kiem tra luong nguoi dung.
- Neu sua `Data-Business`, can kiem tra logic nghiep vu, status, session, schema.
- Neu sua ca hai, can kiem tra ca build va luong chuc nang.

## Lenh verify co san trong du an

Chay tai thu muc goc du an tren he dieu hanh dang dung.

- Linux/macOS: `/media/nha/New Volume/quanlynhahang`
- Windows: `D:\quanlynhahang`

### Build debug

```sh
./gradlew assembleDebug
```

Hoac tren Windows:

```bat
gradlew.bat assembleDebug
```

Dung khi:

- Sua Java code
- Sua resource
- Sua layout
- Sua manifest
- Sua adapter/fragment/activity

Muc dich:

- Kiem tra compile
- Kiem tra resource, ID, import, manifest merge o muc co ban

### Unit test

```sh
./gradlew testDebugUnitTest
```

Hoac tren Windows:

```bat
gradlew.bat testDebugUnitTest
```

Dung khi:

- Co thay doi logic co the duoc bao phu boi unit test
- Co them hoac sua test trong `app/src/test/`
- Muon co mot muc verify nhe hon so voi full instrumentation

Luu y hien tai:

- Repo moi co test mau co ban trong `app/src/test/java/com/example/quanlynhahang/ExampleUnitTest.java`
- Nghia la lenh nay huu ich de kiem tra cau hinh test va loi compile, nhung chua bao phu nghiep vu that su nhieu

### Instrumented test

```sh
./gradlew connectedDebugAndroidTest
```

Hoac tren Windows:

```bat
gradlew.bat connectedDebugAndroidTest
```

Dung khi:

- Sua code can test tren thiet bi/emulator that
- Sua hanh vi phu thuoc Android framework
- Co emulator hoac device dang ket noi san sang

Luu y hien tai:

- Repo moi co test mau trong `app/src/androidTest/java/com/example/quanlynhahang/ExampleInstrumentedTest.java`
- Lenh nay can co moi truong Android test san sang, neu khong se khong chay duoc

## Chon muc verify theo loai thay doi

### 1. Sua nho o UI

Vi du:

- Doi text
- Doi hien thi empty state
- Doi layout nho
- Doi dieu huong trong Fragment/Activity ma khong dong vao schema

Nen verify:

1. Linux/macOS: `./gradlew assembleDebug`
2. Windows: `gradlew.bat assembleDebug`
3. Tu review lai luong man hinh bi anh huong

### 2. Sua logic dat mon, dat ban, request, session, role

Vi du:

- Doi login flow
- Doi cach dat hang
- Doi status don hang
- Doi dat ban
- Doi role guard

Nen verify:

1. Linux/macOS: `./gradlew assembleDebug`
2. Linux/macOS: `./gradlew testDebugUnitTest`
3. Windows: `gradlew.bat assembleDebug`
4. Windows: `gradlew.bat testDebugUnitTest`
5. Kiem tra tay luong nghiep vu lien quan theo checklist ben duoi

### 3. Sua schema, du lieu local, `DatabaseHelper`

Vi du:

- Them cot moi
- Doi default value
- Doi seed data
- Doi logic insert/update/query quan trong

Nen verify:

1. Linux/macOS: `./gradlew assembleDebug`
2. Linux/macOS: `./gradlew testDebugUnitTest`
3. Windows: `gradlew.bat assembleDebug`
4. Windows: `gradlew.bat testDebugUnitTest`
5. Kiem tra lai cac man hinh doc/ghi du lieu lien quan
6. Neu co moi truong phu hop, can uu tien test tren may/emulator

### 4. Sua lon hoac sua xuyen nhieu flow

Vi du:

- Doi login + role + redirect
- Doi order status dong thoi o khach va nhan vien
- Doi admin user management co lien quan session

Nen verify:

1. Linux/macOS: `./gradlew assembleDebug`
2. Linux/macOS: `./gradlew testDebugUnitTest`
3. Linux/macOS: `./gradlew connectedDebugAndroidTest` neu co emulator/device san sang
4. Windows: `gradlew.bat assembleDebug`
5. Windows: `gradlew.bat testDebugUnitTest`
6. Windows: `gradlew.bat connectedDebugAndroidTest` neu co emulator/device san sang
7. Di lai checklist thu cong cho tung flow bi anh huong

## Checklist verify thu cong theo flow

### Dang nhap va phan vai

- Dang ky tai khoan moi thanh cong
- Dang nhap bang thong tin hop le thanh cong
- Sai mat khau thi bi chan
- Khach hang vao `MainActivity` dung
- Nhan vien vao `EmployeeActivity` dung
- Admin vao `AdminActivity` dung
- Session loi thi bi xoa hoac fail-safe

### Dat mon

- Them mon vao gio thanh cong
- Tang/giam/xoa mon trong gio dung
- Tong tien cap nhat dung
- Chua dang nhap thi dat hang bi chuyen den login
- Dang nhap xong tiep tuc dat hang duoc
- Dat hang thanh cong tao ra don moi
- Lich su don hien thi dung
- Huy don chi xay ra khi hop le

### Dat ban va yeu cau phuc vu

- Dat ban voi thoi gian hop le thanh cong
- Dat ban qua khu bi chan
- So khach vuot gioi han bi chan
- Gui yeu cau phuc vu thanh cong
- Huy dat ban cap nhat dung
- Lich su ben khach va danh sach ben nhan vien khop nhau

### Nhan vien

- Role sai khong vao duoc man nhan vien
- Xac nhan don cap nhat dung
- Hoan tat don cap nhat dung
- Huy don cap nhat dung
- Xac nhan dat ban cap nhat dung
- Hoan tat dat ban cap nhat dung
- Danh dau request da xong cap nhat dung
- Counter va empty state cap nhat theo du lieu moi

### Admin

- Role sai khong vao duoc man admin
- Them mon thanh cong
- Sua mon thanh cong
- Bat/tat mon phuc vu cap nhat dung
- Tim kiem mon hoat dong dung
- Loc user theo role dung
- Doi role user cap nhat dung
- Bat/tat trang thai user cap nhat dung
- So lieu thong ke van khop voi danh sach hien thi

## Cach bao cao ket qua verify

Khi hoan thanh mot task, nen ghi ro:

- Da chay lenh nao
- Lenh nao khong chay duoc va vi sao
- Da verify tay luong nao
- Rủi ro con lai neu chua co emulator/device hoac chua co test phu hop

## Mau bao cao ngan

```text
Verify da thuc hien:
- Linux/macOS: da chay `./gradlew assembleDebug`
- Linux/macOS: da chay `./gradlew testDebugUnitTest`
- Da ra soat tay luong dang nhap va dat mon

Chua verify:
- Chua chay `./gradlew connectedDebugAndroidTest` vi chua co emulator/device san sang

Rui ro con lai:
- Can test tren may that de chac chan flow UI Android hoat dong dung
```
