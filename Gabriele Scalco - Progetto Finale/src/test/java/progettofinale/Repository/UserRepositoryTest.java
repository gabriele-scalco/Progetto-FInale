package progettofinale.Repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import progettofinale.Model.User;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        // Popola il database con utenti di esempio
        User user1 = new User("user1@example.com", "password1", "User One");
        User user2 = new User("user2@example.com", "password2", "User Two");
        userRepository.save(user1);
        userRepository.save(user2);
    }

    /**
     * Verifica che un utente esistente possa essere recuperato correttamente tramite email.
     */
    @Test
    void testFindByEmail_UserExists() {
        User foundUser = userRepository.findByEmail("user1@example.com");

        assertNotNull(foundUser); // Verifica che l'utente non sia null
        assertEquals("user1@example.com", foundUser.getEmail()); // Verifica che l'email corrisponda
        assertEquals("User One", foundUser.getName()); // Verifica che il nome corrisponda
    }

    /**
     * Verifica che una ricerca con un'email non presente restituisca un risultato nullo.
     */
    @Test
    void testFindByEmail_UserDoesNotExist() {
        User foundUser = userRepository.findByEmail("nonexistent@example.com");

        assertNull(foundUser); // Verifica che il risultato sia null
    }

    /**
     * Verifica che un nuovo utente venga salvato correttamente nel database.
     */
    @Test
    void testSaveNewUser() {
        User newUser = new User("newuser@example.com", "password3", "New User");
        userRepository.save(newUser);

        User foundUser = userRepository.findByEmail("newuser@example.com");

        assertNotNull(foundUser); // Verifica che il nuovo utente sia stato salvato
        assertEquals("newuser@example.com", foundUser.getEmail()); // Verifica che l'email corrisponda
        assertEquals("New User", foundUser.getName()); // Verifica che il nome corrisponda
    }

    /**
     * Verifica che un utente esistente possa essere aggiornato correttamente.
     */
    @Test
    void testUpdateUser() {
        User existingUser = userRepository.findByEmail("user1@example.com");
        assertNotNull(existingUser); // Verifica che l'utente esista prima dell'aggiornamento

        existingUser.setName("Updated User One");
        userRepository.save(existingUser);

        User updatedUser = userRepository.findByEmail("user1@example.com");

        assertNotNull(updatedUser); // Verifica che l'utente aggiornato esista
        assertEquals("Updated User One", updatedUser.getName()); // Verifica che il nome sia stato aggiornato
    }

    /**
     * Verifica che un utente possa essere eliminato correttamente.
     */
    @Test
    void testDeleteUser() {
        User existingUser = userRepository.findByEmail("user2@example.com");
        assertNotNull(existingUser); // Verifica che l'utente esista prima della cancellazione

        userRepository.delete(existingUser);

        User deletedUser = userRepository.findByEmail("user2@example.com");

        assertNull(deletedUser); // Verifica che l'utente sia stato eliminato
    }
}
