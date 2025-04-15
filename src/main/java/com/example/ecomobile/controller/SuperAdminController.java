package com.example.ecomobile.controller;

import com.example.ecomobile.dto.SuperAdminDTO;
import com.example.ecomobile.dto.UserDTO;
import com.example.ecomobile.entity.Role;
import com.example.ecomobile.entity.User;
import com.example.ecomobile.enums.RoleName;
import com.example.ecomobile.repo.OrderRepository;
import com.example.ecomobile.repo.ProductRepository;
import com.example.ecomobile.repo.RoleRepository;
import com.example.ecomobile.repo.UserRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api")
public class SuperAdminController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public SuperAdminController(UserRepository userRepository, RoleRepository roleRepository, ProductRepository productRepository, OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    @GetMapping("/admin-list")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        // "ROLE_ADMIN" rolini tanlab, barcha foydalanuvchilarni olish
        List<User> roleAdmin = userRepository.findByRoleName("ROLE_ADMIN");


        // User obyektlarini UserDTO formatiga o‘zgartirish
        List<UserDTO> userDTOs = roleAdmin.stream()
                .map(user -> UserDTO.builder()
                        .firstname(user.getFirstname())
                        .lastname(user.getLastname())
                        .email(user.getEmail())
                        .phone(user.getPhone())
                        .age(user.getAge())
                        .gender(user.getGender())
                        .build())
                .collect(Collectors.toList());

        // UserDTO formatidagi ma'lumotni qaytarish
        return ResponseEntity.ok(userDTOs);
    }


    @PostMapping("/edit-admin/{id}")
    public ResponseEntity<User> editAdmin(@PathVariable Integer id) {
        User user = userRepository.findById(id).orElseThrow();
        return ResponseEntity.ok(user);
    }

    @PostMapping("/edit-role/{id}/{name}/{email}/{role}")
    public HttpEntity<?> editRole(@PathVariable Integer id,@PathVariable String name,@PathVariable String email, @PathVariable String role) {
        User user = userRepository.findById(id).orElseThrow();
        List<Role> roles;
        if (role.equals("admin")) {
            roles= roleRepository.findByRoleName(RoleName.ROLE_ADMIN);
        }else if (role.equals("super-admin")) {
            roles=roleRepository.findByRoleName(RoleName.ROLE_SUPER_ADMIN);
        }else {
            roles=roleRepository.findByRoleName(RoleName.ROLE_USER);
        }
        user.setFirstname(name);
        user.setEmail(email);
        user.setRoles(roles);
        userRepository.save(user);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/add-admin")
    public HttpEntity<?> addAdmin(@RequestBody SuperAdminDTO superAdminDTO) {

        System.out.println("name = " + superAdminDTO.getName());
        System.out.println("email = " + superAdminDTO.getEmail());
        System.out.println("role = " + superAdminDTO.getRole());
        List<Role> roles;
        if (superAdminDTO.getRole().equals("admin")) {
            roles= roleRepository.findByRoleName(RoleName.ROLE_ADMIN);
        }else if (superAdminDTO.getRole().equals("super-admin")) {
            roles=roleRepository.findByRoleName(RoleName.ROLE_SUPER_ADMIN);
        }else {
            roles=roleRepository.findByRoleName(RoleName.ROLE_USER);
        }
        User user=User.builder()
                .firstname(superAdminDTO.getName())
                .email(superAdminDTO.getEmail())
                .roles(roles)
                .build();
        userRepository.save(user);
        return ResponseEntity.ok(user);
    }




    @DeleteMapping("/delete-admin/{id}")
    public HttpEntity<?> deleteAdmin(@PathVariable Integer id) {
        User user = userRepository.findById(id).orElseThrow();
        List<Role> roleName = roleRepository.findByRoleName(RoleName.ROLE_USER);
        user.setRoles(roleName);
        userRepository.save(user);
        return ResponseEntity.ok(user);
    }


    @GetMapping("/totalOrder")
    public ResponseEntity<Integer> getTotalOrder() {
        return ResponseEntity.ok(orderRepository.findAllCountOrders());
    }


    @GetMapping("/totalProducts")
    public ResponseEntity<Integer> getTotalProducts() {
        return ResponseEntity.ok(productRepository.findAllCountProducts());
    }

    @GetMapping("/totalQuantity")
    public ResponseEntity<Integer> getTotalQuantity() {
        return ResponseEntity.ok(orderRepository.findTotalQuantity());
    }
}
