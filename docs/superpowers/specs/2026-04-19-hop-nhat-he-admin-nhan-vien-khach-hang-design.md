# Thiết kế hợp nhất hệ admin, nhân viên, khách hàng

Ngày: 2026-04-19

## 1. Mục tiêu

Hoàn thiện và đồng nhất toàn bộ hệ thống theo 3 hướng cùng lúc:
- điều hướng và phân quyền nhất quán giữa admin, nhân viên, khách hàng
- nghiệp vụ dữ liệu và trạng thái dùng chung một nguồn chuẩn
- giao diện và cấu trúc màn hình đồng bộ, dễ hiểu, dễ mở rộng

Thiết kế được chốt theo các quyết định sau:
- giữ 2 cổng vào riêng: khách hàng và nội bộ
- gộp admin và nhân viên vào một internal shell chung
- admin là superset của nhân viên: làm được toàn bộ thao tác vận hành và có thêm quyền quản trị
- nhân viên và admin dùng khung giống nhau, nhưng menu của nhân viên chỉ hiện phần được phép
- màn nội bộ chung có 4 khu: Tổng quan, Đơn hàng, Đặt bàn, Yêu cầu phục vụ
- admin có thêm lối sang Quản trị và Xem giao diện khách hàng
- Quản trị là một shell/section con dùng chung session nội bộ và guard quyền, không phải một hệ thứ ba tách biệt
- admin shell con gồm: quản lý món ăn, quản lý người dùng, báo cáo/thống kê sâu, cài đặt hệ thống
- chỉ admin được mở giao diện khách hàng
- giao diện khách hàng mà admin mở là một phiên khách hàng tách biệt với phiên nội bộ
- `NhanVienActivity` và `QuanTriActivity` cũ chỉ còn làm entry redirect

## 2. Bối cảnh code hiện tại

Các điểm đang làm nền cho thiết kế này:
- `app/src/main/java/com/example/quanlynhahang/StaffLauncherActivity.java` đang là cổng vào nội bộ và điều hướng theo vai trò sang `NhanVienActivity` hoặc `QuanTriActivity`
- `app/src/main/java/com/example/quanlynhahang/NhanVienActivity.java` đã gần như là một shell vận hành với 4 khu: Tổng quan, Đơn hàng, Đặt bàn, Yêu cầu phục vụ, và có hỗ trợ mở đúng tab qua `EXTRA_TAB_MUC_TIEU`
- `app/src/main/java/com/example/quanlynhahang/QuanTriActivity.java` hiện vừa chứa tổng quan admin, món ăn, người dùng, vừa gọi sang luồng vận hành nhân viên qua shortcut, đồng thời có lối mở giao diện khách hàng
- `app/src/main/java/com/example/quanlynhahang/helper/DieuHuongVaiTroHelper.java` và các activity hiện tại vẫn guard quyền bằng cách trỏ trực tiếp vào `NhanVienActivity`, `QuanTriActivity`, `MainActivity`
- phía khách hàng đã có trung tâm hoạt động riêng ở `app/src/main/java/com/example/quanlynhahang/TrungTamHoatDongFragment.java`
- trạng thái nghiệp vụ chuẩn đang nằm trong model và `DatabaseHelper`, chưa có hệ trạng thái tách riêng cho từng vai trò

## 3. Kiến trúc đích

### 3.1. Cổng vào hệ thống

Giữ 2 cổng vào rõ ràng:
- `CustomerLauncherActivity`: cổng khách hàng
- `StaffLauncherActivity`: cổng nội bộ

`CustomerLauncherActivity` tiếp tục dẫn vào hệ khách.
`StaffLauncherActivity` sẽ được chuyển thành entry chính cho internal shell mới.

### 3.2. Internal shell chung

Tạo một internal shell mới làm điểm vào chung cho admin và nhân viên.

