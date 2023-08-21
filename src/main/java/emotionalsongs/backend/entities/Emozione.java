package emotionalsongs.backend.entities;

import java.io.Serializable;

/**
 * @author Acquati Luca
 * @author Jamil Muhammad Qasim
 * @author Naturale Lorenzo
 * @author Volonterio Luca
 */

/**
 * Classe che rappresenta un'emozione'.
 * @see emotionalsongs.backend.entities
 * @version 1.0
 */
public class Emozione {
    private String name;
    private String commento;
    private double score;

    /**
     * Costruttore per creare un'istanza di Emozione utilizzando solo il nome dell'emozione.
     *
     * @param name    Il nome dell'emozione.
     */
    public Emozione(String name) {
        this.name = name;
        this.score = 0;
        this.commento = "";
    }

    /**
     * Costruttore per creare un'istanza di Emozione utilizzando il nome dell'emozione e il punteggio associato.
     *
     * @param name    Il nome dell'emozione.
     * @param score   Il punteggio dato all'emozione.
     */
    public Emozione(String name, double score) {
        this.name = name;
        this.score = score;
    }

    /**
     * Costruttore per creare un'istanza di Emozione utilizzando il nome dell'emozione, il punteggio associato e il commento associato all'emozione.
     *
     * @param name      Il nome dell'emozione.
     * @param score     Il punteggio dato all'emozione.
     * @param commento  Il commento associato all'emozione.
     */
    public Emozione(String name, double score, String commento) {
        this.name = name;
        this.commento = commento;
        this.score = score;
    }

    /**
     * Ottiene il nome dell'emozione.
     * @return Il nome dell'emozione.
     */
    public String getName() {
        return name;
    }

    /**
     * Imposta il nome  dell'emozione.
     * @param name Il commento da inserire.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Ottiene il punteggio associato all'emozione.
     * @return Il punteggio associato all'emozione.
     */
    public double getScore() {
        return score;
    }

    /**
     * Imposta il punteggio per l'emozione.
     * @param score Il punteggio da inserire.
     */
    public void setScore(double score) {
        this.score = score;
    }


    /**
     * Restituisce una rappresentazione testuale dell'oggetto Emozione.
     * @return Una stringa contenente il nome, il commento e il punteggio dell'emozione.
     */
    @Override
    public String toString() {
        return "Emozione{" +
                "name='" + name + '\'' +
                ", commento='" + commento + '\'' +
                ", score=" + score +
                '}';
    }

    /**
     * Ottiene il commento associato all'emozione.
     * @return Il commento associato all'emozione.
     */
    public String getCommento() {
        return commento;
    }

    /**
     * Imposta il commento per l'emozione.
     * @param commento Il commento da inserire.
     */
    public void setCommento(String commento) {
        this.commento = commento;
    }
}
