package com.ait.shop.service;

import com.ait.shop.domain.ConfirmationCode;
import com.ait.shop.domain.User;
import com.ait.shop.domain.enums.Role;
import com.ait.shop.dto.user.UserRegistrationDto;
import com.ait.shop.exceptions.types.ConfirmationException;
import com.ait.shop.exceptions.types.RegistrationException;
import com.ait.shop.repository.UserRepository;
import com.ait.shop.security.AuthUserDetails;
import com.ait.shop.service.interfaces.ConfirmationCodeService;
import com.ait.shop.service.interfaces.EmailService;
import com.ait.shop.service.interfaces.UserService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class UserServiceImpl implements UserService {

    private final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository repository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final ConfirmationCodeService confirmationCodeService;

    public UserServiceImpl(
            UserRepository repository,
            BCryptPasswordEncoder passwordEncoder,
            EmailService emailService,
            ConfirmationCodeService confirmationCodeService
    ) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.confirmationCodeService = confirmationCodeService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = repository.findByEmail(email)
                .orElseThrow(
                        () -> new UsernameNotFoundException(
                                String.format("User with email %s not found", email)
                        )
                );

        return new AuthUserDetails(user);
    }

    @Override
    public void register(UserRegistrationDto registrationDto) {
        /*
            Три возможных сценария при регистрации пользователя:
            1. Пользователь пришел регистрироваться первый раз (в БД его еще нет)
            2. Не первая попытка регистрации (в БД пользователь уже есть, confirmed - false)
            3. Попытка регистрации на email, который уже используется (в БД пользователь уже есть, confirmed - true)
         */

        String email = registrationDto.getEmail();
        User user = repository.findByEmail(email).orElse(null);

        if (user == null) {
            //Сценарий 1 (частично)

            user = new User();
            user.setEmail(email);
            user.setRole(Role.ROLE_USER);
            user.setConfirmed(false);


        } else if (user.isConfirmed()) {
            //Сценарий 3

            throw new RegistrationException(String.format("Email %s already in use", email));
        }

        //Общие действия для сценариев 1 и 2

        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setName(registrationDto.getName());

        repository.save(user);

        logger.info("User saved to the database: {}", user);

        //Отправляем email о том, что пользователь должен подтвердить регистрацию
        emailService.sendConfirmationEmail(user);
    }

    @Override
    @Transactional
    public void confirm(String codeValue) {
        ConfirmationCode confirmationCode = confirmationCodeService.findByValue(codeValue);

        if (confirmationCode.getExpiration().isBefore(LocalDateTime.now())) {
            throw new ConfirmationException("Confirmation code is expired");
        }

        User user = confirmationCode.getUser();
        Objects.requireNonNull(user, "Confirmation code is not valid");

        if (user.isConfirmed()) {
            throw new ConfirmationException("Email is already confirmed");
        }

        user.setConfirmed(true);

        logger.info("User id {} is confirmed", user.getId());
    }
}
