package ai.datalens;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Basic test to verify containerized testing framework works
 * This test doesn't depend on Spring Boot context loading
 */
class BasicContainerizedTest {

    @Test
    void verifyContainerizedTestingWorks() {
        // Given
        String message = "Containerized testing framework";
        
        // When
        String result = message + " works!";
        
        // Then
        assertThat(result).isEqualTo("Containerized testing framework works!");
        assertThat(result).contains("Containerized");
        assertThat(result).contains("works!");
    }

    @Test
    void verifyMathOperations() {
        // Given
        int a = 10;
        int b = 5;
        
        // When
        int sum = a + b;
        int product = a * b;
        
        // Then
        assertThat(sum).isEqualTo(15);
        assertThat(product).isEqualTo(50);
    }

    @Test
    void verifyStringOperations() {
        // Given
        String text = "Data Lens AI";
        
        // When
        String upperCase = text.toUpperCase();
        String lowerCase = text.toLowerCase();
        
        // Then
        assertThat(upperCase).isEqualTo("DATA LENS AI");
        assertThat(lowerCase).isEqualTo("data lens ai");
        assertThat(text.length()).isEqualTo(12);
    }
}