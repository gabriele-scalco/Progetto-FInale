package progettofinale.Repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import progettofinale.Model.Message;
import progettofinale.Model.User;
import progettofinale.Model.Bikemodel.Bike;
import progettofinale.Model.Bikemodel.MountainBike;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ActiveProfiles("test")
class MessageRepositoryTest {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BikeRepository bikeRepository;

    private User sender;
    private User receiver;
    private Bike bike;

    @BeforeEach
    void setUp() {
        // Crea utenti e salva nel repository
        sender = new User("sender@example.com", "password", "Sender");
        receiver = new User("receiver@example.com", "password", "Receiver");
        userRepository.save(sender);
        userRepository.save(receiver);

        // Crea una bici e salva nel repository
        bike = new MountainBike("BrandA", "M", "Test bike", 300.0, "CityA", sender);
        bikeRepository.save(bike);

        // Crea messaggi e salva nel repository
        Message message1 = new Message(sender, receiver, bike, "Hello");
        message1.setTimestamp(LocalDateTime.now().minusDays(1)); // Messaggio inviato ieri
        Message message2 = new Message(receiver, sender, bike, "Hi there");
        message2.setTimestamp(LocalDateTime.now()); // Messaggio inviato oggi

        messageRepository.save(message1);
        messageRepository.save(message2);
    }

    /**
     * Verifica che tutti i messaggi inviati da un utente come mittente siano recuperati correttamente.
     */
    @Test
    void testFindAllByUserId_AsOnlySender() {
        List<Message> messages = messageRepository.findAllByUserId(sender.getId());

        assertEquals(2, messages.size()); // Verifica che siano stati recuperati 2 messaggi
        assertEquals("Hello", messages.get(0).getContent()); // Verifica il contenuto del primo messaggio
        assertEquals("Hi there", messages.get(1).getContent()); // Verifica il contenuto del secondo messaggio
    }

    /**
     * Verifica che tutti i messaggi ricevuti da un utente siano recuperati correttamente.
     */
    @Test
    void testFindAllByUserId_AsOnlyReceiver() {
        List<Message> messages = messageRepository.findAllByUserId(receiver.getId());

        assertEquals(2, messages.size()); // Verifica che siano stati recuperati 2 messaggi
        assertEquals("Hello", messages.get(0).getContent()); // Verifica il contenuto del primo messaggio
        assertEquals("Hi there", messages.get(1).getContent()); // Verifica il contenuto del secondo messaggio
    }

    /**
     * Verifica che nessun messaggio venga restituito se un utente non ha inviato o ricevuto alcun messaggio.
     */
    @Test
    void testFindAllByUserId_NoMessages() {
        User userWithoutMessages = new User("nomessages@example.com", "password", "NoMessagesUser");
        userRepository.save(userWithoutMessages);

        List<Message> messages = messageRepository.findAllByUserId(userWithoutMessages.getId());

        assertEquals(0, messages.size()); // Verifica che non siano stati recuperati messaggi
    }

    /**
     * Verifica che i messaggi siano ordinati cronologicamente (dal più vecchio al più recente).
     */
    @Test
    void testMessagesAreSortedByTimestamp() {
        List<Message> messages = messageRepository.findAllByUserId(sender.getId());

        assertEquals(2, messages.size()); // Verifica che siano stati recuperati 2 messaggi
        assertEquals("Hello", messages.get(0).getContent()); // Verifica che il primo messaggio sia il più vecchio
        assertEquals("Hi there", messages.get(1).getContent()); // Verifica che il secondo messaggio sia il più recente
    }
}
