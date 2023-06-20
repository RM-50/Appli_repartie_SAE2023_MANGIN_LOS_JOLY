package service_restaurant;

import org.json.JSONArray;
import org.json.JSONObject;

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
    public JSONArray getRestos() throws RemoteException{
        try {

            ResultSet coordonnes = getCoordonnees(login, password);
            JSONArray jsonArray = new JSONArray();
            while (coordonnes.next()) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("nom_resto", coordonnes.getString("nom_resto"));
                jsonObject.put("coordonees", coordonnes.getString("coordonees"));
                jsonArray.put(jsonObject);
            }
            return jsonArray;
        }catch (SQLException e){
            System.out.println("Erreur lors de la récupération des coordonnées des restaurants");
            return null;
        }
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
