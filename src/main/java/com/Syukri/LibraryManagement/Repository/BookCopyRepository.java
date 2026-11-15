package com.Syukri.LibraryManagement.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.Syukri.LibraryManagement.Entity.BookCopy;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookCopyRepository extends JpaRepository<BookCopy, UUID> {
    Optional<BookCopy> findById(UUID id);
    Optional<BookCopy> findByBookId(UUID bookId);

    @Query("SELECT c FROM BookCopy c WHERE c.book.ISBN = :isbn")
    List<BookCopy> findByISBN(@Param("isbn") String isbn);
}
