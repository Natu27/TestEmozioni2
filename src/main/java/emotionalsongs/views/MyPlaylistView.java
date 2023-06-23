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
import emotionalsongs.backend.Servizi;
import emotionalsongs.backend.entities.Playlist;
import emotionalsongs.backend.exceptions.playlist.NomePlaylistGiaPresente;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

@PageTitle("Playlist")
@Route(value = "my-playlist", layout = MainLayout.class)
public class MyPlaylistView extends VerticalLayout {

    Button registerButton;
    Button loginButton;
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
    //Grid<Canzone> gridCanzoni = new Grid<>(Canzone.class);
    Button addCanzone;
    String nomePlaylist;
    Grid<Playlist> gridPlaylist = new Grid<>(Playlist.class);
    ClientES clientES = new ClientES();
    Servizi stub = clientES.getStub();
    String username = (String) VaadinSession.getCurrent().getAttribute("username");

    public MyPlaylistView() throws Exception {

        if (username==null) {
            noLogged = new VerticalLayout();

            registerButton = new Button("Registrati");
            registerButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
            registerButton.addClickListener(e ->
                    UI.getCurrent().navigate(RegistrazioneView.class));

            loginButton = new Button("Accedi");
            loginButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);

            logo = new Image("images/EmSongs.png", "EmoSong logo");
            logo.setWidth("200px");

            header = new H2("Devi aver effettuato l'accesso per visualizzare questa pagina");
            header.addClassNames(Margin.Top.XLARGE, Margin.Bottom.MEDIUM);

            //TODO: aggiungere azione al pulsante accedi
            horizontalLayout = new HorizontalLayout();
            horizontalLayout.add(logo, header, loginButton, new Paragraph("o"), registerButton);

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
                    addPlaylist(playlistName.getValue(),username);
                    //dialog.close();
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
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

    private void configureLayout() {
        layoutTitolo = new HorizontalLayout();
        layoutTitolo.setAlignItems(FlexComponent.Alignment.CENTER);
        iconTitolo = new Icon(VaadinIcon.MUSIC);
        iconTitolo.setColor("#006af5");
        titoloPagina = new H3("Le tue Playlist");
        layoutTitolo.add(iconTitolo, titoloPagina);
    }


    private void dialogCreatePlaylist() {
        H1 title = new H1("Nuova Playlist");
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

    private void addPlaylist(String titolo, String username) throws RemoteException, NomePlaylistGiaPresente {
        titolo = titolo.trim();
        if(titolo.equals(""))
            Notification.show("Impossibile creare playlist - Dati Mancanti", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        else {
            result = stub.myPlaylist(username);
            if(nomePlaylistPresente(titolo)) throw new NomePlaylistGiaPresente();
            if (stub.addPlaylist(titolo, username) == 1) {
                Notification.show("Playlist creata!", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                this.configureGrid();
                dialog.close();

            } else {
                Notification.show("Impossibile creare playlist", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        }
    }

    private boolean nomePlaylistPresente(String myTitle) {
        for(Playlist p : result) {
            if(p.getTitolo().equals(myTitle)) {
                return true;
            }
        }
        return false;
    }

    private void configureGrid() throws RemoteException {
        result = new ArrayList<>();
        result = stub.myPlaylist(username);
        gridPlaylist.getColumnByKey("id").setVisible(false);
        gridPlaylist.getColumnByKey("username").setVisible(false);
        gridPlaylist.getColumnByKey("titolo").setVisible(true);
        gridPlaylist.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        gridPlaylist.setHeight("1000px");
        gridPlaylist.setItems(result);
    }

    private void playlistGridColumn(){
        gridPlaylist.addColumn(
                new ComponentRenderer<>(Button::new, (button, titolo) -> {
                    button.addThemeVariants(ButtonVariant.LUMO_ICON,
                            ButtonVariant.LUMO_PRIMARY);
                    button.addClickListener(e -> {
                        VaadinSession.getCurrent().setAttribute("playlistTitle", titolo.getTitolo());
                        view = new Dialog(viewForm);
                        nomePlaylist = titolo.getTitolo();
                        view.setHeaderTitle("Titolo ➡ " + nomePlaylist);
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
                            if (stub.removePlaylist(username, titolo.getTitolo()) == 1) {
                                Notification.show("Playlist cancellata!", 3000, Notification.Position.MIDDLE)
                                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                                this.configureGrid();
                            } else {
                                Notification.show("Impossibile cancellare la playlist", 3000, Notification.Position.MIDDLE)
                                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                            }
                        } catch (RemoteException ex) {
                            throw new RuntimeException(ex);
                        }
                    }, "No", event2 -> {
                            delete.close();
                        });
                        delete.open();
                    });
                    button.setIcon(new Icon(VaadinIcon.TRASH));
                })).setHeader("Elimina");
    }

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
                throw new RuntimeException(e);
            }
            //TODO : rendere griglia ricerca utilizzabile per aggiungere brani alla playlist selezionata
        });
        rename = new Button("Rinomina", VaadinIcon.PENCIL.create());
        rename.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        rename.addClickListener(e-> {
            editTitle.open();
        });
        actionButton = new HorizontalLayout();
        HorizontalLayout chiudi = new HorizontalLayout();
        chiudi.add(closeButton);
        actionButton.add(addCanzone, rename,closeButton);
        actionButton.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        actionButton.setAlignItems(FlexComponent.Alignment.CENTER);
        viewForm.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        viewForm.setAlignItems(FlexComponent.Alignment.CENTER);
        viewForm.add(/*gridCanzoni,*/actionButton,closeButton);
    }

    private void configureEditDialog(){
        Button closeButton = new Button("Chiudi", VaadinIcon.CLOSE_CIRCLE.create());
        closeButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        rename = new Button("Rinomina", VaadinIcon.PENCIL.create());
        confirmNewTitle = new Button("Rinomina", VaadinIcon.CHECK_CIRCLE.create());
        confirmNewTitle.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        editTitle = new Dialog(renamePlaylist);
        editTitle.setHeaderTitle("Rinomina");
        editTitle.setCloseOnEsc(true);
        newTitle = new TextField("Nuovo titolo");
        newTitle.setRequired(true);
        //newTitle.setErrorMessage("Il campo non può essere vuoto!");
        renamePlaylist.add(newTitle,confirmNewTitle,closeButton);
        renamePlaylist.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        renamePlaylist.setAlignItems(FlexComponent.Alignment.CENTER);
        confirmNewTitle.addClickListener(e->{
            try {
                if(stub.renamePlaylist(username,newTitle.getValue(),(String) VaadinSession.getCurrent().getAttribute("playlistTitle"))==1){
                    Notification.show("Playlist modificata!", 3000, Notification.Position.MIDDLE)
                            .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                    this.configureGrid();
                }else {
                    Notification.show("Impossibile modificare la playlist", 3000, Notification.Position.MIDDLE)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }
            editTitle.close();
            newTitle.clear();
        });
        closeButton.addClickListener(event-> editTitle.close());
    }

}