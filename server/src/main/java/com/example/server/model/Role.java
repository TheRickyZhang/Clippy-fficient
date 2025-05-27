package com.example.server.model;

import jakarta.persistence.*;

@Entity @Table(name="roles")
public class Role {
    @Id @GeneratedValue
    Long id;

    @Column(nullable=false, unique=true)
    String name;
}