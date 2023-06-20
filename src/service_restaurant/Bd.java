package service_restaurant;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Bd {

    public static ResultSet getCoordonnees(String login, String pwd) throws SQLException {
        Connection connection = ConnectionBd.getInstance(login, pwd);
        java.sql.Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT nom_resto, coordonnees_resto FROM RESTAURANT");
        return resultSet;
    }

    public static void reserver(String nom, String prenom, int nb_convives, String tel, String resto, String login, String pwd) throws SQLException {
        Connection connection = ConnectionBd.getInstance(login, pwd);
        java.sql.Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT nom FROM CLIENT WHERE nom = '" + nom + "' AND prenom = '" + prenom + "'");
        if (!resultSet.next()) {
            statement.executeUpdate("INSERT INTO CLIENT VALUES (client_seq.nextval, '" + nom + "', '" + prenom + "', '" + tel + "')");
        }
        resultSet = statement.executeQuery("SELECT id_client FROM CLIENT WHERE nom = '" + nom + "' AND prenom = '" + prenom + "'");
        resultSet.next();
        int id_client = resultSet.getInt("id_client");
        resultSet = statement.executeQuery("SELECT id_resto FROM RESTAURANT WHERE nom_resto = '" + resto + "'");
        resultSet.next();
        int id_resto = resultSet.getInt("id_resto");
        resultSet = statement.executeQuery("SELECT num_table FROM TABL WHERE id_resto = " + id_resto + " AND num_table NOT IN (SELECT num_table FROM RESERVATION WHERE id_resto = " + id_resto + ")");
        if (!resultSet.next()) {
            throw new SQLException("Il n'y a plus de table disponible dans ce restaurant");
        }else {
            resultSet.next();
            int num_table = resultSet.getInt("num_table");
            statement.executeUpdate("INSERT INTO RESERVATION VALUES (reservation_seq.nextval, " + id_client + ", " + id_resto + ", " + num_table + ", " + nb_convives + ")");
        }



    }
}
