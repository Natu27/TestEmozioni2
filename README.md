# EmotionalSongs

Emotional Songs è un progetto sviluppato nell’ambito del progetto di Laboratorio Interdisciplinare B per il corso di laurea in Informatica dell’Università degli Studi dell’Insubria.

EmotionalSongs è un’applicazione che, a partire da un repository di canzoni, permette di creare una o più playlist di brani musicali e annotarli singolarmente con le emozioni che vengono suscitate durante l’ascolto, secondo una scala standard di 9 stati emozionali (Geneva Emotional Music Scales – GEMS). 

L’applicazione permette inoltre di mostrare un prospetto riassuntivo delle emozioni segnalate dagli utenti ed esprimere commenti ed opinioni sui brani ascoltati.

## Avviare l'applicazione

Per avviare l’applicazione è sufficiente cliccare sul seguente link:
[EmotionalSongs](http://emotionalsongs.us-west-2.elasticbeanstalk.com)

## Link utili
- Manuali --> [Manuali](https://sites.google.com/view/es-user-manual/home-page).
- JavaDoc --> [JavaDoc](https://emotionalsongsdoc.netlify.app/)

## Struttura del progetto

- `MainLayout.java` in `src/main/java` contiene le impostazioni per la navigazione e la gestione della GUI.
- package `backend` in `src/main/java/emotionalsongs/` contiene il lato server dell'applicazione.
- package `views`   in  `src/main/java/emotionalsongs/` contiene i Frame dell'applicazione.
- package `themes`  in  `frontend/` contiene i file CSS personalizzati.
