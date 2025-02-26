package com.example.ecomobile.repo;

import com.example.ecomobile.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer> {
}