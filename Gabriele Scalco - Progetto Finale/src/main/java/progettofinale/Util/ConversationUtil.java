package progettofinale.Util;

import progettofinale.Model.*;
import progettofinale.Model.Bikemodel.*;

import java.util.*;

/**
 * Utility per raggruppare i messaggi in conversazioni.
 * Una conversazione è definita da un interlocutore e una bicicletta.
 */
public class ConversationUtil {

    /**
     * Raggruppa i messaggi in conversazioni basate su interlocutore e bicicletta.
     * Ogni conversazione è unica per combinazione di interlocutore e bicicletta.
     *
     * @param messages          La lista di messaggi da raggruppare.
     * @param currentUserId     L'ID dell'utente corrente (mittente o destinatario dei messaggi).
     * @return                  Una lista di conversazioni raggruppate.
     */
    public static List<Conversation> groupMessagesByConversation(List<Message> messages, Long currentUserId) {
        // Mappa per organizzare le conversazioni in base a un identificatore unico
        Map<String, Conversation> conversationMap = new HashMap<>();

        for (Message message : messages) {
            // Determina chi è l'altro utente nella conversazione
            User otherUser;
            if (message.getSender().getId().equals(currentUserId)) {
                otherUser = message.getReceiver(); // L'altro utente è il destinatario
            } else {
                otherUser = message.getSender(); // L'altro utente è il mittente
            }
            Bike bike = message.getBike(); // La bicicletta coinvolta nella conversazione

            // Genera una chiave unica per identificare la conversazione
            String conversationKey = otherUser.getId() + "-bike-" + bike.getId();

            // Recupera o crea una nuova conversazione
            Conversation conversation = conversationMap.get(conversationKey);
            if (conversation == null) {
                // Crea una nuova conversazione con l'altro utente e la bicicletta
                conversation = new Conversation(otherUser, bike, new ArrayList<>());
                conversationMap.put(conversationKey, conversation);
            }

            // Aggiunge il messaggio corrente alla conversazione
            conversation.getMessages().add(message);
        }

        // Ritorna tutte le conversazioni come lista
        return new ArrayList<>(conversationMap.values());
    }
}
