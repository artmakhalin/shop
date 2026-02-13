package com.ait.shop.dto.mapping;

import com.ait.shop.domain.Cart;
import com.ait.shop.dto.cart.CartDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = PositionMapper.class)
public interface CartMapper {

    CartDto mapEntityToDto(Cart entity);
}
