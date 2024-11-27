package progettofinale.Repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import progettofinale.Model.Bikemodel.*;
import progettofinale.Model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ActiveProfiles("test")
class BikeRepositoryTest {

    @Autowired
    private BikeRepository bikeRepository;

    @Autowired
    private UserRepository userRepository;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        // Crea utenti di esempio
        user1 = new User("user1@example.com", "password", "User1");
        user2 = new User("user2@example.com", "password", "User2");
        userRepository.save(user1);
        userRepository.save(user2);

        // Aggiungi bici di diversi tipi per user1
        Bike mountainBike = new MountainBike("BrandA", "M", "Mountain bike", 500.0, "City1", user1);
        mountainBike.setImage("MountainImage".getBytes());
        Bike roadBike = new RoadBike("BrandB", "L", "Road bike", 600.0, "City2", user1);
        roadBike.setImage("RoadImage".getBytes());
        Bike electricBike = new ElectricBike("BrandC", "S", "Electric bike", 700.0, "City3", user1);
        electricBike.setImage("ElectricImage".getBytes());

        bikeRepository.save(mountainBike);
        bikeRepository.save(roadBike);
        bikeRepository.save(electricBike);

        // Aggiungi una bici per user2
        Bike anotherMountainBike = new MountainBike("BrandD", "XL", "Another mountain bike", 800.0, "City4", user2);
        anotherMountainBike.setImage("AnotherMountainImage".getBytes());
        bikeRepository.save(anotherMountainBike);
    }

    /**
     * Verifica che tutte le bici di diversi tipi associate a un utente vengano recuperate correttamente.
     */
    @Test
    void testFindByUser_AllBikeTypes() {
        List<Bike> bikes = bikeRepository.findByUser(user1);

        assertEquals(3, bikes.size()); // Verifica che siano state trovate 3 bici
        assertEquals("BrandA", bikes.get(0).getBrand()); // Verifica la marca della prima bici
        assertEquals("BrandB", bikes.get(1).getBrand()); // Verifica la marca della seconda bici
        assertEquals("BrandC", bikes.get(2).getBrand()); // Verifica la marca della terza bici

        assertEquals("MountainImage", new String(bikes.get(0).getImage())); // Verifica l'immagine della prima bici
        assertEquals("RoadImage", new String(bikes.get(1).getImage())); // Verifica l'immagine della seconda bici
        assertEquals("ElectricImage", new String(bikes.get(2).getImage())); // Verifica l'immagine della terza bici
    }

    /**
     * Verifica che una bici associata a un utente specifico venga recuperata correttamente.
     */
    @Test
    void testFindByUser_SpecificUserWithOneBike() {
        List<Bike> bikes = bikeRepository.findByUser(user2);

        assertEquals(1, bikes.size()); // Verifica che sia stata trovata solo una bici
        assertEquals("BrandD", bikes.get(0).getBrand()); // Verifica la marca della bici
        assertEquals("AnotherMountainImage", new String(bikes.get(0).getImage())); // Verifica l'immagine della bici
    }

    /**
     * Verifica che un utente senza bici non restituisca alcun risultato.
     */
    @Test
    void testFindByUser_NoBikes() {
        User user3 = new User("user3@example.com", "password", "User3");
        userRepository.save(user3);

        List<Bike> bikes = bikeRepository.findByUser(user3);

        assertEquals(0, bikes.size()); // Verifica che non siano state trovate bici
    }

    /**
     * Verifica che le bici siano filtrate correttamente in base al tipo e all'utente.
     */
    @Test
    void testFindByUser_FilteredByType() {
        // Recupera solo le mountain bike di user1
        List<Bike> bikes = bikeRepository.findByUser(user1);

        long mountainBikeCount = bikes.stream()
            .filter(bike -> bike instanceof MountainBike)
            .count();

        assertEquals(1, mountainBikeCount); // Verifica che ci sia esattamente una mountain bike
    }
}
