package emotionalsongs.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import emotionalsongs.backend.ClientES;
import emotionalsongs.backend.entities.Canzone;
import emotionalsongs.backend.entities.Playlist;
import emotionalsongs.backend.entities.Utente;
import emotionalsongs.backend.exceptions.playlist.NomePlaylistGiaPresente;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Acquati Luca
 * @author Jamil Muhammad Qasim
 * @author Naturale Lorenzo
 * @author Volonterio Luca
 * <p></p>
 * Classe che rappresenta la vista delle playlist dell'utente.
 * @version 1.0
 */
@PageTitle("Playlist")
@Route(value = "my-playlist", layout = MainLayout.class)
public class MyPlaylistView extends VerticalLayout {

    Button registerButton;
    HorizontalLayout horizontalLayout;
    HorizontalLayout layoutTitolo;
    Icon iconTitolo;
    H3 titoloPagina;
    VerticalLayout noLogged;
    VerticalLayout newPlaylistForm;
    H2 header;
    Image logo;
    TextField playlistName;
    Button createPlaylist;
    Button newPlaylist;
    Dialog dialog;
    List<Playlist> result;
    HorizontalLayout actionButton;
    ConfirmDialog delete;
    Dialog view;
    VerticalLayout viewForm = new VerticalLayout();
    VerticalLayout renamePlaylist = new VerticalLayout();
    Button rename;
    Dialog editTitle;
    TextField newTitle;
    Button confirmNewTitle;
    Grid<Canzone> gridCanzoni = new Grid<>(Canzone.class);
    ArrayList<Canzone> resultSongPlaylist;
    Button addCanzone;
    String nomePlaylist;
    Grid<Playlist> gridPlaylist = new Grid<>(Playlist.class);
    ClientES client = ClientES.getInstance();
    Utente utente = (Utente) VaadinSession.getCurrent().getAttribute("utente");

