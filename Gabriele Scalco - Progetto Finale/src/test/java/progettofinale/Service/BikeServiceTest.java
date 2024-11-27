package progettofinale.Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import progettofinale.Model.Bikemodel.*;
import progettofinale.Model.User;
import progettofinale.Repository.BikeRepository;
import progettofinale.Repository.MessageRepository;
import progettofinale.Repository.UserRepository;
import progettofinale.Service.SortingStrategy.SortingStrategy;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BikeServiceTest {

    @Mock
    private BikeRepository bikeRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BikeService bikeService;

    private Bike bike1;
    private Bike bike2;
    private User user;
    private SortingStrategy sortingStrategy;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User("test@example.com", "password", "Test User");
        bike1 = new MountainBike("BrandA", "M", "Mountain bike", 500.0, "City1", user);
        bike1.setImage("MountainImage".getBytes());
        bike2 = new RoadBike("BrandB", "L", "Road bike", 600.0, "City2", user);
        bike2.setImage("RoadImage".getBytes());

        sortingStrategy = bikes -> bikes.stream()
                .sorted((b1, b2) -> Double.compare(b1.getPrice(), b2.getPrice()))
                .toList();
    }

    /**
     * Verifica che una bici venga eliminata correttamente e che le wishlist vengano aggiornate.
     */
    @Test
    void testDeleteBike_BikeExists() {
        // Simula utenti con wishlist
        User user1 = new User("user1@example.com", "password", "User One");
        user1.setWishlist(new ArrayList<>(List.of(bike1)));
        User user2 = new User("user2@example.com", "password", "User Two");
        user2.setWishlist(new ArrayList<>(List.of(bike2, bike1)));

        when(bikeRepository.findById(1L)).thenReturn(Optional.of(bike1));
        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        // Esegue il metodo
        bikeService.deleteBike(1L);

        // Verifica che la bici venga rimossa dalle wishlist degli utenti
        assertFalse(user1.getWishlist().contains(bike1)); // Controlla la rimozione dalla wishlist di user1
        assertFalse(user2.getWishlist().contains(bike1)); // Controlla la rimozione dalla wishlist di user2
        assertTrue(user2.getWishlist().contains(bike2)); // Controlla che le altre bici rimangano nella wishlist

        // Verifica chiamate ai repository
        verify(bikeRepository, times(1)).findById(1L); // La bici viene cercata
        verify(messageRepository, times(1)).deleteByBike(bike1); // I messaggi associati vengono eliminati
        verify(userRepository, times(1)).findAll(); // Gli utenti vengono recuperati
        verify(bikeRepository, times(1)).deleteById(1L); // La bici viene eliminata
    }

    /**
     * Verifica che venga restituita una lista vuota se il database non contiene bici.
     */
    @Test
    void testGetAllBikes_EmptyDatabase() {
        when(bikeRepository.findAll()).thenReturn(Collections.emptyList());

        // Esegue il metodo
        List<Bike> bikes = bikeService.getAllBikes();

        // Verifica che la lista sia vuota
        assertTrue(bikes.isEmpty()); // Controlla che non vengano restituite bici

        // Verifica chiamata al repository
        verify(bikeRepository, times(1)).findAll(); // Il repository viene chiamato
    }

    /**
     * Verifica il comportamento del metodo updateBike con una bici valida.
     */
    @Test
    void testUpdateBike_ValidBike() {
        Bike updatedBike = new MountainBike("UpdatedBrand", "S", "Updated description", 700.0, "UpdatedCity", user);

        when(bikeRepository.findById(1L)).thenReturn(Optional.of(bike1));
        when(bikeRepository.save(any(Bike.class))).thenReturn(updatedBike);

        // Esegue il metodo
        Bike result = bikeService.updateBike(1L, updatedBike);

        // Verifica i valori aggiornati
        assertEquals("UpdatedBrand", result.getBrand()); // Controlla il brand aggiornato
        assertEquals("S", result.getSize()); // Controlla la taglia aggiornata
        assertEquals("Updated description", result.getDescription()); // Controlla la descrizione aggiornata
        assertEquals(700.0, result.getPrice()); // Controlla il prezzo aggiornato
        assertEquals("UpdatedCity", result.getPlace()); // Controlla la città aggiornata

        // Verifica interazioni con il repository
        verify(bikeRepository, times(1)).findById(1L); // La bici viene cercata
        verify(bikeRepository, times(1)).save(any(Bike.class)); // La bici aggiornata viene salvata
    }

    /**
     * Verifica il comportamento del metodo searchBikes con parametri validi.
     */
    @Test
    void testSearchBikes_ValidParams() {
        when(bikeRepository.findAll()).thenReturn(List.of(bike1, bike2));

        // Esegue il metodo
        List<Bike> result = bikeService.searchBikes("M", 500.0, "BrandA", "Mountain", sortingStrategy);

        // Verifica i risultati della ricerca
        assertEquals(1, result.size()); // Controlla che venga trovata una sola bici
        assertEquals(bike1, result.get(0)); // Controlla che la bici trovata sia quella attesa
    }

    /**
     * Verifica che il metodo searchBikes gestisca parametri nulli.
     */
    @Test
    void testSearchBikes_NullParams() {
        when(bikeRepository.findAll()).thenReturn(List.of(bike1, bike2));

        // Esegue il metodo
        List<Bike> result = bikeService.searchBikes(null, null, null, null, sortingStrategy);

        // Verifica che tutte le bici vengano restituite
        assertEquals(2, result.size()); // Controlla che tutte le bici vengano restituite
        assertTrue(result.contains(bike1) && result.contains(bike2)); // Controlla che entrambe le bici siano incluse
    }

    /**
     * Verifica che venga gestita l'assenza di immagine con un valore predefinito.
     */
    @Test
    void testConvertImageToBase64_NullImage() {
        bike1.setImage(null); // Simula una bici senza immagine

        // Mock il repository per restituire bike1
        when(bikeRepository.findAll()).thenReturn(List.of(bike1));

        // Esegue il metodo
        bikeService.getAllBikes();

        // Verifica che l'immagine predefinita venga impostata
        assertEquals("/images/default-bike.jpg", bike1.getImagePath()); // Controlla il percorso dell'immagine predefinita
    }

    /**
     * Verifica che un'immagine venga convertita correttamente in Base64.
     */
    @Test
    void testConvertImageToBase64_WithImage() {
        when(bikeRepository.findAll()).thenReturn(List.of(bike1)); // Mock per restituire bike1

        // Esegue il metodo
        bikeService.getAllBikes();

        // Verifica la conversione in Base64
        assertNotNull(bike1.getImagePath()); // Controlla che l'immagine convertita non sia null
        assertTrue(bike1.getImagePath().startsWith("data:image/jpeg;base64,")); // Controlla che inizi con il prefisso Base64
    }
}
