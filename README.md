# Quản lý nhà hàng

Ứng dụng Android native viết bằng Java để mô phỏng các luồng khách hàng, nhân viên nội bộ và quản trị cho bài tập quản lý nhà hàng.

## Tính năng chính

### Khách hàng
- Xem trang chủ và thực đơn
- Thêm món vào giỏ hàng và xác nhận đơn
- Theo dõi đơn hàng
- Đặt bàn
- Gửi yêu cầu phục vụ
- Quản lý tài khoản cá nhân

### Nội bộ
- Trung tâm nội bộ cho nhân viên
- Theo dõi tổng quan, đơn hàng, đặt bàn và yêu cầu phục vụ
- Điều hướng theo vai trò từ launcher nội bộ

### Quản trị
- Trung tâm quản trị riêng cho admin
- Quản lý món ăn
- Quản lý bàn
- Quản lý người dùng
- Xem báo cáo và cài đặt

## Công nghệ sử dụng
- Java 11
- Android Views/XML
- Material Components
- SQLite cục bộ qua `DatabaseHelper`
- Gradle Kotlin DSL

## Cấu hình hiện tại
- `minSdk = 28`
- `targetSdk = 36`
- `compileSdk = 36`
- package: `com.example.quanlynhahang`

## Cấu trúc chính

```text
app/src/main/java/com/example/quanlynhahang/
├── adapter/      # Adapter cho RecyclerView và phối hợp UI
├── data/         # Database, session, seed data, giỏ hàng
├── helper/       # Helper điều hướng, định dạng, nghiệp vụ
├── model/        # Model dữ liệu
├── *.Activity    # Màn hình chính
└── *.Fragment    # Màn hình chức năng
```

## Dữ liệu mẫu
- Dữ liệu mẫu được seed khi cơ sở dữ liệu được khởi tạo/mở.
- Logic seed hiện nằm ở `app/src/main/java/com/example/quanlynhahang/data/SeedDataHelper.java`.
- `DatabaseHelper` tập trung vào schema, migration và CRUD/query/update.

## Tài khoản demo
- Khách hàng: `quocbao@nhahang.vn` / `1`
- Nhân viên: `thaovy@nhahang.vn` / `1`
- Quản trị: `minhanh@nhahang.vn` / `1`

Các nút đăng nhập nhanh nằm ngay trên màn hình `DangNhapActivity`.

## Cách chạy dự án

### Bằng Android Studio
1. Mở thư mục dự án trong Android Studio.
2. Chờ Gradle sync hoàn tất.
3. Chạy app trên emulator hoặc thiết bị thật.
4. Launcher mặc định hiện tại là `CustomerLauncherActivity`.

### Bằng Gradle
Nếu chạy bằng terminal trên máy này, cần chỉ rõ JDK của Android Studio:

```bash
JAVA_HOME="/opt/android-studio/jbr" ./gradlew assembleDebug
```

## Chạy kiểm tra

### Unit test
```bash
JAVA_HOME="/opt/android-studio/jbr" ./gradlew testDebugUnitTest
```

### Instrumented test
Cần emulator hoặc thiết bị Android đang kết nối:

```bash
JAVA_HOME="/opt/android-studio/jbr" ./gradlew connectedDebugAndroidTest
```

## Một số entry point quan trọng
- `CustomerLauncherActivity`: vào luồng khách hàng
- `StaffLauncherActivity`: vào luồng nội bộ theo vai trò nhân viên/admin
- `MainActivity`: shell khách hàng
- `TrungTamNoiBoActivity`: shell nội bộ
- `TrungTamQuanTriActivity`: khu quản trị

## Ghi chú
- Một số thay đổi gần đây đã hợp nhất luồng nội bộ và quản trị, đồng thời bỏ `NhanVienActivity` và `QuanTriActivity` cũ.
- Hai layout legacy tương ứng cũng đã được dọn khỏi `res/layout`.
