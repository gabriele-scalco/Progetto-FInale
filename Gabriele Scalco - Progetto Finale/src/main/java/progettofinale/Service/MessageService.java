package progettofinale.Service;

import progettofinale.Repository.MessageRepository;
import progettofinale.Model.Conversation;
import progettofinale.Model.Message;
import progettofinale.Util.ConversationUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service per la gestione delle operazioni relative ai messaggi.
 * Fornisce metodi per salvare i messaggi e recuperare le conversazioni
 * basate sui messaggi di un utente specifico.
 */
@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    /**
     * Salva un messaggio nel database.
     * Imposta automaticamente il timestamp corrente prima di salvare.
     *
     * @param message     Il messaggio da salvare.
     * @return            Il messaggio salvato con ID generato e timestamp impostato.
     */
    @Transactional
    public Message saveMessage(Message message) {
        if (message == null) {
            throw new IllegalArgumentException("Messaggio nullo non valido");
        }// Controlla se messaggio vuoto
        
        message.setTimestamp(LocalDateTime.now()); // Imposta il timestamp al momento attuale
        return messageRepository.save(message); // Salva il messaggio nel database
    }

    /**
     * Recupera tutte le conversazioni di un utente specifico.
     * I messaggi sono raggruppati per interlocutore e bicicletta.
     *
     * @param userId     L'ID dell'utente per cui recuperare le conversazioni.
     * @return           Una lista di conversazioni, ciascuna contenente i messaggi relativi.
     */
    @Transactional(readOnly = true)
    public List<Conversation> getConversations(Long userId) {
        // Recupera tutti i messaggi dell'utente (come mittente o destinatario)
        List<Message> allMessages = messageRepository.findAllByUserId(userId);

        // Raggruppa i messaggi in conversazioni basate sull'interlocutore e la bicicletta
        return ConversationUtil.groupMessagesByConversation(allMessages, userId);
    }
}
