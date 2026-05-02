package synamyk.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import synamyk.dto.admin.AdminNewsListResponse;
import synamyk.entities.NewsArticle;
import synamyk.repo.NewsArticleRepository;
import synamyk.service.MinioService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class AdminNewsService {

    private final NewsArticleRepository newsRepo;
    private final MinioService minioService;

    public Page<AdminNewsListResponse> list(
            int page, int size,
            String search,
            String type,
            Boolean active,
            LocalDate dateFrom,
            LocalDate dateTo) {

        LocalDateTime from = dateFrom != null ? dateFrom.atStartOfDay() : null;
        LocalDateTime to = dateTo != null ? dateTo.atTime(LocalTime.MAX) : null;

        return newsRepo.findAllByFilters(
                blank(search), blank(type), active, from, to,
                PageRequest.of(page, size)
        ).map(this::toResponse);
    }

    @Transactional
    public AdminNewsListResponse toggleStatus(Long id) {
        NewsArticle a = newsRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Новость не найдена: " + id));
        a.setActive(!a.getActive());
        return toResponse(newsRepo.save(a));
    }

    private AdminNewsListResponse toResponse(NewsArticle a) {
        return AdminNewsListResponse.builder()
                .id(a.getId())
                .title(a.getTitle())
                .coverImageUrl(minioService.presign(a.getCoverImageUrl()))
                .type(a.getType())
                .viewCount(a.getViewCount())
                .authorName("Админ")
                .publishedAt(a.getPublishedAt())
                .active(a.getActive())
                .build();
    }

    private String blank(String s) {
        return (s != null && !s.isBlank()) ? s.trim() : null;
    }
}
