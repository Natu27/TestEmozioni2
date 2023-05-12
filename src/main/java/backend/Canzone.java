package backend;

import lombok.*;
import javax.persistence.*;

@Entity
@NoArgsConstructor
@Table(name = "public.Canzoni")
public class Canzone {
    @Id
    private int id;

    private int anno;

    private String codice;

    private String artista;

    private String titolo;

    public Canzone(int id, int anno, String codice, String artista, String titolo) {
        this.id = id;
        this.anno = anno;
        this.codice = codice;
        this.artista = artista;
        this.titolo = titolo;
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