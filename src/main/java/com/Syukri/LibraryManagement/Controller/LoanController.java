package com.Syukri.LibraryManagement.Controller;

import com.Syukri.LibraryManagement.Dto.LoanRequestDTO;
import com.Syukri.LibraryManagement.Exception.BookAlreadyBorrowedException;
import com.Syukri.LibraryManagement.Exception.BookNotOnLoanException;
import com.Syukri.LibraryManagement.Exception.DataIDNotFound;
import com.Syukri.LibraryManagement.Service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping("api/loans")
public class LoanController {

    @Autowired
    private LoanService loanService;

    @PostMapping("/borrow")
    public ResponseEntity<?> borrowBook(@RequestBody LoanRequestDTO request) {
        try {
            return ResponseEntity.ok().body(
                    loanService.borrowBook(request.getBorrowerId(), request.getBookId()));
        } catch (BookAlreadyBorrowedException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error(e));
        } catch (DataIDNotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error(e));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error(e));
        }
    }

    @PostMapping("/return/{bookId}")
    public ResponseEntity<?> returnBook(@PathVariable UUID bookId) {
        try {
            return ResponseEntity.ok().body(loanService.returnBook(bookId));
        } catch (BookNotOnLoanException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error(e));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error(e));
        }
    }

    private Map<String, Object> error(Exception e) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("message", e.getMessage());
        return errorResponse;
    }
}
