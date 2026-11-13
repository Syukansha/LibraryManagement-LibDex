package com.Syukri.LibraryManagement.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "books")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Books {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "Id")
    private UUID id;

    @Column(name = "Title", nullable = false, length = 255)
    private String title;

    @Column(name = "Author", nullable = false, length = 255)
    private String author;

    @Column(name = "ISBN", nullable = false, unique = true, length = 13)
    private String ISBN;

    @Builder.Default
    @Column(name = "Availability", nullable = false)
    private Boolean available = true;
}
