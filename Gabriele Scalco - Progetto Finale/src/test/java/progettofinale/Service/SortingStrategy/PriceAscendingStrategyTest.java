package progettofinale.Service.SortingStrategy;

import progettofinale.Model.Bikemodel.*;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PriceAscendingStrategyTest {

    /**
     * Verifica che le biciclette siano ordinate correttamente in base al prezzo in ordine crescente.
     */
    @Test
    void testSort() {
        Bike bike1 = new MountainBike("Trek", "Large", "Good condition", 500.0, "Rome", null);
        Bike bike2 = new RoadBike("Giant", "Medium", "Like new", 300.0, "Milan", null);
        Bike bike3 = new ElectricBike("Specialized", "Small", "Brand new", 700.0, "Naples", null);

        List<Bike> bikes = Arrays.asList(bike1, bike2, bike3);
        PriceAscendingStrategy strategy = new PriceAscendingStrategy();

        List<Bike> sortedBikes = strategy.sort(bikes);

        assertEquals(bike2, sortedBikes.get(0)); // Verifica che la bici più economica sia la prima
        assertEquals(bike1, sortedBikes.get(1)); // Verifica che la bici intermedia sia la seconda
        assertEquals(bike3, sortedBikes.get(2)); // Verifica che la bici più costosa sia l'ultima
    }

    /**
     * Verifica che una lista vuota non sollevi eccezioni e ritorni una lista vuota.
     */
    @Test
    void testSort_EmptyList() {
        List<Bike> bikes = Collections.emptyList();
        PriceAscendingStrategy strategy = new PriceAscendingStrategy();

        List<Bike> sortedBikes = strategy.sort(bikes);

        assertTrue(sortedBikes.isEmpty()); // Verifica che la lista ordinata sia vuota
    }

    /**
     * Verifica che una lista con una sola bici non venga modificata.
     */
    @Test
    void testSort_SingleBike() {
        Bike bike = new MountainBike("Trek", "Large", "Good condition", 500.0, "Rome", null);
        List<Bike> bikes = Collections.singletonList(bike);
        PriceAscendingStrategy strategy = new PriceAscendingStrategy();

        List<Bike> sortedBikes = strategy.sort(bikes);

        assertEquals(1, sortedBikes.size()); // Verifica che la lista ordinata contenga un solo elemento
        assertEquals(bike, sortedBikes.get(0)); // Verifica che l'unica bici rimanga invariata
    }

    /**
     * Verifica che le biciclette con lo stesso prezzo vengano mantenute nell'ordine originale.
     */
    @Test
    void testSort_BikesWithSamePrice() {
        Bike bike1 = new MountainBike("Trek", "Large", "Good condition", 500.0, "Rome", null);
        Bike bike2 = new RoadBike("Giant", "Medium", "Like new", 500.0, "Milan", null);

        List<Bike> bikes = Arrays.asList(bike1, bike2);
        PriceAscendingStrategy strategy = new PriceAscendingStrategy();

        List<Bike> sortedBikes = strategy.sort(bikes);

        assertEquals(bike1, sortedBikes.get(0)); // Verifica che la prima bici rimanga la prima
        assertEquals(bike2, sortedBikes.get(1)); // Verifica che la seconda bici rimanga la seconda
    }
}
