package progettofinale.Model.Bikemodel;

import progettofinale.Model.User;
import jakarta.persistence.*;

/**
 * Classe che rappresenta una bicicletta generica.
 * Contiene attributi come marca, taglia, prezzo e immagine, e metodi per gestire questi dati.
 */

@Entity
// Specifica che tutte le sottoclassi di Bike saranno mappate in una singola tabella nel database.
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
// Colonna che distingue il tipo di sottoclasse (es. MountainBike, RoadBike).
@DiscriminatorColumn(name = "type")
public abstract class Bike {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bike_seq")
    @SequenceGenerator(name = "bike_seq", sequenceName = "bike_sequence", allocationSize = 1)
    private Long id;

    private String brand;  // Marca della bicicletta
    private String size;   // Dimensione (es: M, L, XL)
    private String description;  // Descrizione della bicicletta
    private double price;  // Prezzo 
    private String place;  // Regione dove la bicicletta è disponibile

    @Lob
    @Column(name = "image", nullable = true)
    private byte[] image;  // Immagine binaria della bicicletta

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;  // Utente proprietario della bicicletta

    /**
     * Il campo `imagePath` è utilizzato per rappresentare l'immagine della bicicletta 
     * in un formato leggibile dal frontend. Poiché le immagini sono memorizzate nel 
     * database come array di byte (`byte[]`),`imagePath` permette di convertirle in
     * stringhe codificate in Base64, pronte per essere usate nel tag HTML <img>. 
     * Questo campo non è salvato nel database grazie all'annotazione `@Transient`.
     */
    @Transient
    private String imagePath; 

    // Costruttore di default per JPA
    public Bike() {}

    // Costruttore completo per inizializzare i campi principali della bicicletta.
    public Bike(String brand, String size, String description, double price, String place, User user) {
        this.brand = brand;
        this.size = size;
        this.description = description;
        this.price = price;
        this.place = place;
        this.user = user;
    }

    /**
     * Metodo astratto per ottenere il tipo di bicicletta.
     * Le sottoclassi devono implementare questo metodo per fornire il tipo specifico.
     * Ritorna il tipo di bicicletta come stringa.
     */
    public abstract String getBikeType();

    // Getter e Setter
    public Long getId() { return id; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getPlace() { return place; }
    public void setPlace(String place) { this.place = place; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public byte[] getImage() { return image; }
    public void setImage(byte[] image) { this.image = image; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
}
