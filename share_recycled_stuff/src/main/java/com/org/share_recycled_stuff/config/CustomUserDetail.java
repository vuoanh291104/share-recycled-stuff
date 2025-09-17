package com.org.share_recycled_stuff.config;

import com.org.share_recycled_stuff.entity.Account;
import com.org.share_recycled_stuff.entity.UserRole;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class CustomUserDetail implements UserDetails {
    private final Long accountId;
    private final String email;
    private final String password;
    private final boolean isVerified;
    private final boolean isLocked;
    private final Set<SimpleGrantedAuthority> authorities;
    private final String googleId;

    public CustomUserDetail(Account account, Set<UserRole> userRoles) {
        this.accountId = account.getId();
        this.email = account.getEmail();
        this.password = account.getPassword();
        this.isVerified = account.isVerified();
        this.isLocked = account.isLocked();
        this.googleId = account.getGoogleId();

        this.authorities = userRoles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleType().name()))
                .collect(Collectors.toSet());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !isLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isVerified && !isLocked;
    }

    public boolean hasRole(String role) {
        return authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_" + role));
    }
}
