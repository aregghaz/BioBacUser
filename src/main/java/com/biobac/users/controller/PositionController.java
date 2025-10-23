package com.biobac.users.controller;

import com.biobac.users.dto.PaginationMetadata;
import com.biobac.users.request.FilterCriteria;
import com.biobac.users.request.PositionRequest;
import com.biobac.users.response.ApiResponse;
import com.biobac.users.response.PositionResponse;
import com.biobac.users.service.PositionService;
import com.biobac.users.utils.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/position")
@RequiredArgsConstructor
public class PositionController {
    private final PositionService positionService;

    @PostMapping
    public ApiResponse<PositionResponse> create(@RequestBody PositionRequest request) {
        return ResponseUtil.success("Position created successfully", positionService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<PositionResponse> update(@PathVariable Long id, @RequestBody PositionRequest request) {
        return ResponseUtil.success("Position updated successfully", positionService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable Long id) {
        positionService.delete(id);
        return ResponseUtil.success("Position deleted successfully");
    }

    @GetMapping("/{id}")
    public ApiResponse<PositionResponse> getById(@PathVariable Long id) {
        return ResponseUtil.success("Position fetched successfully", positionService.getById(id));
    }

    @GetMapping
    public ApiResponse<List<PositionResponse>> getAll() {
        return ResponseUtil.success("Positions fetched successfully", positionService.getAll());
    }

    @PostMapping("/all")
    public ApiResponse<List<PositionResponse>> getPagination(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortDir,
            @RequestBody Map<String, FilterCriteria> filters) {
        Pair<List<PositionResponse>, PaginationMetadata> result = positionService.getPagination(filters, page, size, sortBy, sortDir);
        return ResponseUtil.success("Positions fetched successfully", result.getFirst(), result.getSecond());
    }
}
