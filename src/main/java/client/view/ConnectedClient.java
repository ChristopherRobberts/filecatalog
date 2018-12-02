package client.view;

import common.ClientInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ConnectedClient extends UnicastRemoteObject implements ClientInterface {

    private CommandLineOutput cmd = new CommandLineOutput();

    public ConnectedClient() throws RemoteException {
        super();
    }

    @Override
    public void notifyModificationComplete(String action) {
        cmd.println(action + " performed successfully");
    }

    @Override
    public void notifyFileOwner(String user, String action, String fileName) {
        cmd.println(user + " has performed a "+ action + " on your file: " + fileName);
    }

    @Override
    public void successfulRegister() {
        cmd.println("user registered");
    }

    @Override
    public void uploaded() {
        cmd.println("file uploaded");
    }

    @Override
    public void loggedIn(String username) {
        cmd.println("you are now logged in as " + username);
    }

    @Override
    public void loggedOut(String username) {
        cmd.println("you are now logged out, bye bye " + username);
    }

    @Override
    public void invalidUsername() { cmd.println("invalid username or password");}
}
