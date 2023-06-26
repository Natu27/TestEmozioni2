package emotionalsongs.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import emotionalsongs.backend.entities.Canzone;
import emotionalsongs.backend.entities.Emozione;

import java.util.*;

@PageTitle("InsEmozioni")
@Route(value = "ins-emozioni", layout = MainLayout.class)
public class InsEmozioniView extends Dialog {

    String playlistTitle = (String) VaadinSession.getCurrent().getAttribute("playlistTitle");
    String username = (String) VaadinSession.getCurrent().getAttribute("username");
    Canzone songSelected;
    Grid<Emozione> grid;

    public InsEmozioniView(Canzone songSelected) {
        this.songSelected = songSelected;

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
            List<Emozione> punteggi = getAllScores();
            for(Emozione emo : punteggi) {
                System.out.println(emo.toString());
            }
            System.out.println(); //Separare score !=
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

    private List<Emozione> getAllScores() {
        List<Emozione> scores = new ArrayList<>();

        grid.getDataProvider().fetch(new Query<>()).forEach(scores::add);

        return scores;
    }


    private void getGridVotazione() {
        grid = new Grid<>(Emozione.class);
        grid.setItems(getEmotions()); // Metodo per ottenere le emozioni da visualizzare

        grid.getColumnByKey("score").setVisible(false);
        grid.addComponentColumn(emotion -> {
            ComboBox<Integer> scoreComboBox = new ComboBox<>();
            scoreComboBox.setItems(0, 1, 2, 3, 4, 5);
            scoreComboBox.setValue(emotion.getScore());
            scoreComboBox.addValueChangeListener(event -> {
                if(event.getValue() != null) {
                    int newScore = event.getValue();
                    emotion.setScore(newScore); // Aggiorna il punteggio nell'oggetto Emotion
                }
            });
            return scoreComboBox;
        }).setHeader("Punteggio");
    }

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
