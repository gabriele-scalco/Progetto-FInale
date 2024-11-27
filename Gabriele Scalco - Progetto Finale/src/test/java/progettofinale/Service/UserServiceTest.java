package progettofinale.Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import progettofinale.Model.*;
import progettofinale.Model.Bikemodel.Bike;
import progettofinale.Model.Bikemodel.MountainBike;
import progettofinale.Repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ResponseStatusException;
import progettofinale.Util.ImageUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;

    private Bike bike;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Configura un utente reale
        user = new User("test@example.com", "password", "Test User");

        // Configura una bici come spy per simulare il comportamento del database
        bike = spy(new MountainBike());
        doReturn(1L).when(bike).getId(); // Mocka l'ID
        bike.setImage(null); // Nessuna immagine inizialmente
    }

     /**
     * Verifica il recupero di un utente tramite email (utente esistente).
     */
    @Test
    void testRetrieveUserByEmail_UserExists() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(user); // Mock per restituire un utente

        User result = userService.findByEmail("test@example.com");

        assertEquals(user, result); // Verifica che l'utente recuperato sia corretto
        verify(userRepository, times(1)).findByEmail("test@example.com"); // Verifica la chiamata al repository
    }

    /**
     * Verifica comportamento quando l'utente cercato tramite email non esiste.
     */
    @Test
    void testRetrieveUserByEmail_UserNotFound() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(null); // Mock per utente inesistente

        assertThrows(ResponseStatusException.class, () -> userService.findByEmail("unknown@example.com")); // Verifica l'eccezione
        verify(userRepository, times(1)).findByEmail("unknown@example.com"); // Verifica la chiamata al repository
    }


    /**
     * Verifica che un nuovo utente venga aggiunto correttamente con password criptata.
     */
    @Test
    void testAddUser() {
        String encodedPassword = "encodedPassword";
        when(passwordEncoder.encode("password")).thenReturn(encodedPassword); // Mock per la codifica della password
        when(userRepository.save(user)).thenReturn(user); // Mock del salvataggio

        User result = userService.addUser(user);

        assertEquals(encodedPassword, user.getPassword()); // Verifica la codifica della password
        assertEquals(user, result); // Verifica che l'utente salvato sia quello atteso
        verify(passwordEncoder, times(1)).encode("password"); // Verifica che il metodo encode() sia stato chiamato
        verify(userRepository, times(1)).save(user); // Verifica il salvataggio dell'utente
    }


    /**
     * Verifica che una bici venga aggiunta correttamente alla wishlist.
     */
    @Test
    void testAddToWishlist() {
        user.setWishlist(new ArrayList<>()); // Configura una wishlist vuota

        when(userRepository.save(user)).thenReturn(user); // Mock del salvataggio

        userService.addToWishlist(user, bike);

        assertEquals(1, user.getWishlist().size()); // Verifica la dimensione della wishlist
        assertTrue(user.getWishlist().contains(bike)); // Verifica che la bici sia stata aggiunta
        verify(userRepository, times(1)).save(user); // Verifica il salvataggio dell'utente aggiornato
    }

    /**
     * Verifica comportamento quando si tenta di aggiungere una bici già presente
     * nella wishlist.
     */
    @Test
    void testAddToWishlist_BikeAlreadyExists() {
        user.setWishlist(new ArrayList<>(List.of(bike))); // Configura una wishlist con la bici già presente

        userService.addToWishlist(user, bike);

        assertEquals(1, user.getWishlist().size()); // Verifica che non ci siano duplicati
        verify(userRepository, never()).save(user); // Verifica che il salvataggio non sia avvenuto
    }

    /**
     * Verifica comportamento quando si tenta di rimuovere una bici da un utente
     * inesistente.
     */
    @Test
    void testRemoveBikeFromWishlist_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty()); // Mock per utente inesistente

        assertThrows(IllegalArgumentException.class, () -> userService.removeBikeFromWishlist(1L, 1L)); // Verifica
                                                                                                        // l'eccezione
        verify(userRepository, times(1)).findById(1L); // Verifica la chiamata al repository
        verify(userRepository, never()).save(any(User.class)); // Verifica che il salvataggio non sia avvenuto
    }

    /**
     * Verifica comportamento quando si tenta di rimuovere una bici non presente
     * nella wishlist.
     */
    @Test
    void testRemoveBikeFromWishlist_BikeNotInWishlist() {
        user.setWishlist(new ArrayList<>());
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.removeBikeFromWishlist(1L, 1L);

        assertTrue(user.getWishlist().isEmpty()); // Verifica che la wishlist sia vuota
        verify(userRepository, times(1)).findById(1L); // Verifica che il metodo `findById` sia stato chiamato una sola volta
        verify(userRepository, never()).save(user); // Verifica che il metodo `save` non sia stato chiamato
    }

    /**
     * Verifica che una bici venga rimossa correttamente dalla wishlist.
     */
    @Test
    void testRemoveBikeFromWishlist_BikeExists() {
        user.setWishlist(new ArrayList<>(List.of(bike)));

        // Simula il comportamento del repository
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Rimuove la bici dalla wishlist
        userService.removeBikeFromWishlist(1L, 1L);

        assertTrue(user.getWishlist().isEmpty()); // Verifica che la wishlist sia vuota
        verify(userRepository, times(1)).findById(1L); // Verifica il recupero dell'utente
        verify(userRepository, times(1)).save(user); // Verifica il salvataggio dell'utente aggiornato
    }

    /**
     * Verifica il recupero di un utente con una wishlist contenente una bici con
     * immagine.
     */
    @Test
    void testRetrieveUserWithWishlistByEmail_UserExists() {
        bike.setImage(new byte[] { 1, 2, 3 }); // Configura un'immagine per la bici
        user.setWishlist(new ArrayList<>(List.of(bike))); // Configura la wishlist
        when(userRepository.findByEmail("test@example.com")).thenReturn(user); // Mock per utente esistente

        User result = userService.retrieveUserWithWishlistByEmail("test@example.com");

        String expectedBase64 = "data:image/jpeg;base64," + ImageUtil.encodeToBase64(bike.getImage()); // Immagine
                                                                                                       // codificata in
                                                                                                       // Base64
        assertEquals(expectedBase64, result.getWishlist().get(0).getImagePath()); // Verifica la codifica dell'immagine
        verify(userRepository, times(1)).findByEmail("test@example.com"); // Verifica la chiamata al repository
    }

    /**
     * Verifica comportamento quando la bici non ha un'immagine.
     */
    @Test
    void testRetrieveUserWithWishlistByEmail_BikeWithoutImage() {
        user.setWishlist(new ArrayList<>(List.of(bike))); // Configura la wishlist
        when(userRepository.findByEmail("test@example.com")).thenReturn(user); // Mock per utente esistente

        User result = userService.retrieveUserWithWishlistByEmail("test@example.com");

        assertEquals("/images/default-bike.jpg", result.getWishlist().get(0).getImagePath()); // Verifica l'immagine
                                                                                              // predefinita
        verify(userRepository, times(1)).findByEmail("test@example.com"); // Verifica la chiamata al repository
    }

    /**
     * Verifica comportamento quando si tenta di recuperare un utente inesistente.
     */
    @Test
    void testRetrieveUserWithWishlistByEmail_UserNotFound() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(null); // Mock per utente inesistente

        assertThrows(ResponseStatusException.class,
                () -> userService.retrieveUserWithWishlistByEmail("unknown@example.com")); // Verifica l'eccezione
        verify(userRepository, times(1)).findByEmail("unknown@example.com"); // Verifica la chiamata al repository
    }
}
