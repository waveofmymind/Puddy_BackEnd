package com.team.puddy.global.config.auth;

import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Slf4j
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtUserDetails implements UserDetails {
    private Long id;
    private String role;
    private boolean isAuthenticated;


    public JwtUserDetails(DecodedJWT decodedJWT) {
        id = decodedJWT.getClaim("id").asLong();
        role = decodedJWT.getClaim("auth").asString();
        isAuthenticated = true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (isAuthenticated) {
            return Collections.singletonList(new SimpleGrantedAuthority(role));
        }
        return Collections.singletonList(new SimpleGrantedAuthority(null));
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    public Long getUserId() {
        return id;
    }
}