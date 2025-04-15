package com.example.ecomobile.repo;

import com.example.ecomobile.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
    @Query("SELECT u.attachment.id FROM User u WHERE u.id = :userId")
    Optional<Integer> findAttachmentIdByUserId(@Param("userId") Integer userId);
    boolean existsUsersByEmail(@NotBlank(message = "Email cannot be empty") @Email(message = "Invalid email format") String email);

    @Query(value = """
        SELECT u.*
        FROM users u
        JOIN users_roles r ON u.id = r.user_id
        JOIN roles e ON r.roles_id = e.id
        WHERE e.role_name = :roleName
        """, nativeQuery = true)
    List<User> findByRoleName(@Param("roleName") String roleName);

}
