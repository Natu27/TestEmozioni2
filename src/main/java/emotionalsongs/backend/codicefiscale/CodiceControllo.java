package emotionalsongs.backend.codicefiscale;

/**
 * La classe {@code CodiceControlo} permette di calcolare il codice di controllo del codice fiscale
 * {@code @package} backend.codicefiscale
 */
public class CodiceControllo {

    /**
     * Ottiene il codice di controllo alfabetico associando il resto della divisione per 26 della
     * somma delle posizioni delle lettere ad una lettera.
     * @param cfProvvisorio {@code String}
     * @return il carattere di controllo di controllo {@code String}
     */
    public static String getCodice(String cfProvvisorio) {

        int sommaPari = 0;
        int sommaDispari = 0;
        int somma, resto;
        StringBuilder letterePari = new StringBuilder();
        StringBuilder lettereDispari = new StringBuilder();

        String[] caratteriDispari = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E",
                "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
        int[] valoriDispari = {1, 0, 5, 7, 9, 13, 15, 17, 19, 21, 1, 0, 5, 7, 9, 13, 15, 17, 19, 21, 2, 4, 18,
                20, 11, 3, 6, 8, 12, 14, 16, 10, 22, 25, 24, 23};

        String[] caratteriPari = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F",
                "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
        int[] valoriPari = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,
                16, 17, 18, 19, 20, 21, 22, 23, 24, 25};

        for (int i = 0; i < cfProvvisorio.length(); i++) {
            char c = cfProvvisorio.charAt(i);
            if (i % 2 == 0) {
                lettereDispari.append(c);
            } else {
                letterePari.append(c);
            }
        }
        for (int i = 0; i < letterePari.length(); i++) {
            String c = String.valueOf(letterePari.charAt(i));
            for(int j = 0; j < caratteriPari.length; j ++){
                if(c.equals(caratteriPari[j]))
                    sommaPari += valoriPari[j];
            }

        }
        for (int i = 0; i < lettereDispari.length(); i++) {
            String c = String.valueOf(lettereDispari.charAt(i));
            for(int j = 0; j < caratteriDispari.length; j ++){
                if(c.equals(caratteriDispari[j]))
                    sommaDispari += valoriDispari[j];
            }

        }
        somma = sommaDispari + sommaPari;
        resto = somma % 26;
        return switch (resto) {
            case 0 -> "A";
            case 1 -> "B";
            case 2 -> "C";
            case 3 -> "D";
            case 4 -> "E";
            case 5 -> "F";
            case 6 -> "G";
            case 7 -> "H";
            case 8 -> "I";
            case 9 -> "J";
            case 10 -> "K";
            case 11 -> "L";
            case 12 -> "M";
            case 13 -> "N";
            case 14 -> "O";
            case 15 -> "P";
            case 16 -> "Q";
            case 17 -> "R";
            case 18 -> "S";
            case 19 -> "T";
            case 20 -> "U";
            case 21 -> "V";
            case 22 -> "W";
            case 23 -> "X";
            case 24 -> "Y";
            case 25 -> "Z";
            default -> String.valueOf(resto);
        };

    }
}