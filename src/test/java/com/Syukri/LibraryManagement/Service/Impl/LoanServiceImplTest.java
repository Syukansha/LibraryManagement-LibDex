package com.Syukri.LibraryManagement.Service.Impl;

import com.Syukri.LibraryManagement.Dto.LoanDTO;
import com.Syukri.LibraryManagement.Entity.BookCopy;
import com.Syukri.LibraryManagement.Entity.Books;
import com.Syukri.LibraryManagement.Entity.Borrower;
import com.Syukri.LibraryManagement.Entity.Loan;
import com.Syukri.LibraryManagement.Exception.BookAlreadyBorrowedException;
import com.Syukri.LibraryManagement.Exception.BookNotOnLoanException;
import com.Syukri.LibraryManagement.Exception.UserIDNotFound;
import com.Syukri.LibraryManagement.Repository.BookCopyRepository;
import com.Syukri.LibraryManagement.Repository.BooksRepository;
import com.Syukri.LibraryManagement.Repository.BorrowerRepository;
import com.Syukri.LibraryManagement.Repository.LoanRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanServiceImplTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private BookCopyRepository bookCopyRepository;

    @Mock
    private BooksRepository booksRepository;

    @Mock
    private BorrowerRepository borrowerRepository;

    @InjectMocks
    private LoanServiceImpl loanService;

    private BookCopy newCopy(Books book, boolean available) {
        BookCopy copy = new BookCopy();
        copy.setId(UUID.randomUUID());
        copy.setBook(book);
        copy.setAvailable(available);
        return copy;
    }

    @Test
    void borrowBook_marksCopyUnavailableAndReturnsDto() {
        UUID borrowerId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();
        Borrower borrower = Borrower.builder().id(borrowerId).name("alice").email("a@test.com").build();
        Books book = Books.builder().id(bookId).title("Dune").author("Herbert").ISBN("978-0").build();
        BookCopy copy = newCopy(book, true);

        when(borrowerRepository.findById(borrowerId)).thenReturn(Optional.of(borrower));
        when(booksRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(loanRepository.findByBookCopyBookIdAndReturnedAtIsNull(bookId)).thenReturn(Optional.empty());
        when(bookCopyRepository.findByBookId(bookId)).thenReturn(Optional.of(copy));
        when(loanRepository.save(any(Loan.class))).thenAnswer(inv -> inv.getArgument(0));

        LoanDTO result = loanService.borrowBook(borrowerId, bookId);

        assertThat(copy.isAvailable()).isFalse();
        verify(bookCopyRepository).save(copy);
        assertThat(result.getBorrowerId()).isEqualTo(borrowerId);
        assertThat(result.getBookId()).isEqualTo(bookId);
        assertThat(result.getBorrowedAt()).isNotNull();
        assertThat(result.getReturnedAt()).isNull();
    }

    @Test
    void borrowBook_throws_whenBorrowerNotFound() {
        UUID borrowerId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();
        when(borrowerRepository.findById(borrowerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> loanService.borrowBook(borrowerId, bookId))
                .isInstanceOf(UserIDNotFound.class);
        verify(loanRepository, never()).save(any());
    }

    @Test
    void borrowBook_throws_whenBookNotFound() {
        UUID borrowerId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();
        Borrower borrower = Borrower.builder().id(borrowerId).name("alice").email("a@test.com").build();
        when(borrowerRepository.findById(borrowerId)).thenReturn(Optional.of(borrower));
        when(booksRepository.findById(bookId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> loanService.borrowBook(borrowerId, bookId))
                .isInstanceOf(UserIDNotFound.class);
        verify(loanRepository, never()).save(any());
    }

    @Test
    void borrowBook_throws_whenBookAlreadyOnLoan() {
        UUID borrowerId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();
        Borrower borrower = Borrower.builder().id(borrowerId).name("alice").email("a@test.com").build();
        Books book = Books.builder().id(bookId).title("Dune").build();
        Loan openLoan = Loan.builder().id(UUID.randomUUID()).borrower(borrower).build();

        when(borrowerRepository.findById(borrowerId)).thenReturn(Optional.of(borrower));
        when(booksRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(loanRepository.findByBookCopyBookIdAndReturnedAtIsNull(bookId)).thenReturn(Optional.of(openLoan));

        assertThatThrownBy(() -> loanService.borrowBook(borrowerId, bookId))
                .isInstanceOf(BookAlreadyBorrowedException.class);
        verify(bookCopyRepository, never()).save(any());
    }

    @Test
    void borrowBook_throws_whenNoCopyExists() {
        UUID borrowerId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();
        Borrower borrower = Borrower.builder().id(borrowerId).name("alice").email("a@test.com").build();
        Books book = Books.builder().id(bookId).title("Dune").build();

        when(borrowerRepository.findById(borrowerId)).thenReturn(Optional.of(borrower));
        when(booksRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(loanRepository.findByBookCopyBookIdAndReturnedAtIsNull(bookId)).thenReturn(Optional.empty());
        when(bookCopyRepository.findByBookId(bookId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> loanService.borrowBook(borrowerId, bookId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No book copy found");
        verify(loanRepository, never()).save(any());
    }

    @Test
    void borrowBook_throws_whenCopyNotAvailable() {
        UUID borrowerId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();
        Borrower borrower = Borrower.builder().id(borrowerId).name("alice").email("a@test.com").build();
        Books book = Books.builder().id(bookId).title("Dune").build();
        BookCopy copy = newCopy(book, false);

        when(borrowerRepository.findById(borrowerId)).thenReturn(Optional.of(borrower));
        when(booksRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(loanRepository.findByBookCopyBookIdAndReturnedAtIsNull(bookId)).thenReturn(Optional.empty());
        when(bookCopyRepository.findByBookId(bookId)).thenReturn(Optional.of(copy));

        assertThatThrownBy(() -> loanService.borrowBook(borrowerId, bookId))
                .isInstanceOf(BookAlreadyBorrowedException.class);
        verify(bookCopyRepository, never()).save(any());
        verify(loanRepository, never()).save(any());
    }

    @Test
    void returnBook_marksCopyAvailableAndSetsReturnedAt() {
        UUID bookId = UUID.randomUUID();
        Books book = Books.builder().id(bookId).title("Dune").build();
        BookCopy copy = newCopy(book, false);
        Borrower borrower = Borrower.builder().id(UUID.randomUUID()).name("alice").email("a@test.com").build();
        Loan loan = Loan.builder()
                .id(UUID.randomUUID())
                .borrower(borrower)
                .bookCopy(copy)
                .borrowedAt(Instant.now().minusSeconds(3600))
                .build();

        when(loanRepository.findByBookCopyBookIdAndReturnedAtIsNull(bookId)).thenReturn(Optional.of(loan));
        when(loanRepository.save(any(Loan.class))).thenAnswer(inv -> inv.getArgument(0));

        LoanDTO result = loanService.returnBook(bookId);

        assertThat(copy.isAvailable()).isTrue();
        verify(bookCopyRepository).save(copy);
        assertThat(result.getReturnedAt()).isNotNull();
        assertThat(result.getBookId()).isEqualTo(bookId);
    }

    @Test
    void returnBook_throws_whenBookNotOnLoan() {
        UUID bookId = UUID.randomUUID();
        when(loanRepository.findByBookCopyBookIdAndReturnedAtIsNull(bookId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> loanService.returnBook(bookId))
                .isInstanceOf(BookNotOnLoanException.class);
        verify(bookCopyRepository, never()).save(any());
    }
}
