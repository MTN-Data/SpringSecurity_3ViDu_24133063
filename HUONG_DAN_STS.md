# Mở và chạy 3 ví dụ trên Spring Tool Suite

## 1. Cấu hình SQL Server (làm một lần)

- Server: `localhost,14330` trong SSMS (SSMS dùng dấu phẩy).
- Authentication: SQL Server Authentication. Login: `sa`.
- Chạy toàn bộ `database/create_all_databases.sql`. Script chỉ tạo database chưa tồn tại, không xóa dữ liệu.
- Kết quả có `UTEShop1`, `UTEShop2`, `UTEShop3`. Hibernate tự tạo các bảng khi ứng dụng khởi động.
- Trong MỖI project ViDu, copy `.env.example` thành `.env` cạnh `pom.xml`.
- Mở `.env`, thay `YOUR_SQL_SERVER_PASSWORD` bằng mật khẩu SQL Server của bạn. Không phải mật khẩu đăng nhập web.
- File `.env` dùng cú pháp Java properties: không bọc giá trị bằng dấu nháy; nếu mật khẩu có dấu gạch chéo ngược, viết hai dấu `\\`.
- Có thể dùng Run Configurations > Environment > Add > `DB_PASSWORD` thay cho `.env`.

## 2. Import vào STS

1. Giải nén ZIP ra một thư mục thật, ví dụ `D:\Java\SpringSecurity_3ViDu_24133063`.
2. File > Import > Maven > Existing Maven Projects.
3. Browse đến thư mục vừa giải nén. Chọn cả 3 project ViDu; project ngoài cùng là bộ tổng hợp Maven.
4. Finish, chờ tải thư viện. Cần Internet lần đầu.
5. Window > Preferences > Java > Installed JREs: chọn **JDK 17 trở lên** (có thể dùng JDK 21/26; cấu hình biên dịch là Java 17). Kiểm tra project cũng dùng JDK này.
6. Chuột phải từng project > Maven > Update Project (Alt+F5).
7. Nếu STS báo thiếu `UserMapperImpl`, chạy Run As > Maven build... với Goals `clean compile`, rồi Refresh (F5). MapStruct tạo mã trong `target/generated-sources/annotations`; không tự viết lớp này.
8. Chuột phải từng project ViDu > Run As > Spring Boot App, hoặc mở `Application.java` và chạy.
9. Run Configurations > Arguments > Working directory phải là thư mục project ViDu đang chạy để tìm đúng `.env`.

Mặc định đã chọn profile `sqlserver`. Không phải sửa port/username trong Java.

| Ví dụ | Địa chỉ | Database | Nội dung |
|---|---|---|---|
| 1 | http://localhost:8080 | UTEShop1 | Login email, header người dùng, Thymeleaf fragments không Layout Dialect |
| 2 | http://localhost:8081 | UTEShop2 | Custom login username/email, họ tên và ảnh trên header |
| 3 | http://localhost:8082 | UTEShop3 | Đăng ký, OTP, quên mật khẩu, CRUD User/Product, tìm kiếm, phân trang, Cloudinary |

## 3. Tài khoản web được tạo sẵn

| Vai trò | Email | Username (ví dụ 2, 3) | Mật khẩu |
|---|---|---|---|
| ADMIN | admin@uteshop.vn | admin | Admin@12345 |
| USER | user@uteshop.vn | tien | Demo@12345 |

Đây là dữ liệu học tập với `DEMO_SEED=true`. Dữ liệu đã tồn tại sẽ không bị reset mật khẩu khi khởi động lại.
Ví dụ 1 nhập email, không nhập username. `sa` chỉ là tài khoản kết nối SQL Server.

## 4. Chạy test

Trong STS: chuột phải project > Run As > Maven test, hoặc Maven build với Goals `clean verify`.
Từ thư mục gốc trên Windows: `mvnw.cmd clean verify`. Test tự động dùng profile `test` và H2 in-memory riêng; không cần SQL Server đang bật, không thay đổi dữ liệu SQL của bạn.

Muốn mở web để thử khi SQL Server chưa sẵn sàng: Run Configurations > Arguments > Program arguments nhập `--spring.profiles.active=demo`. Bỏ đối số này để trở về SQL Server.

