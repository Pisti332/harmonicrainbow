package com.pisti.harmonicrainbow.security;

import com.pisti.harmonicrainbow.repository.UsersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {
    private final UsersRepo userRepository;

    @Autowired
    public MyUserDetailsService(UsersRepo userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.pisti.harmonicrainbow.model.User userFromRepo = userRepository.findFirstByEmail(username);
        if (userFromRepo == null) {
            throw new UsernameNotFoundException(username);
        }
        return User
                .builder()
                .password(userFromRepo.getPassword())
                .username(userFromRepo.getEmail())
                .build();
    }
    public boolean isUserActive(String username) {
        com.pisti.harmonicrainbow.model.User userFromRepo = userRepository.findFirstByEmail(username);
        if (userFromRepo == null) {
            throw new UsernameNotFoundException(username);
        }
        return userFromRepo.isActive();
    }

}
