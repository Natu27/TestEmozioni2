package emotionalsongs.backend.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
public class Canzone implements Serializable {
    @Id
    private int id;
    private int anno;
    private String codice;
    private String titolo;
    private String artista;

    public Canzone() {}

    public Canzone(int id, int anno, String artista, String titolo, String codice) {
        this.id = id;
        this.anno = anno;
        this.artista = artista;
        this.titolo = titolo;
        this.codice = codice;
    }

    public Canzone(int anno, String autore, String titolo) {
        this.titolo = titolo;
        this.artista = autore;
        this.anno = anno;
    }

    public int getId() {
        return id;
    }

    public int getAnno() {
        return anno;
    }

    public String getCodice() {
        return codice;
    }

    public String getArtista() {
        return artista;
    }

    public String getTitolo() {
        return titolo;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAnno(int anno) {
        this.anno = anno;
    }

    public void setCodice(String codice) {
        this.codice = codice;
    }

    public void setArtista(String artista) {
        this.artista = artista;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }
}