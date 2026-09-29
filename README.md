# Spring Security - 3 ví dụ

Repository tổng hợp 3 ví dụ thực hành Spring Security đã hoàn thành.

## Ví dụ 1 - Login bằng Email

- Đăng nhập bằng email và mật khẩu.
- Xác thực bằng Spring Security và BCrypt.
- Quản lý trạng thái đăng nhập bằng Session.
- Phân quyền USER/ADMIN và bảo vệ trang quản trị.
- Hiển thị thông tin người dùng sau khi đăng nhập.
- Chuyển đổi Entity sang DTO bằng MapStruct.
- Lưu dữ liệu bằng SQL Server.

## Ví dụ 2 - Custom Login

- Đăng nhập bằng username hoặc email.
- Sử dụng `CustomUserDetails` và `CustomUserDetailsService`.
- Hiển thị fullname, email và avatar của người dùng trên giao diện.
- Phân quyền USER/ADMIN, quản lý Session và đăng xuất.
- Chuyển đổi Entity/DTO bằng MapStruct.
- Lưu dữ liệu bằng SQL Server.

## Ví dụ 3 - Full Spring Security

- Đăng ký tài khoản và xác nhận OTP qua email.
- Đăng nhập bằng username hoặc email, Session và phân quyền USER/ADMIN.
- Quên mật khẩu, xác nhận OTP và đặt lại mật khẩu.
- CRUD, tìm kiếm, phân trang và thống kê người dùng.
- CRUD, tìm kiếm, phân trang và phân quyền sở hữu sản phẩm.
- Upload và hiển thị ảnh sản phẩm bằng Cloudinary.
- Lưu dữ liệu bằng SQL Server.

## Công nghệ chính

- Java 17
- Spring Boot 4.1.1
- Spring Security
- Spring Data JPA
- Thymeleaf
- MapStruct 1.6.3
- SQL Server
- Spring Mail
- Cloudinary
- Maven

## Kết quả

Ba ví dụ đã triển khai theo mức độ tăng dần từ đăng nhập bằng email, custom login bằng username/email đến hệ thống xác thực và quản lý người dùng - sản phẩm hoàn chỉnh hơn ở Ví dụ 3.ung cấp. Bắt đầu với **HUONG_DAN_STS.md**.


- Admin web: admin@uteshop.vn / Admin@12345.

