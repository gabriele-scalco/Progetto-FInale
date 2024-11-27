package progettofinale.Controller;

import progettofinale.Model.*;
import progettofinale.Model.Bikemodel.*;
import progettofinale.Service.*;
import progettofinale.Service.SortingStrategy.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * Controller per gestire le operazioni relative alle biciclette.
 * Fornisce metodi per visualizzare, aggiungere, aggiornare, eliminare biciclette
 */
@Controller
public class BikeController {

    @Autowired
    private BikeService bikeService;

    @Autowired
    private UserService userService;

    /**
     * Mostra la pagina principale con tutte le biciclette disponibili.
     *
     * @param model Il modello utilizzato per passare dati alla vista.
     * @return Vista "bici".
     */
    @GetMapping("/")
    public String viewBikes(Model model) {
        // Recupera tutte le biciclette dal database
        List<Bike> bikes = bikeService.getAllBikes();

        // Aggiunge la lista di biciclette al modello per la vista
        model.addAttribute("bikes", bikes);
        return "bici";
    }

    /**
     * Mostra il profilo dell'utente autenticato, incluse le biciclette e la wishlist.
     * Include anche il form per aggiungere una nuova bici.
     *
     * @param model     Il modello utilizzato per passare dati alla vista.
     * @param principal Contiene i dettagli dell'utente autenticato.
     * @return Vista "user-profile".
     */
    @GetMapping("/profile")
    public String showUserProfile(Model model, Principal principal) {
        // Recupera l'email dell'utente autenticato
        String username = principal.getName();

        // Cerca l'utente nel database tramite l'email
        User user = userService.retrieveUserWithWishlistByEmail(username);

        // Recupera le biciclette create dall'utente
        List<Bike> userBikes = bikeService.getBikesByUser(user);

        // Passa i dati dell'utente, le biciclette e la wishlist alla vista
        model.addAttribute("user", user);
        model.addAttribute("bikes", userBikes);
        model.addAttribute("bikeTypes", List.of("Mountain", "Road", "Electric")); // Tipi di biciclette disponibili
        model.addAttribute("wishlist", user.getWishlist());
        return "user-profile";
    }

    /**
     * Filtra le biciclette in base ai criteri specificati.
     *
     * @param size        Dimensione della bici.
     * @param place       Region di vendita della bici.
     * @param maxPrice    Prezzo massimo della bici.
     * @param brand       Marca della bici.
     * @param bikeType    Tipo di bici (es. Mountain, Road, Electric).
     * @param order       Ordinamento (es. ascendente o discendente).
     * @param model       Il modello utilizzato per passare dati alla vista.
     * @return            Vista "bici".
     */
    @GetMapping("/filter")
    public String filterBikes(@RequestParam(required = false) String size,
                              @RequestParam(required = false) String place,
                              @RequestParam(required = false) Double maxPrice,
                              @RequestParam(required = false) String brand,
                              @RequestParam(required = false) String bikeType,
                              @RequestParam(name = "order", required = false) String order,    
                              Model model) {
        // Determina la strategia di ordinamento in base al parametro "order"
        SortingStrategy sortingStrategy;
        if ("desc".equals(order)) {
            sortingStrategy = new PriceDescendingStrategy(); // Ordina per prezzo decrescente
        } else {
            sortingStrategy = new PriceAscendingStrategy(); // Ordina per prezzo crescente
        }

        // Filtra le biciclette in base ai criteri forniti
        List<Bike> filteredBikes = bikeService.searchBikes(size, maxPrice, brand, bikeType, sortingStrategy);

        // Aggiunge le biciclette filtrate al modello per la vista
        model.addAttribute("bikes", filteredBikes);
        return "bici";
    }

