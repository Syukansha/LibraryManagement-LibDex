package com.Syukri.LibraryManagement.Repository;

import com.Syukri.LibraryManagement.Entity.Borrower;
import com.Syukri.LibraryManagement.Entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;


public interface BorrowerRepository extends JpaRepository<Borrower, UUID> {
    Optional<Borrower> findByName(String username);
    Optional<Borrower> findByEmail(String email);
    Optional<Borrower> findById(UUID id);
}
