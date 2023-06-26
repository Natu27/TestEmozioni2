package emotionalsongs.backend.entities;

public class Emozione {
    private String name;
    private int score;

    public Emozione(String name) {
        this.name = name;
        this.score = 0;
    }
    public Emozione(String name, int score) {
        this.name = name;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "Emozione{" +
                "name='" + name + '\'' +
                ", score=" + score +
                '}';
    }
}
