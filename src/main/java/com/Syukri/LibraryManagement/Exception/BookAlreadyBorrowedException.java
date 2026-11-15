package com.Syukri.LibraryManagement.Exception;

import java.util.UUID;

public class BookAlreadyBorrowedException extends RuntimeException {
    public BookAlreadyBorrowedException(UUID bookId) {
        super("Book is already on loan and not yet returned: " + bookId);
    }
}
