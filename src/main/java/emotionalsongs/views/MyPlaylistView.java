package emotionalsongs.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;

@PageTitle("Playlist")
@Route(value = "my-playlist", layout = MainLayout.class)
public class MyPlaylistView extends VerticalLayout {

    Button registerButton;
    Button loginButton;
    HorizontalLayout horizontalLayout;
    VerticalLayout noLogged;
    H2 header;
    Image logo;
    Object username = VaadinSession.getCurrent().getAttribute("username");
    public MyPlaylistView() {

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

                horizontalLayout = new HorizontalLayout();
                horizontalLayout.add(logo, header, loginButton, new Paragraph("o"), registerButton);

                noLogged.add(logo, header, horizontalLayout);

                add(noLogged);

                noLogged.setJustifyContentMode(JustifyContentMode.CENTER);
                noLogged.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
                noLogged.getStyle().set("text-align", "center");

            } else {
                H1 testo = new H1("Loggato!! " + username);
                add(testo);
            }



    }


}
