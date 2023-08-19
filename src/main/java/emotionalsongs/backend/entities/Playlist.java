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
 * Classe che rappresenta una playlist.
 * @see emotionalsongs.backend.entities
 * @version 1.0
 */
@Entity
public class Playlist implements Serializable {
    @Id
    private int id;
    private String titolo;
    private int username;

    /**
     * Costruttore vuoto per la creazione di un'istanza di Playlist.
     */
    public Playlist() {}

    /**
     * Costruttore per creare un'istanza di Playlist utilizzando tutti gli attributi specificati.
     *
     * @param id        L'ID univoco della canzone.
     * @param titolo    Il titolo della canzone.
     * @param userId    L'ID dell'utente che crea la playlist.
     */
    public Playlist(int id, String titolo, int userId) {
        this.id = id;
        this.titolo = titolo;
        username = userId;
    }

    /**
     * Ottiene l'ID univoco della playlist.
     * @return L'ID della playlist.
     */
    public int getId() {
        return id;
    }

    /**
     * Imposta l'id della playlist.
     */
    public void setId(int id) {
        this.id= id;
    }

    /**
     * Ottiene Lo userneme dell'utente a cui è associata la playlist.
     * @return Lo userneme dell'utente a cui è associata la playlist.
     */
    public int getUsername() {
        return username;
    }

    /**
     * Imposta lo username collegato alla playlist.
     */
    public void setUsername(int username) {
        this.username = username;
    }

    /**
     * Ottiene Il titolo della playlist.
     * @return Il titolo dela playlist.
     */
    public String getTitolo() {
        return titolo;
    }

    /**
     * Imposta il titolo della playlist
     */
    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }
}
