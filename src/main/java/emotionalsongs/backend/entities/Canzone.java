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
 * Classe che rappresenta una canzone.
 * @see emotionalsongs.backend.entities
 * @version 1.0
 */
@Entity
public class Canzone implements Serializable {
    @Id
    private int id;
    private int anno;
    private String codice;
    private String titolo;
    private String artista;

    /**
     * Costruttore vuoto per la creazione di un'istanza di Canzone.
     */
    public Canzone() {}

    /**
     * Costruttore per creare un'istanza di Canzone utilizzando tutti gli attributi specificati.
     *
     * @param id      L'ID univoco della canzone.
     * @param anno    L'anno di uscita della canzone.
     * @param artista L'artista o autore della canzone.
     * @param titolo  Il titolo della canzone.
     * @param codice  Il codice associato alla canzone.
     */
    public Canzone(int id, int anno, String artista, String titolo, String codice) {
        this.id = id;
        this.anno = anno;
        this.artista = artista;
        this.titolo = titolo;
        this.codice = codice;
    }

    /**
     * Costruttore per creare un'istanza di Canzone utilizzando l'anno, l'autore e il titolo.
     *
     * @param anno   L'anno di uscita della canzone.
     * @param autore L'artista o autore della canzone.
     * @param titolo Il titolo della canzone.
     */
    public Canzone(int anno, String autore, String titolo) {
        this.titolo = titolo;
        this.artista = autore;
        this.anno = anno;
    }

    /**
     * Ottiene l'ID univoco della canzone.
     * @return L'ID della canzone.
     */
    public int getId() {
        return id;
    }

    /**
     * Ottiene l'anno della canzone.
     * @return L'anno della canzone.
     */
    public int getAnno() {
        return anno;
    }

    /**
     * Ottiene il codice della canzone.
     * @return Il codice della canzone.
     */
    public String getCodice() {
        return codice;
    }

    /**
     * Ottiene l'artista della canzone.
     * @return L'artista della canzone.
     */
    public String getArtista() {
        return artista;
    }

    /**
     * Ottiene il titolo della canzone.
     * @return Il titolo della canzone.
     */
    public String getTitolo() {
        return titolo;
    }

    /**
     * Imposta l'ID della canzone.
     * @param id Il nuovo ID da impostare.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Imposta l'anno della canzone.
     * @param anno L'anno da impostare.
     */
    public void setAnno(int anno) {
        this.anno = anno;
    }

    /**
     * Imposta il codice della canzone.
     * @param codice L'anno da impostare.
     */
    public void setCodice(String codice) {
        this.codice = codice;
    }

    /**
     * Imposta l'artista della canzone.
     * @param artista L'artista da impostare.
     */
    public void setArtista(String artista) {
        this.artista = artista;
    }

    /**
     * Imposta il titolo della canzone.
     * @param titolo Il titolo da impostare.
     */
    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }
}