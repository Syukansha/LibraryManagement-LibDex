package com.Syukri.LibraryManagement.Dto;

import lombok.*;

import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BorrowerDTO {
    private UUID id;
    private String name;
    private String email;
}
