package com.Syukri.LibraryManagement.Dto;

import lombok.*;

import java.util.UUID;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoanRequestDTO {
    private UUID borrowerId;
    private UUID bookId;
}
