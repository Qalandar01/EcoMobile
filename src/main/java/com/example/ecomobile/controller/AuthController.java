package com.example.ecomobile.controller;

import com.example.ecomobile.dto.OauthClientDTO;
import com.example.ecomobile.repo.UserRepository;
import com.example.ecomobile.service.JwtService;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    Dotenv dotenv = Dotenv.load();

    private final UserRepository userRepository;
    private final JwtService jwtService;
    @Value("${spring.oauth.auth-url}")
    private String authUrl;
//    @Value("${spring.oauth.google.client-id}")
    private String clientId = dotenv.get("GOOGLE_CLIENT_ID");
    @Value("${spring.oauth.redirect-url}")
    private String redirectUri;
//    @Value("${spring.oauth.google.client-secret}")
    private String clientSecret = dotenv.get("GOOGLE_CLIENT_SECRET");
    @Value("${spring.google.token-url}")
    private String tokenUrl;
    @Value("${spring.google.user.info-url}")
    private String userInfo;

    public AuthController(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @GetMapping("/oauth2")
    public HttpEntity<?> loginWithGoogle(){
        String authorizationUrl = authUrl + "?client_id=" + clientId + "&redirect_uri=" + redirectUri + "&response_type=code&scope=openid%20profile%20email";
        Map<String, String> response = new HashMap<>();
        response.put("authorizationUrl",authorizationUrl);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/oauth2/callback")
    public HttpEntity<?> callback(@RequestParam String code){
        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String,String> params = new LinkedMultiValueMap<>();
        params.add("client_id",clientId);
        params.add("client_secret",clientSecret);
        params.add("code",code);
        params.add("grant_type","authorization_code");
        params.add("redirect_uri",redirectUri);

        ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, params, Map.class);
        Map<String, String > tokens = response.getBody();

        String accessToken = tokens.get("access_token");
        String email = fetchUserInfo(accessToken);

        if (!userRepository.existsUsersByEmail(email)){
            return ResponseEntity.badRequest().build();
        }

        String token = jwtService.generateToken(userRepository.findByEmail(email).orElseThrow());

        return ResponseEntity.status(301).location(URI.create("http://localhost:63342/EcoMobile/frontend/product.html?_ijt=f7s6h6iqmtadr66cvqv9uhduuv&_ij_reload=RELOAD_ON_SAVE&token="+token)).build();

    }

    private String fetchUserInfo(String accessToken){
        RestTemplate restTemplate  = new RestTemplate();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization","Bearer"+accessToken);

        HttpEntity<String> entity = new HttpEntity<>(httpHeaders);

        ResponseEntity<OauthClientDTO> response = restTemplate.exchange(
                userInfo,
                HttpMethod.GET,
                entity,
                OauthClientDTO.class
        );
        OauthClientDTO clientDTO = response.getBody();
        assert clientDTO != null;
        return clientDTO.getEmail();
    }
}
