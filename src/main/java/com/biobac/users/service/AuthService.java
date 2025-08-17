package com.biobac.users.service;

import com.biobac.users.request.AuthRequest;
import com.biobac.users.response.AuthResponse;

public interface AuthService {
    AuthResponse login(AuthRequest request);
    AuthResponse refreshToken(String token);
}
