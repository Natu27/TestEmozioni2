package emotionalsongs.views;


import emotionalsongs.backend.Canzone;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import emotionalsongs.backend.ClientES;

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
    TextField annoDaCercare;
    Button searchButton;
    Grid<Canzone> grid = new Grid<>(Canzone.class);
    List<Canzone> result;

    public RicercaTitoloView() {
        //this.canzoneService = canzoneService;
        setSpacing(true);
        setSizeFull();
        layoutTitolo = new HorizontalLayout();
            layoutTitolo.setAlignItems(FlexComponent.Alignment.CENTER);
            iconTitolo = new Icon(VaadinIcon.SEARCH);
            iconTitolo.setColor("#006af5");
            titoloPagina = new H3("Titolo - Autore - Anno");

        layoutTitolo.add(iconTitolo, titoloPagina);

            titoloDaCercare = new TextField();
            autoreDaCercare = new TextField();
            annoDaCercare = new TextField();
            titoloDaCercare.setPlaceholder("Inserisci titolo...");
            autoreDaCercare.setPlaceholder("Inserisci autore...");
            annoDaCercare.setPlaceholder("Inserisci anno...");
            searchButton = new Button("Cerca", buttonClickEvent -> {
                search();
            });

            searchButton.setAutofocus(true);
            searchButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            searchButton.setIcon(VaadinIcon.SEARCH.create());

            toolbar = new HorizontalLayout(titoloDaCercare, autoreDaCercare, annoDaCercare, searchButton);
            toolbar.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
            toolbar.setAlignItems(Alignment.CENTER);
            toolbar.setWidthFull();

            configureGrid();

            //Da modificare
            ClientES cES = new ClientES();
            result = cES.findAll();
            grid.setItems(result);
            System.out.println(result.size());

        add(layoutTitolo, toolbar, grid);
    }

    private void configureGrid() {
        grid.setSizeFull();
        grid.setColumns("titolo","artista","anno");
    }

    private void search() {
        titoloDaCercare.setValue("");
        titoloDaCercare.setPlaceholder("Inserisci titolo...");
    }
}
