package synamyk.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import synamyk.dto.*;
import synamyk.entities.OTPCode;
import synamyk.service.AuthService;
import synamyk.service.SmsProService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Auth endpoints: registration, login, OTP, password reset")
public class AuthController {

    private final AuthService authService;
    private final SmsProService smsProService;

    /**
     * Step 1: Register with phone + password. Sends OTP to phone.
     */
    @PostMapping("/register")
    @Operation(summary = "Register new user (step 1: phone + password)")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    /**
     * Step 2: Verify OTP received via SMS.
     * Use type=REGISTRATION for registration flow.
     */
    @PostMapping("/verify-otp")
    @Operation(summary = "Verify OTP code")
    public ResponseEntity<OtpVerifyResponse> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        return ResponseEntity.ok(smsProService.verifyOtp(request));
    }

    /**
     * Step 3: Complete profile with firstName, lastName, region.
     */
    @PostMapping("/complete-profile")
    @Operation(summary = "Complete registration profile (step 3: name + region)")
    public ResponseEntity<AuthResponse> completeProfile(@Valid @RequestBody CompleteProfileRequest request) {
        return ResponseEntity.ok(authService.completeProfile(request));
    }

    /**
     * Login with phone + password.
     */
    @PostMapping("/login")
    @Operation(summary = "Login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    /**
     * Send OTP for password reset.
     */
    @PostMapping("/send-otp")
    @Operation(summary = "Send OTP (for password reset)")
    public ResponseEntity<OtpSendResponse> sendOtp(
            @RequestParam String phone,
            @RequestParam(defaultValue = "PASSWORD_RESET") String type) {
        return ResponseEntity.ok(smsProService.sendOtp(phone, OTPCode.OtpType.valueOf(type)));
    }

    /**
     * Resend OTP.
     */
    @PostMapping("/resend-otp")
    @Operation(summary = "Resend OTP")
    public ResponseEntity<OtpSendResponse> resendOtp(
            @RequestParam String phone,
            @RequestParam String type) {
        return ResponseEntity.ok(smsProService.resendOtp(phone, OTPCode.OtpType.valueOf(type)));
    }

    /**
     * Reset password after OTP verification.
     */
    @PostMapping("/reset-password")
    @Operation(summary = "Reset password (requires OTP verified via /verify-otp first)")
    public ResponseEntity<ApiResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(new ApiResponse(true, "Password reset successfully."));
    }
}