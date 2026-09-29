# Ví dụ 1 - Spring Security Login bằng Email

## Nội dung đã thực hiện

Ví dụ 1 xây dựng chức năng đăng nhập cho ứng dụng web bằng Spring Boot và Spring Security.

Các chức năng đã hoàn thành:

- Đăng nhập bằng email và mật khẩu.
- Xác thực người dùng bằng Spring Security.
- Mật khẩu được mã hóa bằng BCrypt.
- Lưu trạng thái đăng nhập bằng Session.
- Phân quyền người dùng theo USER/ADMIN.
- Giới hạn trang quản trị chỉ dành cho ADMIN.
- Hiển thị fullname, email và role của người dùng sau khi đăng nhập.
- Đăng xuất và hủy Session đăng nhập.
- Chuyển đổi dữ liệu User sang DTO bằng MapStruct.
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

Ứng dụng đã thực hiện được luồng đăng nhập bằng email, xác thực và phân quyền người dùng. Thông tin tài khoản được lấy từ cơ sở dữ liệu, hiển thị trên giao diện và phiên đăng nhập được quản lý bằng Spring Security Session.