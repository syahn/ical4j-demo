package com.calendar.data;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * Created by NAVER on 2017-08-11.
 */
public class User extends org.springframework.security.core.userdetails.User{

    private String id;

    public User(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
