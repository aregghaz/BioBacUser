package com.biobac.users.service;

import com.biobac.users.entity.RefreshToken;
import com.biobac.users.entity.User;

public interface RefreshTokenService {
    RefreshToken createRefreshToken(User user);
    RefreshToken verifyRefreshToken(String token);
}
