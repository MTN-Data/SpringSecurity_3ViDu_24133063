package vn.edu.hcmute.uteshop.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.hcmute.uteshop.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
    long countByUserId(Long userId);
    List<Product> findTop4ByOrderByCreatedAtDesc();

    @Query("""
            select p from Product p
            where (:isAdmin = true or p.user.id = :userId)
              and (lower(p.name) like lower(concat('%', :keyword, '%'))
                   or lower(p.description) like lower(concat('%', :keyword, '%')))
            """)
    Page<Product> search(
            @Param("isAdmin") boolean isAdmin,
            @Param("userId") Long userId,
            @Param("keyword") String keyword,
            Pageable pageable
    );
}
