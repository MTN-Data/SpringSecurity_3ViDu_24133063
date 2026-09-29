package vn.edu.hcmute.uteshop.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.hcmute.uteshop.entity.OtpToken;

public interface OtpTokenRepository extends JpaRepository<OtpToken, Long> {
    Optional<OtpToken> findTopByEmailAndTypeOrderByCreatedAtDesc(String email, String type);
    List<OtpToken> findByEmailAndTypeAndUsedFalse(String email, String type);
}
