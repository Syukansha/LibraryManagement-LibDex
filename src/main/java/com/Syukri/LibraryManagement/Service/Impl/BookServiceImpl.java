package com.Syukri.LibraryManagement.Service.Impl;

import com.Syukri.LibraryManagement.Dto.BooksDTO;
import com.Syukri.LibraryManagement.Entity.BookCopy;
import com.Syukri.LibraryManagement.Entity.Books;
import com.Syukri.LibraryManagement.Exception.UserIDNotFound;
import com.Syukri.LibraryManagement.Exception.UserNotFound;
import com.Syukri.LibraryManagement.Repository.BookCopyRepository;
import com.Syukri.LibraryManagement.Repository.BooksRepository;
import com.Syukri.LibraryManagement.Service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class BookServiceImpl implements BookService {
    @Autowired
    private BooksRepository booksRepository;

    @Autowired
    private BookCopyRepository bookCopyRepository;

    @Override
    public Optional<BooksDTO> getBookByTitle(String title) {
        Optional<Books> book = Optional.ofNullable(booksRepository.findByTitle(title)
                .orElseThrow(() -> new UserNotFound(title)));
        if(book.isPresent()){
            BooksDTO.BooksDTOBuilder booksDTO = BooksDTO.builder()
                    .id(book.get().getId())
                    .title(book.get().getTitle())
                    .author(book.get().getAuthor())
                    .ISBN(book.get().getISBN())
                    .available(book.get().getAvailable());
            return Optional.of(booksDTO.build());
        }
        else {
            throw new UserNotFound(title);
        }
    }

    @Override
    @Transactional
    public Books registerBook(Books book) {
        try {
            Books.BooksBuilder bookBuilder = Books.builder()
                    .title(book.getTitle())
                    .author(book.getAuthor())
                    .ISBN(book.getISBN());
            if(book.getAvailable() != null) {
                bookBuilder.available(book.getAvailable());
            }
            Books savedBook = booksRepository.save(bookBuilder.build());

            BookCopy copy = new BookCopy();
            copy.setBook(savedBook);
            copy.setAvailable(true);
            bookCopyRepository.save(copy);

            return savedBook;
        }catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public BooksDTO updateBook(Optional<Books> book) {
        try {
            Books request = book.orElseThrow(() ->
                    new IllegalArgumentException("Book data is required"));

            Books existingBook = booksRepository.findById(request.getId())
                    .orElseThrow(() ->
                            new UserNotFound(request.getTitle()));

            if (request.getTitle() != null) {
                existingBook.setTitle(request.getTitle());
            }

            if (request.getAuthor() != null) {
                existingBook.setAuthor(request.getAuthor());
            }

            if (request.getISBN() != null) {
                existingBook.setISBN(request.getISBN());
            }

            if (request.getAvailable() != null) {
                existingBook.setAvailable(request.getAvailable());
            }

            BooksDTO.BooksDTOBuilder booksDTO = BooksDTO.builder()
                    .id(existingBook.getId())
                    .title(existingBook.getTitle())
                    .author(existingBook.getAuthor())
                    .ISBN(existingBook.getISBN())
                    .available(existingBook.getAvailable());
            booksRepository.save(existingBook);
            return booksDTO.build();

        }catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void deleteBook(UUID bookID) {
        try {
            Books getBook = booksRepository.findById(bookID).orElseThrow(() -> {
                throw new UserIDNotFound(bookID);});
            booksRepository.delete(getBook);
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

}
