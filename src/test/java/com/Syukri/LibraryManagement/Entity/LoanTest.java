package com.Syukri.LibraryManagement.Entity;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Pure unit tests for the domain rules inside {@link Loan#markReturned(Instant)}.
 * No mocks needed — this exercises the entity logic directly.
 */
class LoanTest {

    @Test
    void markReturned_setsReturnedAt_whenValid() {
        Instant borrowedAt = Instant.now().minusSeconds(3600);
        Instant returnedAt = Instant.now();
        Loan loan = Loan.builder()
                .id(UUID.randomUUID())
                .borrowedAt(borrowedAt)
                .build();

        loan.markReturned(returnedAt);

        assertThat(loan.getReturnedAt()).isEqualTo(returnedAt);
    }

    @Test
    void markReturned_throws_whenAlreadyReturned() {
        Loan loan = Loan.builder()
                .id(UUID.randomUUID())
                .borrowedAt(Instant.now().minusSeconds(7200))
                .returnedAt(Instant.now().minusSeconds(3600))
                .build();

        assertThatThrownBy(() -> loan.markReturned(Instant.now()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already been returned");
    }

    @Test
    void markReturned_throws_whenReturnTimeBeforeBorrowTime() {
        Instant borrowedAt = Instant.now();
        Loan loan = Loan.builder()
                .id(UUID.randomUUID())
                .borrowedAt(borrowedAt)
                .build();

        assertThatThrownBy(() -> loan.markReturned(borrowedAt.minusSeconds(60)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be before borrowing time");
    }

    @Test
    void markReturned_throws_whenReturnedAtIsNull() {
        Loan loan = Loan.builder()
                .id(UUID.randomUUID())
                .borrowedAt(Instant.now())
                .build();

        assertThatThrownBy(() -> loan.markReturned(null))
                .isInstanceOf(NullPointerException.class);
    }
}
