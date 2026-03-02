package com.ait.shop.service.interfaces;

import com.ait.shop.domain.User;

public interface EmailService {

    void sendConfirmationEmail(User user);
}
