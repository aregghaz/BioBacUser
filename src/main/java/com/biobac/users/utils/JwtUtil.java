package com.biobac.users.utils;

import com.biobac.users.entity.Permission;
import com.biobac.users.entity.User;
import com.biobac.users.exception.InvalidTokenException;
import com.biobac.users.exception.TokenExpiredException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtUtil {
    private final Key key;
    private final long jwtExpirationMs;
    private final Key refreshKey;

    public JwtUtil(@Value("${jwt.secret}") String jwtSecret,
                   @Value("${jwt.expiration-ms}") long jwtExpirationMs,
                   @Value("${jwt.refresh-token.secret}") String refreshSecret) {
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        this.jwtExpirationMs = jwtExpirationMs;
        this.refreshKey = Keys.hmacShaKeyFor(refreshSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateRefreshToken(User user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .signWith(refreshKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateRefreshToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(refreshKey)
                    .build()
                    .parseClaimsJws(token);
            return claims.getBody().getSubject() != null;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String generateAccessToken(User user) {
        Map<String, Object> claims = new HashMap<>();

        claims.put("userId", user.getId());

        Set<String> allowedPerms = Set.of(
                "ALL_GROUP_ACCESS",
                "RECEIVE_INGREDIENT_STATUS_UPDATE",
                "INGREDIENT_ENTRY_EXPENSE_UPDATE"
        );

        Set<String> perms = new HashSet<>();

        if (user.getPermissions() != null) {
            perms = user.getPermissions().stream()
                    .map(Permission::getName)
                    .filter(allowedPerms::contains)
                    .collect(Collectors.toSet());
        }

        if (!perms.isEmpty()) {
            claims.put("perms", perms);
        }

        Date now = new Date();
        Date exp = new Date(now.getTime() + jwtExpirationMs);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUsername())
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return parseClaims(token).getBody().getSubject();
    }

    public List<String> extractRoles(String token) {
        Claims claims = parseClaims(token).getBody();
        Object rolesObj = claims.get("roles");
        if (rolesObj instanceof Collection<?> coll) {
            List<String> roles = new ArrayList<>();
            for (Object o : coll) {
                if (o != null) roles.add(o.toString());
            }
            return roles;
        }
        return Collections.emptyList();
    }

    public List<String> extractPermissions(String token) {
        Claims claims = parseClaims(token).getBody();
        Object permsObj = claims.get("perms");
        if (permsObj instanceof Collection<?> coll) {
            List<String> perms = new ArrayList<>();
            for (Object o : coll) {
                if (o != null) perms.add(o.toString());
            }
            return perms;
        }
        return Collections.emptyList();
    }

    public void validateAccessToken(String token) {
        try {
            parseClaims(token);
        } catch (ExpiredJwtException e) {
            throw new TokenExpiredException("Access token expired");
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidTokenException("Invalid access token");
        }
    }

    private Jws<Claims> parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
    }
}
