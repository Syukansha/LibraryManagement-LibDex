package com.Syukri.LibraryManagement.Dto;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.util.UUID;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BooksDTO {

    private UUID id;
    private String title;
    private String author;
    private String ISBN;
}
