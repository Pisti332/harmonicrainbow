package com.pisti.harmonicrainbow.configuration;

import com.pisti.harmonicrainbow.repository.UsersRepo;
import com.pisti.harmonicrainbow.security.MyUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfiguration {

    private final UsersRepo userRepository;

    @Bean
    public UserDetailsService userDetailsService() {
        return new MyUserDetailsService(userRepository);
    }
    @Bean
    public PasswordEncoder Encoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(Encoder());
        return authProvider;
    }
}
