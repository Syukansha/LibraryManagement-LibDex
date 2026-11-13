package com.Syukri.LibraryManagement.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "BookCopy")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookCopy {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BookId", nullable = false)
    private Books book;

    @Column(name = "Availability", nullable = false)
    private boolean available = true;

}
