package com.ordenes_back.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity

public class SecurityConfig {
    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults()) // Nueva sintaxis Spring Boot 3
                .authorizeHttpRequests(auth -> auth
                                // permitimos login y registro
                                .requestMatchers("/api/auth/register", "/api/auth/login").permitAll()
                                // permitimos Swagger para los endpoints
                                .requestMatchers(
                                        "/swagger-ui.html",
                                        "/swagger-ui/**",
                                        "/v3/api-docs",
                                        "/v3/api-docs/**",
                                        "/api-docs/**"
                                ).permitAll()

                                // rutas de los endpoints
                                .requestMatchers("/api/admin/**").hasAnyAuthority("ADMIN", "ROLE_ADMIN")
                                .requestMatchers("/api/user/**").hasAnyAuthority("USER", "ROLE_USER", "ADMIN", "ROLE_ADMIN")

                                // Rutas de órdenes con métodos HTTP nativos
                                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/ordenes", "/api/ordenes/**").hasAnyAuthority("USER", "ROLE_USER", "ADMIN", "ROLE_ADMIN")
                                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/ordenes", "/api/ordenes/**").hasAnyAuthority("USER", "ROLE_USER", "ADMIN", "ROLE_ADMIN")
                                .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/ordenes/**").hasAnyAuthority("USER", "ROLE_USER", "ADMIN", "ROLE_ADMIN")
                                .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/ordenes/**").hasAnyAuthority("ADMIN", "ROLE_ADMIN")

                                // obligatorio autenticacion
                                .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    // configuramos el cors para en frontend
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        // Permite localhost (Angular dev) y quité la IP quemada de tu proyecto viejo
        config.setAllowedOrigins(List.of("http://localhost:4200"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
