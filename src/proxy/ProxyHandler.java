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
        } else if (requestedUrl.getPath().equals("/SAEAppliRepartie/reserverTable")) {
            String query = requestedUrl.getQuery();
            String[] parametres = query.split("&");
            if (parametres.length < 5){
                responseData = "{\n  \"code\": 500\n  \"msg\": \"Erreur lors de la réservation : il manque des renseignements\"".getBytes();
            }
            else {
                String nom = parametres[0].substring(parametres[0].indexOf('=') + 1);
                String prenom = parametres[1].substring(parametres[1].indexOf('=') + 1);
                String nbConvives = parametres[2].substring(parametres[2].indexOf('=') + 1);
                String resto = parametres[3].substring(parametres[3].indexOf('=') + 1);
                String numTel = parametres[4].substring(parametres[4].indexOf('=') + 1);
                ServiceRestaurant serviceRestaurant = proxy.getServiceRestaurant();
                try {
                    boolean reserve = serviceRestaurant.reserverTable(nom, prenom, Integer.parseInt(nbConvives), resto, numTel);
                    if (!reserve) {
                        responseData = "{\n  \"code\": 500\n  \"msg\": \"Erreur lors de la réservation\"\n}".getBytes();
                    }else {
                        responseData = "{\n  \"code\": 200\n  \"msg\": \"Reservation effectuee\"\n}}".getBytes();
                    }
                } catch (RemoteException | NullPointerException e) {
                    proxy.supprimerServiceRestaurant();
                    e.printStackTrace();
                    System.out.println("Erreur lors de la reservation");
                } catch (NumberFormatException e) {
                    responseData = "{\n  \"code\": 500\n  \"msg\": \"Erreur lors de la réservation : le nombre de convives doit être entier\"\n}".getBytes();
                }
            }
        }
        // On définit le content-type
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*/*");

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