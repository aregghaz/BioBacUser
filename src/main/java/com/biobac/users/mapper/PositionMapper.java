package com.biobac.users.mapper;

import com.biobac.users.entity.Position;
import com.biobac.users.request.PositionRequest;
import com.biobac.users.response.PositionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PositionMapper {
    PositionResponse toResponse(Position entity);

    Position toEntity(PositionRequest request);

    void updateFromRequest(PositionRequest request, @MappingTarget Position entity);
}
