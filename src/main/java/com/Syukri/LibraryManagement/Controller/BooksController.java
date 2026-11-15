package com.Syukri.LibraryManagement.Controller;

import com.Syukri.LibraryManagement.Dto.BooksDTO;
import com.Syukri.LibraryManagement.Entity.Books;
import com.Syukri.LibraryManagement.Service.BookService;
import jakarta.validation.Valid;
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
@RequestMapping("api/books")
public class BooksController {
    @Autowired
    private BookService bookService;

    @GetMapping("/get-book/{title}")
    public ResponseEntity<BooksDTO> getBookByTitle(@PathVariable String title) {
        try {
            Optional<BooksDTO> booksDTO = bookService.getBookByTitle(title);
            return ResponseEntity.ok().body(booksDTO.get());

        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/register-book")
    public ResponseEntity<?> registerBook(@Valid @RequestBody Books getBook) {
        try {
            Books book = bookService.registerBook(getBook);
            BooksDTO.BooksDTOBuilder bookDto = BooksDTO.builder()
                    .id(book.getId())
                    .title(book.getTitle())
                    .author(book.getAuthor())
                    .ISBN(book.getISBN())
                    .available(book.getAvailable());
            return ResponseEntity.ok().body(bookDto.build());

        }catch (Exception e){
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error registering book: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @PatchMapping("/edit-book")
    public ResponseEntity<?> updateBook(@RequestBody Books book) {
        try {
            if(book.getId() != null) {
                return ResponseEntity.ok().body(bookService.updateBook(Optional.ofNullable(book)));
            }else {
                return ResponseEntity.badRequest().body("Book ID is null");
            }
        }catch (Exception e){
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error updating book: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @DeleteMapping("/delete-book/{bookID}")
    public ResponseEntity<?> deleteBook(@PathVariable UUID bookID) {
        try {
            bookService.deleteBook(bookID);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Successfully deleted selected book ID: "+bookID);
            return ResponseEntity.ok().body(response);
        }catch (Exception e){
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error deleting book: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

}
