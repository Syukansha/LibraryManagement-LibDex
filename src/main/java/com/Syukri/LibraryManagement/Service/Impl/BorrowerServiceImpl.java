package com.Syukri.LibraryManagement.Service.Impl;

import com.Syukri.LibraryManagement.Dto.BorrowerDTO;
import com.Syukri.LibraryManagement.Entity.Borrower;
import com.Syukri.LibraryManagement.Exception.DataIDNotFound;
import com.Syukri.LibraryManagement.Exception.UserNotFound;
import com.Syukri.LibraryManagement.Exception.UsernameEmailAlreadyExisted;
import com.Syukri.LibraryManagement.Repository.BorrowerRepository;
import com.Syukri.LibraryManagement.Service.BorrowerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class BorrowerServiceImpl implements BorrowerService {
    @Autowired
    private BorrowerRepository borrowerRepository;

    @Override
    public Optional<BorrowerDTO> getBorrowerByName(String username) {
        Optional<Borrower> borrower = Optional.ofNullable(borrowerRepository.findByName(username)
                .orElseThrow(() -> new UserNotFound(username)));
        if(borrower.isPresent()){
            BorrowerDTO.BorrowerDTOBuilder borrowerDTO = BorrowerDTO.builder()
                    .id(borrower.get().getId())
                    .name(borrower.get().getName())
                    .email(borrower.get().getEmail());
            return Optional.of(borrowerDTO.build());
        }
        else {
            throw new UserNotFound(username);
        }
    }

    @Override
    public Borrower registerBorrower(Borrower borrower) {
        if(checkIfNameEmailExists(borrower.getName(), borrower.getEmail())) {
            try {
                Borrower.BorrowerBuilder borrowerBuilder = Borrower.builder()
                        .name(borrower.getName())
                        .email(borrower.getEmail());
                return borrowerRepository.save(borrowerBuilder.build());
            }catch (RuntimeException e) {
                throw new RuntimeException(e.getMessage());
            }
        }else {
            throw new UsernameEmailAlreadyExisted();
        }
    }

    @Override
    public boolean checkIfNameEmailExists(String name, String email) {
        if(borrowerRepository.findByName(name).isPresent() || borrowerRepository.findByEmail(email).isPresent()){
            return false;
        }else {
            return true;
        }
    }

    @Override
    public BorrowerDTO updateBorrower(Optional<Borrower> borrower) {
        try {
            Borrower request = borrower.orElseThrow(() ->
                    new IllegalArgumentException("Borrower data is required"));

            Borrower existingBorrower = borrowerRepository.findById(request.getId())
                    .orElseThrow(() ->
                            new UserNotFound(request.getName()));

            if (request.getName() != null) {
                existingBorrower.setName(request.getName());
            }

            if (request.getEmail() != null) {
                existingBorrower.setEmail(request.getEmail());
            }
            BorrowerDTO.BorrowerDTOBuilder borrowerDTO = BorrowerDTO.builder()
                    .id(existingBorrower.getId())
                    .email(existingBorrower.getEmail())
                    .name(existingBorrower.getName());
            borrowerRepository.save(existingBorrower);
            return borrowerDTO.build();

        }catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void deleteBorrower(UUID borrowerID) {
        try {
            Borrower getBorrower = borrowerRepository.findById(borrowerID).orElseThrow(() -> {
                throw new DataIDNotFound(borrowerID);});
            borrowerRepository.delete(getBorrower);
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

}
