package com.Syukri.LibraryManagement.Exception;

public class UserNotFound extends RuntimeException {
    public UserNotFound(String username) {
        super("User not found with username: "+username);
    }

}
