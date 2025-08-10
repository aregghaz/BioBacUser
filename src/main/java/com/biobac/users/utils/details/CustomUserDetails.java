package com.biobac.users.utils.details;

import com.biobac.users.entity.Permission;
import com.biobac.users.entity.Role;
import com.biobac.users.entity.User;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {
    private final User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        if (user.getRoles() != null) {
            // Add role authorities
            authorities.addAll(
                    user.getRoles().stream()
                            .map(Role::getName)
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toSet())
            );
            // Add permission authorities from roles
            user.getRoles().forEach(role -> {
                if (role.getPermissions() != null) {
                    authorities.addAll(
                            role.getPermissions().stream()
                                    .map(Permission::getName)
                                    .map(SimpleGrantedAuthority::new)
                                    .collect(Collectors.toSet())
                    );
                }
            });
        }
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isEnabled() {
        return user.getActive();
    }
}
