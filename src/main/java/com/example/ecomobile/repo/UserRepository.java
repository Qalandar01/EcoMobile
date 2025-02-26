package com.example.ecomobile.repo;

import com.example.ecomobile.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
}