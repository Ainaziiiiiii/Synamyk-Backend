package synamyk.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import synamyk.entities.User;
import synamyk.enums.Role;
import synamyk.repo.UserRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.phone:+996700000000}")
    private String adminPhone;

    @Value("${admin.password:Admin1234!}")
    private String adminPassword;

    @Value("${admin.first-name:Admin}")
    private String adminFirstName;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        initAdmin();
    }

    private void initAdmin() {
        String phone = normalizePhone(adminPhone);

        if (userRepository.existsByPhone(phone)) {
            log.debug("Admin already exists: {}", phone);
            return;
        }

        User admin = User.builder()
                .phone(phone)
                .password(passwordEncoder.encode(adminPassword))
                .firstName(adminFirstName)
                .role(Role.ADMIN)
                .phoneVerified(true)
                .active(true)
                .language("RU")
                .build();

        userRepository.save(admin);
        log.info("Admin account created: {}", phone);
    }

    private String normalizePhone(String phone) {
        String cleaned = phone.replaceAll("[^0-9]", "");
        if (cleaned.startsWith("0")) cleaned = "996" + cleaned.substring(1);
        if (!cleaned.startsWith("996")) cleaned = "996" + cleaned;
        return cleaned;
    }
}
