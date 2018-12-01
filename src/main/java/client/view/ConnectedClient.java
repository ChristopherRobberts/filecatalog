package client.view;

import common.ClientInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ConnectedClient extends UnicastRemoteObject implements ClientInterface {

    private CommandLineOutput cmd = new CommandLineOutput();

    public ConnectedClient() throws RemoteException {
        super();
    }

    public void receiveMessage(String message) {
        cmd.println(message);
    }
}
