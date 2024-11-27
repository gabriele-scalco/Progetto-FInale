package progettofinale.Model;

import progettofinale.Model.Bikemodel.*;
import jakarta.persistence.*;
import java.util.*;

/**
 * Classe che rappresenta un utente della piattaforma.
 * Contiene informazioni sull'email, password, nome utente e una lista di biciclette aggiunte alla wishlist. 
 * Può essere estesa in futuro per includere dettagli aggiuntivi come indirizzo, numero di telefono, ecc.
 */
@Entity
@Table(name = "users") // Specifica un nome di tabella personalizzato per evitare conflitti con parole riservate
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @SequenceGenerator(name = "user_seq", sequenceName = "user_sequence", allocationSize = 1)
    private Long id; // Identificatore unico dell'utente
    private String email; // Email dell'utente (usata anche per il login)
    private String password; // Password criptata dell'utente
    private String name; // Nome utente

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_wishlist",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "bike_id")
    )
    private List<Bike> wishlist = new ArrayList<>(); // Lista delle biciclette preferite dall'utente

     // Costruttore di default richiesto da JPA.
    public User() {}

    // Costruttore per inizializzare un utente con i dettagli principali.
    public User(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    // Getter e Setter

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Bike> getWishlist() {
        return wishlist;
    }

    public void setWishlist(List<Bike> wishlist) {
        this.wishlist = wishlist;
    }
}
