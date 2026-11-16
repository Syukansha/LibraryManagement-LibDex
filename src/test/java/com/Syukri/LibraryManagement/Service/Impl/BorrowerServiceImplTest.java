package com.Syukri.LibraryManagement.Service.Impl;

import com.Syukri.LibraryManagement.Dto.BorrowerDTO;
import com.Syukri.LibraryManagement.Entity.Borrower;
import com.Syukri.LibraryManagement.Exception.UserNotFound;
import com.Syukri.LibraryManagement.Exception.UsernameEmailAlreadyExisted;
import com.Syukri.LibraryManagement.Repository.BorrowerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BorrowerServiceImplTest {

    @Mock
    private BorrowerRepository borrowerRepository;

    @InjectMocks
    private BorrowerServiceImpl borrowerService;

    @Test
    void getBorrowerByName_returnsDto_whenBorrowerExists() {
        UUID id = UUID.randomUUID();
        Borrower borrower = Borrower.builder().id(id).name("alice").email("alice@test.com").build();
        when(borrowerRepository.findByName("alice")).thenReturn(Optional.of(borrower));

        Optional<BorrowerDTO> result = borrowerService.getBorrowerByName("alice");

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(id);
        assertThat(result.get().getName()).isEqualTo("alice");
        assertThat(result.get().getEmail()).isEqualTo("alice@test.com");
    }

    @Test
    void getBorrowerByName_throws_whenBorrowerMissing() {
        when(borrowerRepository.findByName("bob")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> borrowerService.getBorrowerByName("bob"))
                .isInstanceOf(UserNotFound.class);
    }

    @Test
    void registerBorrower_saves_whenNameAndEmailAreNew() {
        Borrower input = Borrower.builder().name("carol").email("carol@test.com").build();
        when(borrowerRepository.findByName("carol")).thenReturn(Optional.empty());
        when(borrowerRepository.findByEmail("carol@test.com")).thenReturn(Optional.empty());
        when(borrowerRepository.save(any(Borrower.class))).thenAnswer(inv -> inv.getArgument(0));

        Borrower saved = borrowerService.registerBorrower(input);

        assertThat(saved.getName()).isEqualTo("carol");
        assertThat(saved.getEmail()).isEqualTo("carol@test.com");
        verify(borrowerRepository).save(any(Borrower.class));
    }

    @Test
    void registerBorrower_throws_whenNameAlreadyExists() {
        Borrower input = Borrower.builder().name("dave").email("dave@test.com").build();
        when(borrowerRepository.findByName("dave"))
                .thenReturn(Optional.of(Borrower.builder().name("dave").build()));

        assertThatThrownBy(() -> borrowerService.registerBorrower(input))
                .isInstanceOf(UsernameEmailAlreadyExisted.class);
        verify(borrowerRepository, never()).save(any());
    }

    @Test
    void registerBorrower_throws_whenEmailAlreadyExists() {
        Borrower input = Borrower.builder().name("new").email("taken@test.com").build();
        when(borrowerRepository.findByName("new")).thenReturn(Optional.empty());
        when(borrowerRepository.findByEmail("taken@test.com"))
                .thenReturn(Optional.of(Borrower.builder().email("taken@test.com").build()));

        assertThatThrownBy(() -> borrowerService.registerBorrower(input))
                .isInstanceOf(UsernameEmailAlreadyExisted.class);
        verify(borrowerRepository, never()).save(any());
    }

    @Test
    void checkIfNameEmailExists_returnsTrue_whenNeitherExists() {
        when(borrowerRepository.findByName("ghost")).thenReturn(Optional.empty());
        when(borrowerRepository.findByEmail("ghost@test.com")).thenReturn(Optional.empty());

        assertThat(borrowerService.checkIfNameEmailExists("ghost", "ghost@test.com")).isTrue();
    }

    @Test
    void checkIfNameEmailExists_returnsFalse_whenNameExists() {
        when(borrowerRepository.findByName("existing"))
                .thenReturn(Optional.of(Borrower.builder().name("existing").build()));

        assertThat(borrowerService.checkIfNameEmailExists("existing", "any@test.com")).isFalse();
    }

    @Test
    void updateBorrower_returnsUpdatedDto_whenBorrowerExists() {
        UUID id = UUID.randomUUID();
        Borrower existing = Borrower.builder().id(id).name("old").email("old@test.com").build();
        Borrower request = Borrower.builder().id(id).name("new").email("new@test.com").build();
        when(borrowerRepository.findById(id)).thenReturn(Optional.of(existing));
        when(borrowerRepository.save(any(Borrower.class))).thenAnswer(inv -> inv.getArgument(0));

        BorrowerDTO result = borrowerService.updateBorrower(Optional.of(request));

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getName()).isEqualTo("new");
        assertThat(result.getEmail()).isEqualTo("new@test.com");
        verify(borrowerRepository).save(existing);
    }

    @Test
    void updateBorrower_throws_whenIdNotFound() {
        UUID id = UUID.randomUUID();
        Borrower request = Borrower.builder().id(id).name("nobody").build();
        when(borrowerRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> borrowerService.updateBorrower(Optional.of(request)))
                .isInstanceOf(RuntimeException.class);
        verify(borrowerRepository, never()).save(any());
    }

    @Test
    void updateBorrower_throws_whenNoDataSupplied() {
        assertThatThrownBy(() -> borrowerService.updateBorrower(Optional.empty()))
                .isInstanceOf(RuntimeException.class);
        verify(borrowerRepository, never()).save(any());
    }

    @Test
    void deleteBorrower_removesBorrower_whenIdExists() {
        UUID id = UUID.randomUUID();
        Borrower borrower = Borrower.builder().id(id).name("erin").build();
        when(borrowerRepository.findById(id)).thenReturn(Optional.of(borrower));

        borrowerService.deleteBorrower(id);

        verify(borrowerRepository).delete(borrower);
    }

    @Test
    void deleteBorrower_throws_whenIdMissing() {
        UUID id = UUID.randomUUID();
        when(borrowerRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> borrowerService.deleteBorrower(id))
                .isInstanceOf(RuntimeException.class);
        verify(borrowerRepository, never()).delete(any());
    }
}
