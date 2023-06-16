package service_central;

import service_restaurant.ServiceRestaurant;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServiceCentral extends Remote {

    public void enregistrerServiceRestaurant(ServiceRestaurant s) throws RemoteException;

    public ServiceRestaurant getServiceRestaurant() throws RemoteException;
}
