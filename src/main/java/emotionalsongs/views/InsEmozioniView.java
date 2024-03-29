package emotionalsongs.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import emotionalsongs.backend.ClientES;
import emotionalsongs.backend.entities.Canzone;
import emotionalsongs.backend.entities.Emozione;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Acquati Luca
 * @author Jamil Muhammad Qasim
 * @author Naturale Lorenzo
 * @author Volonterio Luca
 * <p></p>
 * Classe che rappresenta la vista per la valutazione delle emozioni.
 * @version 1.0
 */

@PageTitle("InsEmozioni")
@Route(value = "ins-emozioni", layout = MainLayout.class)
public class InsEmozioniView extends Dialog {
    String playlistTitle = (String) VaadinSession.getCurrent().getAttribute("playlistTitle");
    Canzone songSelected;
    Grid<Emozione> grid;
    ClientES client = ClientES.getInstance();

    /**
     * Costruttore per la vista per la valutazione delle emozioni associate ad una canzone.
     *
     * @param songSelected La canzone selezionata per la valutazione delle emozioni.
     */
    public InsEmozioniView(Canzone songSelected) {
        this.songSelected = songSelected;

        setWidth("800px");
        setCloseOnEsc(true);

        // Crea il layout per le informazioni dettagliate
        VerticalLayout layoutInfo = new VerticalLayout();
        layoutInfo.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        layoutInfo.setAlignItems(FlexComponent.Alignment.CENTER);
        layoutInfo.setPadding(true);
        layoutInfo.setSpacing(true);

        // Aggiungi le informazioni dettagliate al layout
        H2 titoloPlaylist = new H2("Playlist: " + playlistTitle);
        String info = "Titolo: " + songSelected.getTitolo() +
                " - Artista: " + songSelected.getArtista() +
                " - Anno: " + songSelected.getAnno();

        //Button confirm
        Button confirmButton = new Button("Conferma", VaadinIcon.CHECK_CIRCLE.create());
        confirmButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        confirmButton.addClickListener(e -> {
            List<Emozione> emozioniVotate = getAllScores();
            try {
                List<Canzone> playlistSong = client.showCanzoniPlaylist((Integer)VaadinSession.getCurrent().getAttribute("playlistId"));
                if (branoPresente(playlistSong, songSelected)) {
                    client.insEmoBranoPlaylist((Integer) VaadinSession.getCurrent().getAttribute("playlistId"), songSelected.getId(), emozioniVotate);
                    Notification.show("Votazione eseguita!", 3000, Notification.Position.MIDDLE)
                            .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                    this.close();
                }
                else{
                    Notification.show("Impossibile votare il brano selezionato", 3000, Notification.Position.MIDDLE)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
            }
            catch (SQLException ex) {
                try {
                    client.updateEmoBranoPlaylist((Integer) VaadinSession.getCurrent().getAttribute("playlistId"), songSelected.getId(), emozioniVotate);
                    Notification.show("Votazione aggiornata!", 3000, Notification.Position.MIDDLE)
                            .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                    this.close();
                } catch (SQLException exception) {
                    Notification.show("Impossibile effettuare l'operazione", 3000, Notification.Position.MIDDLE)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
            }
        });

        //Button close
        Button closeButton = new Button("Annulla", VaadinIcon.CLOSE_CIRCLE.create());
        closeButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        closeButton.addClickListener(e -> this.close());

        HorizontalLayout layoutButtons = new HorizontalLayout(confirmButton, closeButton);
        layoutButtons.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        layoutButtons.setAlignItems(FlexComponent.Alignment.CENTER);
        //Grid Votazione
        getGridVotazione();
        layoutInfo.add(titoloPlaylist, new H3(info), grid, layoutButtons);
        add(layoutInfo);
    }

    /**
     * Metodo privato che permette di ottenere tutti i punteggi di tutte le emozioni.
     * @return Una {@code List} contente tutti i punteggi.
     */
    private List<Emozione> getAllScores() {
        List<Emozione> scores = new ArrayList<>();

        grid.getDataProvider().fetch(new Query<>()).forEach(scores::add);

        return scores;
    }

    /**
     * Metodo privato che consente di controllare se il brano è già presente nella lista delle canzoni votate.
     * @param listaCanzoni La lista delle canzoni votate.
     * @param song La canzone da controllare.
     * @return true se la canzone è presente, false altrimenti
     */
    private boolean branoPresente (List<Canzone> listaCanzoni, Canzone song) {
        for (Canzone c:listaCanzoni) {
            if (c.getId()== song.getId())
                return true;
        }
        return false;
    }

    /**
     * Metodo privato che permette la creazione della griglia contente le emozioni da valutare.
     */
    private void getGridVotazione() {
        grid = new Grid<>(Emozione.class);
        grid.setItems(getEmotions()); // Metodo per ottenere le emozioni da visualizzare

        grid.getColumnByKey("score").setVisible(false);
        grid.getColumnByKey("commento").setVisible(false);

        grid.addComponentColumn(emotion -> {
            ComboBox<Integer> scoreComboBox = new ComboBox<>();
            scoreComboBox.setItems(0, 1, 2, 3, 4, 5);
            scoreComboBox.setValue((int) emotion.getScore());
            scoreComboBox.addValueChangeListener(event -> {
                if(event.getValue() != null) {
                    int newScore = event.getValue();
                    emotion.setScore(newScore); // Aggiorna il punteggio nell'oggetto Emotion
                }
            });
            return scoreComboBox;
        }).setHeader("Punteggio");

        grid.addComponentColumn(emotion -> {
            Button commentButton = new Button("Aggiungi", VaadinIcon.CLIPBOARD_TEXT.create());
            commentButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            commentButton.addClickListener(event -> {
                Dialog dialog = new Dialog();
                dialog.setWidth("500px");

                H3 titolo = new H3("Commento " + emotion.getName() + " \uD83D\uDDB9 ⬇");
                HorizontalLayout titleLayout = new HorizontalLayout(titolo);
                titleLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
                titleLayout.setAlignItems(FlexComponent.Alignment.CENTER);

                TextArea commentTextArea = new TextArea();
                commentTextArea.setMaxLength(256);
                commentTextArea.setWidth("100%");

                Button saveButton = new Button("Conferma", e -> {
                    String comment = commentTextArea.getValue();
                    emotion.setCommento(comment);
                    dialog.close();
                });
                saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
                saveButton.setIcon(VaadinIcon.CHECK_CIRCLE.create());
                saveButton.setAutofocus(true);

                Button closeButton = new Button("Annulla", e -> dialog.close());
                closeButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
                closeButton.setIcon(VaadinIcon.CLOSE_CIRCLE.create());
                closeButton.setAutofocus(true);

                HorizontalLayout buttonsLayout = new HorizontalLayout(saveButton, closeButton);
                buttonsLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
                buttonsLayout.setAlignItems(FlexComponent.Alignment.CENTER);

                Component spacer1 = createSpacer();
                Component spacer2 = createSpacer();
                dialog.add(titleLayout, spacer1, commentTextArea, spacer2, buttonsLayout);
                dialog.open();
            });
            return commentButton;
        }).setHeader("Commento");
    }

    /**
     * Metodo privato che crea uno spacer
     * @return spacer Il divisore che viene creato.
     */
    private Component createSpacer() {
        Div spacer = new Div();
        spacer.setWidth("20px");
        spacer.setHeight("20px");
        return spacer;
    }

    /**
     * Metodo privato che permette di ottenere le emozioni.
     * @return Ritorna un'{@code ArrayList} contente le emozioni.
     */
    private ArrayList<Emozione> getEmotions() {
        ArrayList<Emozione> emotions = new ArrayList<>();
        emotions.add(new Emozione("Amazement"));
        emotions.add(new Emozione("Solemnity"));
        emotions.add(new Emozione("Tenderness"));
        emotions.add(new Emozione("Nostalgia"));
        emotions.add(new Emozione("Calmness"));
        emotions.add(new Emozione("Power"));
        emotions.add(new Emozione("Joy"));
        emotions.add(new Emozione("Tension"));
        emotions.add(new Emozione("Sadness"));
        return emotions;
    }
}
