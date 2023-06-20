package service_restaurant;

import service_central.ServiceCentral;
import service_ens_sup.EtablissementsEnsSup;
import service_ens_sup.ServiceEnsSup;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class LancerServiceRestaurant {
    // args[2] : login de la base de données
    // args[3] : mot de passe de la base de données
    public static void main(String[] args) {
        if (args.length < 4){
            throw new Error("Il faut préciser l'adresse ip et le port de l'annuaire ainsi que le login et mot de passe de la base de données  !");
        }

        String ip = args[0];
        String login = args[2];
        String password = args[3];

        // Récupération du numéro de port
        try {
            int port = Integer.parseInt(args[1]);

            // Instanciation du service
            Restaurant service = new Restaurant(login, password);

            // Création de la référence
            try {
                ServiceRestaurant ref = (ServiceRestaurant) UnicastRemoteObject.exportObject(service, 0);

                // Récupération de l'annuaire
                try{
                    Registry reg = LocateRegistry.getRegistry(ip, port);

                    // Récupération du service
                    try {
                        ServiceCentral serviceCentral = (ServiceCentral) reg.lookup("serviceCentral");

                        // Enregistrement de la référence sur le service central
                        try {
                            serviceCentral.enregistrerServiceRestaurant(service);
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
