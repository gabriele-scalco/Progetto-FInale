package progettofinale.Controller;

import progettofinale.Model.Bikemodel.*;
import progettofinale.Model.*;
import progettofinale.Service.*;
import progettofinale.Service.SortingStrategy.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BikeControllerTest {

    @Mock
    private BikeService bikeService;

    @Mock
    private UserService userService;

    @Mock
    private Model model;

    @Mock
    private Principal principal;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private BikeController bikeController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Verifica che la lista delle biciclette venga visualizzata correttamente.
     */
    @Test
    void testViewBikes() {
        List<Bike> bikes = Arrays.asList(
            new MountainBike("Trek", "Large", "Good condition", 500.0, "Rome", null),
            new RoadBike("Giant", "Medium", "Like new", 600.0, "Milan", null)
        );
        when(bikeService.getAllBikes()).thenReturn(bikes);

        String viewName = bikeController.viewBikes(model);

        assertEquals("bici", viewName); // Verifica che il nome della vista sia corretto
        verify(model).addAttribute("bikes", bikes); // Verifica che la lista di biciclette sia aggiunta al modello
    }

    /**
     * Verifica che il profilo utente venga visualizzato correttamente.
     */
    @Test
    void testShowUserProfile() {
        // Configura l'utente e i suoi dati
        User user = new User();
        user.setEmail("user@example.com");

        MountainBike wishlistBike = new MountainBike("Trek", "Large", "Wishlist item", 500.0, "Rome", null);
        user.setWishlist(Arrays.asList(wishlistBike));

        MountainBike userBike = new MountainBike("Trek", "Large", "User's Mountain Bike", 500.0, "Rome", null);

        // Configura il comportamento del mock
        when(principal.getName()).thenReturn("user@example.com");
        when(userService.retrieveUserWithWishlistByEmail("user@example.com")).thenReturn(user);
        when(bikeService.getBikesByUser(user)).thenReturn(Arrays.asList(userBike));

        // Esegui il metodo
        String viewName = bikeController.showUserProfile(model, principal);

        // Verifica
        assertEquals("user-profile", viewName); // Verifica che il nome della vista sia corretto
        verify(model).addAttribute(eq("user"), eq(user)); // Verifica che il modello contenga i dettagli dell'utente
        verify(model).addAttribute(eq("bikes"), argThat(bikes -> ((List<Bike>) bikes).contains(userBike))); // Verifica che il modello contenga le biciclette dell'utente
        verify(model).addAttribute(eq("bikeTypes"), eq(List.of("Mountain", "Road", "Electric"))); // Verifica che il modello contenga i tipi di biciclette
        verify(model).addAttribute(eq("wishlist"), argThat(wishlist -> ((List<Bike>) wishlist).contains(wishlistBike))); // Verifica che il modello contenga la wishlist dell'utente
    }


    /**
     * Verifica che il filtro delle biciclette funzioni correttamente.
     */
    @Test
    void testFilterBikes() {
        List<Bike> filteredBikes = Arrays.asList(
            new MountainBike("Trek", "Large", "Filtered Mountain Bike", 500.0, "Rome", null)
        );
        when(bikeService.searchBikes(
            anyString(), anyDouble(), anyString(), anyString(), any(SortingStrategy.class))
        ).thenReturn(filteredBikes);

        String viewName = bikeController.filterBikes("Large", "Rome", 500.0, "Trek", "Mountain", "asc", model);

        assertEquals("bici", viewName); // Verifica che il nome della vista sia corretto
        verify(model).addAttribute("bikes", filteredBikes); // Verifica che la lista di biciclette filtrate sia aggiunta al modello
    }

    /**
     * Verifica che una bici venga aggiunta correttamente con immagine valida.
     */
    @Test
    void testAddBikeWithImage() throws Exception {
        MockMultipartFile image = new MockMultipartFile(
            "image", "test.jpg", "image/jpeg", "Test Image Content".getBytes()
        );

        User user = new User();
        when(userService.retrieveUserById(1L)).thenReturn(user);
        when(redirectAttributes.addFlashAttribute(anyString(), anyString())).thenReturn(redirectAttributes);

        String redirectUrl = bikeController.addBike(
            "Mountain", "Trek", "Large", "Description", 500.0, "Rome", 1L, image, redirectAttributes
        );

        assertEquals("redirect:/profile", redirectUrl); // Verifica che il reindirizzamento sia verso il profilo
        verify(bikeService).addBike(any(Bike.class)); // Verifica che il servizio aggiunga correttamente la bici
        verify(redirectAttributes).addFlashAttribute("successMessage", "Bici aggiunta con successo!"); // Verifica che il messaggio di successo sia impostato
    }

    /**
     * Verifica che l'aggiunta di una bici fallisca con immagine non valida.
     */
    @Test
    void testAddBikeWithInvalidMimeType() throws Exception {
        MockMultipartFile image = new MockMultipartFile(
            "image", "test.txt", "text/plain", "Invalid Content".getBytes()
        );

        User user = new User();
        when(userService.retrieveUserById(1L)).thenReturn(user);

        String redirectUrl = bikeController.addBike(
            "Mountain", "Trek", "Large", "Description", 500.0, "Rome", 1L, image, redirectAttributes
        );

        assertEquals("redirect:/profile", redirectUrl); // Verifica che il reindirizzamento sia verso il profilo
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Il file caricato non è un'immagine valida."); // Verifica che il messaggio di errore sia impostato
        verifyNoInteractions(bikeService); // Verifica che il servizio non venga chiamato
    }

    /**
     * Verifica che una bici venga eliminata correttamente.
     */
    @Test
    void testDeleteBike() {
        when(redirectAttributes.addFlashAttribute(anyString(), anyString())).thenReturn(redirectAttributes);

        String redirectUrl = bikeController.deleteBike(1L, redirectAttributes);

        assertEquals("redirect:/profile", redirectUrl); // Verifica che il reindirizzamento sia verso il profilo
        verify(bikeService).deleteBike(1L); // Verifica che il servizio elimini la bici specificata
        verify(redirectAttributes).addFlashAttribute("successMessage", "Annuncio eliminato con successo!"); // Verifica che il messaggio di successo sia impostato
    }

    /**
     * Verifica che il form di modifica della bici venga visualizzato correttamente.
     */
    @Test
    void testEditBike() {
        Bike bike = new MountainBike("Trek", "Large", "Edit bike", 500.0, "Rome", null);
        when(bikeService.getBikeById(1L)).thenReturn(bike);

        String viewName = bikeController.editBike(1L, model);

        assertEquals("edit-bike", viewName); // Verifica che il nome della vista sia corretto
        verify(bikeService, times(1)).getBikeById(1L); // Verifica che il servizio recuperi la bici specificata
        verify(model, times(1)).addAttribute("bike", bike); // Verifica che i dettagli della bici siano aggiunti al modello
    }

    /**
     * Verifica che una bici venga aggiornata correttamente.
     */
    @Test
    void testUpdateBike() {
        Bike existingBike = new MountainBike("Trek", "Large", "Old description", 500.0, "Rome", null);
        Bike updatedBike = new MountainBike("Trek", "Large", "Updated description", 600.0, "Rome", null);

        when(bikeService.getBikeById(1L)).thenReturn(existingBike);

        String redirectUrl = bikeController.updateBike(
            1L, "Trek", "Large", "Updated description", 600.0, "Rome", redirectAttributes
        );

        assertEquals("redirect:/profile", redirectUrl); // Verifica che il reindirizzamento sia verso il profilo
        verify(bikeService).updateBike(eq(1L), any(Bike.class)); // Verifica che il servizio aggiorni la bici
        verify(redirectAttributes).addFlashAttribute("successMessage", "Annuncio aggiornato con successo!"); // Verifica che il messaggio di successo sia impostato
    }

    /**
     * Verifica che il filtro delle biciclette funzioni correttamente senza criteri.
     */
    @Test
    void testFilterBikes_EmptyCriteria() {
        List<Bike> bikes = Arrays.asList(
            new MountainBike("Trek", "Large", "Filtered bike", 500.0, "Rome", null)
        );
        when(bikeService.searchBikes(
            isNull(), isNull(), isNull(), isNull(), any(SortingStrategy.class)
        )).thenReturn(bikes);

        String viewName = bikeController.filterBikes(null, null, null, null, null, null, model);

        assertEquals("bici", viewName); // Verifica che il nome della vista sia corretto
        verify(model).addAttribute("bikes", bikes); // Verifica che le bici siano aggiunte al modello
    }
}
