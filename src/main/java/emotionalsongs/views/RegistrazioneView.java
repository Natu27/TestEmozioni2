package emotionalsongs.views;

import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.csv.CSVReader;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
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
import emotionalsongs.backend.codicefiscale.CodiceFiscale;
import org.springframework.beans.factory.annotation.Value;

import java.io.FileReader;
import java.util.ArrayList;
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
    ComboBox<String> luogoNascita;

    @Value("src/main/resources/META-INF/resources/data/Comuni.csv")
    String fileComuni;
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

        registerButton = new Button("Registrati", buttonClickEvent -> registration());

        calcolaCf = new Button("Calcola CF", buttonClickEvent -> calcolaCodFiscale());

        configureLayoutTitolo();
        configureLayoutRegistrazione();
        setComponent();
        configureButton();
        configurePageLayout();
        add(layoutTitolo, pageLayout);
    }

    private void configureLayoutTitolo() {
        layoutTitolo = new HorizontalLayout();
        layoutTitolo.setAlignItems(FlexComponent.Alignment.CENTER);
        iconTitolo = new Icon(VaadinIcon.USERS);
        iconTitolo.setColor("#006af5");
        titoloPagina = new H3("Nuovo Utente");
        layoutTitolo.add(iconTitolo, titoloPagina);
    }

    private void configureLayoutRegistrazione() {
        registrazione = new FormLayout();
        nome = new TextField("Nome");
        cognome = new TextField("Cognome");
        dataNascita = new DatePicker("Data di nascita");
        luogoNascita = new ComboBox<>("Luogo di nascita");
        scelteSesso = Arrays.asList("M", "F");
        sesso = new ComboBox<>("Sesso", scelteSesso);
        codFiscale = new TextField("Codice Fiscale");
        //calcolaCf = new Button("Calcola codice fiscale");
        via_piazza = new TextField("Via/Piazza");
        email = new EmailField("Email");
        username = new TextField("Username");
        password = new PasswordField("Password");
        confirmPassword = new PasswordField("Conferma Password");
        //registrazione.setColspan(via_piazza,2);
        //registrazione.setColspan(luogoNascita,2);
        try {
            CSVReader reader = new CSVReader(new FileReader("src/main/resources/META-INF/resources/data/Comuni.csv"));
            ICommonsList<ICommonsList<String>> data = reader.readAll();
            List<String> items = new ArrayList<>();
            for (ICommonsList<String> row : data) {
                items.add(row.get(0));
            }
            luogoNascita.setItems(items);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

            registrazione.add(nome, cognome, dataNascita, sesso,
                          luogoNascita, via_piazza, codFiscale,
                          calcolaCf, email, username,
                          password,confirmPassword);
    }

    private void configureButton() {
        regButtonLayout = new VerticalLayout();
        regButtonLayout.setAlignItems(Alignment.CENTER);
        registerButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        registerButton.setIcon(VaadinIcon.USERS.create());
        registerButton.setAutofocus(true);
        registerButton.setWidth("420px");
        regButtonLayout.add(registerButton);
    }

    private void configurePageLayout() {
        pageLayout = new VerticalLayout();
        pageLayout.setSizeFull();
        pageLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        pageLayout.add(registrazione, registerButton);
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
        luogoNascita.setRequired(true);
        dataNascita.setRequired(true);
        codFiscale.setMaxLength(16);
        codFiscale.setRequired(true);
        via_piazza.setRequired(true);
        email.setRequired(true);
        username.setRequired(true);
        password.setRequired(true);
        confirmPassword.setRequired(true);

    }

    private void calcolaCodFiscale() {
        if(nome != null && cognome != null && luogoNascita != null && dataNascita != null && sesso != null) {
            // TODO: gestire valori nulli (lato form e/o backend)
            String nomeCf = nome.getValue();
            String cognomeCf = cognome.getValue();
            String luogoNascitaCf = luogoNascita.getValue();
            int meseCf = dataNascita.getValue().getMonthValue();
            int annoCf = dataNascita.getValue().getYear();
            int giornoCf = dataNascita.getValue().getDayOfMonth();
            String sessoCf = sesso.getValue();
            codFiscale.setValue(CodiceFiscale.codiceFiscale(cognomeCf, nomeCf, giornoCf,
                                                            meseCf, annoCf, sessoCf, luogoNascitaCf));
        }
    }

    private void registration() {

    }
}
