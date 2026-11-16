package com.Syukri.LibraryManagement.Exception;

public class BookNotFound extends RuntimeException {
    public BookNotFound(String name) {
        super("Book not found with name: "+name);
    }

}