Internal shell gồm:
- thanh tiêu đề
- menu điều hướng theo vai trò
- content container cho fragment hoặc section hiện hành
- 4 khu chung:
  - Tổng quan
  - Đơn hàng
  - Đặt bàn
  - Yêu cầu phục vụ

Nguyên tắc hiển thị:
- nhân viên chỉ thấy 4 khu chung
- admin vào cùng shell, thấy 4 khu chung và thêm lối sang:
  - Quản trị
  - Xem giao diện khách hàng

### 3.3. Admin section / shell con

Quản trị không nhét toàn bộ vào internal shell chính để tránh shell chính bị phình quá mức.

Thay vào đó, admin có một shell/section con riêng, nhưng:
- vẫn dùng chung session nội bộ
- vẫn dùng chung guard quyền nội bộ
- không được tạo cảm giác như một hệ thứ ba tách biệt khỏi internal shell

Admin shell con gồm 4 phần:
- Quản lý món ăn
- Quản lý người dùng
- Báo cáo / thống kê sâu
- Cài đặt hệ thống

Các phần món ăn và người dùng sẽ tái sử dụng logic và giao diện đang có trong `QuanTriActivity` và các adapter quản trị hiện tại.

### 3.4. Giao diện khách hàng do admin mở

Chỉ admin được thấy lối mở giao diện khách hàng.

Khi admin mở giao diện khách hàng:
- hệ thống tạo hoặc dùng một phiên khách hàng riêng
- không ghi đè session nội bộ của admin
- thoát giao diện khách thì quay lại đúng màn nội bộ trước đó với trạng thái admin còn nguyên

Đây là cơ chế preview / dùng thử đầy đủ theo luồng khách hàng, nhưng tách session để không làm bẩn luồng vận hành nội bộ.

Acceptance criteria cho customer preview của admin:
- mở preview không làm mất internal session hiện tại
- thoát preview quay lại đúng route nội bộ trước đó, gồm đúng tab hoặc section đang đứng
- giỏ hàng, bàn hiện tại, và context đặt món trong preview chỉ ghi vào customer session scope
- đăng nhập hoặc đăng xuất trong preview không làm thay đổi role, route, hoặc trạng thái nội bộ đang mở
- fail nếu bất kỳ dữ liệu preview nào ghi đè internal session hoặc làm admin quay lại sai màn

### 3.5. Route contract

Route contract đích cần được ghi rõ ở tầng điều hướng:
- `InternalShell(tab=overview|orders|reservations|service_requests)`
- `AdminShell(section=dishes|users|reports|settings)`
- `CustomerPreview(returnToInternalRoute=<route nội bộ trước đó>)`

Quy ước legacy bridge:
- giữ support cho `NhanVienActivity.EXTRA_TAB_MUC_TIEU`
- map `orders` → `InternalShell(tab=orders)`
- map `reservations` → `InternalShell(tab=reservations)`
- map `service_requests` → `InternalShell(tab=service_requests)`
- nếu `NhanVienActivity` được gọi mà không có extra, route mặc định là `InternalShell(tab=orders)` để giữ hành vi cũ
- nếu `QuanTriActivity` được gọi trực tiếp theo entry cũ, route mặc định là `InternalShell(tab=overview)`

## 4. Mô hình phiên và phân quyền

### 4.1. Tách session

Session hiện tại trong `SessionManager` đang mang cả user id, role, và current table trong cùng một namespace.

Thiết kế đích yêu cầu tách thành 2 namespace độc lập với ownership rõ ràng:
- phiên nội bộ
  - user nội bộ
  - vai trò admin hoặc nhân viên
  - route nội bộ cuối cùng để quay lại đúng màn khi cần
- phiên khách hàng
  - guest hoặc tài khoản khách hàng
  - bàn hiện tại
  - giỏ hàng / context ăn tại quán
  - route khách cuối cùng
  - route nội bộ nguồn khi admin mở preview

