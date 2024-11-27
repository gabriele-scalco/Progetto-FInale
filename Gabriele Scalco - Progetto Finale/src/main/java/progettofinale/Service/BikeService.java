package progettofinale.Service;

import progettofinale.Repository.*;
import progettofinale.Model.Bikemodel.*;
import progettofinale.Model.*;
import progettofinale.Service.SortingStrategy.*;
import progettofinale.Util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service per la gestione delle operazioni relative alle biciclette.
 * Fornisce metodi per il recupero, l'aggiunta, l'aggiornamento, l'eliminazione
 * e la ricerca delle biciclette. Gestisce anche la conversione delle immagini in Base64.
 */
@Service
public class BikeService {

    @Autowired
    private BikeRepository bikeRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Recupera tutte le biciclette dal database e converte le immagini in formato Base64.
     *
     * @return Una lista di tutte le biciclette con immagini convertite.
     */
    @Transactional
    public List<Bike> getAllBikes() {
        return bikeRepository.findAll().stream()
                .peek(this::convertImageToBase64) // Converte ogni immagine in Base64
                .collect(Collectors.toList());
    }

    /**
     * Aggiunge una nuova bicicletta al database.
     *
     * @param         bike La bicicletta da aggiungere.
     * @return        La bicicletta aggiunta.
     */
    @Transactional
    public Bike addBike(Bike bike) {
        return bikeRepository.save(bike);
    }

    /**
     * Recupera tutte le biciclette associate a un utente specifico.
     * Le immagini delle biciclette vengono convertite in formato Base64.
     *
     * @param user         L'utente proprietario delle biciclette.
     * @return             Una lista di biciclette appartenenti all'utente.
     */
    @Transactional
    public List<Bike> getBikesByUser(User user) {
        return bikeRepository.findByUser(user).stream()
                .peek(this::convertImageToBase64)
                .collect(Collectors.toList());
    }

    /**
     * Recupera una bicicletta specifica per ID e converte l'immagine in formato Base64.
     *
     * @param id                         L'ID della bicicletta da recuperare.
     * @return                           La bicicletta corrispondente all'ID specificato.
     * @throws ResponseStatusException   Se la bicicletta non viene trovata.
     */
    @Transactional
    public Bike getBikeById(Long id) {
        Bike bike = bikeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bike not found"));
        convertImageToBase64(bike);
        return bike;
    }

    /**
     * Aggiorna i dettagli di una bicicletta esistente senza modificare l'immagine.
     *
     * @param id                           L'ID della bicicletta da aggiornare.
     * @param updatedBike                  I nuovi dettagli della bicicletta.
     * @return                             La bicicletta aggiornata.
     * @throws ResponseStatusException     Se la bicicletta non viene trovata.
     */
    @Transactional
    public Bike updateBike(Long id, Bike updatedBike) {
        Bike existingBike = bikeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bike not found"));

        // Aggiorna solo i campi modificabili
        existingBike.setBrand(updatedBike.getBrand());
        existingBike.setSize(updatedBike.getSize());
        existingBike.setDescription(updatedBike.getDescription());
        existingBike.setPrice(updatedBike.getPrice());
        existingBike.setPlace(updatedBike.getPlace());

        // L'immagine non viene modificata
        return bikeRepository.save(existingBike);
    }

    /**
     * Elimina una bicicletta specifica dal database, 
     * inclusi i messaggi associati e i riferimenti nelle wishlist degli utenti.
     *
     * @param id                           L'ID della bicicletta da eliminare.
     * @throws ResponseStatusException     Se la bicicletta non viene trovata.
     */
    @Transactional
    public void deleteBike(Long id) {
        Bike bike = bikeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bike not found"));

        // Rimuovi la bicicletta dalle wishlist di tutti gli utenti
        List<User> users = userRepository.findAll();
        for (User user : users) {
            if (user.getWishlist().contains(bike)) {
                user.getWishlist().remove(bike);
                userRepository.save(user);
            }
        }

        // Elimina tutti i messaggi associati alla bicicletta
        messageRepository.deleteByBike(bike);

        // Elimina la bicicletta
        bikeRepository.deleteById(id);
    }


    /**
     * Cerca biciclette in base ai criteri forniti e ordina i risultati
     * utilizzando una strategia di ordinamento specifica.
     * Le immagini delle biciclette vengono convertite in formato Base64.
     *
     * @param size               Dimensione della bicicletta (opzionale).
     * @param maxPrice           Prezzo massimo della bicicletta (opzionale).
     * @param brand              Marca della bicicletta (opzionale).
     * @param bikeType           Tipo di bicicletta (opzionale).
     * @param sortingStrategy    Strategia di ordinamento da applicare.
     * @return                   Una lista di biciclette filtrate e ordinate.
     */
    @Transactional
    public List<Bike> searchBikes(String size, Double maxPrice, String brand, String bikeType, SortingStrategy sortingStrategy) {
        List<Bike> filteredBikes = bikeRepository.findAll().stream()
                .filter(bike -> (
                        (size == null || size.isEmpty() || bike.getSize().equalsIgnoreCase(size)) &&
                        (maxPrice == null || bike.getPrice() <= maxPrice) &&
                        (brand == null || brand.isEmpty() || bike.getBrand().equalsIgnoreCase(brand)) &&
                        (bikeType == null || bikeType.isEmpty() || bike.getBikeType().equalsIgnoreCase(bikeType))
                ))
                .peek(this::convertImageToBase64) // Converte le immagini in Base64
                .collect(Collectors.toList());

        // Applica la strategia di ordinamento
        return sortingStrategy.sort(filteredBikes);
    }

    /**
     * Converte l'immagine binaria di una bicicletta in una stringa Base64.
     * Se l'immagine è null, imposta un'immagine predefinita.
     *
     * @param bike         La bicicletta di cui convertire l'immagine.
     */
    @Transactional
    private void convertImageToBase64(Bike bike) {
        if (bike.getImage() != null) {
            String base64Image = "data:image/jpeg;base64," + ImageUtil.encodeToBase64(bike.getImage());
            bike.setImagePath(base64Image);
        } else {
            // Imposta un'immagine predefinita se l'immagine non è presente
            bike.setImagePath("/images/default-bike.jpg");
        }
    }
}