    /**
     * Aggiunge una nuova bicicletta per un utente specifico.
     *
     * @param bikeType          Tipo di bici (Mountain, Road, Electric).
     * @param brand             Marca della bici.
     * @param size              Dimensione della bici.
     * @param description       Descrizione della bici.
     * @param price             Prezzo della bici.
     * @param place             Posizione geografica della bici.
     * @param userId            ID dell'utente proprietario della bici.
     * @param image             Immagine della bici (opzionale).
     * @param redirect          Per passare messaggi alla vista.
     * @return                  Redirect alla pagina del profilo utente.
     */
    @PostMapping("/bikes/add")
    public String addBike(
            @RequestParam("bikeType") String bikeType,
            @RequestParam("brand") String brand,
            @RequestParam("size") String size,
            @RequestParam("description") String description,
            @RequestParam("price") double price,
            @RequestParam("place") String place,
            @RequestParam("userId") Long userId,
            @RequestParam(value = "image", required = false) MultipartFile image,
            RedirectAttributes redirectAttributes) {

        // Recupera l'utente che sta aggiungendo la bicicletta
        User user = userService.retrieveUserById(userId);

        // Crea una nuova istanza della bicicletta utilizzando la Factory
        Bike newBike = BikeFactory.createBike(bikeType, brand, size, description, price, place, user);

        try {
            if (image != null && !image.isEmpty()) {
                // Controlla che il file caricato sia un'immagine valida
                if (!image.getContentType().startsWith("image/")) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Il file caricato non è un'immagine valida.");
                    return "redirect:/profile";
                }

                // Controlla che l'immagine non superi il limite di 1 MB
                if (image.getSize() > 1_000_000) {
                    redirectAttributes.addFlashAttribute("errorMessage", "L'immagine supera la dimensione massima di 1 MB.");
                    return "redirect:/profile";
                }

                // Converte il file in un array di byte per salvarlo nel database
                newBike.setImage(image.getBytes());
            }
        } catch (IOException e) {
            // Gestisce eventuali errori durante la lettura del file immagine
            redirectAttributes.addFlashAttribute("errorMessage", "Errore durante il caricamento dell'immagine.");
            return "redirect:/profile";
        }

        // Salva la bicicletta nel database
        bikeService.addBike(newBike);

        // Invia un messaggio di successo 
        redirectAttributes.addFlashAttribute("successMessage", "Bici aggiunta con successo!");
        return "redirect:/profile";
    }

    /**
     * Elimina una bicicletta specifica dal profilo utente.
     *
     * @param id                ID della bicicletta da eliminare.
     * @param redirect          Per passare messaggi alla vista.
     * @return                  Redirect alla pagina del profilo utente.
     */
    @PostMapping("/bikes/delete/{id}")
    public String deleteBike(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        // Elimina la bicicletta dal database tramite il servizio
        bikeService.deleteBike(id);

        // Invia un messaggio di successo 
        redirectAttributes.addFlashAttribute("successMessage", "Annuncio eliminato con successo!");
        return "redirect:/profile";
    }

     /**
     * Mostra la pagina per modificare una bicicletta specifica.
     *
     * @param bikeId             ID della bicicletta da modificare.
     * @param model              Il modello utilizzato per passare dati alla vista.
     * @return                   Vista "edit-bike".
     */
    @GetMapping("/bikes/edit/{id}")
    public String editBike(@PathVariable("id") Long bikeId, Model model) {
        // Recupera la bicicletta da modificare
        Bike bike = bikeService.getBikeById(bikeId);

        // Passa i dettagli della bicicletta alla vista
        model.addAttribute("bike", bike);
        return "edit-bike";
    }

    /**
     * Aggiorna una bicicletta esistente con i nuovi dettagli forniti.
     *
     * @param id                ID della bicicletta da aggiornare.
     * @param brand             Nuova marca della bicicletta.
     * @param size              Nuova dimensione della bicicletta.
     * @param description       Nuova descrizione della bicicletta.
     * @param price             Nuovo prezzo della bicicletta.
     * @param place             Nuova posizione geografica della bicicletta.
     * @param redirect          Per passare messaggi alla vista.
     * @return                  Redirect alla pagina del profilo utente.
     */
    @PostMapping("/bikes/update")
    public String updateBike(@RequestParam("id") Long id,
                             @RequestParam("brand") String brand,
                             @RequestParam("size") String size,
                             @RequestParam("description") String description,
                             @RequestParam("price") double price,
                             @RequestParam("place") String place,
                             RedirectAttributes redirectAttributes) {
        // Recupera la bicicletta da aggiornare
        Bike existingBike = bikeService.getBikeById(id);

        // Aggiorna i campi della bicicletta
        existingBike.setBrand(brand);
        existingBike.setSize(size);
        existingBike.setDescription(description);
        existingBike.setPrice(price);
        existingBike.setPlace(place);

        // Salva le modifiche nel database
        bikeService.updateBike(id, existingBike);

        // Invia un messaggio di successo 
        redirectAttributes.addFlashAttribute("successMessage", "Annuncio aggiornato con successo!");
        return "redirect:/profile";
    }
}
