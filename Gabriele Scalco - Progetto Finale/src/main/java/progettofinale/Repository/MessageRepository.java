package progettofinale.Repository;

import progettofinale.Model.*;
import progettofinale.Model.Bikemodel.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repository per la gestione delle operazioni di persistenza relative alle biciclette.
 * Estende JpaRepository per fornire metodi CRUD standard e definisce metodi personalizzati per query specifiche.
 */
public interface MessageRepository extends JpaRepository<Message, Long> {

    /**
     * Elimina tutti i messaggi associati a una determinata bicicletta.
     *
     * @param bike        La bicicletta i cui messaggi devono essere eliminati.
     */
    void deleteByBike(Bike bike);

    /**
     * Recupera tutti i messaggi relativi a un determinato utente (sia come mittente che come destinatario),
     * includendo i dettagli del mittente, destinatario e bicicletta associata.
     *
     * @param userId      L'ID dell'utente (mittente o destinatario).
     * @return            Una lista di messaggi ordinati per timestamp in ordine crescente.
     */
    @Query("SELECT m FROM Message m " +
           "JOIN FETCH m.sender s " +
           "JOIN FETCH m.receiver r " +
           "JOIN FETCH m.bike b " +
           "WHERE s.id = :userId OR r.id = :userId " +
           "ORDER BY m.timestamp ASC")
    List<Message> findAllByUserId(@Param("userId") Long userId);
}
