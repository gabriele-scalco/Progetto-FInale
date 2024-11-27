package progettofinale.Controller;

import progettofinale.Model.*;
import progettofinale.Model.Bikemodel.*;
import progettofinale.Service.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

/**
 * Controller per la gestione dei messaggi tra utenti.
 * Consente di inviare messaggi, visualizzare la pagina di invio e accedere alla inbox.
 */
@Controller
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @Autowired
    private BikeService bikeService;

    /**
     * Mostra la pagina per inviare un messaggio a un altro utente su una determinata bici.
     *
     * @param receiverId ID del destinatario del messaggio.
     * @param bikeId     ID della bicicletta oggetto del messaggio.
     * @param model      Modello per passare dati alla vista.
     * @return           Vista "send-message".
     */
    @GetMapping("/messages/send/{receiverId}/{bikeId}")
    public String showSendMessagePage(
            @PathVariable("receiverId") Long receiverId,
            @PathVariable("bikeId") Long bikeId,
            Model model) {
        Message message = new Message();
        message.setContent("");

        // Passa ID destinatario, ID bici e text field del messaggio alla vista
        model.addAttribute("receiverId", receiverId);
        model.addAttribute("bikeId", bikeId);
        model.addAttribute("message", message);
        return "send-message";
    }

    /**
     * Salva un messaggio inviato da un utente autenticato a un altro utente.
     *
     * @param content            Contenuto del messaggio.
     * @param receiverId         ID del destinatario del messaggio.
     * @param bikeId             ID della bicicletta oggetto del messaggio.
     * @param principal          Contiene i dettagli dell'utente autenticato.
     * @param redirect           Per passare messaggi di successo o errore alla vista.
     * @return                   Redirect alla homepage.
     */
    @PostMapping("/messages/send")
    public String sendMessage(
            @RequestParam("content") String content,
            @RequestParam("receiverId") Long receiverId,
            @RequestParam("bikeId") Long bikeId,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        // Controllo per contenuto nullo
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Messaggio non può essere vuoto");
        }

        // Recupera l'utente autenticato
        String username = principal.getName();
        User sender = userService.findByEmail(username);

        // Recupera il destinatario e la bici
        User receiver = userService.retrieveUserById(receiverId);
        Bike bike = bikeService.getBikeById(bikeId);

        // Crea un nuovo messaggio
        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setBike(bike);
        message.setContent(content);

        // Salva il messaggio
        messageService.saveMessage(message);

        // Messaggio di successo e redirect
        redirectAttributes.addFlashAttribute("successMessage", "Messaggio inviato con successo!");
        return "redirect:/";
    }

    /**
     * Mostra l'inbox dell'utente autenticato, con tutte le conversazioni disponibili.
     *
     * @param model         Modello per passare dati alla vista.
     * @param principal     Contiene i dettagli dell'utente autenticato.
     * @return              Vista "inbox".
     */
    @GetMapping("/messages/inbox")
    public String showInbox(Model model, Principal principal) {
        // Recupera l'utente autenticato
        String username = principal.getName();
        User currentUser = userService.findByEmail(username);

        // Recupera tutte le conversazioni per l'utente
        List<Conversation> conversations = messageService.getConversations(currentUser.getId());

        model.addAttribute("conversations", conversations);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("newMessage", new Message()); // Oggetto messaggio vuoto per eventuale utilizzo nella vista

        return "inbox";
    }
}
