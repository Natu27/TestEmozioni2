package emotionalsongs.backend.entities;

import javax.persistence.Entity;
import java.io.Serializable;
@Entity
public class Playlist extends AbstractEntity implements Serializable {

    private String titolo;
    private String username;

    public Playlist() {}

    public Playlist(String titolo) {
        this.titolo = titolo;
    }
    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }
}
