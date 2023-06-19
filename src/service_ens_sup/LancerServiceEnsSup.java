package service_ens_sup;

import service_central.ServiceCentral;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class LancerServiceEnsSup {

    // args[0] : adresse ip de la machine hôte de l'annuaire
    // args[1] : numéro de port de l'annuaire
    public static void main(String[] args) {
        // Vérification du nombre de paramètres
        if (args.length < 2){
            throw new Error("Il faut préciser l'adresse ip ainsi que le port de l'annuaire !");
        }
        // Récupération de l'adresse ip
        String ip = args[0];

        // Récupération du numéro de port
        try {
            int port = Integer.parseInt(args[1]);

            // Instanciation du service
            EtablissementsEnsSup service = new EtablissementsEnsSup();

            // Création de la référence
            try {
                ServiceEnsSup ref = (ServiceEnsSup) UnicastRemoteObject.exportObject(service, 0);

                // Récupération de l'annuaire
                try{
                    Registry reg = LocateRegistry.getRegistry(ip, port);

                    // Récupération du service
                    try {
                        ServiceCentral serviceCentral = (ServiceCentral) reg.lookup("serviceCentral");

                        // Enregistrement de la référence sur le service central
                        try {
                            serviceCentral.enregistrerServiceEnsSup(service);
                        } catch (Exception e) {
                            throw new Error("Erreur lors de l'enregistrement auprès du service central");
                        }
                    } catch (NotBoundException | RemoteException e) {
                        throw new Error("Erreur lors de la récupération du service");
                    }
                } catch (RemoteException e){
                    throw new Error("Erreur lors de la récupération de l'annuaire");
                }
            } catch (RemoteException e) {
                throw new Error("Erreur lors de la création de la référence");
            }
        } catch (NumberFormatException e){
            throw new Error("Le numéro de port doit être un entier");
        }
    }
}
