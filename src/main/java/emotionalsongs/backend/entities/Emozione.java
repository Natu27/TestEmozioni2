package emotionalsongs.backend.entities;

import java.io.Serializable;

public class Emozione implements Serializable {
    private String name;
    private String commento;
    private double score;

    public Emozione(String name) {
        this.name = name;
        this.score = 0;
        this.commento = "";
    }
    public Emozione(String name, double score) {
        this.name = name;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }


    @Override
    public String toString() {
        return "Emozione{" +
                "name='" + name + '\'' +
                ", commento='" + commento + '\'' +
                ", score=" + score +
                '}';
    }

    public String getCommento() {
        return commento;
    }

    public void setCommento(String commento) {
        this.commento = commento;
    }
}
