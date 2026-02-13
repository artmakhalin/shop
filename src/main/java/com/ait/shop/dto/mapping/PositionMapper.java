package com.ait.shop.dto.mapping;

import com.ait.shop.domain.Position;
import com.ait.shop.dto.position.PositionDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = ProductMapper.class)
public interface PositionMapper {

    PositionDto mapEntityToDto(Position entity);
}
