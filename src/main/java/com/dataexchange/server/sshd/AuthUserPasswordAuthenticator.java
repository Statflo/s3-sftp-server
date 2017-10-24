package com.dataexchange.server.sshd;

import com.dataexchange.server.conf.SftpServer;
import com.dataexchange.server.domain.UserService;
import com.dataexchange.server.jpa.model.AuthUserEntity;
import com.dataexchange.server.jpa.repository.AuthUserJpaRepository;
import org.apache.sshd.server.auth.password.PasswordAuthenticator;
import org.apache.sshd.server.auth.password.PasswordChangeRequiredException;
import org.apache.sshd.server.session.ServerSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;

@Component
@EnableConfigurationProperties(SftpServer.SftpServerConfiguration.class)
public class AuthUserPasswordAuthenticator implements PasswordAuthenticator {

    private final AuthUserJpaRepository authUserJpaRepository;
    private final PasswordEncoder passwordEncoder;
    private final SftpServer.SftpServerConfiguration properties;
    private final UserService userService;

    @Autowired
    public AuthUserPasswordAuthenticator(AuthUserJpaRepository authUserJpaRepository, PasswordEncoder passwordEncoder,
                                         SftpServer.SftpServerConfiguration properties, UserService userService) {
        this.authUserJpaRepository = authUserJpaRepository;
        this.passwordEncoder = passwordEncoder;
        this.properties = properties;
        this.userService = userService;
    }

    @PostConstruct
    public void setup() {
        properties.getUsers().stream()
                .filter(u -> StringUtils.hasText(u.getPassword()))
                .forEach(u -> userService.createUserIfNotExisting(u.getUsername(), u.getPassword()));
    }

    @Override
    public boolean authenticate(String username, String password, ServerSession session) throws PasswordChangeRequiredException {
        AuthUserEntity authUser = authUserJpaRepository.findByUsername(username);
        if (authUser != null) {
            if (passwordEncoder.matches(password, authUser.getPassword())) {
                return true;
            }
        }

        return false;
    }
}
