package com.Syukri.LibraryManagement.Repository;

import com.Syukri.LibraryManagement.Entity.Books;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;


public interface BooksRepository extends JpaRepository<Books, UUID> {
    Optional<Books> findByTitle(String title);
    Optional<Books> findById(UUID id);

    @Query("SELECT b FROM Books b WHERE b.ISBN = :isbn")
    Optional<Books> findByISBN(@Param("isbn") String isbn);
}
