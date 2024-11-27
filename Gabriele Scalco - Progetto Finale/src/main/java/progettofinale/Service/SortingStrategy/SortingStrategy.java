package progettofinale.Service.SortingStrategy;

import java.util.List;
import progettofinale.Model.Bikemodel.*;

/**
 * Interfaccia che rappresenta una strategia di ordinamento per le biciclette.
 * Implementa il design pattern Strategy, consentendo di definire dinamicamente
 * logiche di ordinamento diverse senza modificare il contesto in cui vengono utilizzate.
 */
public interface SortingStrategy {

    /**
     * Ordina una lista di biciclette in base a una logica definita
     * dall'implementazione specifica della strategia.
     *
     * @param bikes     La lista di biciclette da ordinare.
     * @return          La lista ordinata secondo la strategia specifica.
     */
    List<Bike> sort(List<Bike> bikes);
}
