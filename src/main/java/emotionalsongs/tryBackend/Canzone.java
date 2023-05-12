package emotionalsongs.tryBackend;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Canzone {
    @Id
    private int id;
    private int anno;
    private String codice;
    private String artista;
    private String titolo;

    public Canzone() {}
    public Canzone(int id, int anno, String artista, String titolo, String codice) {
        this.id = id;
        this.anno = anno;
        this.artista = artista;
        this.titolo = titolo;
        this.codice = codice;
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

    public void setTitolo(String titolo) { this.titolo = titolo; }
}
