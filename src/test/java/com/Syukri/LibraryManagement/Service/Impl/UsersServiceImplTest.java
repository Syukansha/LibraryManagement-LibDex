package com.Syukri.LibraryManagement.Service.Impl;

import com.Syukri.LibraryManagement.Dto.UserDTO;
import com.Syukri.LibraryManagement.Entity.Users;
import com.Syukri.LibraryManagement.Exception.UserNotFound;
import com.Syukri.LibraryManagement.Exception.UsernameEmailAlreadyExisted;
import com.Syukri.LibraryManagement.Repository.UsersRepository;
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
class UsersServiceImplTest {

    @Mock
    private UsersRepository usersRepository;

    @InjectMocks
    private UsersServiceImpl usersService;

    @Test
    void getUserByUsername_returnsDto_whenUserExists() {
        UUID id = UUID.randomUUID();
        Users user = Users.builder().id(id).username("alice").email("alice@test.com").build();
        when(usersRepository.findByUsername("alice")).thenReturn(Optional.of(user));

        Optional<UserDTO> result = usersService.getUserByUsername("alice");

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(id);
        assertThat(result.get().getUsername()).isEqualTo("alice");
        assertThat(result.get().getEmail()).isEqualTo("alice@test.com");
    }

    @Test
    void getUserByUsername_throws_whenUserMissing() {
        when(usersRepository.findByUsername("bob")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usersService.getUserByUsername("bob"))
                .isInstanceOf(UserNotFound.class);
    }

    @Test
    void registerUser_savesEncodedPassword_whenUsernameAndEmailAreNew() {
        Users input = Users.builder().username("carol").email("carol@test.com").password("plain").build();
        when(usersRepository.findByUsername("carol")).thenReturn(Optional.empty());
        when(usersRepository.findByEmail("carol@test.com")).thenReturn(Optional.empty());
        when(usersRepository.save(any(Users.class))).thenAnswer(inv -> inv.getArgument(0));

        Users saved = usersService.registerUser(input);

        assertThat(saved.getUsername()).isEqualTo("carol");
        assertThat(saved.getEmail()).isEqualTo("carol@test.com");
        // password must be hashed, never stored as plain text
        assertThat(saved.getPassword()).isNotEqualTo("plain").startsWith("$2");
        verify(usersRepository).save(any(Users.class));
    }

    @Test
    void registerUser_throws_whenUsernameAlreadyExists() {
        Users input = Users.builder().username("dave").email("dave@test.com").password("pw").build();
        when(usersRepository.findByUsername("dave"))
                .thenReturn(Optional.of(Users.builder().username("dave").build()));

        assertThatThrownBy(() -> usersService.registerUser(input))
                .isInstanceOf(UsernameEmailAlreadyExisted.class);
        verify(usersRepository, never()).save(any());
    }

    @Test
    void registerUser_throws_whenEmailAlreadyExists() {
        Users input = Users.builder().username("new").email("taken@test.com").password("pw").build();
        when(usersRepository.findByUsername("new")).thenReturn(Optional.empty());
        when(usersRepository.findByEmail("taken@test.com"))
                .thenReturn(Optional.of(Users.builder().email("taken@test.com").build()));

        assertThatThrownBy(() -> usersService.registerUser(input))
                .isInstanceOf(UsernameEmailAlreadyExisted.class);
        verify(usersRepository, never()).save(any());
    }

    @Test
    void checkIfUsernameEmailExists_returnsTrue_whenNeitherExists() {
        when(usersRepository.findByUsername("ghost")).thenReturn(Optional.empty());
        when(usersRepository.findByEmail("ghost@test.com")).thenReturn(Optional.empty());

        // NOTE: method name is inverted — it returns true when the user does NOT exist
        assertThat(usersService.checkIfUsernameEmailExists("ghost", "ghost@test.com")).isTrue();
    }

    @Test
    void checkIfUsernameEmailExists_returnsFalse_whenUsernameExists() {
        when(usersRepository.findByUsername("existing"))
                .thenReturn(Optional.of(Users.builder().username("existing").build()));

        assertThat(usersService.checkIfUsernameEmailExists("existing", "any@test.com")).isFalse();
    }

    @Test
    void deleteUser_removesUser_whenIdExists() {
        UUID id = UUID.randomUUID();
        Users user = Users.builder().id(id).username("erin").build();
        when(usersRepository.findById(id)).thenReturn(Optional.of(user));

        usersService.deleteUser(id);

        verify(usersRepository).delete(user);
    }

    @Test
    void deleteUser_throws_whenIdMissing() {
        UUID id = UUID.randomUUID();
        when(usersRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usersService.deleteUser(id))
                .isInstanceOf(RuntimeException.class);
        verify(usersRepository, never()).delete(any());
    }

    @Test
    void updateUser_persistsChanges_whenUserExists() {
        UUID id = UUID.randomUUID();
        Users existing = Users.builder().id(id).username("old").email("old@test.com").password("hash").build();
        Users request = Users.builder().id(id).username("new").email("new@test.com").build();
        when(usersRepository.findById(id)).thenReturn(Optional.of(existing));
        when(usersRepository.save(any(Users.class))).thenAnswer(inv -> inv.getArgument(0));

        usersService.updateUser(Optional.of(request));

        verify(usersRepository).save(existing);
        assertThat(existing.getUsername()).isEqualTo("new");
        assertThat(existing.getEmail()).isEqualTo("new@test.com");
        // password untouched because request.getPassword() was null
        assertThat(existing.getPassword()).isEqualTo("hash");
    }

    @Test
    void updateUser_throws_whenIdNotFound() {
        UUID id = UUID.randomUUID();
        Users request = Users.builder().id(id).username("nobody").build();
        when(usersRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usersService.updateUser(Optional.of(request)))
                .isInstanceOf(RuntimeException.class);
        verify(usersRepository, never()).save(any());
    }

    @Test
    void updateUser_throws_whenNoDataSupplied() {
        assertThatThrownBy(() -> usersService.updateUser(Optional.empty()))
                .isInstanceOf(RuntimeException.class);
        verify(usersRepository, never()).save(any());
    }
}
