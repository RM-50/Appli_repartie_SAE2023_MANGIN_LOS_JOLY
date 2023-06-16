package service_central;

import service_restaurant.ServiceRestaurant;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class Distributeur implements ServiceCentral{

    private ServiceRestaurant serviceRestaurant;

    public Distributeur(){}

    @Override
    public void enregistrerServiceRestaurant(ServiceRestaurant s) throws RemoteException {
        if (this.serviceRestaurant == null)
            this.serviceRestaurant = s;
    }

    @Override
    public ServiceRestaurant getServiceRestaurant() throws RemoteException {
        return this.serviceRestaurant;
    }
}
