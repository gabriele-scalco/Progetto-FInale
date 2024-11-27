package progettofinale.Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import progettofinale.Model.*;
import progettofinale.Model.Bikemodel.*;
import progettofinale.Repository.MessageRepository;
import progettofinale.Util.ConversationUtil;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @InjectMocks
    private MessageService messageService;

    private Message message1;
    private Message message2;
    private User sender;
    private User receiver;
    private Bike bike;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        sender = mock(User.class);
        when(sender.getId()).thenReturn(1L);

        receiver = mock(User.class);
        when(receiver.getId()).thenReturn(2L);

        bike = mock(MountainBike.class);
        when(bike.getId()).thenReturn(1L);

        message1 = new Message(sender, receiver, bike, "Hello");
        message1.setTimestamp(LocalDateTime.now().minusDays(1));

        message2 = new Message(receiver, sender, bike, "Hi there");
        message2.setTimestamp(LocalDateTime.now());
    }

    /**
     * Verifica che un messaggio venga salvato correttamente.
     */
    @Test
    void testSaveMessage() {
        when(messageRepository.save(any(Message.class))).thenReturn(message1);

        Message savedMessage = messageService.saveMessage(message1);

        // Verifica che il messaggio salvato abbia i dati corretti
        assertNotNull(savedMessage.getTimestamp()); // Controlla che il timestamp sia settato
        assertEquals(message1.getContent(), savedMessage.getContent()); // Controlla il contenuto
        assertEquals(message1.getSender(), savedMessage.getSender()); // Controlla il mittente
        assertEquals(message1.getReceiver(), savedMessage.getReceiver()); // Controlla il destinatario

        // Verifica che il repository abbia salvato correttamente il messaggio
        ArgumentCaptor<Message> captor = ArgumentCaptor.forClass(Message.class);
        verify(messageRepository).save(captor.capture());
        assertEquals(message1.getContent(), captor.getValue().getContent()); // Controlla che il contenuto corrisponda
    }

    /**
     * Verifica che le conversazioni di un utente vengano recuperate correttamente.
     */
    @Test
    void testGetConversations() {
        Long userId = sender.getId();
        List<Message> messages = Arrays.asList(message1, message2);

        when(messageRepository.findAllByUserId(userId)).thenReturn(messages);

        List<Conversation> expectedConversations = ConversationUtil.groupMessagesByConversation(messages, userId);
        List<Conversation> result = messageService.getConversations(userId);

        assertEquals(expectedConversations.size(), result.size()); // Controlla il numero di conversazioni
        assertEquals(expectedConversations, result); // Controlla il contenuto delle conversazioni

        verify(messageRepository, times(1)).findAllByUserId(userId); // Verifica che il metodo sia stato chiamato
    }

    /**
     * Verifica il comportamento in assenza di messaggi per un utente.
     */
    @Test
    void testGetConversations_NoMessages() {
        Long userId = sender.getId();
        when(messageRepository.findAllByUserId(userId)).thenReturn(List.of());

        List<Conversation> result = messageService.getConversations(userId);

        assertTrue(result.isEmpty()); // Controlla che non ci siano conversazioni
        verify(messageRepository, times(1)).findAllByUserId(userId); // Verifica la chiamata al repository
    }

    /**
     * Verifica che venga sollevata un'eccezione per un messaggio nullo.
     */
    @Test
    void testSaveMessage_NullMessage() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            messageService.saveMessage(null);
        });

        assertEquals("Messaggio nullo non valido", exception.getMessage()); // Controlla il messaggio dell'eccezione
        verifyNoInteractions(messageRepository); // Verifica che il repository non sia stato chiamato
    }

    /**
     * Verifica il comportamento con un messaggio incompleto.
     */
    @Test
    void testSaveMessage_IncompleteMessage() {
        Message incompleteMessage = new Message();
        incompleteMessage.setContent("Incomplete");

        when(messageRepository.save(any(Message.class))).thenReturn(incompleteMessage);

        Message result = messageService.saveMessage(incompleteMessage);

        assertEquals("Incomplete", result.getContent()); // Controlla il contenuto
        assertNull(result.getSender()); // Controlla che il mittente sia nullo
        assertNull(result.getReceiver()); // Controlla che il destinatario sia nullo
        assertNull(result.getBike()); // Controlla che la bici sia nulla
    }

    /**
     * Verifica il comportamento in caso di errore del repository.
     */
    @Test
    void testSaveMessage_RepositoryError() {
        when(messageRepository.save(any(Message.class))).thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            messageService.saveMessage(message1);
        });

        assertEquals("Database error", exception.getMessage()); // Controlla il messaggio dell'errore
        verify(messageRepository, times(1)).save(message1); // Verifica che il metodo sia stato chiamato
    }
}
