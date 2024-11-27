package progettofinale.Model.Bikemodel;

import progettofinale.Model.User;

/**
 * Classe Factory per creare oggetti `Bike` basati sul tipo specificato.
 * Utilizza il pattern Factory per centralizzare la logica di creazione delle sottoclassi di `Bike`.
 */

public class BikeFactory {
    /**
     * Crea una nuova istanza di `Bike` in base al tipo specificato.
     * Ritorna istanza di una sottoclasse di `Bike` corrispondente al tipo specificato.
     * @throws IllegalArgumentException Se il tipo di bicicletta non è supportato.
     */
        public static Bike createBike(String type, String brand, String size, String description, double price, String place, User user) {
        switch (type.toLowerCase()) {
            case "mountain":
                return new MountainBike(brand, size, description, price, place, user);
            case "road":
                return new RoadBike(brand, size, description, price, place, user);
            case "electric":
                return new ElectricBike(brand, size, description, price, place, user);
            default:
                throw new IllegalArgumentException("Tipo di bici non supportato: " + type);
        }
    }
}
