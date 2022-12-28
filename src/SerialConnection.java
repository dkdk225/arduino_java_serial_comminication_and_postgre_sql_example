import com.fazecast.jSerialComm.SerialPort;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SerialConnection{
    private String port;
    private LocalDateTime connectionStart;
    private SerialPort sp;
    SerialConnection(String port){
        this.port = port;
        this.init();
        openPort();
    }
    private void init(){
        this.sp = SerialPort.getCommPort(this.port);
        this.sp.setComPortParameters(9600, 8, 1, 0);
        this.sp.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING, 0, 0);
    }

    public void openPort(){
        if (this.sp.openPort()) {
            this.connectionStart = LocalDateTime.now();
            System.out.println("port opened");
        }else {
            System.out.println("port is not opened");
            return;
        }
    }

    public void closePort() {
        if (this.sp.closePort()) {
            System.out.println("port closed");
        }else{
            System.out.println("port is not closed");
        }
    }

    public void sendByte(byte data) throws IOException{
        SendByte thread = new SendByte(data);
        thread.start();

    }

    public void listenToPortForDegree(Gui caller)  throws IOException{
        ListenToPortForDegree thread = new ListenToPortForDegree(caller);
        thread.start();
    }

    public class SendByte extends Thread{
        byte data;
        SendByte(byte data){
            this.data = data;
        }
        @Override
        public void run() {
            try {
                SerialConnection.this.sp.getOutputStream().write(this.data);
                SerialConnection.this.sp.getOutputStream().flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.out.println("sent number " + data);
        }
    }

    public class ListenToPortForDegree extends Thread{
        private Gui caller;
        public ListenToPortForDegree(Gui caller){
            this.caller = caller;
        }

        @Override
        public void run() {
            SerialConnection.this.sp.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
            InputStream in = SerialConnection.this.sp.getInputStream();
            try
            {
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                String data = "";
                //regex pattern to get degree and time (millis) data
                String pattern = "obtained degree: (-?[0-9]+) and time: (-?[0-9]+)";

                Pattern p = Pattern.compile(pattern);
                //constantly listen to arduino
                while(true) {
                    int line = 0;

                    //read incoming lines of strings
                    while (line == 0){
                        char incoming = (char) in.read();
                        data += incoming;
                        if (incoming == '\n') {
                            line++;
                        };
                    }
                    System.out.print(data);
                    //REGEX incoming line for degree and time data format and add it to database
                    Matcher m = p.matcher(data);
                    if (m.find()){
                        int degrees = Integer.parseInt(m.group(1));
                        Long millis = Long.parseUnsignedLong(m.group(2));
                        LocalDateTime measurementTime = SerialConnection.this.connectionStart.plus(Duration.ofMillis(millis));
                        DegreeModel degree = new DegreeModel(degrees, measurementTime);
                        degree.db.setConnectionProps(Config.DB_URL, Config.DB_USER, Config.DB_PASS);
                        degree.db.insertInto(Config.TABLE_NAME);//insert into DB

                        this.caller.expandChartPanel(degrees, "degree", measurementTime);// add this degree data to the graph at gui
                    }
                    //reset the data
                    data = "";
                }
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

}
