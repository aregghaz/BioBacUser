package com.biobac.users.service;

import com.biobac.users.dto.PaginationMetadata;
import com.biobac.users.request.PositionRequest;
import com.biobac.users.response.PositionResponse;
import com.biobac.users.request.FilterCriteria;
import org.springframework.data.util.Pair;

import java.util.List;
import java.util.Map;

public interface PositionService {
    PositionResponse getById(Long id);

    PositionResponse create(PositionRequest request);

    PositionResponse update(Long id, PositionRequest request);

    void delete(Long id);

    List<PositionResponse> getAll();

    Pair<List<PositionResponse>, PaginationMetadata> getPagination(Map<String, FilterCriteria> filters, Integer page, Integer size, String sortBy, String sortDir);
}
