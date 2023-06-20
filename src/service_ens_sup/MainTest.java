package service_ens_sup;

import java.rmi.RemoteException;

public class MainTest {
    public static void main(String[] args) {
        EtablissementsEnsSup e = new EtablissementsEnsSup();
        try {
            System.out.println(e.getEtablissements());
        } catch (RemoteException ex) {
            System.out.println("Erreur lors de l'utilisation du service ens sup");
        }
    }
}
