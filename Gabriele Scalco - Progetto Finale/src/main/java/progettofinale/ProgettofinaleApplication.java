package progettofinale;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Classe principale per l'avvio dell'applicazione Spring Boot.
 * Contiene il metodo `main`, che funge da punto di ingresso per l'applicazione.
 */
@SpringBootApplication // Indica che questa è una classe di configurazione Spring Boot
public class ProgettofinaleApplication {

		/**
		 * Metodo principale per avviare l'applicazione.
		 * Utilizza SpringApplication per configurare e lanciare il contesto Spring.
		 *
		 * @param args Argomenti passati alla JVM durante l'esecuzione.
		 */
		public static void main(String[] args) {
				SpringApplication.run(ProgettofinaleApplication.class, args); // Avvia l'applicazione
		}

}
