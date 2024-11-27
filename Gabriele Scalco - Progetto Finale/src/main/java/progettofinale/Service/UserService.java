package progettofinale.Service;

import progettofinale.Repository.UserRepository;
import progettofinale.Model.*;
import progettofinale.Model.Bikemodel.*;
import progettofinale.Util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import org.hibernate.Hibernate;

/**
 * Service per la gestione delle operazioni relative agli utenti.
 * Fornisce metodi per aggiungere, rimuovere, recuperare utenti,
 * gestire la wishlist e criptare le password.
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

     /**
     * Recupera un utente in base al suo ID.
     *
     * @param id                             L'ID dell'utente da recuperare.
     * @return                               L'utente corrispondente all'ID.
     * @throws ResponseStatusException       Se l'utente non è trovato.
     */
    @Transactional
    public User retrieveUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    /**
     * Trova un utente in base all'email. 
     * (Metodo di supporto, simile a retrieveUserByEmail).
     *
     * @param email         L'email dell'utente.
     * @return              L'utente corrispondente.
     */
    @Transactional
    public User findByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        return user;
    }

    /**
     * Aggiunge un nuovo utente al database.
     * Cripta la password prima di salvare.
     *
     * @param user     L'utente da aggiungere.
     * @return         L'utente aggiunto con la password criptata.
     */
    @Transactional
    public User addUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Cripta la password
        return userRepository.save(user);
    }

    /**
     * Rimuove un utente dal database in base al suo ID.
     *
     * @param id                             L'ID dell'utente da rimuovere.
     * @throws ResponseStatusException       Se l'utente non è trovato.
     */
    @Transactional
    public void removeUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        userRepository.deleteById(id);
    }

    /**
     * Aggiunge una bicicletta alla wishlist di un utente.
     *
     * @param user         L'utente a cui aggiungere la bicicletta.
     * @param bike         La bicicletta da aggiungere alla wishlist.
     */
    @Transactional
    public void addToWishlist(User user, Bike bike) {
        if (user.getWishlist().contains(bike)) {
            return; // Evita duplicati
        }
        user.getWishlist().add(bike);
        userRepository.save(user);
    }


    /**
     * Rimuove una bicicletta dalla wishlist di un utente.
     *
     * @param userId                         L'ID dell'utente.
     * @param bikeId                         L'ID della bicicletta da rimuovere.
     * @throws IllegalArgumentException      Se l'utente non è trovato.
     */
    @Transactional
    public void removeBikeFromWishlist(Long userId, Long bikeId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        Bike bikeToRemove = user.getWishlist().stream()
                .filter(b -> b.getId().equals(bikeId))
                .findFirst()
                .orElse(null);

        if (bikeToRemove != null) {
            user.getWishlist().remove(bikeToRemove);
            userRepository.save(user);
        }
    }

    /**
     * Recupera un utente in base alla sua email, inizializzando la relazione wishlist
     * e preparando i dati delle immagini associate per il rendering.
     * 
     * Questo metodo carica esplicitamente la wishlist dell'utente per evitare problemi 
     * di LazyInitializationException, converte le immagini delle biciclette in formato Base64 
     * e assegna un'immagine predefinita in caso di assenza.
     * 
     * @param email                         L'email dell'utente da recuperare.
     * @return                              L'utente con la sua wishlist e immagini pronte per la visualizzazione.
     * @throws ResponseStatusException      Se l'utente non viene trovato.
     */
    @Transactional
    public User retrieveUserWithWishlistByEmail(String email) {
        // Recupera l'utente dal database tramite l'email
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        // Inizializza la relazione wishlist per evitare LazyInitializationException
        Hibernate.initialize(user.getWishlist());

        // Converte le immagini in Base64 per ciascuna bicicletta nella wishlist
        user.getWishlist().forEach(bike -> {
            if (bike.getImage() != null) {
                // Codifica l'immagine in Base64
                String base64Image = "data:image/jpeg;base64," + ImageUtil.encodeToBase64(bike.getImage());
                bike.setImagePath(base64Image);
            } else {
                // Assegna un'immagine predefinita se nessuna immagine è presente
                bike.setImagePath("/images/default-bike.jpg");
            }
        });

        return user;
    }


}
