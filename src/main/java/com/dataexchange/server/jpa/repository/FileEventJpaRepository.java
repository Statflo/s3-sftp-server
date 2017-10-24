package com.dataexchange.server.jpa.repository;

import com.dataexchange.server.jpa.model.FileEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileEventJpaRepository extends JpaRepository<FileEventEntity, Long> {
}
