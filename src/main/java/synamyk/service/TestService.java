package synamyk.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import synamyk.dto.SubTestResponse;
import synamyk.dto.TestDetailResponse;
import synamyk.dto.TestListResponse;
import synamyk.entities.Test;
import synamyk.repo.QuestionRepository;
import synamyk.repo.SubTestRepository;
import synamyk.repo.TestRepository;
import synamyk.repo.TestSessionRepository;
import synamyk.repo.UserTestAccessRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TestService {

    private final TestRepository testRepository;
    private final SubTestRepository subTestRepository;
    private final QuestionRepository questionRepository;
    private final UserTestAccessRepository accessRepository;
    private final TestSessionRepository sessionRepository;

    public List<TestListResponse> getAllTests() {
        return testRepository.findByActiveTrueOrderByCreatedAtAsc().stream()
                .map(t -> TestListResponse.builder()
                        .id(t.getId())
                        .title(t.getTitle())
                        .description(t.getDescription())
                        .iconUrl(t.getIconUrl())
                        .price(t.getPrice())
                        .subTestCount(subTestRepository
                                .findByTestIdAndActiveTrueOrderByLevelOrderAsc(t.getId()).size())
                        .build())
                .toList();
    }

    public TestDetailResponse getTestDetail(Long testId, Long userId) {
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test not found"));

        boolean hasAccess = accessRepository.existsByUserIdAndTestId(userId, testId);

        List<SubTestResponse> subTests = subTestRepository
                .findByTestIdAndActiveTrueOrderByLevelOrderAsc(testId)
                .stream()
                .map(st -> {
                    long questionCount = questionRepository.countBySubTestIdAndActiveTrue(st.getId());
                    boolean subTestAccess = !st.getIsPaid() || hasAccess;

                    boolean hasCompleted = !sessionRepository
                            .findByUserIdAndSubTestIdOrderByCreatedAtDesc(userId, st.getId())
                            .stream()
                            .filter(s -> s.getStatus() == synamyk.entities.TestSession.SessionStatus.COMPLETED)
                            .toList()
                            .isEmpty();

                    return SubTestResponse.builder()
                            .id(st.getId())
                            .title(st.getTitle())
                            .levelName(st.getLevelName())
                            .levelOrder(st.getLevelOrder())
                            .isPaid(st.getIsPaid())
                            .durationMinutes(st.getDurationMinutes())
                            .questionCount(questionCount)
                            .hasAccess(subTestAccess)
                            .hasCompleted(hasCompleted)
                            .build();
                })
                .toList();

        return TestDetailResponse.builder()
                .id(test.getId())
                .title(test.getTitle())
                .description(test.getDescription())
                .price(test.getPrice())
                .hasPaidAccess(hasAccess)
                .subTests(subTests)
                .build();
    }
}