package emotionalsongs.backend.codicefiscale;

/**
 * La classe {@code NomeCognome} permette di ottenere la prime 6 lettere del codice fiscale
 * @package backend.codicefiscale
 */
public class NomeCognome {

    /**
     * Permette di ottenere le prime 3 lettere del codice fiscale
     * @param cognome {@code String}
     * @return le tre lettere estratte come {@code String}
     */
    public static String getCognome(String cognome) {
        String cognomeNoSpazi = cognome.replaceAll("\\s+", " ");
        String consonanti = "bcdfghjklmnpqrstvwxyz";
        String cognomeEstratto = "";
        int count = 0;

        for (int i = 0; i < cognomeNoSpazi.length() && count < 3; i++) {
            char c = cognomeNoSpazi.charAt(i);
            if (consonanti.indexOf(Character.toLowerCase(c)) != -1) {
                cognomeEstratto += Character.toUpperCase(c);
                count++;
            }
        }

        if (count < 3) {
            for (int i = 0; i < cognomeNoSpazi.length(); i++) {
                char c = cognomeNoSpazi.charAt(i);
                if (consonanti.indexOf(Character.toLowerCase(c)) == -1) {
                    cognomeEstratto += Character.toUpperCase(c);
                    break;
                }
            }
        }

        return cognomeEstratto;
    }

    /**
     * Permette di ottenere le seconde 3 lettere del codice fiscale
     * @param nome {@code String}
     * @return le tre lettere estratte come {@code String}
     */
    public static String getNome(String nome) {
        String nomeNoSpazi = nome.replaceAll("\\s+", " ");
        String consonanti = "bcdfghjklmnpqrstvwxyz";
        String nomeEstratto = "";
        int count = 0;

        for (int i = 0; i < nomeNoSpazi.length() && count < 3; i++) {
            char c = nomeNoSpazi.charAt(i);
            if (consonanti.indexOf(Character.toLowerCase(c)) != -1) {
                nomeEstratto += Character.toUpperCase(c);
                count++;
            }
        }
        if (count < 3) {
            for (int i = 0; i < nomeNoSpazi.length(); i++) {
                char c = nomeNoSpazi.charAt(i);
                if (consonanti.indexOf(Character.toLowerCase(c)) == -1) {
                    nomeEstratto += Character.toUpperCase(c);
                    break;
                }
            }
        }

        return nomeEstratto;
    }
}
