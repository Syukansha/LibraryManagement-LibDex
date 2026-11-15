package com.Syukri.LibraryManagement.Controller;

import com.Syukri.LibraryManagement.Dto.UserDTO;
import com.Syukri.LibraryManagement.Entity.Users;
import com.Syukri.LibraryManagement.Security.AccountCredentials;
import com.Syukri.LibraryManagement.Security.JwtImpl;
import com.Syukri.LibraryManagement.Service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping("api/users")
public class UserController {

    @Autowired
    private UsersService usersService;


    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtImpl jwtImpl;

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticateUser(@RequestBody AccountCredentials accountCredentials) {
        UsernamePasswordAuthenticationToken creds = new UsernamePasswordAuthenticationToken(accountCredentials.getUsername(), accountCredentials.getPassword());

        Authentication auth = authenticationManager.authenticate(creds);

        String jwts = jwtImpl.generateToken(auth.getName());
        Map<String, String> body = new HashMap<>();
        body.put("token", jwts);

        return ResponseEntity.ok(body);
    }

    @GetMapping("/get-user/{username}")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable String username) {
        try {
            Optional<UserDTO> userDTO = usersService.getUserByUsername(username);
            return ResponseEntity.ok().body(userDTO.get());

        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }

    }

    @PostMapping("/register-user")
    public ResponseEntity<?> registerUser(@RequestBody Users user) {
        try {
            UserDTO userDTO = new UserDTO();
            userDTO.setUsername(user.getUsername());
            usersService.registerUser(user);
            return ResponseEntity.ok().body(userDTO);

        }catch (Exception e){
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error registering user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @PatchMapping("/edit-user")
    public ResponseEntity<?> editUser(@RequestBody Users user) {
        try {
            if(user.getId() != null) {
                return ResponseEntity.ok().body(usersService.updateUser(Optional.ofNullable(user)));
            }else {
                return ResponseEntity.badRequest().body("UserID is null");
            }
        }catch (Exception e){
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error updating user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }


}
