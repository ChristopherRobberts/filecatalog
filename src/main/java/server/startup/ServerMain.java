package server.startup;

import server.TCPSocketCommunication.ServerFileContentHandler;
import server.controller.Controller;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerMain {

    public static void main(String[] args) {
        try {
            try {
                LocateRegistry.getRegistry().list();
            } catch (RemoteException e) {
                LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
            }
            try {
                Naming.rebind(Controller.REMOTE_OBJECT_NAME, new Controller());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        ServerFileContentHandler fileContentHandler = new ServerFileContentHandler();
        fileContentHandler.start();
    }
}
