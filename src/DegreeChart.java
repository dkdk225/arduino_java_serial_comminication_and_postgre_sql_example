import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DegreeChart{

    private DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    private String chartTitle;
    private String xAxisLabel;
    private String yAxisLabel;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    public DegreeChart(String chartTitle, String yAxisLabel, String xAxisLabel) {
        this.chartTitle = chartTitle;
        this.yAxisLabel = yAxisLabel;
        this.xAxisLabel = xAxisLabel;
    }

    private JFreeChart generateChart(){
        JFreeChart chart = ChartFactory.createLineChart(
                this.chartTitle, // Chart title
                this.xAxisLabel, // X-Axis Label
                this.yAxisLabel, // Y-Axis Label
                this.dataset
        );
        return chart;
    }
    public ChartPanel generatePanel(int width, int height){
        JFreeChart chart = this.generateChart();
        ChartPanel panel = new ChartPanel(chart);
        panel.setPreferredSize(new Dimension(width, height));
        panel.setBackground(Color.white);
        return panel;
    }

    public void addDegree(int degree, String series, LocalDateTime measurementTime){
        String columnKey = measurementTime.format(this.dateFormatter);
        this.dataset.addValue(degree, series, columnKey);
    }

}