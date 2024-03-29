package emotionalsongs.views;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import emotionalsongs.backend.ClientES;
import emotionalsongs.backend.entities.Emozione;
import emotionalsongs.backend.exceptions.emozioni.NoVotazioni;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * @author Acquati Luca
 * @author Jamil Muhammad Qasim
 * @author Naturale Lorenzo
 * @author Volonterio Luca
 * <p></p>
 * Classe che rappresenta la vista per la creazione dell'istogramma della media delle emozioni.
 * @version 1.0
 */
public class HistogramView extends VerticalLayout {
    ClientES client = ClientES.getInstance();

    /**
     * Costruttore della vista {@code HistogramView}
     * @param idSong L'ID della canzone di cui si vogliono visualizzare le emozioni.
     * @throws Exception Per possibili eccezioni durante la costruzione della vista.
     */
    public HistogramView(int idSong) throws Exception {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        try {
            fillDatasetWithAverageEmotions(dataset, idSong);
        } catch (NoVotazioni e) {
            this.setVisible(false);
            throw new NoVotazioni();
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Distribuzione Emozioni",
                "Emozioni",
                "Media",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setRange(0, 5.5);

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setMaximumBarWidth(0.05);

        LegendTitle legend = chart.getLegend();
        Font font = legend.getItemFont(); legend.setItemFont(font.deriveFont(14f));

        TextTitle title = chart.getTitle();
        Font titleFont = title.getFont();
        title.setFont(titleFont.deriveFont(18f));

        byte[] chartImageBytes;
        /*Converte il grafico creato con la libreris JFreeChart in immagine
        per visualizzarlo utilizzando un componente Vaadin*/
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ChartUtils.writeChartAsPNG(outputStream, chart, 800, 450);
            chartImageBytes = outputStream.toByteArray();
        } catch (IOException e) {
            Notification.show("Impossibile effettuare l'operazione", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        Div chartContainer = new Div();
        chartContainer.getStyle().set("width", "800px");
        chartContainer.getStyle().set("height", "450px");
        chartContainer.getStyle().set("background-image", "url(data:image/png;base64," + java.util.Base64.getEncoder().encodeToString(chartImageBytes) + ")");
        setAlignSelf(Alignment.CENTER, chartContainer);

        add(chartContainer);
    }

    /**
     * Riempie il dataset per la creazione del grafico con le medie delle emozioni per la canzone specificata.
     *
     * @param dataset Il dataset in cui inserire le medie delle emozioni.
     * @param idSong  L'ID della canzone per cui ottenere le medie delle emozioni.
     * @throws SQLException Eccezione sollevata in caso di errore nell'accesso al database.
     * @throws NoVotazioni Eccezione sollevata se non sono disponibili votazioni per la canzone.
     */
    private void fillDatasetWithAverageEmotions(DefaultCategoryDataset dataset, int idSong) throws SQLException, NoVotazioni {
        List<Emozione> emozioni = client.getVotazioniMedie(idSong);
        for(Emozione e : emozioni) {
            dataset.addValue(e.getScore(), e.getName(), " ");
        }
    }
}
