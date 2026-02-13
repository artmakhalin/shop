package com.ait.shop.dto.mapping;

import com.ait.shop.domain.Product;
import com.ait.shop.dto.product.ProductDto;
import com.ait.shop.dto.product.ProductSaveDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductDto mapEntityToDto(Product entity);
    Product mapDtoToEntity(ProductSaveDto dto);
}
