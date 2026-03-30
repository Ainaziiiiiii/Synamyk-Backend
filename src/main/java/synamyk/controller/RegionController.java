package synamyk.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import synamyk.dto.RegionDto;
import synamyk.service.RegionService;

import java.util.List;

@RestController
@RequestMapping("/api/regions")
@RequiredArgsConstructor
@Tag(name = "Регионы", description = "Справочник регионов Кыргызстана. Публичный эндпоинт — JWT не требуется.")
public class RegionController {

    private final RegionService regionService;

    @GetMapping
    @Operation(summary = "Список всех регионов", description = "Используется при регистрации и смене региона в профиле.")
    public ResponseEntity<List<RegionDto>> getRegions() {
        return ResponseEntity.ok(regionService.getAllRegions());
    }
}