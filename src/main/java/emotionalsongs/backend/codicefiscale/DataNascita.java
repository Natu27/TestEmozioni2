package emotionalsongs.backend.codicefiscale;

/**
 * La classe {@code DataNascita} permette di ottenere la prime 6 lettere del codice fiscale
 * @package backend.codicefiscale
 */
public class DataNascita {
    /**
     * Ottiene le ultime due cifre dell'anno di nascita, se l'ultima cifra dell'anno di
     * nascita è minore di 10 viene aggiunto un 0 davanti alla cifra.
     * @param annoNascita {@code int}
     * @return le ultime due cifre dell'anno di nascita come {@code String}
     */
    public static String getYear(int annoNascita) {
        String anno = "";
        int year = annoNascita % 100;
        if(year < 10)
            anno = "0"+year;
        else anno = String.valueOf(year);
        return anno;
    }

    /**
     * Ottiene la lettere abbinata all'anno di nascita.
     * @param mese {@code int}
     * @return la lettere abbinata al mese di nascita come {@code String}
     */
    public static String getMonth(int mese) {
        switch (mese) {
            case 1: return "A";
            case 2: return "B";
            case 3: return "C";
            case 4: return "D";
            case 5: return "E";
            case 6: return "H";
            case 7: return "L";
            case 8: return "M";
            case 9: return "P";
            case 10: return "R";
            case 11: return "S";
            case 12: return "T";
            default: return "mese non valido";
        }
    }

    /**
     * Ottiene le ultime due cifre dell'anno di nascita, se l'ultima cifra dell'anno di
     * nascita è minore di 10 viene aggiunto un 0 davanti alla cifra.
     * @param giornoNascita {@code int}
     * @param sesso {@code String}
     * @return le due cifre del giorno di nascita in base al sesso come {@code String}.
     */
    public static String getDay(int giornoNascita, String sesso) {
        String gg = "0" + giornoNascita;
        if (sesso.equals("F")) {
            giornoNascita = giornoNascita + 40;
            return String.valueOf(giornoNascita);
        }else{
            if(giornoNascita < 10) {
                return gg;
            }else{
                return String.valueOf(giornoNascita);
            }

        }
    }

}
