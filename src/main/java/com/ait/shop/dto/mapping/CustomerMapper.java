package com.ait.shop.dto.mapping;

import com.ait.shop.domain.Customer;
import com.ait.shop.dto.customer.CustomerDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = CartMapper.class)
public interface CustomerMapper {

    CustomerDto mapEntityToDto(Customer entity);
}
