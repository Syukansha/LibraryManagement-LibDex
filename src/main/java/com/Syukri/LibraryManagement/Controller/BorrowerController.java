package com.Syukri.LibraryManagement.Controller;

import com.Syukri.LibraryManagement.Dto.BorrowerDTO;
import com.Syukri.LibraryManagement.Dto.UserDTO;
import com.Syukri.LibraryManagement.Entity.Borrower;
import com.Syukri.LibraryManagement.Entity.Users;
import com.Syukri.LibraryManagement.Service.BorrowerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping("api/borrower")
public class BorrowerController {
    @Autowired
    private BorrowerService borrowerService;

    @GetMapping("/get-borrower/{name}")
    public ResponseEntity<BorrowerDTO> getUserByUsername(@PathVariable String name) {
        try {
            Optional<BorrowerDTO> borrowerDTO = borrowerService.getBorrowerByName(name);
            return ResponseEntity.ok().body(borrowerDTO.get());

        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/register-borrower")
    public ResponseEntity<?> registerBorrower(@RequestBody Borrower getBorrower) {
        try {
            Borrower borrower = borrowerService.registerBorrower(getBorrower);
            UserDTO.UserDTOBuilder borrowerDto = UserDTO.builder()
                    .id(borrower.getId())
                    .username(borrower.getName())
                    .email(borrower.getEmail());
            return ResponseEntity.ok().body(borrowerDto.build());

        }catch (Exception e){
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error registering user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @PatchMapping("/edit-borrower")
    public ResponseEntity<?> updateUser(@RequestBody Borrower borrower) {
        try {
            if(borrower.getId() != null) {
                return ResponseEntity.ok().body(borrowerService.updateBorrower(Optional.ofNullable(borrower)));
            }else {
                return ResponseEntity.badRequest().body("Borrower ID is null");
            }
        }catch (Exception e){
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error updating borrower: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @DeleteMapping("/delete-borrower/{borrowerID}")
    public ResponseEntity<?> deleteUser(@PathVariable UUID borrowerID) {
        try {
            borrowerService.deleteBorrower(borrowerID);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Successfully deleted selected borrower ID: "+borrowerID);
            return ResponseEntity.ok().body(response);
        }catch (Exception e){
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error deleting borrower: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

}
