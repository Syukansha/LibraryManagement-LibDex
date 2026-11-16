package com.Syukri.LibraryManagement.Service.Impl;

import com.Syukri.LibraryManagement.Dto.LoanDTO;
import com.Syukri.LibraryManagement.Entity.BookCopy;
import com.Syukri.LibraryManagement.Entity.Books;
import com.Syukri.LibraryManagement.Entity.Borrower;
import com.Syukri.LibraryManagement.Entity.Loan;
import com.Syukri.LibraryManagement.Exception.BookAlreadyBorrowedException;
import com.Syukri.LibraryManagement.Exception.BookNotOnLoanException;
import com.Syukri.LibraryManagement.Exception.DataIDNotFound;
import com.Syukri.LibraryManagement.Repository.BookCopyRepository;
import com.Syukri.LibraryManagement.Repository.BooksRepository;
import com.Syukri.LibraryManagement.Repository.BorrowerRepository;
import com.Syukri.LibraryManagement.Repository.LoanRepository;
import com.Syukri.LibraryManagement.Service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class LoanServiceImpl implements LoanService {

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private BookCopyRepository bookCopyRepository;

    @Autowired
    private BooksRepository booksRepository;

    @Autowired
    private BorrowerRepository borrowerRepository;

    @Override
    @Transactional
    public LoanDTO borrowBook(UUID borrowerId, UUID bookId) {
        Borrower borrower = borrowerRepository.findById(borrowerId)
                .orElseThrow(() -> new DataIDNotFound(borrowerId));

        Books book = booksRepository.findById(bookId)
                .orElseThrow(() -> new DataIDNotFound(bookId));

        if (loanRepository.findByBookCopyBookIdAndReturnedAtIsNull(bookId).isPresent()) {
            throw new BookAlreadyBorrowedException(bookId);
        }

        BookCopy copy = bookCopyRepository.findByBookId(bookId)
                .orElseThrow(() -> new RuntimeException("No book copy found for book: " + bookId));

        if (!copy.isAvailable()) {
            throw new BookAlreadyBorrowedException(bookId);
        }

        copy.setAvailable(false);
        bookCopyRepository.save(copy);

        Loan loan = Loan.builder()
                .borrower(borrower)
                .bookCopy(copy)
                .borrowedAt(Instant.now())
                .build();

        return toDto(loanRepository.save(loan));
    }

    @Override
    @Transactional
    public LoanDTO returnBook(UUID bookId) {
        Loan loan = loanRepository.findByBookCopyBookIdAndReturnedAtIsNull(bookId)
                .orElseThrow(() -> new BookNotOnLoanException(bookId));

        loan.markReturned(Instant.now());

        BookCopy copy = loan.getBookCopy();
        copy.setAvailable(true);
        bookCopyRepository.save(copy);

        return toDto(loanRepository.save(loan));
    }

    private LoanDTO toDto(Loan loan) {
        return LoanDTO.builder()
                .loanId(loan.getId())
                .borrowerId(loan.getBorrower().getId())
                .bookId(loan.getBookCopy().getBook().getId())
                .borrowedAt(loan.getBorrowedAt())
                .returnedAt(loan.getReturnedAt())
                .build();
    }
}
