package com.biobac.users.service;

import com.biobac.users.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

public interface UserService {
    @Transactional
    User save(User user);

    UserDetails loadUserByUsername(String username);
}