Contract ownership ở mức key:
- internal session giữ các key cùng scope với: `internal_is_logged_in`, `internal_user_id`, `internal_user_role`, `internal_last_route`
- customer session giữ các key cùng scope với: `customer_is_logged_in`, `customer_user_id`, `customer_current_table`, `customer_last_route`, `customer_preview_source_route`
- cart, order context ăn tại quán, current table, và các badge liên quan thuộc hẳn customer session scope
- `QuanLyGioHang` không được tiếp tục hoạt động như singleton toàn cục không namespace; nó phải được ràng vào customer session scope hoặc có cơ chế cô lập tương đương

Nguyên tắc:
- đăng xuất nội bộ chỉ xóa phiên nội bộ
- đăng xuất khách chỉ xóa phiên khách
- admin preview khách không được làm rớt phiên nội bộ

### 4.2. Guard phân quyền thống nhất

Thay vì để guard rải ở `MainActivity`, `NhanVienActivity`, `QuanTriActivity`, launcher, helper, cần gom về một luật thống nhất:
- khách hoặc guest: chỉ được vào hệ khách
- nhân viên: vào internal shell chung
- admin: vào internal shell chung, admin section, và preview khách hàng

Admin là superset của nhân viên:
- làm toàn bộ thao tác vận hành như nhân viên
- có thêm quyền quản trị sâu

### 4.3. Dữ liệu thuộc session nào

Dữ liệu phải được quy về đúng session:
- giỏ hàng, bàn hiện tại, ngữ cảnh ăn tại quán: thuộc session khách hàng
- quyền quản trị, quyền vận hành, menu nội bộ: thuộc session nội bộ

## 5. Trạng thái nghiệp vụ chuẩn và đồng bộ dữ liệu

### 5.1. Bộ trạng thái chuẩn

Tiếp tục dùng đúng bộ trạng thái chuẩn hiện có trong model:

#### Đơn hàng
Nguồn chuẩn: `app/src/main/java/com/example/quanlynhahang/model/DonHang.java`
- `CHO_XAC_NHAN`
- `DANG_CHUAN_BI`
- `SAN_SANG_PHUC_VU`
- `HOAN_THANH`
- `DA_HUY`

#### Đặt bàn
Nguồn chuẩn: `app/src/main/java/com/example/quanlynhahang/model/DatBan.java`
- `PENDING`
- `ACTIVE`
- `COMPLETED`
- `CANCELLED`
- `EXPIRED`

#### Yêu cầu phục vụ
Nguồn chuẩn: `app/src/main/java/com/example/quanlynhahang/model/YeuCauPhucVu.java`
- `DANG_CHO`
- `DANG_XU_LY`
- `DA_XU_LY`
- `DA_HUY`

Không tạo thêm nhánh trạng thái riêng cho admin hay nhân viên.
Khác biệt giữa các vai trò chỉ nằm ở quyền thao tác và cách hiển thị.

### 5.2. Cách hiển thị cho khách hàng

Khách hàng dùng nhãn thân thiện, nhưng có dòng chi tiết phản ánh trạng thái nội bộ.

Ví dụ:
- `CHO_XAC_NHAN` → “Đã nhận yêu cầu” / “Đơn đang chờ nhà hàng xác nhận”
- `DANG_CHUAN_BI` → “Đang chuẩn bị món” / “Bếp đang xử lý đơn của bạn”
- `SAN_SANG_PHUC_VU` → “Sẵn sàng phục vụ” / “Đơn đã sẵn sàng để giao hoặc phục vụ”
- `ACTIVE` → “Đang giữ bàn” / “Bàn của bạn đang trong thời gian hiệu lực”
- `DANG_XU_LY` → “Nhân viên đang tới” / “Yêu cầu đã được nhân viên tiếp nhận”

### 5.3. Luật chuyển trạng thái

Giữ và chuẩn hóa các luật nghiệp vụ hiện có:
- khách chỉ được hủy đơn khi còn `CHO_XAC_NHAN`
- khách chỉ được hủy đặt bàn khi còn `PENDING`
- khách chỉ được hủy yêu cầu khi còn `DANG_CHO`
- nội bộ dùng cùng một luật chuyển trạng thái cho admin và nhân viên

