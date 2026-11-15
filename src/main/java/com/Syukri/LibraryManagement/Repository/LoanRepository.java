package com.Syukri.LibraryManagement.Repository;

import com.Syukri.LibraryManagement.Entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LoanRepository extends JpaRepository<Loan, UUID> {
    Optional<Loan> findByBookCopyBookIdAndReturnedAtIsNull(UUID bookId);
}
