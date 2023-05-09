package emotionalsongs.views.registrazione;

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

@PageTitle("Registrazione")
@Route(value = "registrazione", layout = MainLayout.class)
public class RegistrazioneView extends VerticalLayout {

    public RegistrazioneView() {
        HorizontalLayout titleLayout = new HorizontalLayout();
        titleLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        Icon icon = new Icon(VaadinIcon.USERS);
        icon.setColor("#006af5");
        H3 label = new H3("Nuovo Utente");
        titleLayout.add(icon, label);
        setSpacing(false);

        FormLayout formLayout = new FormLayout();
        TextField nome = new TextField("Nome");
        TextField cognome = new TextField("Cognome");
        TextField codFiscale = new TextField("Codice Fiscale");
        TextField via_piazza = new TextField("Via/Piazza");
        EmailField email = new EmailField("Email");
        TextField username = new TextField("Username");
        PasswordField password = new PasswordField("Password");
        PasswordField confirmPassword = new PasswordField("Conferma Password");

        nome.setSuffixComponent(VaadinIcon.USER.create());
        cognome.setSuffixComponent(VaadinIcon.USER.create());
        codFiscale.setSuffixComponent(VaadinIcon.BARCODE.create());
        via_piazza.setSuffixComponent(VaadinIcon.HOME.create());
        email.setSuffixComponent(VaadinIcon.MAILBOX.create());
        username.setSuffixComponent(VaadinIcon.USER.create());

        formLayout.add(nome, cognome, codFiscale, via_piazza, email, username, password, confirmPassword);

        VerticalLayout buttonLayout = new VerticalLayout();
        Button submitButton = new Button("Iscriviti alla Community");
        buttonLayout.setAlignItems(Alignment.CENTER);
        submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        submitButton.setIcon(VaadinIcon.USERS.create());
        submitButton.setAutofocus(true);
        submitButton.setWidth("420px");
        buttonLayout.add(submitButton);

        VerticalLayout pageLayout = new VerticalLayout();
        pageLayout.setSizeFull();
        pageLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        pageLayout.add(formLayout, buttonLayout);

        add(titleLayout, pageLayout);

        setSizeFull();
    }

}
