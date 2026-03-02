package com.ait.shop.repository;

import com.ait.shop.domain.ConfirmationCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConfirmationCodeRepository extends JpaRepository<ConfirmationCode, Long> {

    Optional<ConfirmationCode> findByValue(String value);
}
