package emotionalsongs.views;


import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import emotionalsongs.backend.ClientES;
import emotionalsongs.backend.Servizi;
import emotionalsongs.backend.exceptions.Utente.PasswordErrata;
import emotionalsongs.backend.exceptions.Utente.UsernameErrato;
import emotionalsongs.components.appnav.AppNav;
import emotionalsongs.components.appnav.AppNavItem;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.vaadin.lineawesome.LineAwesomeIcon;
import com.vaadin.flow.component.button.Button;

import java.rmi.RemoteException;

/**
 * The main view is a top-level placeholder for other views.
 */
public class MainLayout extends AppLayout {
    private H2 viewTitle;
    HorizontalLayout top;
    VerticalLayout loginForm;
    Button login;
    Button registerButton;
    Button loginButton;
    Button exitButton;
    Dialog dialog;
    ClientES clientES = new ClientES();
    Servizi stub = clientES.getStub();
    TextField user;
    PasswordField password;
    public MainLayout() throws Exception {

        configureTopLayout();
        configureLoginForm();

        login.addClickListener(click -> dialog.setOpened(true));

        dialog = new Dialog(loginForm);
            loginButton.addClickListener(click -> {
                try {
                    login();
                } catch (PasswordErrata e) {
                    Notification.show("Username Errato", 3000, Notification.Position.MIDDLE)
                                .addThemeVariants(NotificationVariant.LUMO_ERROR);
                } catch (UsernameErrato e) {
                    Notification.show("Password Errata", 3000, Notification.Position.MIDDLE)
                                .addThemeVariants(NotificationVariant.LUMO_ERROR);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            });
            registerButton.addClickListener(click -> register());
            exitButton.addClickListener(click -> dialog.close());
            dialog.setCloseOnEsc(true);

        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
        addToNavbar(top);

    }

    private void configureTopLayout() {
        login = new Button("Login", VaadinIcon.USER.create());
        login.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        top = new HorizontalLayout(login);
        top.setWidthFull();
        top.setMargin(true);
        top.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        top.setAlignSelf(FlexComponent.Alignment.END, login);
        top.add(login);
    }

    private void configureLoginForm() {
        H1 titleLogin = new H1("Login");
        user = new TextField();
        password = new PasswordField();
        user.setWidthFull();
        password.setWidthFull();
        user.setRequired(true);
        password.setRequired(true);
        loginButton = new Button("Accedi", VaadinIcon.USER.create());
        loginButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        loginButton.setWidthFull();
        registerButton = new Button("Registrati", VaadinIcon.USERS.create());
        registerButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        registerButton.setWidthFull();
        exitButton = new Button("Chiudi",VaadinIcon.CLOSE_CIRCLE.create());
        exitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        loginForm = new VerticalLayout(titleLogin, user, password, loginButton, registerButton, exitButton);
        loginForm.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        loginForm.setAlignItems(FlexComponent.Alignment.CENTER);
        //exitButton.getStyle().set("color", "red");
    }

    private void login() throws PasswordErrata, UsernameErrato, RemoteException {
        stub.login(user.getValue(), password.getValue());
    }

    private void register() {
        UI.getCurrent().navigate(RegistrazioneView.class);
        dialog.close();
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.getElement().setAttribute("aria-label", "Menu toggle");

        viewTitle = new H2();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        addToNavbar(true, toggle, viewTitle);
    }

    private void addDrawerContent() {
        H1 appName = new H1("EmotionalSongs \uD83C\uDFBC");
        appName.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);
        Header header = new Header(appName);

        Scroller scroller = new Scroller(createNavigation());

        addToDrawer(header, scroller, new Footer());
    }

    private AppNav createNavigation() {
        // AppNav is not yet an official component.
        // For documentation, visit https://github.com/vaadin/vcf-nav#readme
        AppNav nav = new AppNav();

        nav.addItem(new AppNavItem("Ricerca", RicercaTitoloView.class, LineAwesomeIcon.SEARCH_SOLID.create()));
        nav.addItem(new AppNavItem("My Playlist", MyPlaylistView.class, LineAwesomeIcon.MUSIC_SOLID.create()));

        return nav;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }
}
