package service_restaurant;

import org.json.JSONArray;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServiceRestaurant extends Remote {

    public JSONArray getRestos() throws RemoteException;

    public boolean reserverTable(String nom, String prenom, int nbConvives, String numTel) throws RemoteException;
}
