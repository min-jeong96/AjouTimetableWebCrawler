import org.sqlite.SQLiteConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataReader {
    private Connection connection;
    private String dbFileName;
    private boolean isOpened = false;

    public final static String DATABASE = "AjouTimeTable.db";
    static {
        try { Class.forName("org.sqlite.JDBC");
        } catch(Exception e) {
            e.printStackTrace(); }
    }

    public DataReader(String databaseFileName) { this.dbFileName = databaseFileName; }

    public boolean open() {
        try {
            // 읽기 전용
            SQLiteConfig config = new SQLiteConfig();
            config.setReadOnly(true);
            this.connection = DriverManager.getConnection("jdbc:sqlite:/" + this.dbFileName, config.toProperties());
        } catch(SQLException e) {
            e.printStackTrace();
            return false;
        }

        isOpened = true;
        return true;
    }

    public boolean close() {
        if(this.isOpened == false) {
            return true;
        }

        try {
            this.connection.close();
        } catch(SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}