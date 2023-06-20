package service_restaurant;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionBd{

    private static Connection connectionBd;

    private ConnectionBd(String login, String pwd) throws SQLException {
        String url = "jdbc:oracle:thin:@charlemagne.iutnc.univ-lorraine.fr:1521:infodb";
        connectionBd = DriverManager.getConnection(url, login, pwd);
    }

    public static Connection getInstance(String login, String pwd) throws SQLException {
        if (connectionBd == null)
            new ConnectionBd(login, pwd);
        return connectionBd;
    }
}
