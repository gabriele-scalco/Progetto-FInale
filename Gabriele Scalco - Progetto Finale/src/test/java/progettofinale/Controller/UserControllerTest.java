package progettofinale.Controller;

import progettofinale.Model.Bikemodel.Bike;
import progettofinale.Model.User;
import progettofinale.Service.BikeService;
import progettofinale.Service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private BikeService bikeService;

    @Mock
    private Model model;

    @Mock
    private Principal principal;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Verifica che il metodo login restituisca correttamente la vista "login".
     */
    @Test
    void testLogin() {
        String viewName = userController.login();

        assertEquals("login", viewName); // Verifica che la vista restituita sia "login"
    }

    /**
     * Verifica che il metodo register aggiunga correttamente l'attributo "user" e restituisca la vista "register".
     */
    @Test
    void testRegister() {
        String viewName = userController.register(model);

        assertEquals("register", viewName); // Verifica che la vista restituita sia "register"
        verify(model).addAttribute(eq("user"), any(User.class)); // Verifica che il modello contenga un nuovo utente
    }

    /**
     * Verifica che l'utente venga registrato con successo e che venga reindirizzato alla vista "login".
     */
    @Test
    void testRegisterUserSuccess() {
        User user = new User();

        String viewName = userController.registerUser(user, model);

        assertEquals("redirect:/login", viewName); // Verifica che l'utente venga reindirizzato alla vista "login"
        verify(userService).addUser(user); // Verifica che il servizio registri l'utente
    }

    /**
     * Verifica che il metodo gestisca correttamente un errore durante la registrazione dell'utente.
     */
    @Test
    void testRegisterUserFailure() {
        User user = new User();
        doThrow(new RuntimeException("Errore")).when(userService).addUser(user);

        String viewName = userController.registerUser(user, model);

        assertEquals("register", viewName); // Verifica che la vista restituita sia "register" in caso di errore
        verify(model).addAttribute("error", "Errore nella registrazione dell'utente."); // Verifica che venga aggiunto l'attributo "error" con il messaggio corretto
    }

    /**
     * Verifica che una bici venga aggiunta correttamente alla wishlist dell'utente.
     */
    @Test
    void testAddToWishlist() {
        String username = "test@example.com";
        Long bikeId = 1L;
        User user = new User();
        Bike bike = mock(Bike.class);

        when(principal.getName()).thenReturn(username);
        when(userService.findByEmail(username)).thenReturn(user);
        when(bikeService.getBikeById(bikeId)).thenReturn(bike);

        String redirectUrl = userController.addToWishlist(bikeId, principal, redirectAttributes);

        assertEquals("redirect:/", redirectUrl); // Verifica il redirect alla homepage
        verify(userService).addToWishlist(user, bike); // Verifica che la bici venga aggiunta alla wishlist
        verify(redirectAttributes).addFlashAttribute("successMessage", "Bici aggiunta alla wishlist!"); // Verifica il messaggio di successo
    }

    /**
     * Verifica che venga gestito correttamente un errore durante l'aggiunta di una bici alla wishlist.
     */
    @Test
    void testAddToWishlistFailure() {
        String username = "test@example.com";
        Long bikeId = 1L;

        when(principal.getName()).thenReturn(username);
        doThrow(new RuntimeException("Errore")).when(userService).findByEmail(username);

        String redirectUrl = userController.addToWishlist(bikeId, principal, redirectAttributes);

        assertEquals("redirect:/", redirectUrl); // Verifica il redirect alla homepage
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Errore durante l'aggiunta alla wishlist."); // Verifica il messaggio di errore
    }

    /**
     * Verifica che una bici venga rimossa correttamente dalla wishlist dell'utente.
     */
    @Test
    void testRemoveFromWishlist() {
        String username = "test@example.com";
        Long bikeId = 1L;

        // Crea un mock per l'utente con un ID predefinito
        User user = mock(User.class);
        when(user.getId()).thenReturn(1L);

        // Simula il comportamento dei metodi chiamati nel test
        when(principal.getName()).thenReturn(username);
        when(userService.findByEmail(username)).thenReturn(user);

        String redirectUrl = userController.removeFromWishlist(bikeId, principal, redirectAttributes);

        assertEquals("redirect:/profile", redirectUrl); // Verifica il redirect al profilo utente
        verify(userService).removeBikeFromWishlist(user.getId(), bikeId); // Verifica che la bici venga rimossa dalla wishlist
        verify(redirectAttributes).addFlashAttribute("successMessage", "Bici rimossa dalla wishlist!"); // Verifica il messaggio di successo
    }

    /**
     * Verifica che venga gestito correttamente un errore durante la rimozione di una bici dalla wishlist.
     */
    @Test
    void testRemoveFromWishlistFailure() {
        String username = "test@example.com";
        Long bikeId = 1L;

        when(principal.getName()).thenReturn(username);
        doThrow(new RuntimeException("Errore")).when(userService).findByEmail(username);

        String redirectUrl = userController.removeFromWishlist(bikeId, principal, redirectAttributes);

        assertEquals("redirect:/profile", redirectUrl); // Verifica il redirect al profilo utente
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Errore durante la rimozione dalla wishlist."); // Verifica il messaggio di errore
    }
}
