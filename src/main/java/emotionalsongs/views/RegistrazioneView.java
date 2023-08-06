package emotionalsongs.views;

import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.csv.CSVReader;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
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
import emotionalsongs.backend.ClientES;
import me.matteomerola.codicefiscale.FiscalCodeCalculator;
import me.matteomerola.codicefiscale.exceptions.NotSuchCityException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
    List<Character> scelteSesso;
    ComboBox<Character> sesso;
    ComboBox<String> luogoNascita;
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
    ClientES client = ClientES.getInstance();

    public RegistrazioneView() throws Exception {
        setSpacing(false);
        setSizeFull();

        registerButton = new Button("Registrati", buttonClickEvent -> {
            try {
                registration();
            } catch (RemoteException e) {
                Notification.show("Impossibile effettuare l'operazione", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

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
        scelteSesso = Arrays.asList('M', 'F');
        sesso = new ComboBox<>("Sesso", scelteSesso);
        codFiscale = new TextField("Codice Fiscale");
        //calcolaCf = new Button("Calcola codice fiscale");
        via_piazza = new TextField("Via/Piazza");
        email = new EmailField("Email");
        username = new TextField("Username");
        password = new PasswordField("Password");
        confirmPassword = new PasswordField("Conferma Password");
        try {
            String fileName = "META-INF/resources/data/ComuniECodici.csv";
            InputStream is = this.getClass().getClassLoader().getResourceAsStream(fileName);
            assert is != null;
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            CSVReader reader = new CSVReader(br);
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
        nome.setPattern("^[a-zA-Z\\s]*$");
        nome.setErrorMessage("Non sono ammessi numeri e/o caratteri speciali, solo lettere e spazi");
        cognome.setRequired(true);
        cognome.setPattern("^[a-zA-Z\\s]*$");
        cognome.setErrorMessage("Non sono ammessi numeri e/o caratteri speciali, solo lettere e spazi");
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
        if(!nome.getValue().equals("") && !cognome.getValue().equals("") && luogoNascita.getValue() != null && dataNascita.getValue() != null && sesso.getValue() != null) {
            String nomeCf = nome.getValue();
            String cognomeCf = cognome.getValue();
            String luogoNascitaCf = luogoNascita.getValue();
            LocalDate selectedDate = dataNascita.getValue();
            Date date = Date.from(selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            Character sessoCf = sesso.getValue();

            FiscalCodeCalculator fiscalCodeCalculator = new FiscalCodeCalculator();
            try {
                codFiscale.setValue(fiscalCodeCalculator.calculateFC(nomeCf, cognomeCf, sessoCf, date, luogoNascitaCf));
            } catch (NotSuchCityException e) {
                Notification.show("Città Inesistente", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        } else {
            Notification.show("Dati Mancanti", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void registration() throws RemoteException {
        String nome = this.nome.getValue();
        String cognome = this.cognome.getValue();
        String user = username.getValue();
        String password = this.password.getValue();
        String confPassword = this.confirmPassword.getValue();
        String indirizzo = this.via_piazza.getValue();
        String codFiscale = this.codFiscale.getValue();
        String email = this.email.getValue();
        if(nome.isEmpty() || cognome.isEmpty() || dataNascita.isEmpty() || sesso.isEmpty() || luogoNascita.isEmpty() || via_piazza.isEmpty() ||codFiscale.isEmpty() || username.isEmpty() || email.isEmpty()
                || password.isEmpty() || confirmPassword.isEmpty()){
            Notification.show("Dati Mancanti", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }else {
            if (password.equals(confPassword)) {
                client.registrazione(nome, cognome, indirizzo, codFiscale, email, user, password);
                Notification.show("Registrazione effettuata", 4000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                UI.getCurrent().navigate(RicercaView.class);
            } else {
                Notification.show("Le password non coincidono", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        }
    }
}
