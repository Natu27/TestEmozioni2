package emotionalsongs.backend.codicefiscale;

import org.springframework.beans.factory.annotation.Value;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * La classe {@code Comune} permette di ottenere il codice catastale del comune di nascita
 * @package backend.codicefiscale
 */
public class Comune {

    private static String linea = "";
    private static String delimitatore = ",";
    static String codice;
    @Value("${csv.file.path.2}")
    private static String csvFile;

    /**
     * Ottiene il codice di controllo alfabetico associato al comune di nascita.
     * i codici vengono letti da un file CSV.
     * @param comuneNascita {@code String}
     * @return in codice alfanumerico abbinato al comune di nascita come {@code String}
     */
    public static String getCodice(String comuneNascita){
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            br.readLine();

            while ((linea = br.readLine()) != null) {
                String[] values = linea.split(delimitatore);
                String comune = values[0];

                if(comune.equals(comuneNascita))
                    codice = values[1];
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return codice;
    }

}
