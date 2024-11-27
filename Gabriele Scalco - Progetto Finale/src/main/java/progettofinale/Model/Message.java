package progettofinale.Model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import progettofinale.Model.Bikemodel.*;

/**
 * Classe che rappresenta un messaggio tra due utenti.
 * Contiene informazioni sul mittente, destinatario, contenuto del messaggio, data di invio 
 * e un riferimento alla bicicletta oggetto della conversazione.
 */
@Entity
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Identificatore univoco del messaggio

    @ManyToOne
    @JoinColumn(name = "sender_id", referencedColumnName = "id", nullable = false)
    private User sender; // Mittente del messaggio

    @ManyToOne
    @JoinColumn(name = "receiver_id", referencedColumnName = "id", nullable = false)
    private User receiver; // Destinatario del messaggio

    @ManyToOne
    @JoinColumn(name = "bike_id", referencedColumnName = "id", nullable = false)
    private Bike bike; // Bicicletta associata al messaggio

    private String content; // Contenuto del messaggio
    private LocalDateTime timestamp; // Data e ora di invio del messaggio

    // Costruttore di default richiesto da JPA.
    public Message() {}

    // Costruttore per inizializzare un messaggio con i dettagli principali.
    public Message(User sender, User receiver, Bike bike, String content) {
        this.sender = sender;
        this.receiver = receiver;
        this.bike = bike;
        this.content = content;
        this.timestamp = null; // Il timestamp verrà impostato in un secondo momento
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public Bike getBike() {
        return bike;
    }

    public void setBike(Bike bike) {
        this.bike = bike;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
