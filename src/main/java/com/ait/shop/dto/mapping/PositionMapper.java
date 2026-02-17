package com.ait.shop.dto.mapping;

import com.ait.shop.domain.Position;
import com.ait.shop.dto.position.PositionDto;
import org.mapstruct.Mapper;

import java.util.Set;

@Mapper(componentModel = "spring", uses = ProductMapper.class)
public interface PositionMapper {

    PositionDto mapEntityToDto(Position entity);
    Set<PositionDto> mapEntitySetToDtoSet(Set<Position> entitySet);
}
