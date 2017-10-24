package com.dataexchange.server.domain;

import com.dataexchange.server.jpa.model.AuthUserEntity;
import com.dataexchange.server.jpa.repository.AuthUserJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    private final AuthUserJpaRepository authUserJpaRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(AuthUserJpaRepository authUserJpaRepository, PasswordEncoder passwordEncoder) {
        this.authUserJpaRepository = authUserJpaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void checkOrAutoCreateUserForPrivateKeyAuthentication(String username) {
        AuthUserEntity authUser = authUserJpaRepository.findByUsername(username);
        if (authUser == null) {
            LOGGER.info("Auto creating user {] with private key", username);
            authUser = new AuthUserEntity();
            authUser.setUsername(username);
            authUser.setPassword("AUTO_GENERATED_SSH_KEY");
            authUserJpaRepository.save(authUser);
        }
    }

    public void createUserIfNotExisting(String username, String password) {
        AuthUserEntity authUser = authUserJpaRepository.findByUsername(username);
        if (authUser == null) {
            LOGGER.info("Auto creating user {] with password", username);
            authUser = new AuthUserEntity();
            authUser.setUsername(username);
            authUser.setPassword(passwordEncoder.encode(password));
            authUserJpaRepository.save(authUser);
        }
    }
}
