package emotionalsongs.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.LumoUtility;
import emotionalsongs.backend.ClientES;
import emotionalsongs.backend.Servizi;
import emotionalsongs.backend.entities.Canzone;
import emotionalsongs.backend.entities.Emozione;
import emotionalsongs.backend.exceptions.NessunaCanzoneTrovata;
import emotionalsongs.backend.exceptions.emozioni.NoCommenti;
import emotionalsongs.backend.exceptions.emozioni.NoVotazioni;

import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@PageTitle("Ricerca")
@Route(value = "ricerca", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
public class RicercaView extends VerticalLayout {
    HorizontalLayout layoutTitolo;
    HorizontalLayout toolbar;
    HorizontalLayout emotions;
    Icon iconTitolo;
    H3 titoloPagina;
    TextField titoloDaCercare;
    TextField autoreDaCercare;
    ComboBox<Integer> annoDaCercare;
    Button searchButton;
    Button emoButton;
    Grid<Canzone> grid = new Grid<>(Canzone.class);
    List<Canzone> result;
    ClientES clientES = ClientES.getInstance();
    Servizi stub = clientES.getStub();
    List<Integer> anni = stub.getAnni("", "");

    public RicercaView() throws Exception {
        setSpacing(true);
        setSizeFull();

        searchButton = new Button("Cerca", buttonClickEvent -> search());
        emoButton = new Button("Visualizza Emozioni", buttonClickEvent -> {
            try {
                visualizzaEmo();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        configureLayout();
        configureSearchBar();
        configureGrid();
        configureEmotions();

        // TODO: se si vuole tenere il risultato della ricerca cachato, vanno cachati anche i parametri di ricerca
        result = (List<Canzone>) UI.getCurrent().getSession().getAttribute("result");
        if (result != null) {
            if(result.isEmpty()) {
                grid.setVisible(false);
                emoButton.setVisible(false);
            }
            grid.setItems(result);
        } else {
            search();
        }

        add(layoutTitolo, toolbar, grid, emotions);
    }

    private void configureLayout() {
        layoutTitolo = new HorizontalLayout();
        layoutTitolo.setAlignItems(FlexComponent.Alignment.CENTER);
        iconTitolo = new Icon(VaadinIcon.SEARCH);
        iconTitolo.setColor("#006af5");
        titoloPagina = new H3("Titolo - Autore - Anno");
        layoutTitolo.add(iconTitolo, titoloPagina);
    }

    private void configureSearchBar() {
        titoloDaCercare = new TextField();
        autoreDaCercare = new TextField();
        annoDaCercare = new ComboBox<>();
        titoloDaCercare.setPlaceholder("Inserisci titolo...");
        autoreDaCercare.setPlaceholder("Inserisci autore...");
        annoDaCercare.setPlaceholder("Inserisci anno...");
        annoDaCercare.setItems(anni);
        searchButton.setAutofocus(true);
        searchButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        searchButton.setIcon(VaadinIcon.SEARCH.create());
        toolbar = new HorizontalLayout(titoloDaCercare, autoreDaCercare, annoDaCercare, searchButton);
        toolbar.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        toolbar.setAlignItems(Alignment.CENTER);
        toolbar.setWidthFull();
    }

    private void configureGrid() {
        grid.setSizeFull();
        grid.setColumns("id","titolo", "artista", "anno");
        //TODO id invisibile ora valorizzato e funzionante --> per V
        grid.getColumnByKey("id").setVisible(false);
        grid.getColumns().get(3).setSortable(false); // sort disattivato perché non funziona su questa colonna
    }

    private void configureEmotions() {
        emoButton.setAutofocus(true);
        emoButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        emoButton.setIcon(VaadinIcon.BAR_CHART_H.create());
        emotions = new HorizontalLayout(emoButton);
        emotions.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        emotions.setAlignItems(Alignment.CENTER);
        emotions.setWidthFull();
    }

    private void search() {
        try {
            result = stub.searchSong(titoloDaCercare.getValue(), autoreDaCercare.getValue(), annoDaCercare.getValue());
            grid.setItems(result);
            anni = stub.getAnni(titoloDaCercare.getValue(), autoreDaCercare.getValue()); // retrieve anni per cui ci sono canzoni con titolo e autore desiderato
            //Per memorizzare la grid corrente
            UI.getCurrent().getSession().setAttribute("result", result);

            grid.setVisible(true);
            emoButton.setVisible(true);

        } catch (RemoteException | SQLException e) {
            Notification.show("Impossibile effettuare l'operazione", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        } catch (NessunaCanzoneTrovata e) {
            result = new ArrayList<>();
            grid.setItems(result);
            UI.getCurrent().getSession().setAttribute("result", result);

            grid.setVisible(false);
            emoButton.setVisible(false);

            Notification.show("Nessuna canzone trovata", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            try {
                anni = stub.getAnni("", "");
            } catch (RemoteException | SQLException ex) {
                Notification.show("Impossibile effettuare l'operazione", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        }
        Integer cachedValue = annoDaCercare.getValue();
        if (cachedValue != null && !anni.contains(cachedValue))
            anni.add(cachedValue);

        annoDaCercare.setItems(anni);
        if (cachedValue != null)
            annoDaCercare.setValue(cachedValue);
    }

    private void visualizzaEmo() throws Exception {
        Canzone selectedTuple = grid.asSingleSelect().getValue();
        VaadinSession.getCurrent().setAttribute("canzoneselezionata", selectedTuple);
        if (selectedTuple != null) {
            // Apri la finestra di dialogo per mostrare le informazioni aggiuntive
            openDetailsDialog(selectedTuple);
        } else {
            Notification.show("Nessuna canzone selezionata", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void openDetailsDialog(Canzone selectedTuple) throws Exception {
        Dialog dialog = new Dialog();
        //dialog.setSizeFull();

        // Crea il layout per le informazioni dettagliate
        VerticalLayout layout = new VerticalLayout();
        layout.setJustifyContentMode(JustifyContentMode.CENTER);
        layout.setAlignItems(Alignment.CENTER);
        layout.setPadding(true);
        layout.setSpacing(true);

        // Aggiungi le informazioni dettagliate al layout
        String info = "Titolo: " + selectedTuple.getTitolo() +
                " - Artista: " + selectedTuple.getArtista() +
                " - Anno: " + selectedTuple.getAnno();
        layout.add(new H3(info));

        try {
            HistogramView chart = new HistogramView(selectedTuple.getId());
            layout.add(chart);

            dialog.add(layout);

        } catch (NoVotazioni e) {
            Image logo = new Image("images/EmSongs.png", "EmoSong logo");
            logo.setWidth("200px");

            H2 header = new H2("Nessuna emozione presente per il brano selezionato");
            header.addClassNames(LumoUtility.Margin.Top.XLARGE, LumoUtility.Margin.Bottom.MEDIUM);

            VerticalLayout noEmotions = new VerticalLayout();
            noEmotions.setJustifyContentMode(JustifyContentMode.CENTER);
            noEmotions.setAlignItems(Alignment.CENTER);
            noEmotions.add(logo, header);
            layout.add(noEmotions);
            dialog.add(layout);
        }
        Button closeButton = new Button("Chiudi", buttonClickEvent -> dialog.close());
        closeButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        closeButton.setIcon(VaadinIcon.CLOSE_CIRCLE.create());

        Button commentsButton = new Button("Visualizza Commenti", buttonClickEvent ->{
            try {
                List<Emozione> commenti = stub.getCommenti(selectedTuple.getId());
                cleanComments(commenti);
                Dialog dialogCommenti = new Dialog();

                Grid<Emozione> gridCommenti = new Grid<>(Emozione.class);
                gridCommenti.setItems(commenti);
                gridCommenti.setColumns("name","commento");
                gridCommenti.getColumnByKey("name").setWidth("150px");
                gridCommenti.getColumnByKey("commento").setAutoWidth(true);

                Button closeButton1 = new Button("Chiudi", clickEvent -> dialogCommenti.close());
                closeButton1.addThemeVariants(ButtonVariant.LUMO_ERROR);
                closeButton1.setIcon(VaadinIcon.CLOSE_CIRCLE.create());
                VerticalLayout commentsLayout = new VerticalLayout(new H3(info), gridCommenti, closeButton1);
                commentsLayout.setJustifyContentMode(JustifyContentMode.CENTER);
                commentsLayout.setAlignItems(Alignment.CENTER);

                dialogCommenti.add(commentsLayout);
                dialogCommenti.setWidthFull();
                dialogCommenti.open();
            }
            catch (NoCommenti e) {
                Notification.show("Nessun commento presente per il brano selezionato", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
            catch (RemoteException | SQLException e) {
                Notification.show("Impossibile effettuare l'operazione", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        commentsButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        commentsButton.setIcon(VaadinIcon.CLIPBOARD_TEXT.create());

        HorizontalLayout buttonsLayout = new HorizontalLayout(commentsButton, closeButton);
        buttonsLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        buttonsLayout.setAlignItems(Alignment.CENTER);

        dialog.add(buttonsLayout);
        dialog.open();
        // Deseleziona la tupla dopo aver aperto il dialogo
        grid.asSingleSelect().clear();
    }

    private void cleanComments(List<Emozione> commenti) {
        commenti.removeIf(e -> e.getCommento() == null);
    }
}
