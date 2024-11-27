package progettofinale.Controller;

import progettofinale.Model.*;
import progettofinale.Model.Bikemodel.*;
import progettofinale.Service.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

/**
 * Controller per la gestione degli utenti.
 * Fornisce funzionalità per il login, la registrazione e la gestione della wishlist degli utenti nel sistema.
 */
@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private BikeService bikeService; 

    /**
     * Mostra la pagina di login.
     *
     * @return         Vista "login".
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /**
     * Mostra la pagina di registrazione con un form vuoto per un nuovo utente.
     *
     * @param model     Modello per passare dati alla vista.
     * @return          Vista "register".
     */
    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User()); // Aggiunge un oggetto utente vuoto per il form di registrazione
        return "register";
    }

    /**
     * Registra un nuovo utente nel sistema.
     *
     * @param user      Oggetto utente popolato dal form di registrazione.
     * @param model     Modello per passare messaggi di errore alla vista.
     * @return          Redirect alla pagina di login in caso di successo, pagina di registrazione in caso di errore.
     */
    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user, Model model) {
        try {
            userService.addUser(user); // Salva l'utente tramite il servizio
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("error", "Errore nella registrazione dell'utente.");
            return "register"; 
        }
    }

    /**
     * Aggiunge una bicicletta alla wishlist dell'utente autenticato.
     *
     * @param bikeId            ID della bicicletta da aggiungere alla wishlist.
     * @param principal         Contiene i dettagli dell'utente autenticato.
     * @param redirect          Per passare messaggi alla vista.
     * @return                  Redirect alla homepage.
     */
    @PostMapping("/wishlist/add")
    public String addToWishlist(@RequestParam("bikeId") Long bikeId,
                                Principal principal,
                                RedirectAttributes redirectAttributes) {
        try {
            // Recupera l'utente autenticato
            String username = principal.getName();
            User user = userService.findByEmail(username);

            // Recupera la bicicletta da aggiungere alla wishlist
            Bike bike = bikeService.getBikeById(bikeId);

            // Aggiunge la bicicletta alla wishlist dell'utente
            userService.addToWishlist(user, bike);

            // Aggiunge un messaggio di successo
            redirectAttributes.addFlashAttribute("successMessage", "Bici aggiunta alla wishlist!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Errore durante l'aggiunta alla wishlist.");
        }
        return "redirect:/";
    }

    /**
     * Rimuove una bicicletta dalla wishlist di un utente.
     *
     * @param bikeId            ID della bicicletta da rimuovere.
     * @param principal         Contiene i dettagli dell'utente autenticato.
     * @param redirect          Per passare messaggi alla vista.
     * @return                  Redirect alla pagina del profilo utente.
     */
    @PostMapping("/wishlist/remove")
    public String removeFromWishlist(@RequestParam("bikeId") Long bikeId,
                                     Principal principal,
                                     RedirectAttributes redirectAttributes) {
        try {
            // Recupera l'utente autenticato
            String username = principal.getName();
            User user = userService.findByEmail(username);

            // Rimuove la bicicletta dalla wishlist dell'utente
            userService.removeBikeFromWishlist(user.getId(), bikeId);

            // Aggiunge un messaggio di successo
            redirectAttributes.addFlashAttribute("successMessage", "Bici rimossa dalla wishlist!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Errore durante la rimozione dalla wishlist.");
        }
        return "redirect:/profile";
    }
}
