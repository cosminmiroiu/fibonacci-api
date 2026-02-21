package com.fibapp.service;

import com.fibapp.exception.FibonacciRuntimeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class FibonacciServiceTest {

    private FibonacciService fibonacciService;

    @BeforeEach
    void setUp() {
        fibonacciService = new FibonacciService();
    }

    // ==========================================
    // Tests for next() method
    // ==========================================

    @Test
    void next_newClient_returnsOneAndInitializesSequence() {
        // Arrange
        var clientId = "client-1";

        // Act
        var result = fibonacciService.next(clientId);

        // Assert
        assertThat(result).isEqualTo(1L);
        assertThat(fibonacciService.list(clientId)).containsExactly(1L);
    }

    @Test
    void next_multipleCalls_generatesCorrectFibonacciSequence() {
        // Arrange
        var clientId = "client-1";
        fibonacciService.next(clientId); // 1
        fibonacciService.next(clientId); // 1
        fibonacciService.next(clientId); // 2
        fibonacciService.next(clientId); // 3

        // Act
        var result = fibonacciService.next(clientId);

        // Assert
        assertThat(result).isEqualTo(5L);
        assertThat(fibonacciService.list(clientId)).containsExactly(1L, 1L, 2L, 3L, 5L);
    }

    @Test
    void next_multipleClients_maintainsIsolatedStates() {
        // Arrange
        var clientA = "client-A";
        var clientB = "client-B";

        // Act
        fibonacciService.next(clientA);
        fibonacciService.next(clientA); // Sequence A: 1, 1
        fibonacciService.next(clientB); // Sequence B: 1

        // Assert
        assertThat(fibonacciService.list(clientA)).containsExactly(1L, 1L);
        assertThat(fibonacciService.list(clientB)).containsExactly(1L);
    }

    // ==========================================
    // Tests for back() method
    // ==========================================

    @Test
    void back_sequenceHasAtLeastTwoElements_removesLastAndReturnsOk() {
        // Arrange
        var clientId = "client-1";
        fibonacciService.next(clientId); // 1
        fibonacciService.next(clientId); // 1
        fibonacciService.next(clientId); // 2

        // Act
        var result = fibonacciService.back(clientId);

        // Assert
        assertThat(result).isEqualTo("OK");
        assertThat(fibonacciService.list(clientId)).containsExactly(1L, 1L);
    }

    @Test
    void back_sequenceHasOnlyOneElement_throwsException() {
        // Arrange
        var clientId = "client-1";
        fibonacciService.next(clientId); // sequence has exactly one element

        // Act & Assert
        assertThatThrownBy(() -> fibonacciService.back(clientId))
                .isInstanceOf(FibonacciRuntimeException.class)
                .hasMessage("Back limit reached");
    }

    @Test
    void back_clientDoesNotExist_throwsException() {
        // Arrange
        var clientId = "non-existent-client";

        // Act & Assert
        assertThatThrownBy(() -> fibonacciService.back(clientId))
                .isInstanceOf(FibonacciRuntimeException.class)
                .hasMessage("Client does not exist");
    }

    // ==========================================
    // Tests for list() method
    // ==========================================

    @Test
    void list_existingClient_returnsCorrectSequence() {
        // Arrange
        var clientId = "client-1";
        fibonacciService.next(clientId);
        fibonacciService.next(clientId);
        fibonacciService.next(clientId);

        // Act
        var result = fibonacciService.list(clientId);

        // Assert
        assertThat(result).containsExactly(1L, 1L, 2L);
    }

    @Test
    void list_clientDoesNotExist_throwsException() {
        // Arrange
        var clientId = "non-existent-client";

        // Act & Assert
        assertThatThrownBy(() -> fibonacciService.list(clientId))
                .isInstanceOf(FibonacciRuntimeException.class)
                .hasMessage("Client does not exist");
    }

    @Test
    void list_returnsDefensiveCopy_modifyingListDoesNotAffectInternalState() {
        // Arrange
        var clientId = "client-1";
        fibonacciService.next(clientId);
        var returnedList = fibonacciService.list(clientId);

        // Act & Assert
        // In Java 16+, Stream.toList() returns an unmodifiable list,
        // which completely prevents modification and guarantees a defensive approach.
        assertThatThrownBy(() -> returnedList.add(99L))
                .isInstanceOf(UnsupportedOperationException.class);

        // Asserting that internal state has truly remained untouched
        assertThat(fibonacciService.list(clientId)).containsExactly(1L);
    }
}