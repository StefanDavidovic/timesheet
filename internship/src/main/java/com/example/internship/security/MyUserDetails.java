package com.example.internship.security;

import com.example.internship.model.TeamMember;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MyUserDetails implements UserDetails {
    private Long id;

    private String username;

    @JsonIgnore
    private String password;

    public MyUserDetails(Long id, String username, String password,
                         Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
    }

    public static MyUserDetails build(TeamMember user) {
        var role = user.getRole();
        GrantedAuthority authority = new SimpleGrantedAuthority(role.name());
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(authority);

        return new MyUserDetails(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                authorities);
    }

    private Collection<? extends GrantedAuthority> authorities;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public Long getId() {
        return id;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
