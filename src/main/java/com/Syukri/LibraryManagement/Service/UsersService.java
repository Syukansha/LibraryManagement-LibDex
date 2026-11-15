package com.Syukri.LibraryManagement.Service;

import com.Syukri.LibraryManagement.Dto.UserDTO;
import com.Syukri.LibraryManagement.Entity.Users;

import java.util.Optional;
import java.util.UUID;

public interface UsersService {

    Optional<UserDTO> getUserByUsername(String username);
    Users registerUser(Users user);
    boolean checkIfUsernameEmailExists(String username, String email);
    UserDTO updateUser(Optional<Users> user);
}
