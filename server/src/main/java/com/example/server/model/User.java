package com.example.server.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.*;


// TODO: Add Lombok

@Entity
@Table(
        name = "users",
        uniqueConstraints = @UniqueConstraint(columnNames = "email")
)
public class User {
    @Id @GeneratedValue
    Long id;

    @Column(nullable = false)
    String name;

    @Column(nullable = false, unique = true)
    String email;

    @Column(nullable = false)
    String passwordHash;

    @Column(nullable = false, updatable = false)
    Instant createdAt;

    @Column(nullable = false)
    Instant updatedAt;

    @Column(nullable = false)
    Instant lastSyncAt;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    Set<Role> roles = new HashSet<>();

    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    List<Suggestion> suggestions = new ArrayList<>();

    protected User() { }

    public User(String name, String email, String passwordHash) {
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.lastSyncAt = Instant.now();
    }

    @PrePersist
    void onCreate() {
        createdAt = updatedAt = Instant.now();
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }

    public void addSuggestion(Suggestion s) {
        suggestions.add(s);
        s.setUser(this);
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public long getId() {
        return id;
    }

    // getters / setters if you need them
}
