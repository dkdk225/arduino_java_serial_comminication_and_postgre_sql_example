import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DegreeModel {
    private int degree;
    private LocalDateTime measurementTime;
    public DB db = new DB();
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    public DegreeModel(int degree, LocalDateTime measurementTime){
        this.degree = degree;
        this.measurementTime = measurementTime;
    }

    public class DB {

        private String DB_URL;
        private String USER;
        private String PASS;

        public DB(){}
        public void setConnectionProps(String dbUrl, String user, String pass){
            this.DB_URL = dbUrl;
            this.USER = user;
            this.PASS = pass;
        }
        private Connection getConnection(){
            try {
                Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                return conn;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        public void insertInto(String TABLE_NAME){
            if (!this.doesTableExists(TABLE_NAME)){
                this.createTable(TABLE_NAME);
            }
            try {
                String deg = String.valueOf(DegreeModel.this.degree);
                String formattedDate = DegreeModel.this.measurementTime.format(dateFormatter);
                String QUERY = String.format("INSERT INTO %1$s (degree, time) VALUES ('%2$s', '%3$s')", TABLE_NAME, deg, formattedDate);
                Connection conn = this.getConnection();
                Statement stmt = conn.createStatement();
                int rows = stmt.executeUpdate(QUERY);
                if (rows > 0) System.out.println("Degree has been inserted into " + TABLE_NAME);
                stmt.close();
                conn.close();

            } catch (SQLException e) {
                throw new RuntimeException("Be Sure to set connection props using DegreeModel.DB.setConnectionProps",e);
            }
        }
        private void createTable(String TABLE_NAME){
            try {
                String QUERY = String.format("CREATE TABLE %1$s (id serial PRIMARY KEY NOT NULL, degree integer NOT NULL, time timestamp without time zone NOT NULL) ",TABLE_NAME);
                System.out.println(QUERY);
                Connection conn = this.getConnection();
                Statement stmt = conn.createStatement();
                stmt.executeUpdate(QUERY);
                stmt.close();
                conn.close();

            } catch (SQLException e) {
                throw new RuntimeException("Be Sure to set connection props using DegreeModel.DB.setConnectionProps",e);
            }
        }

        private boolean doesTableExists(String tableName){
            try {
                Connection conn = this.getConnection();
                DatabaseMetaData dbm = conn.getMetaData();
                // check if "employee" table is there
                ResultSet tables = dbm.getTables(null, null, tableName, null);
                conn.close();
                if (tables.next()) {
                    // Table exists
                    return true;
                }
                // Table doesn't exists
                return false;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }
    }

}