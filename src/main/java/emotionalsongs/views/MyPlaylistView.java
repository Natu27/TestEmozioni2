package emotionalsongs.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
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
    Button closeButton;
    Button createPlaylist;
    Button newPlaylist;
    Dialog dialog;
    List<Playlist> result;
    Grid<Playlist> grid = new Grid<>(Playlist.class);
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
            grid.setItems(result);

            add(layoutTitolo, newPlaylist,grid);
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
        playlistName = new TextField("Nome playlist");
        playlistName.setWidthFull();
        playlistName.setRequired(true);
        playlistName.setErrorMessage("Il campo non può essere vuoto");
        createPlaylist.setWidthFull();
        closeButton = new Button("Chiudi", buttonClickEvent -> dialog.close());
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
            if(nomePlaylistPresente(titolo)) throw new NomePlaylistGiaPresente();
            if (stub.addPlaylist(titolo, username) == 1) {
                Notification.show("Playlist creata!", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                this.configureGrid();
                dialog.close();
                //Page page = UI.getCurrent().getPage();
                //page.reload();
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
        grid.getColumnByKey("id").setVisible(false);
        grid.getColumnByKey("username").setVisible(false);
        grid.getColumnByKey("titolo").setVisible(true);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setHeight("1000px");
        grid.setItems(result);
    }
}



