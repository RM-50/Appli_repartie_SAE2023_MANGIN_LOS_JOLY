package service_restaurant;

import org.json.JSONArray;

import java.rmi.RemoteException;
import java.sql.ResultSet;
import java.sql.SQLException;

import static service_restaurant.Bd.getCoordonnees;

public class Restaurant implements ServiceRestaurant{

    String login;
    String password;

    public Restaurant(String login, String password){
        this.login = login;
        this.password = password;
    }

    // utilise la methode getCoordonnees de la classe Bd
    @Override
    public JSONArray getRestos() throws RemoteException, SQLException {
        ResultSet coordonnes  = getCoordonnees(login, password);
        JSONArray jsonArray = new JSONArray();
        while (coordonnes.next()){
            jsonArray.put(coordonnes.getString("nom_resto"));
            jsonArray.put(coordonnes.getString("coordonees"));
        }
        return jsonArray;
    }

    @Override
    public boolean reserverTable(String nom, String prenom, int nbConvives, String resto, String numTel) throws RemoteException {
        try{
            Bd.reserver(nom, prenom, nbConvives, numTel, resto, login, password);
            return true;
        }catch (SQLException e){
            return false;
        }
    }
}
