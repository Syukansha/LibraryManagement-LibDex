package com.Syukri.LibraryManagement.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "loans")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "BorrowerId", nullable = false)
    private Borrower borrower;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "BookCopyId", nullable = false)
    private BookCopy bookCopy;

    @Column(name = "BorrowedAt", nullable = false, updatable = false)
    private Instant borrowedAt;

    @Column(name = "ReturnedAt")
    private Instant returnedAt;

    public void markReturned(Instant returnedAt) {
        Objects.requireNonNull(returnedAt);

        if (this.returnedAt != null) {
            throw new IllegalStateException("Loan has already been returned");
        }

        if (returnedAt.isBefore(borrowedAt)) {
            throw new IllegalArgumentException(
                    "Return time cannot be before borrowing time"
            );
        }

        this.returnedAt = returnedAt;
    }
}
