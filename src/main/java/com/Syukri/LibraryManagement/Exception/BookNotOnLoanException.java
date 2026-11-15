package com.Syukri.LibraryManagement.Exception;

import java.util.UUID;

public class BookNotOnLoanException extends RuntimeException {
    public BookNotOnLoanException(UUID bookId) {
        super("Book is not currently on loan: " + bookId);
    }
}
