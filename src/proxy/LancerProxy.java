package proxy;

import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;
import com.sun.net.httpserver.HttpsServer;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.*;
import java.security.cert.CertificateException;

public class LancerProxy {

    // args[0] : numéro de port de l'annuaire
    public static void main(String[] args) throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException, IOException, CertificateException {
        // Vérification du nombre de paramètres
        if (args.length < 1){
            throw new Error("Il faut donner le numéro de port de l'annuaire !");
        }

        try {
            // Récupération du numéro de port
            int port = Integer.parseInt(args[0]);

            // Instanciation du service
            Proxy proxy = new Proxy();
            try {
                // Création de la référence
                ServiceProxy service = (ServiceProxy) UnicastRemoteObject.exportObject(proxy, 0);
                try {
                    // Création de l'annuaire
                    Registry reg = LocateRegistry.createRegistry(port);
                    try {
                        // Association de la référence du service dans l'annuaire
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
