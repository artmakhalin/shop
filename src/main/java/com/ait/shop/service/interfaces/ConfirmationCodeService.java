package com.ait.shop.service.interfaces;

import com.ait.shop.domain.ConfirmationCode;
import com.ait.shop.domain.User;

public interface ConfirmationCodeService {

    String generateConfirmationCode(User user);
    ConfirmationCode findByValue(String value);
}
