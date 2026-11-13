package com.Syukri.LibraryManagement.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "User")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "Id")
    private UUID id;

    @Column(name = "Username", nullable = false, length = 150)
    private String username;

    @Column(name = "Password",nullable = false, unique = true)
    private String password;

    @Column(name = "Email", nullable = false, unique = true, length = 254)
    private String email;
}
