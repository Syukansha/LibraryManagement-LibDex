package com.Syukri.LibraryManagement.Service;

import com.Syukri.LibraryManagement.Dto.LoanDTO;

import java.util.UUID;

public interface LoanService {
    LoanDTO borrowBook(UUID borrowerId, UUID bookId);
    LoanDTO returnBook(UUID bookId);
}
