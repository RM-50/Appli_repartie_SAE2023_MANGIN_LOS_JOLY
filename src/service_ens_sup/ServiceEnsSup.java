package service_ens_sup;

import org.json.JSONObject;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServiceEnsSup extends Remote {

    public String getEtablissements() throws RemoteException;
}
