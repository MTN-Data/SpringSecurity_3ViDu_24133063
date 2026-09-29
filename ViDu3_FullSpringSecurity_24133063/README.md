# Ví dụ 3 - Full Spring Security

## Nội dung đã thực hiện

Ví dụ 3 xây dựng ứng dụng quản lý người dùng và sản phẩm bằng Spring Boot kết hợp Spring Security.

### Xác thực và tài khoản

- Đăng ký tài khoản mới.
- Gửi mã OTP qua Gmail để xác minh tài khoản.
- Xác nhận OTP và kích hoạt tài khoản.
- Gửi lại OTP đăng ký.
- Đăng nhập bằng username hoặc email.
- Lưu trạng thái đăng nhập bằng Spring Security Session.
- Đăng xuất và hủy Session.
- Quên mật khẩu và gửi OTP đặt lại mật khẩu qua email.
- Xác nhận OTP và đổi mật khẩu mới.
- Mật khẩu và OTP được mã hóa bằng BCrypt.
- Phân quyền USER và ADMIN.
- Hiển thị fullname, email và avatar của người dùng đang đăng nhập.

### Quản lý người dùng

- ADMIN có thể thêm, xem, cập nhật và xóa người dùng.
- Tìm kiếm người dùng theo username, email hoặc fullname.
- Phân trang danh sách người dùng.
- Quản lý role USER/ADMIN và trạng thái tài khoản.
- Đếm tổng số người dùng.
- Hiển thị số lượng sản phẩm của từng người dùng.
- Ngăn ADMIN tự xóa, tự khóa hoặc tự hạ quyền tài khoản đang sử dụng.

### Quản lý sản phẩm

- Thêm, xem, cập nhật và xóa sản phẩm.
- Tìm kiếm sản phẩm theo tên hoặc mô tả.
- Phân trang danh sách sản phẩm.
- Sản phẩm được gắn với tài khoản tạo sản phẩm.
- USER chỉ quản lý sản phẩm của mình; ADMIN có thể quản lý toàn bộ sản phẩm.
- Đếm tổng số sản phẩm và số sản phẩm của từng người dùng.
- Upload ảnh sản phẩm JPG/PNG/WEBP lên Cloudinary.
- Giới hạn dung lượng ảnh upload tối đa 5 MB.
- Lưu và hiển thị URL ảnh Cloudinary trên danh sách sản phẩm.

## Công nghệ sử dụng

- Java 17
- Spring Boot 4.1.1
- Spring Security
- Spring Data JPA
- Thymeleaf
- MapStruct 1.6.3
- SQL Server
- Spring Mail
- Cloudinary
- BCrypt
- Maven

## Kết quả

Ứng dụng đã triển khai các chức năng đăng ký, xác thực OTP, đăng nhập, quên mật khẩu, phân quyền, quản lý người dùng và quản lý sản phẩm.

OTP đã được gửi qua Gmail và chức năng upload ảnh sản phẩm lên Cloudinary đã hoạt động, ảnh được hiển thị lại trên danh sách sản phẩm.