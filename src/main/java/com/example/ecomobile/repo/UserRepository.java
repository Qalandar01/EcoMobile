package com.example.ecomobile.repo;

import com.example.ecomobile.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);

    boolean existsUsersByEmail(@NotBlank(message = "Email cannot be empty") @Email(message = "Invalid email format") String email);
}