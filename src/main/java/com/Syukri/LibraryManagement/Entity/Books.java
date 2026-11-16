package com.Syukri.LibraryManagement.Entity;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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

    @NotBlank(message = "title is required")
    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @NotBlank(message = "author is required")
    @Column(name = "author", nullable = false, length = 255)
    private String author;

    @NotBlank(message = "isbn is required")
    @JsonProperty("isbn")
    @JsonAlias("ISBN")
    @Column(name = "isbn", nullable = false, length = 36)
    private String ISBN;
}
