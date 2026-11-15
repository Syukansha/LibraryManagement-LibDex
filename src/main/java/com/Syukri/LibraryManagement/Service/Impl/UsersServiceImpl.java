package com.Syukri.LibraryManagement.Service.Impl;

import com.Syukri.LibraryManagement.Dto.UserDTO;
import com.Syukri.LibraryManagement.Entity.Users;
import com.Syukri.LibraryManagement.Exception.UserNotFound;
import com.Syukri.LibraryManagement.Exception.UsernameEmailAlreadyExisted;
import com.Syukri.LibraryManagement.Repository.UsersRepository;
import com.Syukri.LibraryManagement.Service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsersServiceImpl implements UsersService {

    @Autowired
    private UsersRepository usersRepository;


    @Override
    public Optional<UserDTO> getUserByUsername(String username) {
        Optional<Users> user = Optional.ofNullable(usersRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFound(username)));
        if(user.isPresent()){
            UserDTO.UserDTOBuilder userDTO = UserDTO.builder()
                    .id(user.get().getId())
                    .username(user.get().getUsername())
                    .email(user.get().getEmail());
            return Optional.of(userDTO.build());
        }
        else {
            throw new UserNotFound(username);
        }

    }

    @Override
    public Users registerUser(Users user){
        if(checkIfUsernameEmailExists(user.getUsername(), user.getEmail())) {
            try {
                Users.UsersBuilder builder = Users.builder()
                        .username(user.getUsername())
                        .password(new BCryptPasswordEncoder().encode(user.getPassword()))
                        .email(user.getEmail());
                return usersRepository.save(builder.build());
            }catch (RuntimeException e) {
                throw new RuntimeException(e.getMessage());
            }
        }else {
            throw new UsernameEmailAlreadyExisted();
        }

    }



    @Override
    public boolean checkIfUsernameEmailExists(String username, String email) {
        if(usersRepository.findByUsername(username).isPresent() || usersRepository.findByEmail(email).isPresent()){
            return false;
        }else {
            return true;
        }
    }


}
