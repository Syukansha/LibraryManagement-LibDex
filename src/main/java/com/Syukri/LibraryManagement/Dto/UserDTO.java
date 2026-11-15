package com.Syukri.LibraryManagement.Dto;

import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private UUID id;
    private String username;
    private String email;
}
