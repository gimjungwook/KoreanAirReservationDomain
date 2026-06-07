package com.koreanair.reservation.domain.user;

import java.time.LocalDateTime;

public abstract class User {

    protected Long userId;
    protected String name;
    protected String email;
    protected String phoneNumber;
    protected LocalDateTime registeredAt;

    public Long getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
