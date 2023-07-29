package emotionalsongs.views;


import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.LumoUtility;
import emotionalsongs.backend.ClientES;
import emotionalsongs.backend.DatabaseConnection;
import emotionalsongs.backend.Servizi;
import emotionalsongs.backend.entities.Utente;
import emotionalsongs.backend.exceptions.utente.PasswordErrata;
import emotionalsongs.backend.exceptions.utente.UsernameErrato;
import emotionalsongs.components.appnav.AppNav;
import emotionalsongs.components.appnav.AppNavItem;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.rmi.RemoteException;


/**
 * The main view is a top-level placeholder for other views.
 */
public class MainLayout extends AppLayout {
    private H2 viewTitle;
    HorizontalLayout top;
    VerticalLayout loginForm;
    Button login;
    Button logout;
    Button registerButton;
    Button loginButton;
    Button exitButton;
    Label welcome = new Label(" ");
    Dialog dialog;
    ClientES clientES = new ClientES();
    TextField user;
    PasswordField password;
    Utente utente = (Utente) VaadinSession.getCurrent().getAttribute("utente");
    String currentPage;

    public MainLayout() throws Exception {

        configureTopLayout();
        configureLoginForm();

        if (utente!=null) {
            login.setVisible(false);
            logout.setVisible(true);
            welcome.setVisible(true);
            welcome.setHeightFull();
            welcome.addClassNames("custom-label");
            welcome.setText("Ciao, " + utente.getNome());
        }

        login.addClickListener(click -> dialog.setOpened(true));

        dialog = new Dialog(loginForm);
            loginButton.addClickListener(click -> {
                try {
                    login();
                } catch (UsernameErrato e) {
                    Notification.show("Username Errato", 3000, Notification.Position.MIDDLE)
                                .addThemeVariants(NotificationVariant.LUMO_ERROR);
                } catch (PasswordErrata e) {
                    Notification.show("Password Errata", 3000, Notification.Position.MIDDLE)
                                .addThemeVariants(NotificationVariant.LUMO_ERROR);
                } catch (RemoteException e) {
                    Notification.show("Impossibile effettuare l'operazione", 3000, Notification.Position.MIDDLE)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
            });
            registerButton.addClickListener(click -> register());
            exitButton.addClickListener(click -> dialog.close());
            dialog.setCloseOnEsc(true);
            dialog.setWidth("500px");
            dialog.setHeight("475x");

            logout.addClickListener(click -> logout());

        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
        addToNavbar(top);

    }

    private void configureTopLayout() {
        login = new Button("Login", VaadinIcon.USER.create());
        login.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        logout = new Button("Logout", VaadinIcon.ARROW_FORWARD.create());
        logout.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        logout.setVisible(false);//
        welcome.setVisible(false);
        top = new HorizontalLayout(login,welcome,logout);
        top.setWidthFull();
        top.setMargin(true);
        top.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        top.setAlignSelf(FlexComponent.Alignment.END, welcome);
        top.setAlignSelf(FlexComponent.Alignment.END, login);
        top.setAlignSelf(FlexComponent.Alignment.END, logout);
        top.add(login, welcome, logout);
    }

    private void configureLoginForm() {
        H1 titleLogin = new H1("Login \uD83D\uDC64");
        user = new TextField();
        password = new PasswordField();
        user.setWidthFull();
        user.setLabel("Username");
        password.setWidthFull();
        password.setLabel("Password");
        user.setRequired(true);
        password.setRequired(true);
        loginButton = new Button("Accedi", VaadinIcon.USER.create());
        loginButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        loginButton.setWidthFull();
        registerButton = new Button("Registrati", VaadinIcon.USERS.create());
        registerButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        registerButton.setWidthFull();
        exitButton = new Button("Chiudi",VaadinIcon.CLOSE_CIRCLE.create());
        exitButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        loginForm = new VerticalLayout(titleLogin, user, password, loginButton, registerButton, exitButton);
        loginForm.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        loginForm.setAlignItems(FlexComponent.Alignment.CENTER);
        //exitButton.getStyle().set("color", "red");
    }

    private void login() throws PasswordErrata, UsernameErrato, RemoteException {
        utente = clientES.login(user.getValue(), password.getValue());
        if(!user.getValue().equals("") && !password.getValue().equals("")) {
            clientES.login(user.getValue(), password.getValue());
            dialog.close();
            //Memorizza l'utente che ha effettuato login
            VaadinSession.getCurrent().setAttribute("username", user.getValue());
            VaadinSession.getCurrent().setAttribute("utente", utente);
            login.setVisible(false);
            logout.setVisible(true);
            welcome.setVisible(true);
            welcome.setHeightFull();
            welcome.addClassNames("custom-label");
            welcome.setText("Ciao, " + utente.getNome());
            currentPage = getCurrentPageTitle();
            if(currentPage.equals("Playlist")) {
                UI.getCurrent().navigate(RicercaView.class);
                UI.getCurrent().navigate(MyPlaylistView.class);
            }
        }else {
            Notification.show("Dati Mancanti", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }

    }

    private void logout(){
        VaadinSession.getCurrent().getSession().invalidate();
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
        H1 appName = new H1("EmotionalSongs");
        Image logo = new Image("images/EmSongs.png", "EmoSong logo");
        logo.setWidth("40px");
        logo.setHeight("40px");
        appName.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);
        Header header = new Header(logo, appName);

        Scroller scroller = new Scroller(createNavigation());

        addToDrawer(header, scroller, new Footer());
    }

    private AppNav createNavigation() {
        // AppNav is not yet an official component.
        // For documentation, visit https://github.com/vaadin/vcf-nav#readme
        AppNav nav = new AppNav();

        nav.addItem(new AppNavItem("Ricerca", RicercaView.class, LineAwesomeIcon.SEARCH_SOLID.create()));
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
