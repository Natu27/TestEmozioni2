package emotionalsongs.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
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
import emotionalsongs.backend.ClientES;
import emotionalsongs.backend.Servizi;
import emotionalsongs.backend.entities.Canzone;
import emotionalsongs.backend.exceptions.NessunaCanzoneTrovata;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@PageTitle("AggiuntaBrani")
@Route(value = "aggiunta-brani", layout = MainLayout.class)
public class AggiuntaBraniView extends VerticalLayout {
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
    Grid<Canzone> grid = new Grid<>(Canzone.class);
    List<Canzone> result;
    ClientES clientES = new ClientES();
    Servizi stub = clientES.getStub();
    List<Integer> anni = stub.getAnni("", "");

    public AggiuntaBraniView() throws Exception {
        setSpacing(true);
        setSizeFull();

        searchButton = new Button("Cerca", buttonClickEvent -> search());
        addButton = new Button("Aggiungi Brani", buttonClickEvent -> aggiungiBrani());
        fineButton = new Button("FINE");
        configureLayout();
        configureSearchBar();
        configureGrid();
        configureEmotions();

        if (result != null) {
            grid.setItems(result);
        } else {
            search();
        }

        add(layoutTitolo, toolbar, grid, playlist);
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
        toolbar.setAlignItems(FlexComponent.Alignment.CENTER);
        toolbar.setWidthFull();
    }

    private void configureGrid() {
        grid.setSizeFull();
        grid.setColumns("titolo", "artista", "anno");
        grid.getColumns().get(2).setSortable(false); // sort disattivato perchè non funziona su questa colonna
        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        // grid.getColumns().get(2).setComparator((c1, c2) -> {
        //    return Integer.valueOf(c1.getAnno()).compareTo(Integer.valueOf(c2.getAnno()));
        //});
    }

    private void configureEmotions() {
        addButton.setAutofocus(true);
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addButton.setIcon(VaadinIcon.PLUS_CIRCLE.create());
        fineButton.setAutofocus(true);
        fineButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        fineButton.setIcon(VaadinIcon.CHECK_CIRCLE.create());
        playlist = new HorizontalLayout(addButton, fineButton);
        playlist.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        playlist.setAlignItems(FlexComponent.Alignment.CENTER);
        playlist.setWidthFull();
    }

    private void search() {
        try {
            result = stub.searchSong(titoloDaCercare.getValue(), autoreDaCercare.getValue(), annoDaCercare.getValue());
            grid.setItems(result);
            anni = stub.getAnni(titoloDaCercare.getValue(), autoreDaCercare.getValue()); // retrieve anni per cui ci sono canzoni con titolo e autore desiderato
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NessunaCanzoneTrovata e) {
            result = new ArrayList<>();
            grid.setItems(result);
            Notification.show("Nessuna canzone trovata", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            try {
                anni = stub.getAnni("", "");
            } catch (RemoteException ex) {
                e.printStackTrace();
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
        for(Canzone c : brani)
            System.out.println(c.getTitolo());
        grid.asMultiSelect().clear();
    }
}
