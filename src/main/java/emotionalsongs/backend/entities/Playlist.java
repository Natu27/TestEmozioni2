package emotionalsongs.backend.entities;

import javax.persistence.Entity;
import java.io.Serializable;
@Entity
public class Playlist extends AbstractEntity implements Serializable {

    private int id;
    private String titolo;
    private int username;

    public Playlist() {}

    public Playlist(int id, String titolo, int userId) {
        this.id = id;
        this.titolo = titolo;
        username = userId;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id= id;
    }
    public int getUsername() {
        return username;
    }

    public void setUsername(int username) {
        this.username = username;
    }

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }
}
