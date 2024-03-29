package emotionalsongs.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.avatar.AvatarVariant;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.LumoUtility;
import emotionalsongs.backend.ClientES;
import emotionalsongs.backend.entities.Utente;

import java.sql.SQLException;

/**
 * @author Acquati Luca
 * @author Jamil Muhammad Qasim
 * @author Naturale Lorenzo
 * @author Volonterio Luca
 * <p></p>
 * Classe che rappresenta la vista del profilo dell'utente.
 * @version 1.0
 */

@PageTitle("Profilo")
@Route(value = "profile", layout = MainLayout.class)
public class ProfileView extends VerticalLayout {

    ClientES client = ClientES.getInstance();
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
    FormLayout datiForm;
    TextField nome;
    TextField cognome;
    TextField codFisc;
    FormLayout residenzaForm;
    VerticalLayout residenza;
    TextField via_piazza;
    Button confermaModifiche;
    FormLayout accessoForm;
    VerticalLayout accesso;
    TextField username;
    EmailField email;
    PasswordField password;
    ConfirmDialog dialogoConferma;
    Button eliminaAccount;


    /**
     * Costruttore per la vista del profilo dell'utente.
     * Se l'utente è loggato vengono visualizzati i dati del suo account, altrimenti viene
     * mostrato un messaggio che chiede di effettuare il login.
     */
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
            confermaModifiche.addClickListener(e->{
                dialogoConferma = new ConfirmDialog("⚠️ Confermare le modifiche","Sei sicuro di voler confermare le modifiche?", "Sì", event1 -> {
                    try {
                        if(!email.getValue().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$") || (!password.isEmpty() && password.getValue().length() < 8)){
                            Notification.show("Dati non validi", 3000, Notification.Position.MIDDLE)
                                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
                        } else if (client.modificaDati(utente.getId(),via_piazza.getValue(), email.getValue(), password.getValue()) >= 1) {
                            Notification.show("Modifiche effettuate", 3000, Notification.Position.MIDDLE)
                                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                        } else {
                            Notification.show("Impossibile effettuare le modifiche", 3000, Notification.Position.MIDDLE)
                                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
                        }
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                }, "No", event2 -> dialogoConferma.close());
                dialogoConferma.open();
            });

            eliminaAccount = new Button("Elimina account");
            eliminaAccount.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);
            eliminaAccount.setIcon(VaadinIcon.TRASH.create());
            eliminaAccount.addClickListener(e->{
                dialogoConferma = new ConfirmDialog("❗️ Elimina account","Sei sicuro di voler eliminare il tuo account?", "Sì", event1 -> {
                    try {
                        if(client.eliminaAccount(utente.getId())==1){
                            VaadinSession.getCurrent().getSession().invalidate();
                            UI.getCurrent().navigate("ricerca");
                            Notification.show("Account Eliminato", 3000, Notification.Position.MIDDLE)
                                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                        } else {
                            Notification.show("Impossibile elimiare l'account", 3000, Notification.Position.MIDDLE)
                                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
                        }
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                }, "No", event2 -> dialogoConferma.close());
                dialogoConferma.open();

            });


            confirmLayout.add(confermaModifiche, eliminaAccount);
            confirmLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
            confirmLayout.setAlignItems(FlexComponent.Alignment.CENTER);

            configureLayout();
            configureDatiLayout();
            configureResidenzaLayout();
            datiAccessoLayout();
            add(layoutTitolo, datiPersonali, residenza, accesso, confirmLayout);

        }

    }

    /**
     * Metodo privato che permette la creazione del layout dell'intestazione della pagina.
     */
    private void configureLayout() {
        layoutTitolo = new HorizontalLayout();
        layoutTitolo.setAlignItems(FlexComponent.Alignment.CENTER);
        iconTitolo = new Icon(VaadinIcon.USER_CARD);
        iconTitolo.setColor("#006af5");
        titoloPagina = new H3("I tuoi dati");
        layoutTitolo.add(iconTitolo, titoloPagina);
    }

    /**
     * Metodo privato che permette la creazione del layout contenente i dati anagrafici dell'utente.
     */
        private void configureDatiLayout() throws Exception {
        datiPersonali = new VerticalLayout();

            avatar = new Avatar();
            avatar.addThemeVariants(AvatarVariant.LUMO_XLARGE);

            HorizontalLayout persona = new HorizontalLayout();
            datiForm = new FormLayout();

            nome = new TextField();
            nome.setLabel("Nome");
            nome.setValue(client.myAccount(utente.getId()).get(0));
            nome.setReadOnly(true);

            cognome = new TextField();
            cognome.setLabel("Cognome");
            cognome.setValue(client.myAccount(utente.getId()).get(1));
            cognome.setReadOnly(true);

            codFisc = new TextField();
            codFisc.setLabel("Codice fiscale");
            codFisc.setValue(client.myAccount(utente.getId()).get(4));
            codFisc.setReadOnly(true);

            datiForm.add(nome,cognome, codFisc);
            persona.add(avatar,datiForm);

            datiForm.setWidthFull();

            H3 intestazione = new H3("Dati personali");

            datiPersonali.add(intestazione,persona);

        }

    /**
     * Metodo privato che permette la creazione del layout contenente i dati relativi alla residenza dell'utente.
     */
        private void configureResidenzaLayout() throws Exception{
            residenza = new VerticalLayout();
            residenzaForm = new FormLayout();

            via_piazza = new TextField("Residenza");
            via_piazza.setValue(client.myAccount(utente.getId()).get(3));

            H3 intestazione = new H3("Residenza");

            residenzaForm.add(via_piazza);
            residenzaForm.setColspan(via_piazza,2);

            residenza.add(intestazione, residenzaForm);

        }

    /**
     * Metodo privato che permette la creazione del layout contenente i dati relativi alle credenziali dell'utente.
     */
        private void datiAccessoLayout() throws Exception {
        accesso = new VerticalLayout();
        accessoForm = new FormLayout();

        username = new TextField("Username");
        username.setReadOnly(true);
        username.setValue(client.myAccount(utente.getId()).get(2));
        accessoForm.setColspan(username,2);

        email = new EmailField("Email");
        email.setValue(client.myAccount(utente.getId()).get(5));
        email.getElement().getStyle().set("background", "none");

        password = new PasswordField("Nuova password");
        password.setMinLength(8);
        password.setErrorMessage("Almeno 8 caratteri");

        H3 intestazione = new H3("Credenziali di accesso");
        Label avviso = new Label("Lasciare il campo vuoto per non modificare la password ⬆️");
        accessoForm.add(username,email,password, avviso);
        accessoForm.setColspan(email,2);
        accessoForm.setColspan(password,2);

        accesso.add(intestazione, accessoForm);

        }

}