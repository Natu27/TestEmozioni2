package emotionalsongs.backend.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * @author Acquati Luca
 * @author Jamil Muhammad Qasim
 * @author Naturale Lorenzo
 * @author Volonterio Luca
 */

/**
 * Classe che rappresenta un Utente dell'applicazione.
 * @see emotionalsongs.backend.entities
 * @version 1.0
 */
@Entity
public class Utente implements Serializable {
    @Id
    private int id;
    private String username;

    private String password;
    private String nome;


    /**
     * Costruttore vuoto per la creazione di un'istanza di Utente.
     */
    public Utente() {}

    /**
     * Costruttore per creare un'istanza di Utente utilizzando tutti gli attributi specificati.
     * @param id        L'ID univoco dell'utente.
     * @param username  L'username scelto dall'utente.
     * @param password  La password scelta dall'utente.
     * @param nome      Il nome dell'utente.
     */
    public Utente(int id, String username, String password, String nome){
        this.id = id;
        this.username = username;
        this.password = password;
        this.nome = nome;
    }

    /**
     * Ottiene l'username associato all'utente.
     * @return L'username dell'utente.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Ottiene la password associata all'utente.
     * @return La password dell'utente.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Imposta l'username per l'utente.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Imposta la password per l'utente.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Ottiene Il nome dell'utente.
     * @return Il nome dell'utente..
     */
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Ottiene L'ID dell'utente.
     * @return L'ID dell'utente..
     */
    public int getId() {
        return id;
    }

    /**
     * Imposta l'ID per l'utente.
     */
    public void setId(int id) {
        this.id = id;
    }
}
