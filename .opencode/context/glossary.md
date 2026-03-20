# Bang Thuat Ngu

## Vai tro

- `KHACH_HANG`: nguoi dung cuoi su dung app de xem mon, dat mon, dat ban, gui yeu cau
- `NHAN_VIEN`: nhan vien xu ly don hang, dat ban va yeu cau phuc vu
- `ADMIN`: nguoi quan tri quan ly mon an, tai khoan va so lieu tong quan

## Trang thai don hang

- `PENDING_CONFIRMATION`: khach da tao don, dang cho nhan vien xu ly
- `CONFIRMED`: nhan vien da xac nhan don
- `COMPLETED`: don da hoan tat
- `CANCELED`: don da bi huy

## Trang thai dat ban

- `PENDING_APPROVAL`: dat ban moi tao, dang cho phe duyet
- `CONFIRMED`: dat ban da duoc xac nhan
- `COMPLETED`: dat ban da hoan tat
- `CANCELED`: dat ban da bi huy

## Trang thai yeu cau phuc vu

- `PROCESSING`: yeu cau dang mo, chua xu ly xong
- `DONE`: yeu cau da duoc xu ly

## Thuat ngu ky thuat

- Session: trang thai dang nhap duoc luu boi `SessionManager`
- Embedded mode: che do fragment duoc nhung trong man khac va co the an tieu de
- Seed data: du lieu mac dinh va tai khoan test duoc tao trong `DatabaseHelper`

## Khu vuc man hinh

- Vo khach hang: `MainActivity`
- Khu don hang/yeu cau cua khach: `ActivityHubFragment`
- Man nhan vien: `EmployeeActivity`
- Man admin: `AdminActivity`
