package com.udea.innosistemas.dto;

import com.udea.innosistemas.entity.UserRole;

public class UserInfo {

    private Long id;
    private String email;
    private UserRole role;

    public UserInfo() {
    }

    public UserInfo(Long id, String email, UserRole role) {
        this.id = id;
        this.email = email;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
}