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
    @Column(name = "id")
    private UUID id;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "author", nullable = false, length = 255)
    private String author;

    @Column(name = "isbn", nullable = false, unique = true, length = 13)
    private String ISBN;

    @Builder.Default
    @Column(name = "availability", nullable = false)
    private Boolean available = true;
}
