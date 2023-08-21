package emotionalsongs.components.appnav;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.internal.StateTree;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.router.Router;
import com.vaadin.flow.server.VaadinService;
import java.util.Optional;

/**
 * * @author Acquati Luca
 * @author Jamil Muhammad Qasim
 * @author Naturale Lorenzo
 * @author Volonterio Luca
 * <p>
 * Classe che rappresenta un'elemento per il menù di navigazione {@link AppNav}.
 * Può contenere un'etichetta con un'icona e un link alla vista desiderata.
 */

@JsModule("@vaadin-component-factory/vcf-nav")
@Tag("vcf-nav-item")
public class AppNavItem extends Component {

    /**
     * Crea un elemento del menù che mostra solo l'etichetta senza collegare a nessuna vista
     * @param label l'etichetta per l'elemento
     */
    public AppNavItem(String label) {
        setLabel(label);
    }

    /**
     * Crea un nuovo elemento del menù con l'etichetta data collegandolo ad un link.
     * @param label l'etichetta per l'elemento.
     * @param path il percorso collegato all'etichetta.
     */
    public AppNavItem(String label, String path) {
        setPath(path);
        setLabel(label);
    }

    /**
     * Crea un nuovo elemento del menù con l'etichetta data collegandolo ad una vista.
     *
     * @param label L'etichetta per l'elemento.
     * @param view  La vista a cui collegarsi.
     */
    public AppNavItem(String label, Class<? extends Component> view) {
        setPath(view);
        setLabel(label);
    }

    /**
     * Crea un nuovo elemento del menù con l'etichetta data e l'icona che collega alla path data.
     * 
     * @param label L'etichetta per l'elemento.
     * @param path  La path a cui collegarsi.
     * @param icon  L'icona per l'elemento.
     */
    public AppNavItem(String label, String path, Component icon) {
        setPath(path);
        setLabel(label);
        setIcon(icon);
    }

    /**
     * Crea un nuovo elemento del menù utilizzando l'etichetta data che collega alla vista data.
     * 
     * @param label L'etichetta per l'elemento.
     * @param view La vista a cui collegarsi.
     * @param icon L'icona per l'elemento.
     */
    public AppNavItem(String label, Class<? extends Component> view, Component icon) {
        setPath(view);
        setLabel(label);
        setIcon(icon);
    }

    /**
     * Removes the given menu item from this item.
     * <p>
     * If the given menu item is not a child of this menu item, does nothing.
     * 
     * @param appNavItem
     *            the menu item to remove
     * @return L'oggetto AppNavItem per supportare la concatenazione dei metodi.
     */
    public AppNavItem removeItem(AppNavItem appNavItem) {
        Optional<Component> parent = appNavItem.getParent();
        if (parent.isPresent() && parent.get() == this) {
            getElement().removeChild(appNavItem.getElement());
        }

        return this;
    }

    /**
     * Ottine l'etichetta per l'elemento.
     * 
     * @return L'etichetta oppure {@code null} se l'etichetta non è ancora satta impostata.
     */
    public String getLabel() {
        return getExistingLabelElement().map(Element::getText).orElse(null);
    }

    /**
     * Imposta un'etichetta testuale per l'elemento del menù.
     * @param label L'etichetta da impostare.
     * @return L'oggetto AppNavItem per supportare la concatenazione dei metodi.
     */
    public AppNavItem setLabel(String label) {
        getLabelElement().setText(label);
        return this;
    }

    /**
     * Metodo privato che ottiene un elemento di tipo label esistente all'interno dell'elemento corrente.
     * @return Un oggetto Optional contenente l'elemento label, se presente.
     */
    private Optional<Element> getExistingLabelElement() {
        return getElement().getChildren().filter(child -> !child.hasAttribute("slot")).findFirst();
    }

    /**
     * Metodo privato che permette di ottenere un'elemento di tipo label
     * @return Un oggetto Optional contenente l'elemento label, se presente.
     */
    private Element getLabelElement() {
        return getExistingLabelElement().orElseGet(() -> {
            Element element = Element.createText("");
            getElement().appendChild(element);
            return element;
        });
    }

    /**
     * Imposta la path collegata da collegare all'elemento.
     * 
     * @param path La path a cui collegarsi.
     * @return L'oggetto AppNavItem per supportare la concatenazione dei metodi.
     */
    public AppNavItem setPath(String path) {
        getElement().setAttribute("path", path);
        return this;
    }

    /**
     * Imposta la vista a cui l'elemento deve rimandare.
     * 
     * @param view La vista a cui collegarsi.
     * @return L'oggetto AppNavItem per supportare la concatenazione dei metodi.
     */
    public AppNavItem setPath(Class<? extends Component> view) {
        String url = RouteConfiguration.forRegistry(getRouter().getRegistry()).getUrl(view);
        setPath(url);
        return this;
    }

    /**
     * Ottiene un'istanza del Router per la navigazione.
     *
     * @return L'istanza del Router per la navigazione.
     * @throws IllegalStateException se nessuna istanza del Router è disponibile.
     */
    private Router getRouter() {
        Router router = null;
        if (getElement().getNode().isAttached()) {
            StateTree tree = (StateTree) getElement().getNode().getOwner();
            router = tree.getUI().getInternals().getRouter();
        }
        if (router == null) {
            router = VaadinService.getCurrent().getRouter();
        }
        if (router == null) {
            throw new IllegalStateException("Implicit router instance is not available. "
                    + "Use overloaded method with explicit router parameter.");
        }
        return router;
    }

    /**
     * Ottiene la path collegata all'elemento
     * @return La path collegata all'elemento come stringa.
     */
    public String getPath() {
        return getElement().getAttribute("path");
    }

    /**
     * Ottiene l'indice dell'elemento dell'icona all'interno del componente.
     *
     * @return L'indice dell'elemento dell'icona, o -1 se non è presente.
     */
    private int getIconElementIndex() {
        for (int i = 0; i < getElement().getChildCount(); i++) {
            if ("prefix".equals(getElement().getChild(i).getAttribute("slot"))) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Imposta l'icona per l'elemento del menù.
     * @param icon L'icona da mostrare.
     * @return this instance for chaining
     */
    public AppNavItem setIcon(Component icon) {
        icon.getElement().setAttribute("slot", "prefix");
        int iconElementIndex = getIconElementIndex();
        if (iconElementIndex != -1) {
            getElement().setChild(iconElementIndex, icon.getElement());
        } else {
            getElement().appendChild(icon.getElement());
        }
        return this;
    }

    /**
     * Imposta lo stato di espansione dell'elemento.
     *
     * @param value True per espandere l'elemento, false per chiuderlo.
     * @return L'oggetto AppNavItem per supportare la concatenazione dei metodi.
     */
    public AppNavItem setExpanded(boolean value) {
        if (value) {
            getElement().setAttribute("expanded", "");
        } else {
            getElement().removeAttribute("expanded");
        }
        return this;
    }

}
