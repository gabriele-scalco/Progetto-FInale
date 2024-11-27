package progettofinale.Repository;

import progettofinale.Model.Bikemodel.*;
import progettofinale.Model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repository per la gestione delle operazioni di persistenza relative alle biciclette.
 * Estende JpaRepository per fornire metodi CRUD standard e definisce metodi personalizzati per query specifiche.
 */
public interface BikeRepository extends JpaRepository<Bike, Long> {

    /**
     * Recupera tutte le biciclette associate a un determinato utente.
     *
     * @param user     L'utente proprietario delle biciclette.
     * @return         Una lista di biciclette appartenenti all'utente specificato.
     */
    List<Bike> findByUser(User user);
}
