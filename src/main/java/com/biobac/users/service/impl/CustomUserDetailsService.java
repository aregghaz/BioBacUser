package com.biobac.users.service.impl;


import com.biobac.users.entity.User;
import com.biobac.users.exception.NotFoundException;
import com.biobac.users.repository.UserRepository;
import com.biobac.users.utils.details.CustomUserDetails;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new NotFoundException("User not found");
        }
//        if (!user.get().getActive()) {
//            throw new UserInactiveException("User account is inactive");
//        }
        return new CustomUserDetails(user.get());
    }
}