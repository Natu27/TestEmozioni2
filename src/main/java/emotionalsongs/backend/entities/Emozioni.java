package emotionalsongs.backend.entities;

import java.io.Serializable;

public class Emozioni implements Serializable {
    private int amazement;
    private String amazement_comment;
    private int solemnity;
    private String solemnity_comment;
    private int tenderness;
    private String tenderness_comment;
    private int nostalgia;
    private String nostalgia_comment;
    private int calmness;
    private String calmness_comment;
    private int power;
    private String power_comment;
    private int joy;
    private String joy_comment;
    private int tension;
    private String tension_comment;
    private int sadness;
    private String sadness_comment;


    public Emozioni(int amazement, String amazement_comment, int solemnity, String solemnity_comment, int tenderness, String tenderness_comment, int nostalgia, String nostalgia_comment, int calmness, String calmness_comment, int power, String power_comment, int joy, String joy_comment, int tension, String tension_comment, int sadness, String sadness_comment) {
        this.amazement = amazement;
        this.amazement_comment = amazement_comment;
        this.solemnity = solemnity;
        this.solemnity_comment = solemnity_comment;
        this.tenderness = tenderness;
        this.tenderness_comment = tenderness_comment;
        this.nostalgia = nostalgia;
        this.nostalgia_comment = nostalgia_comment;
        this.calmness = calmness;
        this.calmness_comment = calmness_comment;
        this.power = power;
        this.power_comment = power_comment;
        this.joy = joy;
        this.joy_comment = joy_comment;
        this.tension = tension;
        this.tension_comment = tension_comment;
        this.sadness = sadness;
        this.sadness_comment = sadness_comment;
    }

    public Emozioni(int amazement, int solemnity, int tenderness, int nostalgia, int calmness, int power, int joy, int tension, int sadness) {
        this.amazement = amazement;
        this.solemnity = solemnity;
        this.tenderness = tenderness;
        this.nostalgia = nostalgia;
        this.calmness = calmness;
        this.power = power;
        this.joy = joy;
        this.tension = tension;
        this.sadness = sadness;
    }

    public int getAmazement() {
        return amazement;
    }

    public String getAmazement_comment() {
        return amazement_comment;
    }

    public int getSolemnity() {
        return solemnity;
    }

    public String getSolemnity_comment() {
        return solemnity_comment;
    }

    public int getTenderness() {
        return tenderness;
    }

    public String getTenderness_comment() {
        return tenderness_comment;
    }

    public int getNostalgia() {
        return nostalgia;
    }

    public String getNostalgia_comment() {
        return nostalgia_comment;
    }

    public int getCalmness() {
        return calmness;
    }

    public String getCalmness_comment() {
        return calmness_comment;
    }

    public int getPower() {
        return power;
    }

    public String getPower_comment() {
        return power_comment;
    }

    public int getJoy() {
        return joy;
    }

    public String getJoy_comment() {
        return joy_comment;
    }

    public int getTension() {
        return tension;
    }

    public String getTension_comment() {
        return tension_comment;
    }

    public int getSadness() {
        return sadness;
    }

    public String getSadness_comment() {
        return sadness_comment;
    }

}
