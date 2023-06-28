package emotionalsongs.views;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.VaadinSession;
import emotionalsongs.backend.ClientES;
import emotionalsongs.backend.Servizi;
import emotionalsongs.backend.entities.Canzone;
import emotionalsongs.backend.entities.Emozioni;
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
import java.rmi.RemoteException;


public class HistogramView extends VerticalLayout {

    Object canzone = VaadinSession.getCurrent().getAttribute("canzoneselezionata");
    ClientES clientES = new ClientES();
    Servizi stub = clientES.getStub();

    public HistogramView() throws Exception {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        fillDatasetWithAverageEmotions(dataset);

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
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ChartUtils.writeChartAsPNG(outputStream, chart, 800, 450);
            chartImageBytes = outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        Div chartContainer = new Div();
        chartContainer.getStyle().set("width", "800px");
        chartContainer.getStyle().set("height", "450px");
        chartContainer.getStyle().set("background-image", "url(data:image/png;base64," + java.util.Base64.getEncoder().encodeToString(chartImageBytes) + ")");
        setAlignSelf(Alignment.CENTER, chartContainer);

        add(chartContainer);
    }

    private void fillDatasetWithAverageEmotions(DefaultCategoryDataset dataset) throws RemoteException {
        int songID = ((Canzone) canzone).getId();
        Emozioni e = stub.getAverageSongEmotions(songID);
        if (e != null) {
            dataset.addValue(e.getAmazement(), "Amazement", " ");
            dataset.addValue(e.getSolemnity(), "Solemnity", " ");
            dataset.addValue(e.getTenderness(), "Tenderness", " ");
            dataset.addValue(e.getNostalgia(), "Nostalgia", " ");
            dataset.addValue(e.getCalmness(), "Calmness", " ");
            dataset.addValue(e.getPower(), "Power", " ");
            dataset.addValue(e.getJoy(), "Joy", " ");
            dataset.addValue(e.getTension(), "Tension", " ");
            dataset.addValue(e.getSadness(), "Sadness", " ");
        } else {
            // TODO: messaggio che non ci sono votazioni per la canzone
        }

    }
}