Các luật nội bộ đang có ở `NhanVienActivity` sẽ được tách ra và dùng chung cho internal shell mới.

### 5.4. Liên kết nghiệp vụ chéo

Các ràng buộc nghiệp vụ chéo cần được chuẩn hóa rõ hơn:
- đặt bàn `COMPLETED` chỉ hợp lệ khi có `idDonHangLienKet > 0`
- luật yêu cầu thanh toán gắn với order là luật đích cần chuẩn hóa thêm, vì code hiện tại mới gắn theo bàn/ngữ cảnh chứ chưa gắn `orderId` thật
- bàn hiện tại của khách phải được suy ra thống nhất từ session khách + đơn tại quán + context giỏ hàng, theo tinh thần đang có trong `DichVuKhachHangHelper`

### 5.5. Đồng bộ dữ liệu giữa 3 hệ

Nguyên tắc đồng bộ:
- chỉ có một nguồn dữ liệu nghiệp vụ chuẩn trong DB
- internal shell, admin section, và hệ khách chỉ là các góc nhìn khác nhau trên cùng dữ liệu
- thao tác ở nội bộ cập nhật xong thì khách hàng phải đọc lại đúng trạng thái đó khi resume hoặc refresh
- báo cáo admin chỉ tổng hợp từ các trạng thái chuẩn, không tạo dữ liệu song song

## 6. Cấu trúc màn hình

### 6.1. Internal shell

Internal shell gồm các khu:
- Tổng quan
  - số liệu vận hành
  - cảnh báo cần xử lý
  - quick actions
- Đơn hàng
  - danh sách toàn hệ
  - đổi trạng thái
  - hủy
  - xem chi tiết
- Đặt bàn
  - xác nhận
  - đổi bàn
  - hoàn tất
  - hủy
- Yêu cầu phục vụ
  - nhận xử lý
  - đánh dấu đã xong
  - hủy

### 6.2. Admin shell con

Admin shell con gồm:
- Quản lý món ăn
- Quản lý người dùng
- Báo cáo / thống kê sâu
- Cài đặt hệ thống

### 6.3. Route cũ và route mới

Cần có mapping route cũ → route mới để chuyển đổi an toàn.

| Route / entry cũ | Vai trò hiện tại | Route / đích mới cụ thể |
|---|---|---|
| `StaffLauncherActivity` khi đăng nhập nội bộ thành công | cổng nội bộ | `InternalShell(tab=overview)` |
| `NhanVienActivity` với `EXTRA_TAB_MUC_TIEU=orders` | màn vận hành nhân viên | `InternalShell(tab=orders)` |
| `NhanVienActivity` với `EXTRA_TAB_MUC_TIEU=reservations` | màn vận hành nhân viên | `InternalShell(tab=reservations)` |
| `NhanVienActivity` với `EXTRA_TAB_MUC_TIEU=service_requests` | màn vận hành nhân viên | `InternalShell(tab=service_requests)` |
| `NhanVienActivity` không có extra | màn vận hành nhân viên | `InternalShell(tab=orders)` để giữ hành vi mặc định cũ |
| `QuanTriActivity` mở trực tiếp theo entry cũ | màn admin tổng hợp cũ | `InternalShell(tab=overview)` |
| legacy action của admin sang quản lý món | luồng quản trị cũ | `AdminShell(section=dishes)` |
| legacy action của admin sang quản lý người dùng | luồng quản trị cũ | `AdminShell(section=users)` |
| legacy action của admin sang báo cáo sâu | luồng quản trị mới | `AdminShell(section=reports)` |
| legacy action của admin sang cài đặt | luồng quản trị mới | `AdminShell(section=settings)` |
| shortcut admin sang vận hành đơn hàng | bridge tạm | `InternalShell(tab=orders)` |
| shortcut admin sang vận hành đặt bàn | bridge tạm | `InternalShell(tab=reservations)` |
| shortcut admin sang yêu cầu phục vụ | bridge tạm | `InternalShell(tab=service_requests)` |
| hành động “Xem giao diện khách hàng” của admin | preview khách | `CustomerPreview(returnToInternalRoute=<route hiện tại>)` |
| `CustomerLauncherActivity` | cổng khách | giữ nguyên |
| helper điều hướng theo vai trò | trỏ activity cũ | trỏ route mới theo luật guard thống nhất |

