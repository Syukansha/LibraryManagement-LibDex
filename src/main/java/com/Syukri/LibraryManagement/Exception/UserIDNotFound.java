package com.Syukri.LibraryManagement.Exception;

import java.util.UUID;

public class UserIDNotFound extends RuntimeException {
    public UserIDNotFound(UUID userId) {

        super("User not found with ID: "+ userId);
    }

}
