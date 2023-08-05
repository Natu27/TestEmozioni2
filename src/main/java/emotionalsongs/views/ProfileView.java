package emotionalsongs.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.avatar.AvatarVariant;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.LumoUtility;
import emotionalsongs.backend.ClientES;
import emotionalsongs.backend.Servizi;
import emotionalsongs.backend.entities.Utente;

@PageTitle("Profilo")
@Route(value = "profile", layout = MainLayout.class)
public class ProfileView extends VerticalLayout {

    ClientES clientES = new ClientES();
    Servizi stub = clientES.getStub();
    Utente utente = (Utente) VaadinSession.getCurrent().getAttribute("utente");
    HorizontalLayout layoutTitolo;
    Icon iconTitolo;
    H3 titoloPagina;
    H2 header;
    Image logo;
    Button registerButton;
    VerticalLayout noLogged;
    HorizontalLayout horizontalLayout;
    VerticalLayout datiPersonali;

    Avatar avatar;
    Upload profilePic;
    FormLayout datiForm;
    TextField nome;
    TextField cognome;
    DatePicker dataNascita;
    TextField codFisc;
    TextField luogoNascita;
    TextField sesso;
    FormLayout residenzaForm;
    VerticalLayout residenza;
    TextField via_piazza;
    Button confermaModifiche;
    FormLayout accessoForm;
    VerticalLayout accesso;
    TextField username;
    EmailField email;
    PasswordField password;



    public ProfileView() throws Exception {

        if (utente==null) {
            noLogged = new VerticalLayout();

            registerButton = new Button("Non hai ancora un account? Registrati");
            registerButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
            registerButton.addClickListener(e -> UI.getCurrent().navigate(RegistrazioneView.class));

            logo = new Image("images/EmSongs.png", "EmoSong logo");
            logo.setWidth("200px");

            header = new H2("Devi aver effettuato l'accesso per visualizzare questa pagina");
            header.addClassNames(LumoUtility.Margin.Top.XLARGE, LumoUtility.Margin.Bottom.MEDIUM);

            horizontalLayout = new HorizontalLayout();
            //horizontalLayout.add(logo, header, loginButton, new Paragraph("o"), registerButton);
            horizontalLayout.add(logo, header, registerButton);


            noLogged.add(logo, header, horizontalLayout);

            add(noLogged);

            noLogged.setJustifyContentMode(JustifyContentMode.CENTER);
            noLogged.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
            noLogged.getStyle().set("text-align", "center");

        } else {

            VerticalLayout confirmLayout = new VerticalLayout();

            confermaModifiche = new Button("Conferma modifiche");
            confermaModifiche.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            confermaModifiche.setIcon(VaadinIcon.CHECK_CIRCLE.create());
            confirmLayout.add(confermaModifiche);
            confirmLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
            confirmLayout.setAlignItems(FlexComponent.Alignment.CENTER);

            configureLayout();
            configureDatiLayout();
            configureResidenzaLayout();
            datiAccessoLayout();
            add(layoutTitolo, datiPersonali, residenza, accesso, confirmLayout);

        }

    }

    private void configureLayout() {
        layoutTitolo = new HorizontalLayout();
        layoutTitolo.setAlignItems(FlexComponent.Alignment.CENTER);
        iconTitolo = new Icon(VaadinIcon.USER_CARD);
        iconTitolo.setColor("#006af5");
        titoloPagina = new H3("I tuoi dati");
        layoutTitolo.add(iconTitolo, titoloPagina);
    }
        private void configureDatiLayout() {
        datiPersonali = new VerticalLayout();

            avatar = new Avatar();
            //avatar.setImage("images/EmSongs.png"); // sostituire con immagine caricata
            avatar.addThemeVariants(AvatarVariant.LUMO_XLARGE);

            profilePic = new Upload();

            HorizontalLayout persona = new HorizontalLayout();
            datiForm = new FormLayout();

            nome = new TextField();
            nome.setLabel("Nome");
            nome.setValue("Ciao");// valore restituito da query
            nome.setReadOnly(true);

            cognome = new TextField();
            cognome.setLabel("Cognome");
            cognome.setValue("Ciao");// valore restituito da query
            cognome.setReadOnly(true);

            dataNascita = new DatePicker();
            dataNascita.setLabel("Data di nascita");
            //dataNascita.setValue();//data da query
            dataNascita.setReadOnly(true);

            codFisc = new TextField();
            codFisc.setLabel("Codice fiscale");
            codFisc.setValue("CodFisc");// valore restituito da query
            codFisc.setReadOnly(true);

            luogoNascita = new TextField();
            luogoNascita.setLabel("Luogo di nascita");
            luogoNascita.setValue("LuogoNascita");// valore restituito da query
            luogoNascita.setReadOnly(true);

            sesso = new TextField();
            sesso.setLabel("Sesso");
            sesso.setValue("M");// valore restituito da query
            sesso.setReadOnly(true);

            datiForm.add(nome,cognome,dataNascita,luogoNascita,sesso, codFisc, profilePic);
            datiForm.setColspan(profilePic,2);
            persona.add(avatar, datiForm);

            datiForm.setWidthFull();

            H3 intestazione = new H3("Dati personali");

            datiPersonali.add(intestazione,persona);

        }

        private void configureResidenzaLayout(){
            residenza = new VerticalLayout();
            residenzaForm = new FormLayout();

            via_piazza = new TextField("Residenza");
            via_piazza.setValue("Via .........");// valore restituito da query

            H3 intestazione = new H3("Residenza");

            residenzaForm.add(via_piazza);
            residenzaForm.setColspan(via_piazza,2);

            residenza.add(intestazione, residenzaForm);

        }

        private void datiAccessoLayout() {
        accesso = new VerticalLayout();
        accessoForm = new FormLayout();

        username = new TextField("Username");
        username.setReadOnly(true);
        username.setValue("User");//Sostituire con valore query
        accessoForm.setColspan(username,2);

        email = new EmailField("Email");
        email.setValue("Email@gmail.com");//Query
        email.getElement().getStyle().set("background", "none");

        password = new PasswordField("Password");
        password.setValue("Psw");//query

        H3 intestazione = new H3("Dati per l'accesso");
        accessoForm.add(username,email,password);
        accessoForm.setColspan(email,2);
        accessoForm.setColspan(password,2);

        accesso.add(intestazione, accessoForm);

        }

}