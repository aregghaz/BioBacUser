package com.biobac.users.utils;

import com.biobac.users.entity.Permission;
import com.biobac.users.entity.Role;
import com.biobac.users.entity.User;
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

    public JwtUtil(@Value("${jwt.secret}") String jwtSecret,
                   @Value("${jwt.expiration-ms}") long jwtExpirationMs) {
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        this.jwtExpirationMs = jwtExpirationMs;
    }

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        if (user.getRoles() != null) {
            List<String> roles = user.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.toList());
            claims.put("roles", roles);
            // Also add permissions derived from roles
            Set<String> perms = new HashSet<>();
            user.getRoles().forEach(role -> {
                if (role.getPermissions() != null) {
                    perms.addAll(role.getPermissions().stream()
                            .map(Permission::getName)
                            .collect(Collectors.toSet()));
                }
            });
            if (!perms.isEmpty()) claims.put("perms", perms);
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

    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Jws<Claims> parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
    }
}
