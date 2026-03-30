package synamyk.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import synamyk.dto.*;
import synamyk.entities.OTPCode;
import synamyk.repo.UserRepository;
import synamyk.service.AuthService;
import synamyk.service.SmsProService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Авторизация", description = "Регистрация (3 шага), вход, OTP-верификация, сброс пароля")
public class  AuthController {

    private final AuthService authService;
    private final SmsProService smsProService;
    private final UserRepository userRepository;

    @PostMapping("/register")
    @Operation(
            summary = "Шаг 1 регистрации — телефон и пароль",
            description = "Создаёт аккаунт с номером телефона и паролем. " +
                    "Автоматически отправляет 4-значный OTP-код через SMSPRO. " +
                    "Возвращает JWT-токен для следующих шагов."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Аккаунт создан, OTP отправлен"),
            @ApiResponse(responseCode = "400", description = "Телефон уже зарегистрирован или пароли не совпадают")
    })
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/verify-otp")
    @Operation(
            summary = "Шаг 2 — верификация OTP-кода",
            description = "Проверяет 4-значный код из SMS. " +
                    "Для регистрации используйте `type = REGISTRATION`. " +
                    "Для сброса пароля: `type = PASSWORD_RESET`. " +
                    "После успешной верификации код помечается как использованный."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Код верифицирован"),
            @ApiResponse(responseCode = "400", description = "Неверный или истёкший код")
    })
    public ResponseEntity<OtpVerifyResponse> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        return ResponseEntity.ok(smsProService.verifyOtp(request));
    }

    @PostMapping("/complete-profile")
    @Operation(
            summary = "Шаг 3 — заполнение профиля",
            description = "Сохраняет имя, фамилию и регион пользователя после верификации OTP. " +
                    "Список регионов: GET /api/regions. " +
                    "Возвращает финальный JWT-токен для авторизации."
    )
    @ApiResponse(responseCode = "200", description = "Профиль заполнен, JWT-токен готов")
    public ResponseEntity<AuthResponse> completeProfile(@Valid @RequestBody CompleteProfileRequest request) {
        return ResponseEntity.ok(authService.completeProfile(request));
    }

    @PostMapping("/login")
    @Operation(
            summary = "Вход в аккаунт",
            description = "Аутентификация по номеру телефона и паролю. Возвращает JWT-токен."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Вход выполнен, JWT-токен получен"),
            @ApiResponse(responseCode = "401", description = "Неверный телефон или пароль")
    })
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/send-otp")
    @Operation(
            summary = "Отправить OTP-код",
            description = "Отправляет 4-значный код " +
                    "Доступные типы: `REGISTRATION`, `PASSWORD_RESET`. " +
                    "Повторная отправка возможна только после истечения предыдущего кода."
    )
    @ApiResponse(responseCode = "200", description = "Код отправлен")
    public ResponseEntity<OtpSendResponse> sendOtp(
            @Parameter(description = "Номер телефона") @RequestParam String phone,
            @Parameter(description = "Тип OTP: REGISTRATION или PASSWORD_RESET")
            @RequestParam(defaultValue = "PASSWORD_RESET") String type) {
        return ResponseEntity.ok(smsProService.sendOtp(phone, OTPCode.OtpType.valueOf(type)));
    }

    @PostMapping("/resend-otp")
    @Operation(
            summary = "Повторно отправить OTP-код",
            description = "Инвалидирует предыдущий код и отправляет новый. " +
                    "Используется если пользователь не получил SMS."
    )
    @ApiResponse(responseCode = "200", description = "Новый код отправлен")
    public ResponseEntity<OtpSendResponse> resendOtp(
            @Parameter(description = "Номер телефона") @RequestParam String phone,
            @Parameter(description = "Тип OTP: REGISTRATION или PASSWORD_RESET") @RequestParam String type) {
        return ResponseEntity.ok(smsProService.resendOtp(phone, OTPCode.OtpType.valueOf(type)));
    }

    @PostMapping("/reset-password")
    @Operation(
            summary = "Сбросить пароль",
            description = "Устанавливает новый пароль. Требует предварительной верификации OTP через /verify-otp " +
                    "с типом `PASSWORD_RESET`. Верифицированный код должен быть не старше 10 минут."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пароль успешно сброшен"),
            @ApiResponse(responseCode = "400", description = "OTP не верифицирован, уже использован или истёк")
    })
    public ResponseEntity<MessageResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(new MessageResponse(true, "Пароль успешно сброшен."));
    }

    @PostMapping("/refresh")
    @Operation(
            summary = "Обновить access-токен",
            description = "Принимает refresh-токен (UUID), проверяет его валидность, отзывает старый и " +
                    "возвращает новую пару access + refresh токенов (ротация)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Новая пара токенов выдана"),
            @ApiResponse(responseCode = "400", description = "Refresh-токен недействителен, отозван или истёк")
    })
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request.getRefreshToken()));
    }

    @PostMapping("/logout")
    @SecurityRequirement(name = "Bearer")
    @Operation(
            summary = "Выйти из аккаунта",
            description = "Отзывает все активные refresh-токены текущего пользователя. " +
                    "Access-токен продолжает работать до истечения срока действия (TTL)."
    )
    @ApiResponse(responseCode = "200", description = "Выход выполнен, все refresh-токены отозваны")
    public ResponseEntity<MessageResponse> logout(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = userRepository.findByPhone(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден."))
                .getId();
        authService.logout(userId);
        return ResponseEntity.ok(new MessageResponse(true, "Выход выполнен."));
    }
}