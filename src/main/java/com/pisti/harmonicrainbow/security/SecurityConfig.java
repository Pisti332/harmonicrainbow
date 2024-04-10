package com.pisti.harmonicrainbow.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity(debug = true)
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final JWTAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authz -> {
                    authz
                            .requestMatchers("/",
                                    "/api/user/signup",
                                    "/api/user/signin",
                                    "api/user/confirmtoken",
                                    "/static/**",
                                    "/confirmFailedHtml.html",
                                    "confirmSuccessfulHtml.html",
                                    "index.html",
                                    "assets/img/*",
                                    "favicon.ico",
                                    "main-MTUFV6IG.js",
                                    "polyfills-LZBJRJJE.js",
                                    "styles-3JZCYRXB.css"
                                    ).permitAll()
                            .requestMatchers(HttpMethod.GET, "api/image/admin").hasRole(Role.ADMIN.toString())
                            .anyRequest().hasAnyRole(Role.ADMIN.toString(), Role.USER.toString());
                })
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
