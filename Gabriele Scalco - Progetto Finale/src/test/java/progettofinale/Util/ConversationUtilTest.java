package progettofinale.Util;

import progettofinale.Model.*;
import progettofinale.Model.Bikemodel.*;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConversationUtilTest {

    /**
     * Verifica che i messaggi siano raggruppati correttamente in due conversazioni distinte.
     */
    @Test
    void testGroupMessagesByConversation() {
        User currentUser = mock(User.class);
        when(currentUser.getId()).thenReturn(1L);

        User otherUser1 = mock(User.class);
        when(otherUser1.getId()).thenReturn(2L);

        User otherUser2 = mock(User.class);
        when(otherUser2.getId()).thenReturn(3L);

        Bike bike1 = mock(Bike.class);
        when(bike1.getId()).thenReturn(1L);

        Bike bike2 = mock(Bike.class);
        when(bike2.getId()).thenReturn(2L);

        Message message1 = new Message(currentUser, otherUser1, bike1, "Message 1");
        Message message2 = new Message(otherUser1, currentUser, bike1, "Message 2");
        Message message3 = new Message(currentUser, otherUser2, bike2, "Message 3");

        List<Message> messages = Arrays.asList(message1, message2, message3);

        // Act
        List<Conversation> conversations = ConversationUtil.groupMessagesByConversation(messages, currentUser.getId());

        // Assert
        assertEquals(2, conversations.size()); // Verifica che ci siano due conversazioni

        // Prima conversazione
        Conversation conversation1 = conversations.stream()
                .filter(c -> c.getOtherUser().equals(otherUser1) && c.getBike().equals(bike1))
                .findFirst()
                .orElse(null);
        assertNotNull(conversation1); // Verifica che la conversazione esista
        assertEquals(2, conversation1.getMessages().size()); // Verifica il numero di messaggi
        assertTrue(conversation1.getMessages().contains(message1)); // Verifica che il messaggio1 sia presente
        assertTrue(conversation1.getMessages().contains(message2)); // Verifica che il messaggio2 sia presente

        // Seconda conversazione
        Conversation conversation2 = conversations.stream()
                .filter(c -> c.getOtherUser().equals(otherUser2) && c.getBike().equals(bike2))
                .findFirst()
                .orElse(null);
        assertNotNull(conversation2); // Verifica che la conversazione esista
        assertEquals(1, conversation2.getMessages().size()); // Verifica il numero di messaggi
        assertTrue(conversation2.getMessages().contains(message3)); // Verifica che il messaggio3 sia presente
    }

    /**
     * Verifica il comportamento quando la lista dei messaggi è vuota.
     */
    @Test
    void testGroupMessagesByConversation_EmptyMessages() {
        List<Message> messages = new ArrayList<>();
        Long currentUserId = 1L;

        List<Conversation> conversations = ConversationUtil.groupMessagesByConversation(messages, currentUserId);

        assertTrue(conversations.isEmpty()); // Verifica che non ci siano conversazioni
    }

    /**
     * Verifica che i messaggi con bici diverse creino conversazioni separate.
     */
    @Test
    void testGroupMessagesByConversation_MessagesWithDifferentBikes() {
        User currentUser = mock(User.class);
        when(currentUser.getId()).thenReturn(1L);

        User otherUser = mock(User.class);
        when(otherUser.getId()).thenReturn(2L);

        Bike bike1 = mock(Bike.class);
        when(bike1.getId()).thenReturn(1L);

        Bike bike2 = mock(Bike.class);
        when(bike2.getId()).thenReturn(2L);

        Message message1 = new Message(currentUser, otherUser, bike1, "Message 1");
        Message message2 = new Message(currentUser, otherUser, bike2, "Message 2");

        List<Message> messages = Arrays.asList(message1, message2);

        List<Conversation> conversations = ConversationUtil.groupMessagesByConversation(messages, currentUser.getId());

        assertEquals(2, conversations.size()); // Verifica che ci siano due conversazioni

        // Prima conversazione
        Conversation conversation1 = conversations.stream()
                .filter(c -> c.getBike().equals(bike1))
                .findFirst()
                .orElse(null);
        assertNotNull(conversation1); // Verifica che la conversazione per bike1 esista
        assertEquals(1, conversation1.getMessages().size()); // Verifica che ci sia un messaggio
        assertTrue(conversation1.getMessages().contains(message1)); // Verifica che il messaggio1 sia presente

        // Seconda conversazione
        Conversation conversation2 = conversations.stream()
                .filter(c -> c.getBike().equals(bike2))
                .findFirst()
                .orElse(null);
        assertNotNull(conversation2); // Verifica che la conversazione per bike2 esista
        assertEquals(1, conversation2.getMessages().size()); // Verifica che ci sia un messaggio
        assertTrue(conversation2.getMessages().contains(message2)); // Verifica che il messaggio2 sia presente
    }

    /**
     * Verifica il comportamento quando tutti i messaggi hanno lo stesso destinatario e bici.
     */
    @Test
    void testGroupMessagesByConversation_SameUserSameBike() {
        User currentUser = mock(User.class);
        when(currentUser.getId()).thenReturn(1L);

        User otherUser = mock(User.class);
        when(otherUser.getId()).thenReturn(2L);

        Bike bike = mock(Bike.class);
        when(bike.getId()).thenReturn(1L);

        Message message1 = new Message(currentUser, otherUser, bike, "Message 1");
        Message message2 = new Message(otherUser, currentUser, bike, "Message 2");
        Message message3 = new Message(currentUser, otherUser, bike, "Message 3");

        List<Message> messages = Arrays.asList(message1, message2, message3);

        List<Conversation> conversations = ConversationUtil.groupMessagesByConversation(messages, currentUser.getId());

        assertEquals(1, conversations.size()); // Verifica che ci sia una sola conversazione
        Conversation conversation = conversations.get(0);
        assertEquals(3, conversation.getMessages().size()); // Verifica il numero di messaggi nella conversazione
        assertTrue(conversation.getMessages().containsAll(messages)); // Verifica che tutti i messaggi siano presenti
    }
}
