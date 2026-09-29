package vn.edu.hcmute.uteshop.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import vn.edu.hcmute.uteshop.entity.Product;
import vn.edu.hcmute.uteshop.entity.User;
import vn.edu.hcmute.uteshop.mapper.ProductMapper;
import vn.edu.hcmute.uteshop.repository.ProductRepository;
import vn.edu.hcmute.uteshop.repository.UserRepository;

class ProductServiceTest {
    @Test
    void deniesAccessToAnotherUsersProduct() {
        ProductRepository products = mock(ProductRepository.class);
        Product product = new Product();
        User owner = new User();
        owner.setId(10L);
        product.setUser(owner);
        when(products.findById(1L)).thenReturn(Optional.of(product));
        ProductService service = new ProductService(
                products,
                mock(UserRepository.class),
                mock(ProductMapper.class),
                mock(CloudinaryService.class)
        );

        assertThrows(
                AccessDeniedException.class,
                () -> service.findForEditing(1L, 20L, false)
        );
    }
}
