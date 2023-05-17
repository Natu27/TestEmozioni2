package emotionalsongs.views;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import emotionalsongs.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.Arrays;
import java.util.List;

@PageTitle("Registrazione")
@Route(value = "registrazione", layout = MainLayout.class)
public class RegistrazioneView extends VerticalLayout {

    HorizontalLayout layoutTitolo;
    Icon iconTitolo;
    H3 titoloPagina;
    FormLayout registrazione;
    TextField nome;
    TextField cognome;
    DatePicker dataNascita;
    List<String> scelteSesso;
    ComboBox<String> sesso;
    //ComboBox<String> luogoNascita;
    TextField codFiscale;
    Button calcolaCf;
    TextField via_piazza;
    EmailField email;
    TextField username;
    PasswordField password;
    PasswordField confirmPassword;
    VerticalLayout regButtonLayout;
    Button registerButton;
    VerticalLayout pageLayout;

    public RegistrazioneView() {
        setSpacing(false);
        setSizeFull();
        layoutTitolo = new HorizontalLayout();
            layoutTitolo.setAlignItems(FlexComponent.Alignment.CENTER);
            iconTitolo = new Icon(VaadinIcon.USERS);
            iconTitolo.setColor("#006af5");
            titoloPagina = new H3("Nuovo Utente");
            layoutTitolo.add(iconTitolo, titoloPagina);


        registrazione = new FormLayout();
            nome = new TextField("Nome");
            cognome = new TextField("Cognome");
            dataNascita = new DatePicker("Data di nascita");
            //luogoNascita = new ComboBox<>("Luogo di nascita");
            scelteSesso = Arrays.asList("M", "F");
            sesso = new ComboBox<>("Sesso", scelteSesso);
            codFiscale = new TextField("Codice Fiscale");
            calcolaCf = new Button("Calcola codice fiscale");
            via_piazza = new TextField("Via/Piazza");
            email = new EmailField("Email");
            username = new TextField("Username");
            password = new PasswordField("Password");
            confirmPassword = new PasswordField("Conferma Password");

            setComponent();

        registrazione.setColspan(via_piazza,2);
        //registrazione.setColspan(luogoNascita,2);

        registrazione.add(nome, cognome, dataNascita, sesso,/*luogoNascita,*/ codFiscale, calcolaCf, via_piazza, email, username, password, confirmPassword);

        regButtonLayout = new VerticalLayout();
            registerButton = new Button("Registrati", buttonClickEvent -> {
                registration();
            });
            regButtonLayout.setAlignItems(Alignment.CENTER);
            registerButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            registerButton.setIcon(VaadinIcon.USERS.create());
            registerButton.setAutofocus(true);
            registerButton.setWidth("420px");
            regButtonLayout.add(registerButton);

        pageLayout = new VerticalLayout();
            pageLayout.setSizeFull();
            pageLayout.setAlignItems(FlexComponent.Alignment.CENTER);
            pageLayout.add(registrazione, registerButton);

        add(layoutTitolo, pageLayout);

    }

    private void setComponent(){
        nome.setSuffixComponent(VaadinIcon.USER.create());
        cognome.setSuffixComponent(VaadinIcon.USER.create());
        codFiscale.setSuffixComponent(VaadinIcon.BARCODE.create());
        via_piazza.setSuffixComponent(VaadinIcon.HOME.create());
        email.setSuffixComponent(VaadinIcon.MAILBOX.create());
        username.setSuffixComponent(VaadinIcon.USER.create());
        calcolaCf.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        nome.setRequired(true);
        cognome.setRequired(true);
        sesso.setRequired(true);
        //luogoNascita.setRequired(true);
        dataNascita.setRequired(true);
        codFiscale.setMaxLength(16);
        codFiscale.setRequired(true);
        via_piazza.setRequired(true);
        email.setRequired(true);
        username.setRequired(true);
    }
    private void registration() {

    }
}
