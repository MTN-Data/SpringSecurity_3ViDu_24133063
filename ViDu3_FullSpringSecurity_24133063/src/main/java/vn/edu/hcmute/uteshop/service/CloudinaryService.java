package vn.edu.hcmute.uteshop.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import java.io.IOException;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CloudinaryService {
    private final String cloudName;
    private final String apiKey;
    private final String apiSecret;

    public CloudinaryService(
            @Value("${app.cloudinary.cloud-name:}") String cloudName,
            @Value("${app.cloudinary.api-key:}") String apiKey,
            @Value("${app.cloudinary.api-secret:}") String apiSecret
    ) {
        this.cloudName = cloudName;
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
    }

    public String upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        String contentType = file.getContentType();
        if (!"image/jpeg".equals(contentType)
                && !"image/png".equals(contentType)
                && !"image/webp".equals(contentType)) {
            throw new IllegalArgumentException("Chỉ chấp nhận ảnh JPG, PNG hoặc WEBP.");
        }
        if (file.getSize() > 5L * 1024 * 1024) {
            throw new IllegalArgumentException("Ảnh không được vượt quá 5 MB.");
        }
        if (cloudName.isBlank() || apiKey.isBlank() || apiSecret.isBlank()) {
            throw new IllegalArgumentException("Chưa cấu hình Cloudinary. Có thể nhập URL ảnh để thử trên H2.");
        }
        Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true
        ));
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> result = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap("folder", "uteshop/products", "resource_type", "image")
            );
            return result.get("secure_url").toString();
        } catch (IOException exception) {
            throw new IllegalArgumentException("Không tải được ảnh lên Cloudinary.", exception);
        }
    }
}
