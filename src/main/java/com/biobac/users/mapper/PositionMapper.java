package com.biobac.users.mapper;

import com.biobac.users.entity.Position;
import com.biobac.users.request.PositionRequest;
import com.biobac.users.response.PositionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", uses = {PermissionMapper.class})
public interface PositionMapper {
    PositionResponse toResponse(Position entity);

    List<PositionResponse> toResponseList(List<Position> entities);

    Position toEntity(PositionRequest request);

    void updateFromRequest(PositionRequest request, @MappingTarget Position entity);
}
