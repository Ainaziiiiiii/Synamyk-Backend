package synamyk.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import synamyk.dto.admin.AdminVideoListResponse;
import synamyk.entities.VideoLesson;
import synamyk.repo.VideoLessonRepository;

@Service
@RequiredArgsConstructor
public class AdminVideoService {

    private final VideoLessonRepository videoRepo;
    private final MinioService minioService;

    public Page<AdminVideoListResponse> list(
            int page, int size,
            String search,
            Long testId,
            Boolean active) {

        return videoRepo.findAllByFilters(
                blank(search), testId, active,
                PageRequest.of(page, size)
        ).map(this::toResponse);
    }

    @Transactional
    public AdminVideoListResponse toggleStatus(Long id) {
        VideoLesson v = videoRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Видеоурок не найден: " + id));
        v.setActive(!v.getActive());
        return toResponse(videoRepo.save(v));
    }

    private AdminVideoListResponse toResponse(VideoLesson v) {
        return AdminVideoListResponse.builder()
                .id(v.getId())
                .title(v.getTitle())
                .thumbnailUrl(minioService.presign(v.getThumbnailUrl()))
                .subject(v.getTest() != null ? v.getTest().getTitle() : null)
                .duration(formatDuration(v.getDurationSeconds()))
                .viewCount(v.getViewCount())
                .createdAt(v.getCreatedAt())
                .active(v.getActive())
                .testId(v.getTest() != null ? v.getTest().getId() : null)
                .build();
    }

    private String formatDuration(Integer seconds) {
        if (seconds == null || seconds == 0) return null;
        return String.format("%d:%02d", seconds / 60, seconds % 60);
    }

    private String blank(String s) {
        return (s != null && !s.isBlank()) ? s.trim() : null;
    }
}
