package synamyk.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import synamyk.dto.admin.AdminUserResponse;
import synamyk.dto.admin.AdminUserUpdateRequest;
import synamyk.entities.User;
import synamyk.enums.Role;
import synamyk.repo.TestSessionRepository;
import synamyk.repo.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserRepository userRepo;
    private final TestSessionRepository sessionRepo;
    private final MinioService minioService;

    public Page<AdminUserResponse> list(
            int page, int size,
            String search,
            Boolean active,
            String roleStr,
            LocalDate dateFrom,
            LocalDate dateTo) {

        Role role = null;
        if (roleStr != null && !roleStr.isBlank()) {
            try { role = Role.valueOf(roleStr.toUpperCase()); } catch (IllegalArgumentException ignored) {}
        }

        LocalDateTime from = dateFrom != null ? dateFrom.atStartOfDay() : null;
        LocalDateTime to = dateTo != null ? dateTo.atTime(LocalTime.MAX) : null;

        return userRepo.findAllByFilters(
                blank(search), active, role, from, to,
                PageRequest.of(page, size)
        ).map(this::toResponse);
    }

    public AdminUserResponse getUser(Long id) {
        return toResponse(find(id));
    }

    @Transactional
    public AdminUserResponse updateUser(Long id, AdminUserUpdateRequest req) {
        User u = find(id);
        if (req.getFirstName() != null) u.setFirstName(req.getFirstName());
        if (req.getLastName() != null) u.setLastName(req.getLastName());
        if (req.getEmail() != null) u.setEmail(req.getEmail());
        if (req.getActive() != null) u.setActive(req.getActive());
        if (req.getRole() != null) {
            try { u.setRole(Role.valueOf(req.getRole().toUpperCase())); }
            catch (IllegalArgumentException ignored) {}
        }
        log.info("Admin updated user id={}", id);
        return toResponse(userRepo.save(u));
    }

    public byte[] exportCsv(String search, Boolean active, String roleStr, LocalDate dateFrom, LocalDate dateTo) {
        Role role = parseRole(roleStr);
        LocalDateTime from = dateFrom != null ? dateFrom.atStartOfDay() : null;
        LocalDateTime to = dateTo != null ? dateTo.atTime(LocalTime.MAX) : null;

        List<User> users = userRepo.findAllByFilters(
                blank(search), active, role, from, to,
                org.springframework.data.domain.PageRequest.of(0, Integer.MAX_VALUE)
        ).getContent();

        StringBuilder sb = new StringBuilder();
        sb.append("ID,Имя,Телефон,Email,Регион,Роль,Активен,Верифицирован,Баллы,Дата регистрации\n");
        for (User u : users) {
            AdminUserResponse r = toResponse(u);
            sb.append(r.getId()).append(',')
              .append(csv(r.getFullName())).append(',')
              .append(csv(r.getPhone())).append(',')
              .append(csv(r.getEmail())).append(',')
              .append(csv(r.getRegionName())).append(',')
              .append(r.getRole()).append(',')
              .append(r.getActive()).append(',')
              .append(r.getPhoneVerified()).append(',')
              .append(r.getTotalScore()).append(',')
              .append(r.getRegisteredAt()).append('\n');
        }
        return sb.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }

    @Transactional
    public void deleteUser(Long id) {
        User u = find(id);
        u.setActive(false);
        userRepo.save(u);
        log.info("Admin soft-deleted user id={}", id);
    }

    private AdminUserResponse toResponse(User u) {
        long score = sessionRepo.sumCorrectAnswersByUserId(u.getId());
        String fullName = ((u.getFirstName() != null ? u.getFirstName() : "")
                + " " + (u.getLastName() != null ? u.getLastName() : "")).trim();
        return AdminUserResponse.builder()
                .id(u.getId())
                .fullName(fullName.isEmpty() ? u.getPhone() : fullName)
                .phone(u.getPhone())
                .email(u.getEmail())
                .avatarUrl(minioService.presign(u.getAvatarUrl()))
                .regionName(u.getRegion() != null ? u.getRegion().getName() : null)
                .role(u.getRole().name())
                .active(u.getActive())
                .phoneVerified(u.getPhoneVerified())
                .registeredAt(u.getCreatedAt())
                .totalScore(score)
                .build();
    }

    private User find(Long id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден: " + id));
    }

    private String blank(String s) {
        return (s != null && !s.isBlank()) ? s.trim() : null;
    }

    private Role parseRole(String roleStr) {
        if (roleStr == null || roleStr.isBlank()) return null;
        try { return Role.valueOf(roleStr.toUpperCase()); } catch (IllegalArgumentException e) { return null; }
    }

    private String csv(String v) {
        if (v == null) return "";
        return (v.contains(",") || v.contains("\"")) ? "\"" + v.replace("\"", "\"\"") + "\"" : v;
    }
}
