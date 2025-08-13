package com.biobac.users.service.impl;

import com.biobac.users.exception.NotFoundException;
import com.biobac.users.request.AuthRequest;
import com.biobac.users.response.AuthResponse;
import com.biobac.users.entity.User;
import com.biobac.users.repository.UserRepository;
import com.biobac.users.service.AuthService;
import com.biobac.users.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    public AuthResponse login(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new NotFoundException("User not found with username: " + request.getUsername()));

        return new AuthResponse(jwtUtil.generateToken(user));
    }
}
