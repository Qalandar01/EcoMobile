package com.example.ecomobile.repo;

import com.example.ecomobile.entity.Role;
import com.example.ecomobile.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    List<Role> findByRoleName(RoleName roleName);
}