    /**
     * Costruttore per la vista delle playlist associate all'utente.
     * Se l'utente è loggato vengono visualizzate le palylist collegate al suo account, altrimenti viene
     * mostrato un messaggio,
     */
    public MyPlaylistView() throws Exception {

        if (utente==null) {
            noLogged = new VerticalLayout();

            registerButton = new Button("Non hai ancora un account? Registrati");
            registerButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
            registerButton.addClickListener(e -> UI.getCurrent().navigate(RegistrazioneView.class));

            logo = new Image("images/EmSongs.png", "EmoSong logo");
            logo.setWidth("200px");

            header = new H2("Devi aver effettuato l'accesso per visualizzare questa pagina");
            header.addClassNames(Margin.Top.XLARGE, Margin.Bottom.MEDIUM);

            horizontalLayout = new HorizontalLayout();
            //horizontalLayout.add(logo, header, loginButton, new Paragraph("o"), registerButton);
            horizontalLayout.add(logo, header, registerButton);


            noLogged.add(logo, header, horizontalLayout);

            add(noLogged);

            noLogged.setJustifyContentMode(JustifyContentMode.CENTER);
            noLogged.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
            noLogged.getStyle().set("text-align", "center");

        } else {
            newPlaylist = new Button("Crea nuova playlist", buttonClickEvent -> dialogCreatePlaylist());
            newPlaylist.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            createPlaylist = new Button("Crea Playlist", buttonClickEvent -> {
                try {
                    addPlaylist(playlistName.getValue());
                    //dialog.close();
                } catch (SQLException e) {
                    Notification.show("Impossibile effettuare l'operazione", 3000, Notification.Position.MIDDLE)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                } catch (NomePlaylistGiaPresente e) {
                    Notification.show("Impossibile creare playlist - Nome già presente", 3000, Notification.Position.MIDDLE)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
            });
            newPlaylist.setIcon(VaadinIcon.PLUS.create());
            createPlaylist.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            configureLayout();
            configureGrid();
            playlistGridColumn();
            //gridPlaylist.setItems(result);
            configureViewDialog();
            configureEditDialog();


            add(layoutTitolo, newPlaylist,gridPlaylist);

        }

    }

    /**
     * Metodo privato che permette la creazione del layout dell'intestazione della pagina.
     */
    private void configureLayout() {
        layoutTitolo = new HorizontalLayout();
        layoutTitolo.setAlignItems(FlexComponent.Alignment.CENTER);
        iconTitolo = new Icon(VaadinIcon.MUSIC);
        iconTitolo.setColor("#006af5");
        titoloPagina = new H3("Le tue Playlist");
        layoutTitolo.add(iconTitolo, titoloPagina);
    }


    /**
     * Metodo privato che permette la creazione del layout della finestra per la creazione delle playlist.
     */
    private void dialogCreatePlaylist() {
        H1 title = new H1("Playlist \uD83C\uDFB5");
        dialog = new Dialog();
        playlistName = new TextField("Titolo");
        playlistName.setWidthFull();
        playlistName.setRequired(true);
        //playlistName.setErrorMessage("Il campo non può essere vuoto");
        createPlaylist.setWidthFull();
        Button closeButton = new Button("Chiudi", buttonClickEvent -> dialog.close());
        closeButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        closeButton.setIcon(VaadinIcon.CLOSE_CIRCLE.create());
        dialog.setCloseOnEsc(true);
        dialog.open();

        newPlaylistForm = new VerticalLayout(title, playlistName, createPlaylist, closeButton);
        newPlaylistForm.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        newPlaylistForm.setAlignItems(FlexComponent.Alignment.CENTER);

        dialog.add(newPlaylistForm);

    }

    /**
     * Metodo privato che permette la all'utente di creare una nuova playlist ed aggiungerla alla griglia.
     * @param titolo Il titolo della playlist che si vuole creare.
     */
    private void addPlaylist(String titolo) throws SQLException, NomePlaylistGiaPresente {
        titolo = titolo.trim();
        if(titolo.equals(""))
            Notification.show("Impossibile creare playlist - Necessario inserire titolo", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        else {
            result = client.myPlaylist(utente.getId());
            if(nomePlaylistPresente(titolo)) throw new NomePlaylistGiaPresente();
            if (client.addPlaylist(titolo, utente.getId()) == 1) {
                Notification.show("Playlist creata", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                this.configureGrid();
                dialog.close();

            } else {
                Notification.show("Impossibile creare playlist", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        }
    }

    /**
     * Metodo privato che controlla se il nome inserito per una nuova playlist è già presente.
     * @param myTitle Titolo della playlist che si vuole creare.
     * @return true se il titolo esiste già, false altrimenti.
     */
    private boolean nomePlaylistPresente(String myTitle) {
        for(Playlist p : result) {
            if(p.getTitolo().equals(myTitle)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Metodo privato che permette la creazione del layout della griglia in cui verranno visualizzate le playlist dell'utente.
     */
    private void configureGrid() throws SQLException {
        result = new ArrayList<>();
        result = client.myPlaylist(utente.getId());
        gridPlaylist.getColumnByKey("id").setVisible(false);
        gridPlaylist.getColumnByKey("username").setVisible(false);
        gridPlaylist.getColumnByKey("titolo").setVisible(true);
        gridPlaylist.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        gridPlaylist.setHeight("1000px");
        gridPlaylist.setItems(result);
    }

    /**
     * Metodo privato che permette di aggiungere alla griglia delle playlist le colonne per visualizzare e eliminare le playlist.
     */
    private void playlistGridColumn(){
        gridPlaylist.addColumn(
                new ComponentRenderer<>(Button::new, (button, titolo) -> {
                    button.addThemeVariants(ButtonVariant.LUMO_ICON,
                            ButtonVariant.LUMO_PRIMARY);
                    button.addClickListener(e -> {
                        VaadinSession.getCurrent().setAttribute("playlistTitle", titolo.getTitolo());
                        VaadinSession.getCurrent().setAttribute("playlistId", titolo.getId());
                        view = new Dialog(viewForm);
                        //view.setWidthFull();
                        nomePlaylist = titolo.getTitolo();
                        view.setHeaderTitle("Titolo ➡ " + nomePlaylist);

                        configureGridCanzoni();

                        view.open();
                    });
                    button.setIcon(new Icon(VaadinIcon.FOLDER_OPEN));
                })).setHeader("Visualizza/Modifica");
        gridPlaylist.addColumn(
                new ComponentRenderer<>(Button::new, (button, titolo) -> {
                    button.addThemeVariants(ButtonVariant.LUMO_ICON,
                            ButtonVariant.LUMO_ERROR,
                            ButtonVariant.LUMO_PRIMARY);
                    button.addClickListener(e -> {
                        delete = new ConfirmDialog("⚠️ Conferma eliminazione",
                                "Sei sicuro di voler eliminare la playlist?", "Sì", event1 -> {
                        try {
                            if (client.removePlaylist(utente.getId(), titolo.getTitolo()) == 1) {
                                Notification.show("Playlist cancellata", 3000, Notification.Position.MIDDLE)
                                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                                this.configureGrid();
                            } else {
                                Notification.show("Impossibile cancellare la playlist", 3000, Notification.Position.MIDDLE)
                                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                            }
                        } catch (SQLException ex) {
                            Notification.show("Impossibile effettuare l'operazione", 3000, Notification.Position.MIDDLE)
                                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
                        }
                    }, "No", event2 -> delete.close());
                        delete.open();
                    });
                    button.setIcon(new Icon(VaadinIcon.TRASH));
                })).setHeader("Elimina");
    }

    /**
     * Metodo privato che permette di configurare la finestra per visualizzare i brani di una playlist.
     */
    private void configureViewDialog(){
        Button closeButton = new Button("Chiudi",VaadinIcon.CLOSE_CIRCLE.create());
        closeButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        closeButton.addClickListener(e -> view.close());
        addCanzone = new Button("Aggiungi brani", VaadinIcon.PLUS.create());
        addCanzone.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addCanzone.addClickListener(buttonClickEvent -> {
            view.close();
            try {
                AggiuntaBraniView addBrani = new AggiuntaBraniView();
                addBrani.open();
            } catch (Exception e) {
                Notification.show("Impossibile effettuare l'operazione", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        rename = new Button("Rinomina", VaadinIcon.PENCIL.create());
        rename.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        rename.addClickListener(e-> editTitle.open());
        actionButton = new HorizontalLayout();
        HorizontalLayout chiudi = new HorizontalLayout();
        chiudi.add(closeButton);
        actionButton.add(addCanzone, rename,closeButton);
        actionButton.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        actionButton.setAlignItems(FlexComponent.Alignment.CENTER);
        viewForm.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        viewForm.setAlignItems(FlexComponent.Alignment.CENTER);
        viewForm.add(gridCanzoni,actionButton,closeButton);
    }

    /**
     * Metodo privato che permette di configurare la griglia per visualizzare le playlist.
     */
    private void configureGridCanzoni() {
        gridCanzoni.setColumns("id","titolo", "artista", "anno");
        gridCanzoni.getColumnByKey("id").setVisible(false);
        gridCanzoni.getColumns().get(3).setSortable(false);
        gridCanzoni.addColumn(
                new ComponentRenderer<>(Button::new, (button, titolo) -> {
                    button.addThemeVariants(ButtonVariant.LUMO_ICON,
                            ButtonVariant.LUMO_PRIMARY);
                    button.addClickListener(e -> {
                        InsEmozioniView insEmoDialog;
                        try {
                            insEmoDialog = new InsEmozioniView(titolo);
                            insEmoDialog.open();
                        } catch (Exception ex) {
                            Notification.show("Impossibile effettuare l'operazione", 3000, Notification.Position.MIDDLE)
                                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
                        }
                    });
                    button.setIcon(new Icon(VaadinIcon.BAR_CHART_H));
                    button.setText("Inserisci Emozioni");
                })).setHeader("Votazione");
        gridCanzoni.addColumn(
                new ComponentRenderer<>(Button::new, (button, titolo) -> {
                    button.addThemeVariants(ButtonVariant.LUMO_ICON,
                            ButtonVariant.LUMO_ERROR,
                            ButtonVariant.LUMO_PRIMARY);
                    button.addClickListener(e -> {
                        delete = new ConfirmDialog("⚠️ Conferma rimozione",
                                "Sei sicuro di voler rimuovere " + titolo.getTitolo() + " dalla playlist?", "Sì", event1 -> {
                            try {
                                if (client.removePlaylistSong((Integer) VaadinSession.getCurrent().getAttribute("playlistId"),  titolo.getId()) == 1) {
                                    Notification.show("Canzone rimossa!", 3000, Notification.Position.MIDDLE)
                                            .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                                    this.configureGridCanzoni();
                                } else {
                                    Notification.show("Impossibile rimuovere la canzone", 3000, Notification.Position.MIDDLE)
                                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                                }
                            } catch (SQLException ex) {
                                Notification.show("Impossibile effettuare l'operazione", 3000, Notification.Position.MIDDLE)
                                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                            }
                        }, "No", event2 -> delete.close());
                        delete.open();
                    });
                    button.setIcon(new Icon(VaadinIcon.TRASH));
                })).setHeader("Elimina");



        try {
            resultSongPlaylist = client.showCanzoniPlaylist((Integer) VaadinSession.getCurrent().getAttribute("playlistId"));
            gridCanzoni.setItems(resultSongPlaylist);

            gridCanzoni.setVisible(!resultSongPlaylist.isEmpty());
            if(!resultSongPlaylist.isEmpty()) {
                view.setWidthFull();
            }

        } catch (SQLException e) {
            Notification.show("Impossibile effettuare l'operazione", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    /**
     * Metodo privato che permette di configurare la finestra per modificare una playlist.
     */
    private void configureEditDialog(){
        H2 titleRename = new H2("Rinomina ✒");
        Button closeButton = new Button("Annulla", VaadinIcon.CLOSE_CIRCLE.create());
        closeButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        rename = new Button("Rinomina", VaadinIcon.PENCIL.create());
        confirmNewTitle = new Button("Conferma", VaadinIcon.CHECK_CIRCLE.create());
        confirmNewTitle.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        editTitle = new Dialog(renamePlaylist);
        editTitle.setCloseOnEsc(true);
        newTitle = new TextField("Nuovo titolo");
        newTitle.setRequired(true);

        HorizontalLayout buttonsLayout = new HorizontalLayout(confirmNewTitle, closeButton);
        buttonsLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        buttonsLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        renamePlaylist.add(titleRename, newTitle, buttonsLayout);
        renamePlaylist.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        renamePlaylist.setAlignItems(FlexComponent.Alignment.CENTER);
        confirmNewTitle.addClickListener(e->{
            try {
                result = client.myPlaylist(utente.getId());
                if(nomePlaylistPresente(newTitle.getValue())) throw new NomePlaylistGiaPresente();
                if(newTitle.getValue().trim().equals("")) {
                    Notification.show("Impossibile rinominare playlist - Necessario inserire titolo", 3000, Notification.Position.MIDDLE)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                } else {
                    if (client.renamePlaylist(utente.getId(), newTitle.getValue(), (Integer) VaadinSession.getCurrent().getAttribute("playlistId")) == 1) {
                        Notification.show("Playlist modificata", 3000, Notification.Position.MIDDLE)
                                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                        this.configureGrid();
                        editTitle.close();
                        view.close();
                    } else {
                        Notification.show("Impossibile modificare la playlist", 3000, Notification.Position.MIDDLE)
                                .addThemeVariants(NotificationVariant.LUMO_ERROR);
                    }
                }
            } catch (SQLException ex) {
                Notification.show("Impossibile effettuare l'operazione", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (NomePlaylistGiaPresente ex) {
                Notification.show("Impossibile creare playlist - Nome già presente", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
            newTitle.clear();
        });
        closeButton.addClickListener(event-> editTitle.close());
    }

}