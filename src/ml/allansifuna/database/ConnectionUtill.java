package ml.allansifuna.database;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionUtill {
    public static Connection getConn() throws SQLException {
        Connection conn= DriverManager.getConnection("jdbc:sqlite::resource:ml/allansifuna/database/doublea.db");
        return conn;
    }
}
