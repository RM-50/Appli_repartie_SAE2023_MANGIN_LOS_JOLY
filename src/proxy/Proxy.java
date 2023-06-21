package proxy;

import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;
import com.sun.net.httpserver.HttpsServer;
import service_ens_sup.ServiceEnsSup;
import service_restaurant.ServiceRestaurant;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.rmi.RemoteException;
import java.security.*;
import java.security.cert.CertificateException;

public class Proxy implements ServiceProxy {

    private ServiceRestaurant serviceRestaurant;
    private ServiceEnsSup serviceEnsSup;

    public Proxy(){
        // Création du server https sur le port 8000
        HttpsServer server = null;
        try {
            server = HttpsServer.create(new InetSocketAddress(8000), 0);
        } catch (IOException e) {
            throw new Error("Erreur lors du lancement de la création du Server HTTP");
        }
        server.createContext("/SAEAppliRepartie", new ProxyHandler(this));
        server.setExecutor(null); // creates a default executor
        char[] passphrase = "password".toCharArray();
        // Récupération de la clé
        KeyStore ks = null;
        try {
            ks = KeyStore.getInstance("JKS");
        } catch (KeyStoreException e) {
            throw new Error("Erreur lors de la récupération du KeyStore");
        }
        try {
            ks.load(new FileInputStream("testkeys"), passphrase);
        } catch (IOException | NoSuchAlgorithmException | CertificateException e) {
            throw new Error(e.getMessage());
        }

        KeyManagerFactory kmf = null;
        try {
            kmf = KeyManagerFactory.getInstance("SunX509");
        } catch (NoSuchAlgorithmException e) {
            throw new Error(e.getMessage());
        }
        try {
            kmf.init(ks, passphrase);
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            throw new Error(e.getMessage());
        }

        TrustManagerFactory tmf = null;
        try {
            tmf = TrustManagerFactory.getInstance("SunX509");
        } catch (NoSuchAlgorithmException e) {
            throw new Error(e.getMessage());
        }
        try {
            tmf.init(ks);
        } catch (KeyStoreException e) {
            throw new Error(e.getMessage());
        }

        // Création et initialisation du contexte ssl
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("TLS");
        } catch (NoSuchAlgorithmException e) {
            throw new Error(e.getMessage());
        }
        try {
            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
        } catch (KeyManagementException e) {
            throw new Error(e.getMessage());
        }
        // Configuration du configurateur HTTPS
        server.setHttpsConfigurator (new HttpsConfigurator(sslContext) {
            public void configure (HttpsParameters params) {

                // get the remote address if needed
                InetSocketAddress remote = params.getClientAddress();

                SSLContext c = getSSLContext();

                // get the default parameters
                SSLParameters sslparams = c.getDefaultSSLParameters();


                params.setSSLParameters(sslparams);
                // statement above could throw IAE if any params invalid.
                // eg. if app has a UI and parameters supplied by a user.

            }
        });
        // Lancement du serveur
        server.start();
        System.out.println("Serveur HTTPS lancé");
    }

    @Override
    public void enregistrerServiceRestaurant(ServiceRestaurant s) throws RemoteException {
        if (this.serviceRestaurant == null){
            this.serviceRestaurant = s;
            System.out.println("ServiceRestaurant enregistré");
        }
        else {
            System.out.println("ServiceRestaurant déja existant");
        }
    }

    @Override
    public void enregistrerServiceEnsSup(ServiceEnsSup s) throws RemoteException {
        if (this.serviceEnsSup == null){
            this.serviceEnsSup = s;
            System.out.println("ServiceEnsSup enregistré");
        }
        else {
            System.out.println("ServiceEnsSup déja existant");
        }

    }

    @Override
    public ServiceRestaurant getServiceRestaurant() throws RemoteException {
        System.out.println("envoi de ServiceRestaurant");
        return this.serviceRestaurant;
    }

    @Override
    public ServiceEnsSup getServiceEnsSup() throws RemoteException {
        System.out.println("envoi de ServiceEnsSup");
        return this.serviceEnsSup;
    }

    @Override
    public void supprimerServiceRestaurant() throws RemoteException {
        this.serviceRestaurant = null;
        System.out.println("Suppression de ServiceRestauratnt");
    }

    @Override
    public void supprimerServiceEnsSup() throws RemoteException {
        this.serviceEnsSup = null;
        System.out.println("Suppresion de ServiceEnsSup");
    }
}
