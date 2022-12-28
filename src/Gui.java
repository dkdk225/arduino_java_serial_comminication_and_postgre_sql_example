import org.jfree.chart.ChartPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.time.LocalDateTime;

public class Gui{
    private SerialConnection serial;
    private DegreeChart chart;
    private JFrame frame;
    private ChartPanel panel;

    public void startGui(){
        //init chart
        this.chart = new DegreeChart("Degree over time", "degree", "time");
        //init serial connection
        this.serial = new SerialConnection(Config.PORT_NAME); // get COM3 from config
        //init frame
        this.frame = new JFrame("Final Assignment");
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setLayout(new FlowLayout());
        this.frame.setSize(Config.FRAME_WIDTH,Config.FRAME_HEIGHT); // get "width" and "height" from config
        try {
            this.serial.listenToPortForDegree(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //add buttons
        for (int i = 0; i < 4; i++){
            Button button = new Button("Led "+String.valueOf(i),i);
            this.frame.getContentPane().add(button);
        }
        //add chart panel
        this.addChartPanel(Config.CHART_WIDTH,Config.CHART_HEIGHT); // get "width" and "height" of chart pane from config
        this.frame.setVisible(true);
        this.frame = frame;
    }

    private void addChartPanel(int width, int height){
        this.panel = chart.generatePanel(width,height);
        this.frame.add(this.panel);
    }

    private void removeChartPanel(){
        this.frame.remove(this.panel);
    }

    private void addChartDegreeData(int degree, String series, LocalDateTime measurementTime){
        this.chart.addDegree(degree, series, measurementTime);
    }

    private void reRenderChartPanel(int width, int height) {
        this.removeChartPanel();
        this.addChartPanel(width, height);
        this.frame.revalidate();
        this.frame.validate();
        this.frame.repaint();
    }
    public void expandChartPanel(int degree, String series, LocalDateTime measurementTime){
        this.addChartDegreeData(degree, series, measurementTime);
        this.reRenderChartPanel(Config.CHART_WIDTH, Config.CHART_HEIGHT);
    }

    class Button extends JButton implements ActionListener{
        private Integer id;
        Button(String text, int id){
            super(text);
            this.id = id;
            this.addActionListener(this);
        }
        public int getId () {
            return this.id;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("button has been clicked "+this.id);
            try {
                Gui.this.serial.sendByte(this.id.byteValue());

            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
