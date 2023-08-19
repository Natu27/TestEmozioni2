package emotionalsongs;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Acquati Luca
 * @author Jamil Muhammad Qasim
 * @author Naturale Lorenzo
 * @author Volonterio Luca
 */

/**
 * Punto di avvio dell'applicazione Spring Boot.
 * Questa classe è annotata con {@link SpringBootApplication}, indicando che è la classe principale per avviare l'applicazione Spring Boot.
 * L'annotazione {@link Theme} specifica il tema dell'applicazione, con il valore "emotional-songs".
 * La classe implementa {@link AppShellConfigurator} per fornire la configurazione per la shell dell'applicazione.
 */
@SpringBootApplication
@Theme(value = "emotional-songs", variant = Lumo.DARK)
public class Application implements AppShellConfigurator {

    /**
     * Il metodo principale per avviare l'applicazione Spring Boot.
     *
     * @param args Gli argomenti della riga di comando.
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
