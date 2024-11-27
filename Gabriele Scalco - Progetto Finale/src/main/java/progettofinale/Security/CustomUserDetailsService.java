package progettofinale.Security;

import progettofinale.Repository.UserRepository;
import progettofinale.Model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.transaction.annotation.Transactional;

/**
 * Classe per la gestione dei dettagli dell'utente per Spring Security.
 * Implementa l'interfaccia UserDetailsService per caricare gli utenti
 * dal database in base all'email fornita durante il login.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Carica un utente dal database in base alla sua email.
     * Utilizza UserBuilder di Spring Security per creare un oggetto UserDetails
     * che rappresenta l'utente per il processo di autenticazione.
     *
     * @param email                          L'email dell'utente fornita durante il login.
     * @return                               Un oggetto UserDetails contenente le credenziali e i ruoli dell'utente.
     * @throws UsernameNotFoundException     Se l'utente non è presente nel database.
     */
    @Transactional
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Recupera l'utente dal database tramite il repository
        User user = userRepository.findByEmail(email);

        // Lancia un'eccezione se l'utente non viene trovato
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        // Costruisce un oggetto UserDetails utilizzando UserBuilder di Spring Security
        UserBuilder builder = org.springframework.security.core.userdetails.User.withUsername(user.getEmail());
        builder.password(user.getPassword()); // Imposta la password (già criptata)
        builder.roles("USER"); // Imposta il ruolo dell'utente (in questo caso, "USER")

        // Restituisce l'oggetto UserDetails
        return builder.build();
    }
}
