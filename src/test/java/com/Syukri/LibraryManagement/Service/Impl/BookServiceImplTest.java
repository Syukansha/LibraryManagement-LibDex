package com.Syukri.LibraryManagement.Service.Impl;

import com.Syukri.LibraryManagement.Dto.BooksDTO;
import com.Syukri.LibraryManagement.Entity.BookCopy;
import com.Syukri.LibraryManagement.Entity.Books;
import com.Syukri.LibraryManagement.Exception.UserNotFound;
import com.Syukri.LibraryManagement.Repository.BookCopyRepository;
import com.Syukri.LibraryManagement.Repository.BooksRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    private BooksRepository booksRepository;

    @Mock
    private BookCopyRepository bookCopyRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    void getBookByTitle_returnsDto_whenBookExists() {
        UUID id = UUID.randomUUID();
        Books book = Books.builder().id(id).title("Dune").author("Herbert").ISBN("978-0").available(true).build();
        when(booksRepository.findByTitle("Dune")).thenReturn(Optional.of(book));

        Optional<BooksDTO> result = bookService.getBookByTitle("Dune");

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(id);
        assertThat(result.get().getTitle()).isEqualTo("Dune");
        assertThat(result.get().getAuthor()).isEqualTo("Herbert");
        assertThat(result.get().getISBN()).isEqualTo("978-0");
    }

    @Test
    void getBookByTitle_throws_whenBookMissing() {
        when(booksRepository.findByTitle("Missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.getBookByTitle("Missing"))
                .isInstanceOf(UserNotFound.class);
    }

    @Test
    void registerBook_savesBookAndCreatesAnAvailableCopy() {
        Books input = Books.builder().title("Dune").author("Herbert").ISBN("978-0").build();
        UUID savedId = UUID.randomUUID();
        Books savedBook = Books.builder().id(savedId).title("Dune").author("Herbert").ISBN("978-0").available(true).build();
        when(booksRepository.save(any(Books.class))).thenReturn(savedBook);

        Books result = bookService.registerBook(input);

        assertThat(result).isSameAs(savedBook);

        // a BookCopy must be created, linked to the saved book and available
        ArgumentCaptor<BookCopy> captor = ArgumentCaptor.forClass(BookCopy.class);
        verify(bookCopyRepository).save(captor.capture());
        BookCopy createdCopy = captor.getValue();
        assertThat(createdCopy.getBook()).isSameAs(savedBook);
        assertThat(createdCopy.isAvailable()).isTrue();
    }

    @Test
    void registerBook_throws_whenSaveFails() {
        Books input = Books.builder().title("Dune").author("Herbert").ISBN("978-0").build();
        when(booksRepository.save(any(Books.class))).thenThrow(new RuntimeException("db down"));

        assertThatThrownBy(() -> bookService.registerBook(input))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("db down");
        verify(bookCopyRepository, never()).save(any());
    }

    @Test
    void updateBook_returnsUpdatedDto_whenBookExists() {
        UUID id = UUID.randomUUID();
        Books existing = Books.builder().id(id).title("old").author("oldAuthor").ISBN("old-isbn").available(true).build();
        Books request = Books.builder().id(id).title("new").build(); // author/ISBN left null
        when(booksRepository.findById(id)).thenReturn(Optional.of(existing));

        BooksDTO result = bookService.updateBook(Optional.of(request));

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getTitle()).isEqualTo("new");
        // fields not present in the request stay unchanged
        assertThat(result.getAuthor()).isEqualTo("oldAuthor");
        assertThat(result.getISBN()).isEqualTo("old-isbn");
        verify(booksRepository).save(existing);
    }

    @Test
    void updateBook_throws_whenIdNotFound() {
        UUID id = UUID.randomUUID();
        Books request = Books.builder().id(id).title("ghost").build();
        when(booksRepository.findById(id)).thenReturn(Optional.empty());

        // wrapped as RuntimeException by the catch block
        assertThatThrownBy(() -> bookService.updateBook(Optional.of(request)))
                .isInstanceOf(RuntimeException.class);
        verify(booksRepository, never()).save(any());
    }

    @Test
    void updateBook_throws_whenNoDataSupplied() {
        assertThatThrownBy(() -> bookService.updateBook(Optional.empty()))
                .isInstanceOf(RuntimeException.class);
        verify(booksRepository, never()).findById(any());
        verify(booksRepository, never()).save(any());
    }

    @Test
    void deleteBook_removesBook_whenIdExists() {
        UUID id = UUID.randomUUID();
        Books book = Books.builder().id(id).title("Dune").build();
        when(booksRepository.findById(id)).thenReturn(Optional.of(book));

        bookService.deleteBook(id);

        verify(booksRepository).delete(book);
    }

    @Test
    void deleteBook_throws_whenIdMissing() {
        UUID id = UUID.randomUUID();
        when(booksRepository.findById(id)).thenReturn(Optional.empty());

        // deleteBook wraps everything (including UserIDNotFound) in a plain RuntimeException
        assertThatThrownBy(() -> bookService.deleteBook(id))
                .isInstanceOf(RuntimeException.class);
        verify(booksRepository, never()).delete(any());
    }
}
