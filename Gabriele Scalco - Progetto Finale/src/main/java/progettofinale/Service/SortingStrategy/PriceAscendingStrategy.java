package progettofinale.Service.SortingStrategy;

import java.util.List;
import java.util.Comparator;
import progettofinale.Model.Bikemodel.*;

/**
 * Implementazione della strategia di ordinamento per ordinare le biciclette
 * in base al prezzo in ordine crescente.
 */
public class PriceAscendingStrategy implements SortingStrategy {

    /**
     * Ordina la lista di biciclette in base al prezzo in ordine crescente.
     *
     * @param bikes         La lista di biciclette da ordinare.
     * @return              La lista ordinata in ordine crescente per prezzo.
     */
    @Override
    public List<Bike> sort(List<Bike> bikes) {
        return bikes.stream()
                    .sorted(Comparator.comparingDouble(Bike::getPrice)) // Compara i prezzi delle biciclette
                    .toList(); // Converte il risultato in una lista
    }
}