## 7. Chiến lược chuyển đổi an toàn

### 7.1. Nguyên tắc chung

Không big-bang rewrite.

Giai đoạn đầu phải giữ nguyên:
- schema dữ liệu
- enum trạng thái
- contract dữ liệu từ `DatabaseHelper`
- hành vi nghiệp vụ cốt lõi

Chỉ thay lớp điều hướng và lớp tổ chức màn hình trước.

### 7.2. Thứ tự chuyển đổi

#### Mốc 1 — Dựng internal shell mới
- tạo internal shell mới
- tái dùng logic vận hành từ `NhanVienActivity`
- cho `StaffLauncherActivity` route vào shell mới
- chưa đụng sâu đến admin preview khách

#### Mốc 2 — Tách vận hành ra khỏi `NhanVienActivity`
- di chuyển 4 khu vận hành thành fragment/section dùng được trong internal shell
- `NhanVienActivity` chỉ còn nhận intent cũ và redirect vào shell mới đúng tab
- bridge này phải tiếp tục hỗ trợ `EXTRA_TAB_MUC_TIEU` cho đến khi toàn bộ caller cũ được cập nhật xong

#### Mốc 3 — Tách admin section khỏi `QuanTriActivity`
- di chuyển món ăn, người dùng, báo cáo sâu, cài đặt sang admin shell con
- `QuanTriActivity` chỉ còn redirect vào internal shell hoặc admin section phù hợp

#### Mốc 4 — Chuẩn hóa helper điều hướng và guard quyền
- cập nhật helper điều hướng vai trò về route mới
- gom logic guard về một nơi thống nhất

#### Mốc 5 — Tách session khách và nội bộ
- bổ sung namespace session riêng
- hoàn thiện preview khách hàng cho admin
- đảm bảo thoát preview quay lại nội bộ không mất trạng thái admin

### 7.3. Feature flag / kill switch

Cần thêm feature flag hoặc kill switch cho internal shell mới.

Mục tiêu:
- có thể bật internal shell mới theo cấu hình
- nếu phát hiện lỗi nặng, `StaffLauncherActivity` có thể quay lại route cũ
- giúp rollout và rollback an toàn trong quá trình refactor

### 7.4. Rollback rule

Nếu internal shell mới lỗi ở runtime hoặc fail kiểm chứng luồng vàng:
- launcher nội bộ quay lại entry cũ
- `NhanVienActivity` và `QuanTriActivity` tiếp tục là fallback tạm thời
- không xóa code redirect/fallback cho đến khi toàn bộ luồng vàng pass ổn định

### 7.5. Tiêu chí để activity cũ chỉ còn redirect

`NhanVienActivity` và `QuanTriActivity` chỉ được coi là redirect-only khi thỏa đủ các điều kiện sau:
- toàn bộ logic hiển thị và thao tác nghiệp vụ chính đã được chuyển sang internal shell hoặc admin shell con
- activity cũ không còn render UI nghiệp vụ chính, chỉ đọc legacy extra hoặc entry cũ rồi route sang màn mới
- `NhanVienActivity` vẫn bridge đúng `EXTRA_TAB_MUC_TIEU`
- các shortcut và caller cũ còn lại đều mở được route mới tương đương
- full regression pass trên 5 luồng vàng

### 7.6. Exit criteria để xóa hẳn activity cũ

