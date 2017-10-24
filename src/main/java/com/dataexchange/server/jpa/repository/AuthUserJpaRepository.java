package com.dataexchange.server.jpa.repository;

import com.dataexchange.server.jpa.model.AuthUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthUserJpaRepository extends JpaRepository<AuthUserEntity, Long> {

    AuthUserEntity findByUsername(String username);
}
