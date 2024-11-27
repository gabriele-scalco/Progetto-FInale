package progettofinale.Controller;

import progettofinale.Model.*;
import progettofinale.Model.Bikemodel.*;
import progettofinale.Service.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MessageControllerTest {

    @Mock
    private MessageService messageService;

    @Mock
    private UserService userService;

    @Mock
    private BikeService bikeService;

    @Mock
    private Model model;

    @Mock
    private Principal principal;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private MessageController messageController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Verifica che la pagina di invio messaggi venga mostrata correttamente con i dati necessari.
     */
    @Test
    void testShowSendMessagePage() {
        Long receiverId = 1L;
        Long bikeId = 2L;

        String viewName = messageController.showSendMessagePage(receiverId, bikeId, model);

        assertEquals("send-message", viewName); // Verifica che il nome della vista sia corretto
        verify(model).addAttribute("receiverId", receiverId); // Verifica che il modello contenga l'ID del destinatario
        verify(model).addAttribute("bikeId", bikeId); // Verifica che il modello contenga l'ID della bici
        verify(model).addAttribute(eq("message"), any(Message.class)); // Verifica che il modello contenga un messaggio vuoto
    }

    /**
     * Verifica che l'invio di un messaggio sia gestito correttamente con tutti i dati forniti.
     */
    @Test
    void testSendMessage() {
        String content = "Hello!";
        Long receiverId = 1L;
        Long bikeId = 2L;

        User sender = new User("sender@example.com", "password", "Sender");
        User receiver = new User("receiver@example.com", "password", "Receiver");
        Bike bike = new MountainBike("Trek", "Large", "Test Bike", 500.0, "Rome", null);

        // Mock delle dipendenze
        when(principal.getName()).thenReturn("sender@example.com");
        when(userService.findByEmail("sender@example.com")).thenReturn(sender);
        when(userService.retrieveUserById(receiverId)).thenReturn(receiver);
        when(bikeService.getBikeById(bikeId)).thenReturn(bike);

        String redirectUrl = messageController.sendMessage(content, receiverId, bikeId, principal, redirectAttributes);

        assertEquals("redirect:/", redirectUrl); // Verifica che l'URL di reindirizzamento sia corretto

        // Cattura l'oggetto Message passato al servizio
        ArgumentCaptor<Message> captor = ArgumentCaptor.forClass(Message.class);
        verify(messageService).saveMessage(captor.capture());
        Message capturedMessage = captor.getValue();

        // Verifica che i dati del messaggio siano corretti
        assertEquals(sender, capturedMessage.getSender()); // Verifica il mittente del messaggio
        assertEquals(receiver, capturedMessage.getReceiver()); // Verifica il destinatario del messaggio
        assertEquals(bike, capturedMessage.getBike()); // Verifica la bici associata al messaggio
        assertEquals(content, capturedMessage.getContent()); // Verifica il contenuto del messaggio

        verify(redirectAttributes).addFlashAttribute("successMessage", "Messaggio inviato con successo!"); // Verifica che il messaggio di successo sia impostato
    }

    /**
     * Verifica che la pagina inbox venga mostrata con tutte le conversazioni dell'utente autenticato.
     */
    @Test
    void testShowInbox() {
        User currentUser = mock(User.class); // Mock dell'utente corrente
        when(currentUser.getId()).thenReturn(1L);
        when(currentUser.getEmail()).thenReturn("currentuser@example.com");

        Conversation conversation1 = new Conversation();
        Conversation conversation2 = new Conversation();
        List<Conversation> conversations = Arrays.asList(conversation1, conversation2);

        // Mock delle dipendenze
        when(principal.getName()).thenReturn("currentuser@example.com");
        when(userService.findByEmail("currentuser@example.com")).thenReturn(currentUser);
        when(messageService.getConversations(1L)).thenReturn(conversations);

        String viewName = messageController.showInbox(model, principal);

        assertEquals("inbox", viewName); // Verifica che il nome della vista sia corretto
        verify(model).addAttribute("conversations", conversations); // Verifica che le conversazioni siano aggiunte al modello
        verify(model).addAttribute("currentUser", currentUser); // Verifica che l'utente corrente sia aggiunto al modello
        verify(model).addAttribute(eq("newMessage"), any(Message.class)); // Verifica che un messaggio vuoto sia aggiunto al modello
    }

    /**
     * Verifica che venga sollevata un'eccezione se il contenuto del messaggio è nullo.
     */
    @Test
    void testSendMessageWithNullContent() {
        String content = null; // Contenuto nullo
        Long receiverId = 1L;
        Long bikeId = 2L;

        User sender = new User("sender@example.com", "password", "Sender");
        User receiver = new User("receiver@example.com", "password", "Receiver");
        Bike bike = new MountainBike("Trek", "Large", "Test Bike", 500.0, "Rome", null);

        // Mock delle dipendenze
        when(principal.getName()).thenReturn("sender@example.com");
        when(userService.findByEmail("sender@example.com")).thenReturn(sender);
        when(userService.retrieveUserById(receiverId)).thenReturn(receiver);
        when(bikeService.getBikeById(bikeId)).thenReturn(bike);

        // Verifica che venga lanciata un'eccezione
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            messageController.sendMessage(content, receiverId, bikeId, principal, redirectAttributes);
        });

        assertEquals("Messaggio non può essere vuoto", exception.getMessage()); // Verifica il messaggio dell'eccezione
        verifyNoInteractions(messageService); // Verifica che il servizio non venga chiamato
    }
}
