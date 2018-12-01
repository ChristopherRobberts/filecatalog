package client.startup;

import client.view.CommandLineInterpreter;
import client.view.ConnectedClient;

public class ClientMain {

    public static void main(String[] args) {
        try {
            ConnectedClient cc = new ConnectedClient();
            new CommandLineInterpreter().start(cc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
