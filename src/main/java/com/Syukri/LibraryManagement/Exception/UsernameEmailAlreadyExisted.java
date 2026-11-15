package com.Syukri.LibraryManagement.Exception;

public class UsernameEmailAlreadyExisted extends RuntimeException {
    public UsernameEmailAlreadyExisted()
    {
        super("Email or Username already exists!");
    }
}
