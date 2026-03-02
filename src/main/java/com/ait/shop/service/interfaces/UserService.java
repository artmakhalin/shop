package com.ait.shop.service.interfaces;

import com.ait.shop.dto.user.UserRegistrationDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    void register(UserRegistrationDto registrationDto);
    void confirm(String codeValue);

}
