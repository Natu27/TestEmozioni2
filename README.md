# EmotionalSongs

Emotional Songs è un progetto sviluppato nell’ambito del progetto di Laboratorio Interdisciplinare B per il corso di laurea in Informatica dell’Università degli Studi dell’Insubria.
EmotionalSongs è un’applicazione che, a partire da un repository di canzoni, permette di creare una o più playlist di brani musicali e annotarli singolarmente con le emozioni che vengono suscitate durante l’ascolto, secondo una scala standard di 9 stati emozionali (Geneva Emotional Music Scales – GEMS). L’applicazione permette inoltre di mostrare un prospetto riassuntivo delle emozioni segnalate dagli utenti ed esprimere commenti ed opinioni sui brani ascoltati.

## Avviare l'applicazione

Per avviare l’applicazione è sufficiente cliccare sul seguente link:
[EmotionalSongs](http://emotionalsongs.us-west-2.elasticbeanstalk.com)

## Link utili
- Guide: [Manuali](https://sites.google.com/view/es-user-manual/home-page).
- JavaDoc: [JavaDoc](https://emotionalsongsdoc.netlify.app/)

## Struttura del progetto

- `MainLayout.java` in `src/main/java` contiene le impostazioni per la navigazione (il menù principale, la barra superiore e laterale). Utilizza
  [App Layout](https://vaadin.com/docs/components/app-layout).
- package `views` in `src/main/java` contiene il lato server dell'applicazione.
- la cartella `views` in `frontend/` contiene il lato client dell'applicazione.
- la cartella `themes` in `frontend/` contiene i file CSS personalizzati.
