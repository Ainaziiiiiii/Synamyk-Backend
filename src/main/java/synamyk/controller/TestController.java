package synamyk.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import synamyk.dto.TestDetailResponse;
import synamyk.dto.TestListResponse;
import synamyk.entities.User;
import synamyk.service.TestService;

import java.util.List;

@RestController
@RequestMapping("/api/tests")
@RequiredArgsConstructor
@Tag(name = "Tests", description = "Test listing and details")
public class TestController {

    private final TestService testService;

    @GetMapping
    @Operation(summary = "Get all tests (main page)")
    public ResponseEntity<List<TestListResponse>> getAllTests() {
        return ResponseEntity.ok(testService.getAllTests());
    }

    @GetMapping("/{testId}")
    @Operation(summary = "Get test detail with sub-tests and access info")
    public ResponseEntity<TestDetailResponse> getTestDetail(
            @PathVariable Long testId,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(testService.getTestDetail(testId, user.getId()));
    }
}