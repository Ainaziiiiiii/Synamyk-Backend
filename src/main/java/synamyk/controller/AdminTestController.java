package synamyk.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import synamyk.dto.admin.*;
import synamyk.service.AdminTestService;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin - Tests", description = "Admin endpoints for managing tests, sub-tests, questions")
public class AdminTestController {

    private final AdminTestService adminTestService;

    // ===== TESTS =====

    @GetMapping("/tests")
    @Operation(summary = "Get all tests with sub-tests")
    public ResponseEntity<List<AdminTestResponse>> getAllTests() {
        return ResponseEntity.ok(adminTestService.getAllTests());
    }

    @GetMapping("/tests/{testId}")
    @Operation(summary = "Get single test")
    public ResponseEntity<AdminTestResponse> getTest(@PathVariable Long testId) {
        return ResponseEntity.ok(adminTestService.getTest(testId));
    }

    @PostMapping("/tests")
    @Operation(summary = "Create new test")
    public ResponseEntity<AdminTestResponse> createTest(@Valid @RequestBody CreateTestRequest request) {
        return ResponseEntity.ok(adminTestService.createTest(request));
    }

    @PutMapping("/tests/{testId}")
    @Operation(summary = "Update test")
    public ResponseEntity<AdminTestResponse> updateTest(
            @PathVariable Long testId,
            @Valid @RequestBody CreateTestRequest request) {
        return ResponseEntity.ok(adminTestService.updateTest(testId, request));
    }

    @DeleteMapping("/tests/{testId}")
    @Operation(summary = "Deactivate test")
    public ResponseEntity<Void> deleteTest(@PathVariable Long testId) {
        adminTestService.deleteTest(testId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Admin selects which sub-tests are paid and sets the test price.
     * All other sub-tests of this test will be free.
     */
    @PutMapping("/tests/{testId}/pricing")
    @Operation(summary = "Set paid sub-tests and price for a test")
    public ResponseEntity<AdminTestResponse> updateTestPricing(
            @PathVariable Long testId,
            @Valid @RequestBody UpdateTestPricingRequest request) {
        return ResponseEntity.ok(adminTestService.updateTestPricing(testId, request));
    }

    // ===== SUB-TESTS =====

    @PostMapping("/tests/{testId}/sub-tests")
    @Operation(summary = "Add sub-test to a test")
    public ResponseEntity<AdminTestResponse.AdminSubTestResponse> createSubTest(
            @PathVariable Long testId,
            @Valid @RequestBody CreateSubTestRequest request) {
        return ResponseEntity.ok(adminTestService.createSubTest(testId, request));
    }

    @PutMapping("/sub-tests/{subTestId}")
    @Operation(summary = "Update sub-test")
    public ResponseEntity<AdminTestResponse.AdminSubTestResponse> updateSubTest(
            @PathVariable Long subTestId,
            @Valid @RequestBody CreateSubTestRequest request) {
        return ResponseEntity.ok(adminTestService.updateSubTest(subTestId, request));
    }

    @DeleteMapping("/sub-tests/{subTestId}")
    @Operation(summary = "Deactivate sub-test")
    public ResponseEntity<Void> deleteSubTest(@PathVariable Long subTestId) {
        adminTestService.deleteSubTest(subTestId);
        return ResponseEntity.noContent().build();
    }

    // ===== QUESTIONS =====

    @GetMapping("/sub-tests/{subTestId}/questions")
    @Operation(summary = "Get all questions for a sub-test")
    public ResponseEntity<List<AdminQuestionResponse>> getQuestions(@PathVariable Long subTestId) {
        return ResponseEntity.ok(adminTestService.getQuestions(subTestId));
    }

    @PostMapping("/sub-tests/{subTestId}/questions")
    @Operation(summary = "Add question to sub-test")
    public ResponseEntity<AdminQuestionResponse> createQuestion(
            @PathVariable Long subTestId,
            @Valid @RequestBody CreateQuestionRequest request) {
        return ResponseEntity.ok(adminTestService.createQuestion(subTestId, request));
    }

    @PutMapping("/questions/{questionId}")
    @Operation(summary = "Update question")
    public ResponseEntity<AdminQuestionResponse> updateQuestion(
            @PathVariable Long questionId,
            @Valid @RequestBody CreateQuestionRequest request) {
        return ResponseEntity.ok(adminTestService.updateQuestion(questionId, request));
    }

    @DeleteMapping("/questions/{questionId}")
    @Operation(summary = "Deactivate question")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long questionId) {
        adminTestService.deleteQuestion(questionId);
        return ResponseEntity.noContent().build();
    }
}