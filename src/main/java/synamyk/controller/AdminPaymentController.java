package synamyk.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import synamyk.dto.admin.AdminPaymentResponse;
import synamyk.entities.Payment;
import synamyk.service.AdminPaymentService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@RestController
@RequestMapping("/api/admin/payments")
@RequiredArgsConstructor
@Tag(name = "Админ — Платежи Finik", description = "Управление платежами. Требуется роль ADMIN.")
@SecurityRequirement(name = "Bearer")
public class AdminPaymentController {

    private final AdminPaymentService adminPaymentService;

    @GetMapping
    @Operation(
            summary = "Список платежей",
            description = """
                    Возвращает пагинированный список платежей с возможностью фильтрации.

                    **Фильтры:**
                    - `search` — поиск по ID транзакции, телефону или имени пользователя
                    - `status` — PENDING / COMPLETED / EXPIRED / CANCELLED
                    - `dateFrom` / `dateTo` — диапазон дат (yyyy-MM-dd)
                    """)
    public ResponseEntity<Page<AdminPaymentResponse>> list(
            @Parameter(description = "Номер страницы (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Размер страницы") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Поиск по ID транзакции / телефону / имени") @RequestParam(required = false) String search,
            @Parameter(description = "Статус: PENDING, COMPLETED, EXPIRED, CANCELLED") @RequestParam(required = false) Payment.PaymentStatus status,
            @Parameter(description = "Начало периода (yyyy-MM-dd)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @Parameter(description = "Конец периода (yyyy-MM-dd)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo) {

        LocalDateTime from = dateFrom != null ? dateFrom.atStartOfDay() : null;
        LocalDateTime to = dateTo != null ? dateTo.atTime(LocalTime.MAX) : null;

        return ResponseEntity.ok(adminPaymentService.listPayments(page, size, search, status, from, to));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить платёж по ID")
    public ResponseEntity<AdminPaymentResponse> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(adminPaymentService.getPayment(id));
    }

    @PatchMapping("/{id}/status")
    @Operation(
            summary = "Изменить статус платежа",
            description = "Обновляет статус платежа. Допустимые значения: `PENDING`, `COMPLETED`, `EXPIRED`, `CANCELLED`.")
    public ResponseEntity<AdminPaymentResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam Payment.PaymentStatus status) {
        return ResponseEntity.ok(adminPaymentService.updateStatus(id, status));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить платёж", description = "Удаляет запись платежа. Доступ к тесту НЕ отзывается.")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        adminPaymentService.deletePayment(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/export")
    @Operation(
            summary = "Экспорт платежей",
            description = "Экспортирует список платежей в CSV или Excel. Параметр `format`: `csv` (по умолчанию) или `excel`.")
    public ResponseEntity<byte[]> export(
            @RequestParam(defaultValue = "csv") String format,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Payment.PaymentStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo) throws IOException {

        LocalDateTime from = dateFrom != null ? dateFrom.atStartOfDay() : null;
        LocalDateTime to = dateTo != null ? dateTo.atTime(LocalTime.MAX) : null;

        if ("excel".equalsIgnoreCase(format)) {
            byte[] data = adminPaymentService.exportExcel(search, status, from, to);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            ContentDisposition.attachment().filename("payments.xlsx").build().toString())
                    .body(data);
        }

        byte[] data = adminPaymentService.exportCsv(search, status, from, to);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename("payments.csv").build().toString())
                .body(data);
    }
}
