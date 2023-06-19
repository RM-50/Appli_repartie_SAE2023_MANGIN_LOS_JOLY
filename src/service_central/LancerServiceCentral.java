package service_central;

import com.sun.jdi.InvalidLineNumberException;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class LancerServiceCentral {

    // args[0] : numéro de port de l'annuaire
    public static void main(String[] args) {
        // Vérification du nombre de paramètres
        if (args.length < 1){
            throw new Error("Il faut donner le numéro de port de l'annuaire !");
        }

        try {
            // Récupération du numéro de port
            int port = Integer.parseInt(args[0]);

            // Instanciation du service
            Distributeur d = new Distributeur();
            try {
                // Création de la référence
                ServiceCentral service = (ServiceCentral) UnicastRemoteObject.exportObject(d, 0);
                try {
                    Registry reg = LocateRegistry.createRegistry(port);
                    try {
                        reg.rebind("serviceCentral", service);
                    } catch (RemoteException e) {
                        throw new Error("Erreur lors de l'enregistrement du service dans l'annuaire");
                    }
                } catch (RemoteException e2) {
                    throw new Error("Erreur lors de la création de l'annuaire");
                }
            } catch (RemoteException e) {
                throw new Error("Erreur lors de la création de la référence");
            }
        }catch (NumberFormatException e){
            throw new Error("Le numéro de port doit être un entier");
        }
    }
}
