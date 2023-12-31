package service_ens_sup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.rmi.RemoteException;
import java.time.Duration;

public class EtablissementsEnsSup implements ServiceEnsSup{
    @Override
    public String getEtablissements() throws RemoteException {
        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(20))
                .build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://www.data.gouv.fr/fr/datasets/r/5fb6d2e3-609c-481d-9104-350e9ca134fa"))
                .build();
        HttpResponse<String> response = null;
        JSONObject etablissements = new JSONObject();
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            etablissements.put("code", response.statusCode());
            etablissements.put("content-type", response.headers().allValues("content-type"));
            JSONArray etablissementsEnsSup = new JSONArray(response.body());
            etablissements.put("etablissements",etablissementsEnsSup);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de l'envoi de la requête HTTP ou de la reception de sa réponse");
        }
        return etablissements.toString(2);
    }
}
