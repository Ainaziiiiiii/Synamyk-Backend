package synamyk.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import synamyk.dto.admin.AdminRatingEntryResponse;
import synamyk.repo.TestSessionRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminRatingService {

    private final TestSessionRepository sessionRepo;

    public Page<AdminRatingEntryResponse> getRating(
            int page, int size,
            Long testId,
            LocalDate dateFrom,
            LocalDate dateTo) {

        LocalDateTime from = dateFrom != null ? dateFrom.atStartOfDay() : null;
        LocalDateTime to = dateTo != null ? dateTo.atTime(LocalTime.MAX) : null;

        List<Object[]> rows = testId != null
                ? sessionRepo.findRankingByTestId(testId)
                : sessionRepo.findGlobalRanking(from, to);

        List<AdminRatingEntryResponse> all = new ArrayList<>();
        for (int i = 0; i < rows.size(); i++) {
            Object[] r = rows.get(i);
            String firstName = r[1] != null ? r[1].toString() : "";
            String lastName = r[2] != null ? r[2].toString() : "";
            String fullName = (firstName + " " + lastName).trim();
            String phone = r[3] != null ? r[3].toString() : "";
            long points = r[4] != null ? ((Number) r[4]).longValue() : 0;

            all.add(AdminRatingEntryResponse.builder()
                    .userId(((Number) r[0]).longValue())
                    .fullName(fullName.isEmpty() ? phone : fullName)
                    .phone(phone)
                    .avatarUrl(null)
                    .rank(i + 1)
                    .totalPoints(points)
                    .pvpWins(0)
                    .build());
        }

        int start = page * size;
        int end = Math.min(start + size, all.size());
        List<AdminRatingEntryResponse> pageContent = start >= all.size() ? List.of() : all.subList(start, end);
        return new PageImpl<>(pageContent, PageRequest.of(page, size), all.size());
    }

    @Transactional
    public void resetRating() {
        sessionRepo.resetAllEarnedPoints();
        log.info("Admin reset all earned points");
    }
}
