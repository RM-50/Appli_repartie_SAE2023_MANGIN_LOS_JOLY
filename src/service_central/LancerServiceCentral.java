package service_central;

import com.sun.jdi.InvalidLineNumberException;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class LancerServiceCentral {
    public static void main(String[] args) {
        if (args.length < 1){
            throw new Error("Il faut donner le numéro de port de l'annuaire !");
        }

        // Initialisation du service
        Distributeur d = new Distributeur();
        {
            try {
                // Création de la référence
                ServiceCentral service = (ServiceCentral) UnicastRemoteObject.exportObject(d, 0);
                try{
                    Registry reg = LocateRegistry.createRegistry(Integer.parseInt(args[0]));
                    try{
                        reg.rebind("serviceCentral", service);
                    }catch (Exception e){
                        System.out.println("Erreur lors de l'enregistrement du service dans l'annuaire");
                    }
                }catch (NumberFormatException e){
                    System.out.println("Le numéro de port doit être un nombre entier");
                }catch (Exception e2){
                    System.out.println("Erreur lors de la création de l'annuaire");
                }
            } catch (RemoteException e) {
                System.out.println("Erreur lors de la création de la référence");
            }
        }
    }
}
