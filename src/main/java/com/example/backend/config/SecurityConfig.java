package com.example.backend.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import lombok.RequiredArgsConstructor;

@EnableMethodSecurity(prePostEnabled = true)
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;


    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(AbstractHttpConfigurer::disable)
            .headers(headers -> headers
                .addHeaderWriter(new XFrameOptionsHeaderWriter(XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN)))
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // 公開
                .requestMatchers("/api/auth/**", "/h2-console/**",
                                "/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()

                // 認証のみ
                .requestMatchers("/api/users/me").authenticated()
               // .requestMatchers("/api/cart/**", "/api/orders/**").authenticated()
                        .requestMatchers("/api/cart/**", "/api/orders/**", "/api/mypage/**").permitAll()
                // 管理者
                .requestMatchers("/api/admin/**").hasAnyRole("ADMIN","SUPERUSER")
                .requestMatchers("/api/users/**").hasAnyRole("ADMIN","SUPERUSER")
                // ★追加: グループ並び替え確定は管理者のみ
                .requestMatchers(HttpMethod.PUT, "/api/productgroups/reorder").hasAnyRole("ADMIN","SUPERUSER")
                // ★追加: 売上サマリー(グラフ用)は管理者のみ
                .requestMatchers(HttpMethod.GET, "/api/sales/summary/**").hasAnyRole("ADMIN","SUPERUSER")

                // 公開: 商品/グループのGET一覧（?sort=order も許可）
                .requestMatchers(HttpMethod.GET, "/api/products/**", "/api/productgroups/**").permitAll()

                .requestMatchers(HttpMethod.GET, "/api/history/**").authenticated()
                .anyRequest().authenticated()
            )

            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        var config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173"));
        config.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS","PATCH"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization","Content-Type","X-Requested-With"));
        config.setAllowCredentials(true);

        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
