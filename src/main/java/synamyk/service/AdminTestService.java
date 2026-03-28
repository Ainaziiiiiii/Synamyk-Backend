package synamyk.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import synamyk.dto.admin.*;
import synamyk.entities.*;
import synamyk.repo.*;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminTestService {

    private final TestRepository testRepository;
    private final SubTestRepository subTestRepository;
    private final QuestionRepository questionRepository;
    private final AnswerOptionRepository optionRepository;

    // ===== TESTS =====

    public List<AdminTestResponse> getAllTests() {
        return testRepository.findAll().stream()
                .map(this::toAdminTestResponse)
                .toList();
    }

    public AdminTestResponse getTest(Long testId) {
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test not found"));
        return toAdminTestResponse(test);
    }

    @Transactional
    public AdminTestResponse createTest(CreateTestRequest request) {
        Test test = Test.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .iconUrl(request.getIconUrl())
                .price(request.getPrice())
                .active(true)
                .build();
        return toAdminTestResponse(testRepository.save(test));
    }

    @Transactional
    public AdminTestResponse updateTest(Long testId, CreateTestRequest request) {
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test not found"));
        test.setTitle(request.getTitle());
        test.setDescription(request.getDescription());
        test.setIconUrl(request.getIconUrl());
        test.setPrice(request.getPrice());
        return toAdminTestResponse(testRepository.save(test));
    }

    @Transactional
    public void deleteTest(Long testId) {
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test not found"));
        test.setActive(false);
        testRepository.save(test);
    }

    /**
     * Admin sets which sub-tests are paid and the price for the whole test.
     * Sub-tests NOT in paidSubTestIds will be marked as free.
     */
    @Transactional
    public AdminTestResponse updateTestPricing(Long testId, UpdateTestPricingRequest request) {
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test not found"));

        test.setPrice(request.getPrice());
        testRepository.save(test);

        List<SubTest> subTests = subTestRepository.findByTestIdOrderByLevelOrderAsc(testId);
        for (SubTest st : subTests) {
            st.setIsPaid(request.getPaidSubTestIds().contains(st.getId()));
            subTestRepository.save(st);
        }

        log.info("Updated pricing for testId={}: price={}, paidSubTests={}",
                testId, request.getPrice(), request.getPaidSubTestIds());

        return toAdminTestResponse(test);
    }

    // ===== SUB-TESTS =====

    @Transactional
    public AdminTestResponse.AdminSubTestResponse createSubTest(Long testId, CreateSubTestRequest request) {
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test not found"));

        SubTest subTest = SubTest.builder()
                .test(test)
                .title(request.getTitle())
                .levelName(request.getLevelName())
                .levelOrder(request.getLevelOrder())
                .isPaid(request.getIsPaid() != null && request.getIsPaid())
                .durationMinutes(request.getDurationMinutes())
                .active(true)
                .build();

        subTest = subTestRepository.save(subTest);
        return toAdminSubTestResponse(subTest);
    }

    @Transactional
    public AdminTestResponse.AdminSubTestResponse updateSubTest(Long subTestId, CreateSubTestRequest request) {
        SubTest subTest = subTestRepository.findById(subTestId)
                .orElseThrow(() -> new RuntimeException("SubTest not found"));

        subTest.setTitle(request.getTitle());
        subTest.setLevelName(request.getLevelName());
        subTest.setLevelOrder(request.getLevelOrder());
        subTest.setIsPaid(request.getIsPaid() != null && request.getIsPaid());
        subTest.setDurationMinutes(request.getDurationMinutes());

        return toAdminSubTestResponse(subTestRepository.save(subTest));
    }

    @Transactional
    public void deleteSubTest(Long subTestId) {
        SubTest subTest = subTestRepository.findById(subTestId)
                .orElseThrow(() -> new RuntimeException("SubTest not found"));
        subTest.setActive(false);
        subTestRepository.save(subTest);
    }

    // ===== QUESTIONS =====

    public List<AdminQuestionResponse> getQuestions(Long subTestId) {
        return questionRepository.findBySubTestIdOrderByOrderIndexAsc(subTestId).stream()
                .map(this::toAdminQuestionResponse)
                .toList();
    }

    @Transactional
    public AdminQuestionResponse createQuestion(Long subTestId, CreateQuestionRequest request) {
        SubTest subTest = subTestRepository.findById(subTestId)
                .orElseThrow(() -> new RuntimeException("SubTest not found"));

        boolean hasCorrect = request.getOptions().stream()
                .anyMatch(o -> Boolean.TRUE.equals(o.getIsCorrect()));
        if (!hasCorrect) {
            throw new RuntimeException("At least one option must be marked as correct.");
        }

        Question question = Question.builder()
                .subTest(subTest)
                .text(request.getText())
                .sectionName(request.getSectionName())
                .imageUrl(request.getImageUrl())
                .explanation(request.getExplanation())
                .orderIndex(request.getOrderIndex())
                .pointValue(request.getPointValue())
                .active(true)
                .build();

        question = questionRepository.save(question);

        int optIndex = 0;
        for (CreateQuestionRequest.AnswerOptionRequest optReq : request.getOptions()) {
            AnswerOption option = AnswerOption.builder()
                    .question(question)
                    .label(optReq.getLabel())
                    .text(optReq.getText())
                    .isCorrect(Boolean.TRUE.equals(optReq.getIsCorrect()))
                    .orderIndex(optIndex++)
                    .build();
            optionRepository.save(option);
        }

        return toAdminQuestionResponse(questionRepository.findById(question.getId()).orElseThrow());
    }

    @Transactional
    public AdminQuestionResponse updateQuestion(Long questionId, CreateQuestionRequest request) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        question.setText(request.getText());
        question.setSectionName(request.getSectionName());
        question.setImageUrl(request.getImageUrl());
        question.setExplanation(request.getExplanation());
        question.setOrderIndex(request.getOrderIndex());
        question.setPointValue(request.getPointValue());

        // Replace options
        optionRepository.deleteAll(
                optionRepository.findByQuestionIdOrderByOrderIndexAsc(questionId));

        int optIndex = 0;
        for (CreateQuestionRequest.AnswerOptionRequest optReq : request.getOptions()) {
            AnswerOption option = AnswerOption.builder()
                    .question(question)
                    .label(optReq.getLabel())
                    .text(optReq.getText())
                    .isCorrect(Boolean.TRUE.equals(optReq.getIsCorrect()))
                    .orderIndex(optIndex++)
                    .build();
            optionRepository.save(option);
        }

        return toAdminQuestionResponse(questionRepository.findById(questionId).orElseThrow());
    }

    @Transactional
    public void deleteQuestion(Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));
        question.setActive(false);
        questionRepository.save(question);
    }

    // ===== MAPPERS =====

    private AdminTestResponse toAdminTestResponse(Test test) {
        List<SubTest> subTests = subTestRepository.findByTestIdOrderByLevelOrderAsc(test.getId());
        return AdminTestResponse.builder()
                .id(test.getId())
                .title(test.getTitle())
                .description(test.getDescription())
                .iconUrl(test.getIconUrl())
                .price(test.getPrice())
                .active(test.getActive())
                .subTests(subTests.stream().map(this::toAdminSubTestResponse).toList())
                .build();
    }

    private AdminTestResponse.AdminSubTestResponse toAdminSubTestResponse(SubTest st) {
        return AdminTestResponse.AdminSubTestResponse.builder()
                .id(st.getId())
                .title(st.getTitle())
                .levelName(st.getLevelName())
                .levelOrder(st.getLevelOrder())
                .isPaid(st.getIsPaid())
                .durationMinutes(st.getDurationMinutes())
                .questionCount(questionRepository.countBySubTestIdAndActiveTrue(st.getId()))
                .active(st.getActive())
                .build();
    }

    private AdminQuestionResponse toAdminQuestionResponse(Question q) {
        List<AdminQuestionResponse.OptionResponse> options = optionRepository
                .findByQuestionIdOrderByOrderIndexAsc(q.getId()).stream()
                .map(o -> AdminQuestionResponse.OptionResponse.builder()
                        .id(o.getId())
                        .label(o.getLabel())
                        .text(o.getText())
                        .isCorrect(o.getIsCorrect())
                        .orderIndex(o.getOrderIndex())
                        .build())
                .toList();

        return AdminQuestionResponse.builder()
                .id(q.getId())
                .sectionName(q.getSectionName())
                .text(q.getText())
                .imageUrl(q.getImageUrl())
                .explanation(q.getExplanation())
                .orderIndex(q.getOrderIndex())
                .pointValue(q.getPointValue())
                .active(q.getActive())
                .options(options)
                .build();
    }
}