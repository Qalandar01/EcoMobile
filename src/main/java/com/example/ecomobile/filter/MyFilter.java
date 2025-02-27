package com.example.ecomobile.filter;

import com.example.ecomobile.entity.Role;
import com.example.ecomobile.repo.UserRepository;
import com.example.ecomobile.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;
import java.util.List;

@Component
public class MyFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserRepository usersRepository;

    public MyFilter(JwtService jwtService, UserRepository usersRepository) {
        this.jwtService = jwtService;
        this.usersRepository = usersRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        System.out.println("ishladi");
        String token = request.getHeader("token");
        if (token != null) {
            if (jwtService.validate(token)) {
                String username = jwtService.getUserName(token);
                List<Role> roles = jwtService.getRoles(token);
                var authToken = new UsernamePasswordAuthenticationToken(
                      new User(username, "", roles),
                        null,
                        roles
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }

}
