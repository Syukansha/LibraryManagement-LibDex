package com.Syukri.LibraryManagement.Service;

import com.Syukri.LibraryManagement.Dto.BorrowerDTO;
import com.Syukri.LibraryManagement.Entity.Borrower;

import java.util.Optional;
import java.util.UUID;


public interface BorrowerService {

    Optional<BorrowerDTO> getBorrowerByName(String username);
    Borrower registerBorrower(Borrower borrower);
    boolean checkIfNameEmailExists(String name, String email);
    BorrowerDTO updateBorrower(Optional<Borrower> borrower);
    public void deleteBorrower(UUID borrowerID);
}
