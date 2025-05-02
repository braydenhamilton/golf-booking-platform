package com.golf.teetimecoreapi.model;

import javax.persistence.*;

@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "golf_nz_member_id", unique = true, nullable = false)
    private String golfNZMemberId;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password; // Store hashed passwords!

    // Getters and setters...
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGolfNZMemberId() {
        return golfNZMemberId;
    }

    public void setGolfNZMemberId(String golfNZMemberId) {
        this.golfNZMemberId = golfNZMemberId;
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
