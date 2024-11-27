package progettofinale.Model;

import java.util.List;
import java.util.Objects;
import progettofinale.Model.Bikemodel.*;

/**
 * Classe che rappresenta una conversazione tra due utenti.
 * Una conversazione è definita dai partecipanti e dai messaggi scambiati relativi a una determinata bicicletta. 
 * Questa classe è utilizzata per strutturare le conversazioni nell'inbox utente.
 */

public class Conversation {

    private User otherUser; // L'altro utente coinvolto nella conversazione
    private Bike bike; // La bicicletta oggetto della conversazione
    private List<Message> messages; // I messaggi scambiati nella conversazione

    // Costruttore di default per JPA
    public Conversation() {}

    // Costruttore per inizializzare una conversazione con i dettagli principali.
    public Conversation(User otherUser, Bike bike, List<Message> messages) {
        this.otherUser = otherUser;
        this.bike = bike;
        this.messages = messages;
    }

    // Getters e Setters
    public User getOtherUser() {
        return otherUser;
    }

    public void setOtherUser(User otherUser) {
        this.otherUser = otherUser;
    }

    public Bike getBike() {
        return bike;
    }

    public void setBike(Bike bike) {
        this.bike = bike;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    // Metodi utili per testing

    /**
     * Override del metodo equals() per confrontare due conversazioni.
     * Due conversazioni sono considerate uguali se coinvolgono lo stesso utente,
     * la stessa bicicletta e hanno la stessa lista di messaggi.
     *
     * @param o è l'oggetto da confrontare.
     * ritorna true se le conversazioni sono uguali, altrimenti false.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Conversation that = (Conversation) o;
        return Objects.equals(otherUser, that.otherUser) &&
               Objects.equals(bike, that.bike) &&
               Objects.equals(messages, that.messages);
    }

    /**
     * Override del metodo hashCode() per generare un hash coerente con equals().
     * ritorna l'hash code della conversazione.
     */
    @Override
    public int hashCode() {
        return Objects.hash(otherUser, bike, messages);
    }
}
