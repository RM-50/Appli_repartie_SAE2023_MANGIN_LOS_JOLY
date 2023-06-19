package service_central;

import service_ens_sup.ServiceEnsSup;
import service_restaurant.ServiceRestaurant;

import java.rmi.RemoteException;

public class Distributeur implements ServiceCentral{

    private ServiceRestaurant serviceRestaurant;
    private ServiceEnsSup serviceEnsSup;

    public Distributeur(){}

    @Override
    public void enregistrerServiceRestaurant(ServiceRestaurant s) throws RemoteException {
        if (this.serviceRestaurant == null)
            this.serviceRestaurant = s;
    }

    @Override
    public void enregistrerServiceEnsSup(ServiceEnsSup s) throws RemoteException {
        if (this.serviceEnsSup == null)
            this.serviceEnsSup = s;
    }

    @Override
    public ServiceRestaurant getServiceRestaurant() throws RemoteException {
        return this.serviceRestaurant;
    }
}
