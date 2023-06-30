package emotionalsongs.backend.codicefiscale;


import java.io.*;

/**
 * La classe {@code Comune} permette di ottenere il codice catastale del comune di nascita
 * {@code @package} backend.codicefiscale
 */
public class Comune {

    private static final String delimitatore = ",";
    static String codice;

    /**
     * Ottiene il codice di controllo alfabetico associato al comune di nascita, i codici vengono letti da un file CSV.
     * @param comuneNascita {@code String}
     * @return in codice alfanumerico abbinato al comune di nascita come {@code String}
     */
    public static String getCodice(String comuneNascita) throws IOException {
        String fileName = "META-INF/resources/data/ComuniECodici.csv";

        // Ottenere l'InputStream per il file di risorse
        InputStream is = Comune.class.getClassLoader().getResourceAsStream(fileName);

        // Creare un BufferedReader per leggere il contenuto
        assert is != null;
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String linea;
        while ((linea = br.readLine()) != null) {
            String[] values = linea.split(delimitatore);
            String comune = values[0];

            if(comune.equals(comuneNascita))
                codice = values[1];
        }
        return codice;
    }
}
