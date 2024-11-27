package progettofinale.Security;

import progettofinale.Model.User;
import progettofinale.Repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Verifica che il metodo carichi correttamente un utente esistente tramite email.
     */
    @Test
    void testLoadUserByUsername_Success() {
        String email = "test@example.com";
        String password = "password123";

        // Simula un utente trovato nel repository
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);

        when(userRepository.findByEmail(email)).thenReturn(user);

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

        assertNotNull(userDetails); // Verifica che i dettagli dell'utente non siano null
        assertEquals(email, userDetails.getUsername()); // Verifica che l'email corrisponda
        assertEquals(password, userDetails.getPassword()); // Verifica che la password corrisponda
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_USER"))); // Verifica che l'utente abbia il ruolo "ROLE_USER"

        verify(userRepository, times(1)).findByEmail(email); // Verifica che il repository sia stato chiamato una sola volta
    }

    /**
     * Verifica che venga sollevata un'eccezione se l'utente non viene trovato.
     */
    @Test
    void testLoadUserByUsername_UserNotFound() {
        String email = "nonexistent@example.com";

        // Simula nessun utente trovato nel repository
        when(userRepository.findByEmail(email)).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername(email);
        }); // Verifica che venga sollevata una UsernameNotFoundException

        verify(userRepository, times(1)).findByEmail(email); // Verifica che il repository sia stato chiamato una sola volta
    }

}
