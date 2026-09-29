# Ví dụ 2 - Custom Login với Spring Security

## Nội dung đã thực hiện

Ví dụ 2 mở rộng chức năng đăng nhập bằng Custom Login với Spring Boot và Spring Security.

Các chức năng đã hoàn thành:

- Đăng nhập bằng username hoặc email.
- Xác thực người dùng bằng Spring Security.
- Sử dụng `CustomUserDetails` và `CustomUserDetailsService` để quản lý thông tin người dùng đăng nhập.
- Mật khẩu được mã hóa bằng BCrypt.
- Lưu trạng thái đăng nhập bằng Session.
- Phân quyền người dùng theo USER/ADMIN.
- Giới hạn trang quản trị chỉ dành cho ADMIN.
- Hiển thị fullname, email và avatar của người dùng trên header.
- Hiển thị username và role của người dùng trên giao diện.
- Đăng xuất và hủy Session đăng nhập.
- Chuyển đổi dữ liệu giữa Entity và DTO bằng MapStruct.
- Kết nối và lưu dữ liệu bằng SQL Server.

## Công nghệ sử dụng

- Java 17
- Spring Boot 4.1.1
- Spring Security
- Spring Data JPA
- Thymeleaf
- MapStruct 1.6.3
- SQL Server
- BCrypt
- Maven

## Kết quả

Ứng dụng đã thực hiện được Custom Login cho phép đăng nhập bằng username hoặc email. Sau khi đăng nhập, thông tin fullname, email, avatar, username và role của người dùng được hiển thị trên giao diện; quyền truy cập và Session được quản lý bằng Spring Security.