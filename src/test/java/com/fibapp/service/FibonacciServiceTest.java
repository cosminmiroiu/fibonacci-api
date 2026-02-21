package com.fibapp.service;

import com.fibapp.exception.FibonacciRuntimeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FibonacciServiceTest {

    private FibonacciService fibonacciService;

    @BeforeEach
    void setUp() {
        // Fiindcă am eliminat 'static', fiecare test primește o instanță nouă,
        // izolând complet datele între execuții.
        fibonacciService = new FibonacciService();
    }

    // ==========================================
    // Metoda next()
    // ==========================================

    @Test
    @DisplayName("next: Ar trebui să returneze 1L și să inițializeze indexul la 0 pentru un client nou")
    void next_newClient_returnsFirstFibonacciAndStartsAtZero() {
        // Arrange
        var clientId = "user-123";

        // Act
        var result = fibonacciService.next(clientId);

        // Assert
        assertThat(result).isEqualTo(1L);
        assertThat(fibonacciService.list(clientId))
                .as("Secvența ar trebui să conțină doar primul element")
                .containsExactly(1L);
    }

    @Test
    @DisplayName("next: Ar trebui să incrementeze indexul corect la apeluri multiple")
    void next_multipleCalls_calculatesFibonacciCorrectly() {
        // Arrange
        var clientId = "user-123";
        fibonacciService.next(clientId); // Index 0 -> 1L
        fibonacciService.next(clientId); // Index 1 -> 1L
        fibonacciService.next(clientId); // Index 2 -> 2L

        // Act
        var result = fibonacciService.next(clientId); // Index 3

        // Assert
        assertThat(result).isEqualTo(3L);
        assertThat(fibonacciService.list(clientId)).containsExactly(1L, 1L, 2L, 3L);
    }

    @Test
    @DisplayName("next: Ar trebui să mențină stări independente pentru clienți diferiți")
    void next_differentClients_haveIsolatedIndices() {
        // Arrange
        var clientA = "A";
        var clientB = "B";

        // Act
        fibonacciService.next(clientA);
        fibonacciService.next(clientA);
        var resultB = fibonacciService.next(clientB);

        // Assert
        assertThat(resultB).isEqualTo(1L);
        assertThat(fibonacciService.list(clientA)).hasSize(2);
        assertThat(fibonacciService.list(clientB)).hasSize(1);
    }

    // ==========================================
    // Metoda back()
    // ==========================================

    @Test
    @DisplayName("back: Ar trebui să scadă indexul și să returneze OK când indexul este > 0")
    void back_validState_decrementsIndex() {
        // Arrange
        var clientId = "client-back";
        fibonacciService.next(clientId); // Index 0
        fibonacciService.next(clientId); // Index 1

        // Act
        var status = fibonacciService.back(clientId);

        // Assert
        assertThat(status).isEqualTo("OK");
        assertThat(fibonacciService.list(clientId)).containsExactly(1L);
    }

    @Test
    @DisplayName("back: Ar trebui să arunce excepție dacă indexul este 0")
    void back_atZeroIndex_throwsException() {
        // Arrange
        var clientId = "client-limit";
        fibonacciService.next(clientId); // Index 0

        // Act & Assert
        assertThatThrownBy(() -> fibonacciService.back(clientId))
                .isInstanceOf(FibonacciRuntimeException.class)
                .hasMessage("Back limit reached");
    }

    @Test
    @DisplayName("back: Ar trebui să arunce excepție dacă clientul nu există")
    void back_nonExistentClient_throwsException() {
        // Act & Assert
        assertThatThrownBy(() -> fibonacciService.back("unknown"))
                .isInstanceOf(FibonacciRuntimeException.class)
                .hasMessage("Back limit reached");
    }

    // ==========================================
    // Metoda list()
    // ==========================================

    @Test
    @DisplayName("list: Ar trebui să returneze întreaga secvență până la indexul curent")
    void list_existingClient_returnsFullSequence() {
        // Arrange
        var clientId = "client-list";
        fibonacciService.next(clientId); // 1
        fibonacciService.next(clientId); // 1
        fibonacciService.next(clientId); // 2
        fibonacciService.next(clientId); // 3

        // Act
        var sequence = fibonacciService.list(clientId);

        // Assert
        assertThat(sequence).containsExactly(1L, 1L, 2L, 3L);
    }

    @Test
    @DisplayName("list: Ar trebui să arunce excepție pentru client inexistent")
    void list_unknownClient_throwsException() {
        // Act & Assert
        assertThatThrownBy(() -> fibonacciService.list("none"))
                .isInstanceOf(FibonacciRuntimeException.class)
                .hasMessage("Client does not exist");
    }

    @Test
    @DisplayName("list: Ar trebui să returneze o listă imutabilă (Defensive Copy)")
    void list_returnedList_isImmutable() {
        // Arrange
        var clientId = "immutable-test";
        fibonacciService.next(clientId);
        var sequence = fibonacciService.list(clientId);

        // Act & Assert
        // .toList() din Java 16+ returnează o listă ne-modificabilă
        assertThatThrownBy(() -> sequence.add(99L))
                .isInstanceOf(UnsupportedOperationException.class);
    }
}