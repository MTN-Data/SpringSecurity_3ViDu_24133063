package vn.edu.hcmute.uteshop.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import vn.edu.hcmute.uteshop.entity.OtpToken;
import vn.edu.hcmute.uteshop.repository.OtpTokenRepository;

class OtpServiceTest {
    private final OtpTokenRepository repository = mock(OtpTokenRepository.class);
    private final MailService mail = mock(MailService.class);
    private final PasswordEncoder encoder = new BCryptPasswordEncoder();
    private final OtpService service = new OtpService(repository, encoder, mail);

    @Test
    void issuesSixDigitCodeInDemoMode() {
        when(repository.findByEmailAndTypeAndUsedFalse("test@example.com", OtpService.REGISTER))
                .thenReturn(List.of());
        when(repository.save(any(OtpToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(mail.sendOtp(eq("test@example.com"), any(), any()))
                .thenAnswer(invocation -> invocation.getArgument(1));

        String code = service.issue("TEST@example.com", OtpService.REGISTER);
        assertTrue(code.matches("[0-9]{6}"));
    }

    @Test
    void consumesCodeExactlyOnce() {
        OtpToken token = new OtpToken();
        token.setEmail("test@example.com");
        token.setType(OtpService.RESET);
        token.setOtpHash(encoder.encode("112233"));
        token.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        when(repository.findTopByEmailAndTypeOrderByCreatedAtDesc(
                "test@example.com", OtpService.RESET
        )).thenReturn(Optional.of(token));

        assertTrue(service.consume("test@example.com", OtpService.RESET, "112233"));
        assertTrue(token.isUsed());
        assertFalse(service.consume("test@example.com", OtpService.RESET, "112233"));
    }

    @Test
    void locksTokenAfterFiveIncorrectAttempts() {
        OtpToken token = new OtpToken();
        token.setEmail("test@example.com");
        token.setType(OtpService.REGISTER);
        token.setOtpHash(encoder.encode("112233"));
        token.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        when(repository.findTopByEmailAndTypeOrderByCreatedAtDesc(
                "test@example.com", OtpService.REGISTER
        )).thenReturn(Optional.of(token));

        for (int i = 0; i < 5; i++) {
            assertFalse(service.consume("test@example.com", OtpService.REGISTER, "999999"));
        }
        assertEquals(5, token.getAttempts());
        assertFalse(service.consume("test@example.com", OtpService.REGISTER, "112233"));
    }
}
