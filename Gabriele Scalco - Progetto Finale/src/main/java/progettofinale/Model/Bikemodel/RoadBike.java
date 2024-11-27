package progettofinale.Model.Bikemodel;

import progettofinale.Model.User;
import jakarta.persistence.*;

/**
 * Classe che estende Bike per rappresentare una bicicletta elettrica.
 * Al momento specifica solo la tipologia, ma ulteriori sviluppi potrebbero includere
 * anche attributi specifici come il peso.
 */

@Entity
@DiscriminatorValue("Road")
public class RoadBike extends Bike {
    
    public RoadBike() {
        super();
    }
    
    public RoadBike(String brand, String size, String description, double price, String place, User user) {
        super(brand, size, description, price, place, user);
    }

    @Override
    public String getBikeType() {
        return "Road";
    }
}
