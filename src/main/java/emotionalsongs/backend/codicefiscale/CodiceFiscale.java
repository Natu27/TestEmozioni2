package emotionalsongs.backend.codicefiscale;

/**
 * La classe {@code CodiceFiscale} permette di il codice fiscale
 * @package backend.codicefiscale
 * @see NomeCognome
 * @see DataNascita
 * @see Comune
 * @see CodiceControllo
 */
public class CodiceFiscale {
    /**
     * Ottiene il codice fiscale della persona
     * @param cognome {@code String}
     * @param nome {@code String}
     * @param giorno {@code int}
     * @param mese {@code int}
     * @param anno {@code int}
     * @param sesso {@code String}
     * @param comuneNascita {@code String}
     * @return il codice fiscale {@code String}
     */
    public static String codiceFiscale(String cognome, String nome,int giorno, int mese, int anno,String sesso, String comuneNascita){
        String cogno = NomeCognome.getCognome(cognome);
        String name = NomeCognome.getNome(nome);
        String annoNascita = DataNascita.getYear(anno);
        String meseNascita = DataNascita.getMonth(mese);
        String giornoNascita = DataNascita.getDay(giorno, sesso);
        String codiceComune = Comune.getCodice(comuneNascita);
        String cfProvvisorio = cogno + name + annoNascita + meseNascita + giornoNascita + codiceComune;
        String codiceControllo = CodiceControllo.getCodice(cfProvvisorio);
        String cf = cogno + name + annoNascita + meseNascita + giornoNascita + codiceComune + codiceControllo;

        return cf;
    }

}