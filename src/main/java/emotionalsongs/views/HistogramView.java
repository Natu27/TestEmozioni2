package emotionalsongs.views;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.VaadinSession;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.Axis;
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


public class HistogramView extends VerticalLayout {

    Object canzone = VaadinSession.getCurrent().getAttribute("canzoneselezionata");

    public HistogramView(){
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(1, "Amazement", " ");
        dataset.addValue(1, "Solemnity", " ");
        dataset.addValue(1, "Tenderness", " ");
        dataset.addValue(2, "Nostalgia", " ");
        dataset.addValue(2, "Calmness", " ");
        dataset.addValue(3, "Power", " ");
        dataset.addValue(4, "Joy", " ");
        dataset.addValue(5, "Tension", " ");
        dataset.addValue(5, "Sadness", " ");

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
            ChartUtils.writeChartAsPNG(outputStream, chart, 1000, 700);
            chartImageBytes = outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        Div chartContainer = new Div();
        chartContainer.getStyle().set("width", "1000px");
        chartContainer.getStyle().set("height", "700px");
        chartContainer.getStyle().set("background-image", "url(data:image/png;base64," + java.util.Base64.getEncoder().encodeToString(chartImageBytes) + ")");
        setAlignSelf(Alignment.CENTER, chartContainer);

        add(chartContainer);
    }
}
