package progettofinale.Util;

import java.util.Base64;

/**
 * Utility per la gestione delle immagini.
 * Fornisce metodi per la conversione delle immagini in formato Base64.
 */
public class ImageUtil {

    /**
     * Converte un array di byte rappresentante un'immagine in una stringa codificata in Base64.
     *
     * @param image L'array di byte dell'immagine da convertire.
     * @return La stringa Base64 rappresentante l'immagine, oppure null se l'immagine è null.
     */
    public static String encodeToBase64(byte[] image) {
        return image != null ? Base64.getEncoder().encodeToString(image) : null;
    }
}