Chỉ được phép xóa hẳn `NhanVienActivity` và `QuanTriActivity` khi thỏa đủ các điều kiện sau:
- `DieuHuongVaiTroHelper`, launcher, shortcut, và caller trong codebase không còn bắn trực tiếp vào 2 activity này
- mapping legacy extra đã được hấp thụ hoàn toàn ở route layer mới
- feature flag hoặc kill switch không còn phụ thuộc route cũ làm fallback
- full regression pass sau mốc cuối cùng của refactor
- customer preview của admin đã pass acceptance criteria riêng

## 8. Checklist regression theo 5 luồng vàng

Sau mỗi mốc refactor phải kiểm tra đủ 5 luồng vàng:
1. khách đặt đơn và xem trạng thái
2. khách đặt bàn và hủy đặt bàn
3. khách gửi yêu cầu phục vụ
4. nhân viên xử lý 4 khu vận hành
5. admin vào nội bộ, mở admin section, và preview khách hàng mà không làm rớt phiên nội bộ

Checklist mỗi lần kiểm tra:
- điều hướng đúng cổng vào
- guard vai trò đúng
- dữ liệu hiển thị đúng theo DB
- trạng thái cập nhật đúng giữa khách và nội bộ
- back stack không bị vòng lặp hoặc rơi sai màn
- đăng xuất không làm mất session không liên quan

## 9. Xử lý lỗi và hành vi biên

- nếu session nội bộ hết hạn: quay về cổng nội bộ
- nếu session khách hết hạn: quay về cổng khách hoặc màn đăng nhập khách phù hợp
- nếu admin đang preview khách mà phiên khách lỗi: chỉ reset session khách, không đụng session nội bộ
- nếu route cũ gọi vào màn không còn tồn tại logic cũ: redirect sang route mới tương đương thay vì fail silent
- nếu dữ liệu trạng thái chéo không hợp lệ, ưu tiên hiển thị trạng thái chuẩn từ DB và từ chối thao tác trái luật

## 10. Kiểm thử

### 10.1. Kiểm thử chức năng
- kiểm tra launcher khách và launcher nội bộ
- kiểm tra đăng nhập theo từng vai trò
- kiểm tra redirect từ `NhanVienActivity` cũ
- kiểm tra redirect từ `QuanTriActivity` cũ
- kiểm tra admin vào admin section
- kiểm tra admin preview khách
- kiểm tra quay lại từ preview khách về nội bộ

### 10.2. Kiểm thử nghiệp vụ
- kiểm tra toàn bộ chuyển trạng thái đơn hàng
- kiểm tra đặt bàn và liên kết đơn hàng
- kiểm tra yêu cầu phục vụ và chống gửi trùng
- kiểm tra đồng bộ trạng thái giữa khách và nội bộ

### 10.3. Kiểm thử hồi quy giao diện
- kiểm tra menu theo vai trò
- kiểm tra tab mục tiêu nội bộ
- kiểm tra shortcut cũ còn hoạt động
- kiểm tra wording thân thiện phía khách
- kiểm tra wording nội bộ không bị lẫn nhãn khách hàng

## 11. Phạm vi không làm trong đợt này

- không thay schema DB lớn nếu chưa cần
- không viết lại toàn bộ hệ khách hàng
- không thay toàn bộ business rule cốt lõi nếu logic hiện tại đã đúng
- không biến admin section thành subsystem riêng biệt tách session hoặc tách guard

## 12. Kết luận

Thiết kế này ưu tiên:
- bám sát cấu trúc repo hiện tại
- tận dụng `NhanVienActivity` làm nền internal shell mới
- giảm dần vai trò của `QuanTriActivity` xuống entry redirect + nguồn cho admin section
- giữ hệ chạy an toàn bằng route mapping, feature flag, rollback rule, và regression checklist

Đây là một refactor có định hướng kiến trúc, không phải thay từng màn rời rạc. Trọng tâm là hợp nhất lớp điều hướng, hợp nhất lớp vận hành nội bộ, và giữ một nguồn trạng thái dữ liệu duy nhất cho cả khách hàng, nhân viên, và admin.
