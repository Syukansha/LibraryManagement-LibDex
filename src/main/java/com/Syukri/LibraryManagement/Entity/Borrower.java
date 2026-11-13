package com.Syukri.LibraryManagement.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "Borrower")
@Getter
@Setter
@NoArgsConstructor
public class Borrower {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "Id")
    private UUID id;

    @Column(name = "Name", nullable = false, length = 150)
    private String name;

    @Column(name = "Email", nullable = false, unique = true, length = 254)
    private String email;
}
