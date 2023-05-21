package emotionalsongs.backend.codicefiscale;

/**
 * La classe {@code CodiceControlo} permette di calcolare il codice di controllo del codice fiscale
 * @package backend.codicefiscale
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
        int somma = 0;
        int resto = 0;
        String letterePari = "";
        String lettereDispari = "";

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
                lettereDispari += c;
            } else {
                letterePari += c;
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
        switch (resto) {
            case 0:
                return "A";
            case 1:
                return "B";
            case 2:
                return "C";
            case 3:
                return "D";
            case 4:
                return "E";
            case 5:
                return "F";
            case 6:
                return "G";
            case 7:
                return "H";
            case 8:
                return "I";
            case 9:
                return "J";
            case 10:
                return "K";
            case 11:
                return "L";
            case 12:
                return "M";
            case 13:
                return "N";
            case 14:
                return "O";
            case 15:
                return "P";
            case 16:
                return "Q";
            case 17:
                return "R";
            case 18:
                return "S";
            case 19:
                return "T";
            case 20:
                return "U";
            case 21:
                return "V";
            case 22:
                return "W";
            case 23:
                return "X";
            case 24:
                return "Y";
            case 25:
                return "Z";
        }
        String mese = String.valueOf(resto);

        return mese;
    }
}