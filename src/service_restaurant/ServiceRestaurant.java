package service_restaurant;

import org.json.JSONArray;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;

public interface ServiceRestaurant extends Remote {

    public String getRestos() throws RemoteException;

    public boolean reserverTable(String nom, String prenom, int nbConvives, String resto, String numTel) throws RemoteException;
}
