package com.Syukri.LibraryManagement.Dto;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoanDTO {
    private UUID loanId;
    private UUID borrowerId;
    private UUID bookId;
    private Instant borrowedAt;
    private Instant returnedAt;
}
