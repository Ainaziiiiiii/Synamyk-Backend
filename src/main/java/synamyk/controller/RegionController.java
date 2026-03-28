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
@Tag(name = "Regions", description = "Get list of regions")
public class RegionController {

    private final RegionService regionService;

    @GetMapping
    @Operation(summary = "Get all regions")
    public ResponseEntity<List<RegionDto>> getRegions() {
        return ResponseEntity.ok(regionService.getAllRegions());
    }
}