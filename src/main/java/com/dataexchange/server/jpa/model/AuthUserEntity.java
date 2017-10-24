package com.dataexchange.server.jpa.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "auth_users")
public class AuthUserEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "au_id")
    private Long id;

    @Column(name = "au_username", length = 31, nullable = false, unique = true)
    private String username;

    @Column(name = "au_password", length = 60, nullable = false)
    private String password;

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
