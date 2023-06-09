package emotionalsongs.views;


import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.*;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
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
    ClientES clientES = new ClientES();
    Servizi stub = clientES.getStub();
    List<Integer> anni = stub.getAnni("", "");

    public RicercaTitoloView() throws Exception {
        setSpacing(true);
        setSizeFull();
        // TODO: se si vuole tenere il risultato della ricerca cachato, vanno cachati anche i parametri di ricerca
        result = (List<Canzone>) UI.getCurrent().getSession().getAttribute("result");
        if (result != null) {
            grid.setItems(result);
        }

        searchButton = new Button("Cerca", buttonClickEvent -> search());
        emoButton = new Button("Visualizza Emozioni", buttonClickEvent -> visualizzaEmo());
        configureLayout();
        configureSearchBar();
        configureGrid();
        configureEmotions();

        add(layoutTitolo, toolbar, grid, emotions);
        search();
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
        grid.setColumns("titolo", "artista", "anno");
        grid.getColumns().get(2).setSortable(false); // sort disattivato perchè non funziona su questa colonna
        // grid.getColumns().get(2).setComparator((c1, c2) -> {
        //    return Integer.valueOf(c1.getAnno()).compareTo(Integer.valueOf(c2.getAnno()));
        //});
    }

    private void configureEmotions() {
        emoButton.setAutofocus(true);
        emoButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        emoButton.setIcon(VaadinIcon.HEART.create());
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
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NessunaCanzoneTrovata e) {
            result = new ArrayList<>();
            grid.setItems(result);
            UI.getCurrent().getSession().setAttribute("result", result);
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

    private void visualizzaEmo() {
        Canzone selectedTuple = grid.asSingleSelect().getValue();
        if (selectedTuple != null) {
            // Apri la finestra di dialogo per mostrare le informazioni aggiuntive
            openDetailsDialog(selectedTuple);
        } else {
            Notification.show("Nessuna canzone selezionata", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void openDetailsDialog(Canzone selectedTuple) {
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

        Chart chart = createHistogramChart();
        layout.add(chart);

        dialog.add(layout);

        // Aggiungi un pulsante per chiudere la finestra di dialogo
        Button closeButton = new Button("Chiudi", buttonClickEvent -> {
            dialog.close();
        });
        closeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        closeButton.setIcon(VaadinIcon.CLOSE_CIRCLE.create());
        layout.add(closeButton);

        dialog.open();
        // Deseleziona la tupla dopo aver aperto il dialogo
        grid.asSingleSelect().clear();
    }

    private Chart createHistogramChart() {
        Chart chart = new Chart(ChartType.COLUMN);

        Configuration configuration = chart.getConfiguration();
        configuration.setTitle("Distribuzione Emozioni");

        XAxis xAxis = configuration.getxAxis();
        xAxis.setTitle("Emozioni");
        xAxis.setCategories("Amazement", "Solemnity", "Tenderness",
                            "Nostalgia", "Calmness", "Power",
                            "Joy", "Tension", "Sadness");

        YAxis yAxis = configuration.getyAxis();
        yAxis.setTitle("Media");

        ListSeries series = new ListSeries();
        // Aggiunta i dati dell'istogramma --> per Luca V - la query andrà inserita qua
        // e poi andranno settati i dati di conseguenza
        series.setData(1, 2.5, 0, 0, 5, 0, 0, 0, 0);

        PlotOptionsColumn plotOptions = new PlotOptionsColumn();
        plotOptions.setColorByPoint(true);
        series.setPlotOptions(plotOptions);

        configuration.setSeries(series);

        return chart;
    }

}
