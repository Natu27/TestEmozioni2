package emotionalsongs.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import emotionalsongs.backend.ClientES;
import emotionalsongs.backend.DatabaseConnection;
import emotionalsongs.backend.Servizi;
import emotionalsongs.backend.entities.Canzone;
import emotionalsongs.backend.exceptions.NessunaCanzoneTrovata;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@PageTitle("AggiuntaBrani")
@Route(value = "aggiunta-brani", layout = MainLayout.class)
public class AggiuntaBraniView extends Dialog {
    HorizontalLayout layoutTitolo;
    HorizontalLayout toolbar;
    HorizontalLayout playlist;
    Icon iconTitolo;
    H3 titoloPagina;
    TextField titoloDaCercare;
    TextField autoreDaCercare;
    ComboBox<Integer> annoDaCercare;
    Button searchButton;
    Button addButton;
    Button fineButton;
    Button closeButton;
    Grid<Canzone> grid = new Grid<>(Canzone.class);
    List<Canzone> result;
    ArrayList<Canzone> braniSelezionati = new ArrayList<>();
    ClientES clientES = new ClientES();
    List<Integer> anni = clientES.getAnni("", "");
    int playlistId = (Integer) VaadinSession.getCurrent().getAttribute("playlistId");

    ArrayList<Canzone> braniPrecedentementeSelezionati = clientES.showCanzoniPlaylist(playlistId);

    public AggiuntaBraniView() throws Exception {
        setWidthFull();
        setCloseOnEsc(true);

        searchButton = new Button("Cerca", buttonClickEvent -> search());
        addButton = new Button("Aggiungi Brani", buttonClickEvent -> aggiungiBrani());
        fineButton = new Button("Conferma", buttonClickEvent -> {
            try {
                if (braniSelezionati != null) {
                    clientES.addBraniPlaylist(playlistId, braniSelezionati);
                    Notification.show("Brani inseriti nella Playlist", 3000, Notification.Position.MIDDLE)
                            .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                    this.close();
                } else {
                    Notification.show("Non hai selezionato alcun brano", 3000, Notification.Position.MIDDLE)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
            } catch (RemoteException e) {
                Notification.show("Impossibile effettuare l'operazione: uno o più brani già presenti", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                //e.printStackTrace();
            }
            catch (NessunaCanzoneTrovata e) {
                Notification.show("Non hai selezionato alcun brano", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
            braniSelezionati = new ArrayList<>();
        });
        closeButton = new Button("Annulla", buttonClickEvent -> this.close());
        configureLayout();
        configureSearchBar();
        configureGrid();
        configureCreazionePlaylist();
        Component spacer1 = createSpacer();
        Component spacer2 = createSpacer();
        Component spacer3 = createSpacer();

        if (result != null) {
            grid.setItems(result);
        } else {
            search();
        }

        add(layoutTitolo, spacer1, toolbar, spacer2, grid, spacer3, playlist);
    }

    private void configureLayout() {
        layoutTitolo = new HorizontalLayout();
        layoutTitolo.setAlignItems(FlexComponent.Alignment.CENTER);
        iconTitolo = new Icon(VaadinIcon.PLUS);
        iconTitolo.setColor("#006af5");
        titoloPagina = new H3("Aggiungi Brani \uD83C\uDFB6");
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
        toolbar.setAlignItems(FlexComponent.Alignment.CENTER);
        toolbar.setWidthFull();
    }

    private void configureGrid() {
        //grid.setSizeFull();
        grid.setColumns("id","titolo", "artista", "anno");
        //TODO id invisibile ora valorizzato e funzionante --> per V
        grid.getColumnByKey("id").setVisible(false);
        grid.getColumns().get(3).setSortable(false); // sort disattivato perchè non funziona su questa colonna
        grid.setSelectionMode(Grid.SelectionMode.MULTI);
    }

    private void configureCreazionePlaylist() {
        addButton.setAutofocus(true);
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addButton.setIcon(VaadinIcon.PLUS_CIRCLE.create());
        fineButton.setAutofocus(true);
        fineButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        fineButton.setIcon(VaadinIcon.CHECK_CIRCLE.create());
        closeButton.setAutofocus(true);
        closeButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        closeButton.setIcon(VaadinIcon.CLOSE_CIRCLE.create());
        playlist = new HorizontalLayout(addButton, fineButton, closeButton);
        playlist.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        playlist.setAlignItems(FlexComponent.Alignment.CENTER);
        playlist.setWidthFull();
    }

    private void search() {
        try {
            result = clientES.searchSong(titoloDaCercare.getValue(), autoreDaCercare.getValue(), annoDaCercare.getValue(),
                    (ArrayList<Canzone>) Stream.concat(braniPrecedentementeSelezionati.stream(), braniSelezionati.stream()).collect(Collectors.toList()));
            grid.setItems(result);
            anni = clientES.getAnni(titoloDaCercare.getValue(), autoreDaCercare.getValue()); // retrieve anni per cui ci sono canzoni con titolo e autore desiderato
        } catch (RemoteException e) {
            Notification.show("Impossibile effettuare l'operazione", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        } catch (NessunaCanzoneTrovata e) {
            result = new ArrayList<>();
            grid.setItems(result);
            Notification.show("Nessuna canzone trovata", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            try {
                anni = clientES.getAnni("", "");
            } catch (RemoteException ex) {
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

    private void aggiungiBrani() {
        Set<Canzone> brani = grid.getSelectedItems();
        braniSelezionati.addAll(brani);
        grid.asMultiSelect().clear();
    }

    private Component createSpacer() {
        Div spacer = new Div();
        spacer.setWidth("20px");  // larghezza dello spazio vuoto
        spacer.setHeight("20px"); // altezza dello spazio vuoto
        return spacer;
    }
}
