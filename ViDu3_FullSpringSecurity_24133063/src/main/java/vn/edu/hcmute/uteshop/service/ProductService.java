package vn.edu.hcmute.uteshop.service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.hcmute.uteshop.dto.ProductDTO;
import vn.edu.hcmute.uteshop.entity.Product;
import vn.edu.hcmute.uteshop.entity.User;
import vn.edu.hcmute.uteshop.mapper.ProductMapper;
import vn.edu.hcmute.uteshop.repository.ProductRepository;
import vn.edu.hcmute.uteshop.repository.UserRepository;

@Service
public class ProductService {
    private final ProductRepository products;
    private final UserRepository users;
    private final ProductMapper mapper;
    private final CloudinaryService cloudinary;

    public ProductService(
            ProductRepository products,
            UserRepository users,
            ProductMapper mapper,
            CloudinaryService cloudinary
    ) {
        this.products = products;
        this.users = users;
        this.mapper = mapper;
        this.cloudinary = cloudinary;
    }

    @Transactional(readOnly = true)
    public Page<ProductDTO> search(String keyword, int page, Long userId, boolean isAdmin) {
        return products.search(
                isAdmin,
                userId,
                keyword == null ? "" : keyword.trim(),
                PageRequest.of(Math.max(0, page), 6, Sort.by(Sort.Direction.DESC, "createdAt"))
        ).map(mapper::toDTO);
    }

    @Transactional(readOnly = true)
    public List<ProductDTO> recent() {
        return products.findTop4ByOrderByCreatedAtDesc()
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductDTO findForEditing(Long id, Long actingUserId, boolean isAdmin) {
        return mapper.toDTO(getAuthorized(id, actingUserId, isAdmin));
    }

    @Transactional
    public void create(ProductDTO dto, MultipartFile image, Long ownerId) {
        User user = users.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy chủ sản phẩm."));
        String uploadedUrl = cloudinary.upload(image);
        Product product = mapper.toEntity(dto);
        product.setUser(user);
        if (uploadedUrl != null) {
            product.setImageUrl(uploadedUrl);
        }
        products.save(product);
    }

    @Transactional
    public void update(
            Long id,
            ProductDTO dto,
            MultipartFile image,
            Long actingUserId,
            boolean isAdmin
    ) {
        Product product = getAuthorized(id, actingUserId, isAdmin);
        String uploadedUrl = cloudinary.upload(image);
        mapper.update(dto, product);
        if (uploadedUrl != null) {
            product.setImageUrl(uploadedUrl);
        }
    }

    @Transactional
    public void delete(Long id, Long actingUserId, boolean isAdmin) {
        products.delete(getAuthorized(id, actingUserId, isAdmin));
    }

    @Transactional(readOnly = true)
    public long countAll() {
        return products.count();
    }

    @Transactional(readOnly = true)
    public long countFor(Long userId) {
        return products.countByUserId(userId);
    }

    private Product getAuthorized(Long id, Long actingUserId, boolean isAdmin) {
        Product product = products.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm."));
        if (!isAdmin && !product.getUser().getId().equals(actingUserId)) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "Bạn không có quyền chỉnh sửa hoặc xóa sản phẩm của người khác."
            );
        }
        return product;
    }
}
