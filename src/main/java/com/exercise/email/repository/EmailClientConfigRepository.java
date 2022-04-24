package com.exercise.email.repository;

import com.exercise.email.entity.EmailClientConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailClientConfigRepository extends JpaRepository<EmailClientConfig, String> {

    Optional<EmailClientConfig> findByName(String name);

}
