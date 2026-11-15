package com.Syukri.LibraryManagement.Service;

import com.Syukri.LibraryManagement.Dto.UserDTO;
import com.Syukri.LibraryManagement.Entity.Users;

import java.util.Optional;

public interface UsersService {

    Optional<UserDTO> getUserByUsername(String username);
    public Users registerUser(Users user);
    boolean checkIfUsernameEmailExists(String username, String email);
}
