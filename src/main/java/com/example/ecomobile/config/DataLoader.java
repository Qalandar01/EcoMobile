package com.example.ecomobile.config;

import com.example.ecomobile.entity.ProductColor;
import com.example.ecomobile.entity.ProductSize;
import com.example.ecomobile.entity.Role;
import com.example.ecomobile.entity.User;
import com.example.ecomobile.enums.ColorName;
import com.example.ecomobile.enums.RoleName;
import com.example.ecomobile.enums.Size;
import com.example.ecomobile.repo.ProductColorRepository;
import com.example.ecomobile.repo.ProductSizeRepository;
import com.example.ecomobile.repo.RoleRepository;
import com.example.ecomobile.repo.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

//@Component
public class DataLoader implements CommandLineRunner {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProductSizeRepository productSizeRepository;
    private final ProductColorRepository productColorRepository;

    public DataLoader(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder, ProductSizeRepository productSizeRepository, ProductColorRepository productColorRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.productSizeRepository = productSizeRepository;
        this.productColorRepository = productColorRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        Role role =Role.builder()
                .roleName(RoleName.ROLE_SUPER_ADMIN)
                .build();
        Role role1 =Role.builder()
                .roleName(RoleName.ROLE_ADMIN)
                .build();
        Role role2 =Role.builder()
                .roleName(RoleName.ROLE_USER)
                .build();
        roleRepository.save(role);
        roleRepository.save(role1);
        roleRepository.save(role2);
        User user = User.builder()
                .roles(new ArrayList<>(List.of(role)))
                .email("nayimovalisher06@gmail.com")
                .password(passwordEncoder.encode("1"))
                .firstname("Alisher")
                .lastname("Nayimov")
                .phone("+998914474587")
                .build();
        User user1= User.builder()
                .email("qalandar2201@gmail.com")
                .firstname("Qalandar")
                .password(passwordEncoder.encode("2"))
                .phone("+998914368678")
                .lastname("Qalandarov")
                .roles(new ArrayList<>(List.of(role1)))
                .build();
        User user2= User.builder()
                .roles(new ArrayList<>(List.of(role2)))
                .email("akmalovmavlon5@gmail.com")
                .password(passwordEncoder.encode("3"))
                .firstname("Mavlon")
                .lastname("Akmalov")
                .phone("+998945060701")
                .build();
        userRepository.save(user);
        userRepository.save(user1);
        userRepository.save(user2);

        List<ProductSize> sizes = new ArrayList<>();
        for (Size size : Size.values()) {
            sizes.add(ProductSize.builder().size(size).build());
        }
        productSizeRepository.saveAll(sizes);

        List<ProductColor> colors = new ArrayList<>();
        for (ColorName color : ColorName.values()) {
            colors.add(ProductColor.builder().colorName(color).build());
        }
        productColorRepository.saveAll(colors);

        System.out.println("✅ Data successfully loaded into the database!");

    }
}
