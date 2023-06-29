package emotionalsongs.backend.entities;

import java.io.Serializable;

public class Utente extends AbstractEntity implements Serializable {

    private int id;
    private String username;

    private String password;
    private String nome;

    public Utente(int id, String username, String password, String nome){
        this.id = id;
        this.username = username;
        this.password = password;
        this.nome = nome;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
