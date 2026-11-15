package com.Syukri.LibraryManagement.Service;

import com.Syukri.LibraryManagement.Dto.BooksDTO;
import com.Syukri.LibraryManagement.Entity.Books;

import java.util.Optional;
import java.util.UUID;


public interface BookService {

    Optional<BooksDTO> getBookByTitle(String title);
    Books registerBook(Books book);
    BooksDTO updateBook(Optional<Books> book);
    void deleteBook(UUID bookID);
}
