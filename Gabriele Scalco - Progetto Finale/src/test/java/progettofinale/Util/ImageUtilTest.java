package progettofinale.Util;

import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class ImageUtilTest {

    /**
     * Testa la codifica di un'immagine valida in Base64.
     */
    @Test
    void testEncodeToBase64_ValidImage() {
        // Arrange
        byte[] image = "Test Image Content".getBytes();

        // Act
        String encoded = ImageUtil.encodeToBase64(image);

        // Assert
        assertNotNull(encoded); // Verifica che il risultato non sia null
        assertEquals(Base64.getEncoder().encodeToString(image), encoded); // Verifica che la stringa codificata sia corretta
    }

    /**
     * Testa il comportamento con un'immagine null.
     */
    @Test
    void testEncodeToBase64_NullImage() {
        // Act
        String encoded = ImageUtil.encodeToBase64(null);

        // Assert
        assertNull(encoded); // Verifica che il risultato sia null
    }

    /**
     * Testa la codifica di un'immagine vuota in Base64.
     */
    @Test
    void testEncodeToBase64_EmptyImage() {
        // Arrange
        byte[] image = new byte[0];

        // Act
        String encoded = ImageUtil.encodeToBase64(image);

        // Assert
        assertNotNull(encoded); // Verifica che il risultato non sia null
        assertEquals("", encoded); // Verifica che una stringa vuota venga restituita per un array vuoto
    }
}
