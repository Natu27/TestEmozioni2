package backend;

import lombok.*;

import javax.persistence.*;

@AllArgsConstructor
@Data
@EqualsAndHashCode
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "public.\"Canzoni\"")

public class Canzone {
    @EqualsAndHashCode.Include
    @Id
    private int id;

    private int anno;

    //private String codice;

    private String artista;

    private String titolo;

}