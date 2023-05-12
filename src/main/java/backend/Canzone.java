package backend;

import lombok.*;

import javax.persistence.*;

@AllArgsConstructor
@Data
@EqualsAndHashCode
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "public.\"Canzoni\"")
public class Canzone {
    @EqualsAndHashCode.Include
    @Id
    private int id;

    private int anno;

    private String artista;

    private String titolo;

    public Canzone(String titolo, String autore, int anno) {
        this.titolo = titolo;
        this.artista = autore;
        this.anno = anno;
    }

    public int getId() {
        return id;
    }

    public String getArtista() {
        return artista;
    }

    public String getTitolo() {
        return titolo;
    }
}