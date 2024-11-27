package progettofinale.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

/**
 * Configurazione di Spring Security per gestire l'autenticazione e l'autorizzazione.
 * Definisce i percorsi pubblici e protetti, il comportamento del login/logout
 * e l'integrazione del servizio di autenticazione personalizzato.
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder; // PasswordEncoder configurato in PasswordEncoderConfig

    /**
     * Configura la catena di filtri di sicurezza di Spring Security.
     * Definisce l'accesso ai percorsi, il comportamento del login/logout
     * e la gestione delle eccezioni di autenticazione.
     *
     * @param http           Oggetto HttpSecurity per configurare le regole di sicurezza.
     * @return               La catena di filtri di sicurezza configurata.
     * @throws Exception     Se si verifica un errore durante la configurazione.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
          .csrf().disable()
          .authorizeHttpRequests((requests) -> requests
            // Percorsi accessibili pubblicamente
            .requestMatchers("/", "/filter", "/bici", "/register", "/css/**", "/js/**", "/images/**").permitAll()
            // Percorsi che richiedono autenticazione
            .requestMatchers("/profile", "/bikes/add", "/bikes/delete/{id}", "/wishlist/add", 
                             "/wishlist/remove", "/bikes/edit/{id}", "/bikes/update", 
                             "/messages/send/**", "/messages/inbox").authenticated()
          )
          // Configura il login
          .formLogin((form) -> form
            .loginPage("/login") // Pagina personalizzata per il login
            .permitAll() // Permetti l'accesso alla pagina di login
          )
          // Configura il logout
          .logout((logout) -> logout.permitAll())
          // Configura il comportamento per richieste non autorizzate
          .exceptionHandling((exceptions) -> exceptions
            .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"))
          );

        return http.build();
    }

    /**
     * Configura il provider di autenticazione utilizzando il servizio utente personalizzato
     * e il password encoder.
     *
     * @param auth Oggetto AuthenticationManagerBuilder per configurare l'autenticazione.
     * @throws Exception Se si verifica un errore durante la configurazione.
     */
    @Autowired
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }
}
