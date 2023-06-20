package proxy;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import service_ens_sup.ServiceEnsSup;
import service_restaurant.ServiceRestaurant;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.rmi.RemoteException;

class ProxyHandler implements HttpHandler {

    private Proxy proxy;

    public ProxyHandler(Proxy p){
        proxy = p;
    }
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Récupérer l'URL demandée par le client
        URI requestedUrl = exchange.getRequestURI();
        byte[] responseData = null;
        if (requestedUrl.getPath().equals("/SAEAppliRepartie/restaurants")){
            ServiceRestaurant serviceRestaurant = proxy.getServiceRestaurant();
            try {
                responseData = serviceRestaurant.getRestos().getBytes(StandardCharsets.UTF_8);
            } catch (RemoteException | NullPointerException e) {
                proxy.supprimerServiceRestaurant();
                e.printStackTrace();
                System.out.println("Erreur lors de la récupération des restaurants");
            }
        } else if (requestedUrl.getPath().equals("/SAEAppliRepartie/etablissementsEnsSup")) {
            ServiceEnsSup serviceEnsSup = proxy.getServiceEnsSup();
            try{
                responseData = serviceEnsSup.getEtablissements().getBytes(StandardCharsets.UTF_8);
            }catch (RemoteException | NullPointerException e){
                proxy.supprimerServiceEnsSup();
                e.printStackTrace();
                System.out.println("Erreur lors de la récupération des établissements supérieurs");
            }
        }
        // On définit le content-type
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");

        OutputStream responseBody = null;
        // On vérifie que la réponse existe
        if (responseData != null) {
            // Envoyer les Headers de la reponse
            exchange.sendResponseHeaders(200, responseData.length);

            // Récupérer le flux de sortie de la réponse du client
            responseBody = exchange.getResponseBody();

            // Écrire la réponse du serveur sortant dans le corps de la réponse du client
            responseBody.write(responseData);
        }
        else {
            byte[] erreur = "{\n  \"code\": 500\n  \"msg\": \"Le serveur a rencontré un problème\"\n  \"content-type\": \"application/json; charset=utf-8\"\n}".getBytes(StandardCharsets.UTF_8);
            // Envoyer les Headers de la reponse
            exchange.sendResponseHeaders(500, erreur.length);

            // Récupérer le flux de sortie de la réponse du client
            responseBody = exchange.getResponseBody();

            // Écrire la réponse du serveur sortant dans le corps de la réponse du client
            responseBody.write(erreur);
        }

        // Fermer les flux et terminer la réponse du client
        responseBody.close();
        exchange.close();
    }
}