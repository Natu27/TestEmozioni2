package emotionalsongs.views;


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
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.VaadinSession;
import emotionalsongs.backend.ClientES;
import emotionalsongs.backend.Servizi;
import emotionalsongs.backend.entities.Canzone;
import emotionalsongs.backend.exceptions.NessunaCanzoneTrovata;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

@PageTitle("Ricerca")
@Route(value = "ricerca", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
public class RicercaTitoloView extends VerticalLayout {
    HorizontalLayout layoutTitolo;
    HorizontalLayout toolbar;
    Icon iconTitolo;
    H3 titoloPagina;
    TextField titoloDaCercare;
    TextField autoreDaCercare;
    ComboBox<Integer> annoDaCercare;
    Button searchButton;
    Grid<Canzone> grid = new Grid<>(Canzone.class);
    List<Canzone> result;
    ClientES clientES = new ClientES();
    Servizi stub = clientES.getStub();
    List<Integer> anni = stub.getAnni();

    public RicercaTitoloView() throws Exception {
        setSpacing(true);
        setSizeFull();

        result = VaadinSession.getCurrent().getAttribute(List.class);
        if (result != null) {
            grid.setItems(result);
        }

        searchButton = new Button("Cerca", buttonClickEvent -> search());
        configureLayout();
        configureSearchBar();
        configureGrid();

        add(layoutTitolo, toolbar, grid);

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
        grid.setColumns("titolo","artista","anno");
    }

    private void search() {
        try {
            result = stub.searchSong(titoloDaCercare.getValue(), autoreDaCercare.getValue(), annoDaCercare.getValue());
            grid.setItems(result);
            //Per memorizzare la grid corrente
            VaadinSession.getCurrent().setAttribute(List.class, result);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NessunaCanzoneTrovata e) {
            result = new ArrayList<Canzone>();
            grid.setItems(result);
            VaadinSession.getCurrent().setAttribute(List.class, result);
            Notification.show("Nessuna canzone trovata", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }


}
