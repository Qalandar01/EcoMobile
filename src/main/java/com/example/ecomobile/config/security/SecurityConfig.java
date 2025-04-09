package com.example.ecomobile.config.security;


import com.example.ecomobile.filter.MyFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   CustomUsersDetailsService customUsersDetailsService,
                                                   MyFilter myFilter,
                                                   AuthenticationManager authenticationManager) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req ->
                        req
                                .requestMatchers("/api/login", "/api/file/**","/api/verify-code","/api/notifications", "/api/notification-events",
                                        "/api/forgot-password", "/api/set-new-password","/api/category/**","/api/auth/oauth2/**",
                                        "/api/products/**","/api/products/variant/**","/api/products/variants/**","api/register-mail","api/verification",
                                        "/api/file","/api/basket/**", "/api/brands/**","/api/category/product-count/**",
                                        "/api/colors/**", "/api/orders/**", "/api/locations/**" ,"/api/user/**" ,"/file/**","/api/sizes/**" ,"/api/user/**","/api/categoryWithProQuantity","/api/ratings",
                                        "/login","/api/auth/**","/api/chat/**","/chat","/api/chats/customers","/api/history","/api/chats/send","/api/chats/**","/api/chats/new-messages", "/api/chats/sellers"
                                ).permitAll()
                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                                .requestMatchers("/api/products/save/").hasRole("ADMIN")
                                .requestMatchers("/api/products/delete/").hasRole("ADMIN")
                                .requestMatchers("/api/products/edit/").hasRole("ADMIN")
                                .requestMatchers("/api/order/**").hasAnyRole("USER", "ADMIN")
                                .requestMatchers("/api/admin-li st","api/edit-admin","/api/edit-role",
                                        "/api/add-admin","/api/totalOrder","/api/totalProducts","/api/totalQuantity").hasRole("SUPER_ADMIN")

                                .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationManager(authenticationManager)
                .userDetailsService(customUsersDetailsService)
                .addFilterBefore(myFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(CustomUsersDetailsService customUsersDetailsService) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setPasswordEncoder(passwordEncoder());
        authProvider.setUserDetailsService(customUsersDetailsService);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationProvider authenticationProvider) {
        return new ProviderManager(authenticationProvider);
    }
}
