CREATE TABLE public."Canzoni"
(
    anno       numeric(4) NULL,
    codice     varchar(18) NOT NULL,
    autore     varchar,
    titolo     varchar,
    Canzoni_id serial      NOT NULL,
    CONSTRAINT canzoni_primary_key PRIMARY KEY (Canzoni_id),
    CONSTRAINT canzoni_unique_key UNIQUE (anno, autore, titolo)
);

CREATE TABLE public."Playlist"
(
    Playlist_id serial  NOT NULL,
    titolo      varchar NOT NULL,
    CONSTRAINT playlist_pk PRIMARY KEY (Playlist_id)
);

CREATE TABLE public."User"
(
    User_id         serial  NOT NULL,
    nome            varchar NULL,
    cognome         varchar NULL,
    username        varchar NOT NULL,
    hashed_password varchar NOT NULL,
    indirizzo       varchar NULL,
    cf              varchar NULL,
    email           varchar NOT NULL,
    CONSTRAINT user_pk PRIMARY KEY (User_id),
    CONSTRAINT user_un UNIQUE (username)
);

CREATE TABLE public."possiede"
(
    "User id"     int NOT NULL,
    "Playlist id" int NOT NULL,
    CONSTRAINT possiede_fk FOREIGN KEY ("User id") REFERENCES public."User" (User_id),
    CONSTRAINT possiede_fk_1 FOREIGN KEY ("Playlist id") REFERENCES public."Playlist" (Playlist_id)
);
CREATE TABLE public."contiene"
(
    "Codice Canzone" int NOT NULL,
    "Playlist id"    int NOT NULL,
    CONSTRAINT contiene_fk FOREIGN KEY ("Codice Canzone") REFERENCES public."Canzoni" (Canzoni_id),
    CONSTRAINT contiene_fk1 FOREIGN KEY ("Playlist id") REFERENCES public."Playlist" (Playlist_id)
);

CREATE TABLE public."Emozioni"
(
    Emozioni_id serial  NOT NULL,
    titolo      varchar NOT NULL,
    descrizione varchar NULL,
    punteggio   int     NOT NULL CHECK (punteggio >= 0 AND punteggio <= 5),
    CONSTRAINT Emozioni_pk PRIMARY KEY (Emozioni_id)
);

CREATE TABLE public."referenzia"
(
    "Codice Canzone" int NOT NULL,
    "Emozione id"    int NOT NULL,
    CONSTRAINT referenzia_fk FOREIGN KEY ("Codice Canzone") REFERENCES public."Canzoni" (Canzoni_id),
    CONSTRAINT referenzia_fk1 FOREIGN KEY ("Emozione id") REFERENCES public."Emozioni" (Emozioni_id)
);