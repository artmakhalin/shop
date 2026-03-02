package com.ait.shop.service;

import com.ait.shop.domain.ConfirmationCode;
import com.ait.shop.domain.User;
import com.ait.shop.exceptions.types.EntityNotFoundException;
import com.ait.shop.repository.ConfirmationCodeRepository;
import com.ait.shop.service.interfaces.ConfirmationCodeService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Service
public class ConfirmationCodeServiceImpl implements ConfirmationCodeService {

    private final ConfirmationCodeRepository repository;

    public ConfirmationCodeServiceImpl(ConfirmationCodeRepository repository) {
        this.repository = repository;
    }

    @Override
    public String generateConfirmationCode(User user) {
        String value = UUID.randomUUID().toString();
        LocalDateTime expiration = LocalDateTime.now().plusHours(24);
        ConfirmationCode entity = new ConfirmationCode(value, expiration, user);
        repository.save(entity);

        return value;
    }

    @Override
    public ConfirmationCode findByValue(String value) {
        Objects.requireNonNull(value, "Confirmation code value cannot be null");

        return repository.findByValue(value)
                .orElseThrow(
                        () -> new EntityNotFoundException(ConfirmationCode.class, value)
                );
    }
}
