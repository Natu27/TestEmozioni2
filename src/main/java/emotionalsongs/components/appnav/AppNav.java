package emotionalsongs.components.appnav;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.dom.Element;
import java.util.Optional;

/**
 * Questa classe rappresenta il componente di navigazione AppNav.
 * È un componente Vaadin che offre funzionalità di navigazione avanzate.
 * Utilizza il modulo JavaScript "@vaadin-component-factory/vcf-nav".
 *
 * @see <a href="https://www.npmjs.com/package/@vaadin-component-factory/vcf-nav">Documentazione del modulo JavaScript</a>
 */

@JsModule("@vaadin-component-factory/vcf-nav")
@Tag("vcf-nav")
@NpmPackage(value = "@vaadin-component-factory/vcf-nav", version = "1.0.6")
public class AppNav extends Component implements HasSize, HasStyle {

    /**
     * Crea un nuovo menù senza nessuna etichetta.
     */
    public AppNav() {
    }

    /**
     * Crea un nuovo menù con un'etichetta.
     * @param label L'etichetta da utilizzare.
     */
    public AppNav(String label) {
        setLabel(label);
    }

    /**
     * Permette di aggiungere elementi al menù.
     *
     * @param appNavItems Gli elementi da aggiungere al menù.
     */
    public void addItem(AppNavItem... appNavItems) {
        for (AppNavItem appNavItem : appNavItems) {
            getElement().appendChild(appNavItem.getElement());
        }

    }

    /**
     * Permette di rimuovere gli elementi dal menù.
     * @param appNavItem Gli elementi del menù da rimuovere.
     * @return Il menù senza gli elementi rimossi.
     */
    public AppNav removeItem(AppNavItem appNavItem) {
        Optional<Component> parent = appNavItem.getParent();
        if (parent.isPresent() && parent.get() == this) {
            getElement().removeChild(appNavItem.getElement());
        }

        return this;
    }

    /**
     * Ottiene l'etichetta testuale per la navigazione.
     *
     * @return L'etichetta oppure {@code null} se l'etichetta non è ancora stata impostata.
     */
    public String getLabel() {
        return getExistingLabelElement().map(Element::getText).orElse(null);
    }

    /**
     * Imposta l'etichetta testuale per la navigazione.
     * @param label L'eticehtta da impostare.
     * @return l'etichetta in formato testuale.
     */
    public AppNav setLabel(String label) {
        getLabelElement().setText(label);
        return this;
    }

    private Optional<Element> getExistingLabelElement() {
        return getElement().getChildren().filter(child -> "label".equals(child.getAttribute("slot"))).findFirst();
    }

    private Element getLabelElement() {
        return getExistingLabelElement().orElseGet(() -> {
            Element element = new Element("span");
            element.setAttribute("slot", "label");
            getElement().appendChild(element);
            return element;
        });
    }

    /**
     * Check if the end user is allowed to collapse/hide and expand/show the
     * navigation items.
     * <p>
     * NOTE: The navigation has to have a label for it to be collapsible.
     *
     * @return true if the menu is collapsible, false otherwise
     */
    public boolean isCollapsible() {
        return getElement().hasAttribute("collapsible");
    }

    /**
     * Allow the end user to collapse/hide and expand/show the navigation items.
     * <p>
     * NOTE: The navigation has to have a label for it to be collapsible.
     *
     * @param ignoredCollapsible
     *            true to make the whole navigation component collapsible, false
     *            otherwise
     * @return this instance for chaining
     */
    public AppNav setCollapsible(boolean ignoredCollapsible) {
        getElement().setAttribute("collapsible", "");
        return this;
    }

}
