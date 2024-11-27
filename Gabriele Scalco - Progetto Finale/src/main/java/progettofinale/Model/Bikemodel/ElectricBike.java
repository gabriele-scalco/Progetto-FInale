package progettofinale.Model.Bikemodel;

import progettofinale.Model.User;
import jakarta.persistence.*;

/**
 * Classe che estende Bike per rappresentare una bicicletta elettrica.
 * Al momento specifica solo la tipologia, ma ulteriori sviluppi potrebbero includere
 * anche attributi specifici come la potenza della batteria o l'autonomia.
 */

@Entity
@DiscriminatorValue("Electric")
public class ElectricBike extends Bike {

    public ElectricBike() {
        super();
    }
    
    public ElectricBike(String brand, String size, String description, double price, String place, User user) {
        super(brand, size, description, price, place, user);
    }

    @Override
    public String getBikeType() {
        return "Electric";
    }
}