## 5. Test thủ công để nộp bài

1. Mở trang login, nhập sai mật khẩu: hiển thị lỗi. Đăng nhập đúng: tới dashboard, thấy tên và ảnh header.
2. Đăng nhập USER, thử `/admin` (ví dụ 1, 2) hoặc `/users` (ví dụ 3): bị từ chối. ADMIN truy cập được.
3. Đăng xuất: session bị hủy, vào `/dashboard` phải đăng nhập lại.
4. Ví dụ 3: đăng ký email mới, chưa OTP thì chưa đăng nhập được. Xác nhận OTP rồi đăng nhập.
5. Thử OTP sai; mã đúng chỉ dùng một lần. Gửi lại phải đợi 60 giây. Mã hết hạn sau 5 phút.
6. Quên mật khẩu, nhập OTP, đặt mật khẩu mới và đăng nhập lại.
7. ADMIN: thêm/sửa/xóa User; thử trùng email/username; tìm kiếm; tạo hơn 8 user để thấy phân trang.
8. USER: thêm/sửa/xóa Product của mình, tìm tên/mô tả; tạo hơn 6 sản phẩm để thấy phân trang. Không sửa/xóa sản phẩm của người khác. ADMIN quản lý được tất cả.
9. Xem số lượng User/Product ở dashboard và số Product của User.
10. Cấu hình dịch vụ ở mục 6 rồi thử gửi email thật và tải ảnh lên Cloudinary.

## 6. Email và Cloudinary (ví dụ 3)

Mặc định là chế độ học tập: `MAIL_ENABLED=false`, `DEMO_SHOW_OTP=true`. OTP được hiển thị sau khi đăng ký/quên mật khẩu, **chưa gửi email thật**. Database vẫn là SQL Server.

Để đúng luồng gửi OTP qua email của đề, điền `.env` ví dụ 3 rồi khởi động lại:

```properties
MAIL_ENABLED=true
DEMO_SHOW_OTP=false
MAIL_USERNAME=dia_chi_gmail_cua_ban
MAIL_PASSWORD=mat_khau_ung_dung_gmail
CLOUDINARY_CLOUD_NAME=ten_cloud
CLOUDINARY_API_KEY=api_key
CLOUDINARY_API_SECRET=api_secret
```

Nếu dùng SMTP khác Gmail, thêm `MAIL_HOST` và `MAIL_PORT` phù hợp. Chưa cấu hình Cloudinary vẫn CRUD được bằng URL ảnh hoặc không ảnh; chức năng tải file ảnh lên Cloudinary cần ba thông tin trên. Không giả lập việc tải ảnh thành công.

## 7. Lỗi thường gặp

- `Login failed for user sa`: kiểm tra mật khẩu, chế độ SQL authentication và tài khoản sa.
- `Connection refused`/timeout: SQL Server chưa chạy hoặc chưa lắng nghe TCP 14330. Trong SSMS thử `localhost,14330` trước.
- `Cannot open database`: chạy script tạo database.
- `Could not resolve placeholder DB_PASSWORD`: chưa tạo `.env`, sai Working directory hoặc chưa đặt biến môi trường.
- Port 8080/8081/8082 đã dùng: dừng ứng dụng cũ trong Boot Dashboard; hoặc đặt `PORT=8090` trong `.env` project tương ứng.
- Không tải dependency: kiểm tra Internet/proxy Maven của STS, rồi Maven > Update Project > Force Update.
- Không thấy bảng trong SSMS: Refresh đúng database sau khi Console hiện `Started Application`.

## 8. Đưa lên GitHub

Ảnh đề yêu cầu nộp link GitHub. Tạo repository và upload mã nguồn sau khi test trên máy.
Không upload `.env`, `target`, `data` hoặc thông tin SMTP/Cloudinary. Các project đã có `.gitignore`.
Mã 24133063 được giữ nguyên từ project bạn gửi; nếu đó không phải MSSV của bạn, đổi tên thư mục, artifactId/name trong pom và thông tin giao diện trước khi nộp.
