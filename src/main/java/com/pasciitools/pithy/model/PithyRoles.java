package com.pasciitools.pithy.model;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

public enum PithyRoles {
    USER(new SimpleGrantedAuthority("ROLE_USER")),
    ADMIN(new SimpleGrantedAuthority("ROLE_ADMIN"));
    private final SimpleGrantedAuthority role;

    PithyRoles(SimpleGrantedAuthority role) {
        this.role = role;
    }

    public SimpleGrantedAuthority getRole() {
        return role;
    }
}
