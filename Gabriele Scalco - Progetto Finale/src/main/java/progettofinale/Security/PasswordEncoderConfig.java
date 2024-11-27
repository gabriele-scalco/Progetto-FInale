package progettofinale.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configurazione per il password encoder.
 * Fornisce un bean per la codifica delle password utilizzando l'algoritmo BCrypt.
 * Questo encoder viene utilizzato da Spring Security per salvare e verificare le password degli utenti.
 */
@Configuration
public class PasswordEncoderConfig {

    /**
     * Crea un bean di tipo PasswordEncoder basato su BCrypt.
     * BCrypt è un algoritmo di hashing sicuro per la gestione delle password.
     *
     * @return         Un'istanza di BCryptPasswordEncoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
