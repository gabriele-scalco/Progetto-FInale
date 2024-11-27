package progettofinale.Repository;

import progettofinale.Model.*;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository per la gestione delle operazioni di persistenza relative alle biciclette.
 * Estende JpaRepository per fornire metodi CRUD standard e definisce metodi personalizzati per query specifiche.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Recupera un utente dal database in base alla sua email.
     * Questo metodo è utile per l'autenticazione o per operazioni che richiedono di identificare un utente univocamente.
     *
     * @param email     L'email dell'utente da cercare.
     * @return          L'utente corrispondente all'email specificata, oppure null se non trovato.
     */
    User findByEmail(String email);
}
