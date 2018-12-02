package common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientInterface extends Remote {

    void notifyModificationComplete(String action) throws RemoteException;

    void notifyFileOwner(String user, String action, String fileName) throws RemoteException;

    void uploaded() throws RemoteException;

    void loggedIn(String username) throws RemoteException;

    void loggedOut(String username) throws RemoteException;

    void successfulRegister() throws RemoteException;

    void invalidUsername() throws RemoteException;
}
