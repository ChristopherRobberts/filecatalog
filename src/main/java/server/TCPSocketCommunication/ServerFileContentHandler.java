package server.TCPSocketCommunication;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class ServerFileContentHandler {
    private static final int TIME_UNTIL_TIMEOUT = 600000;
    private static final int LINGER_TIME = 5000;
    private static final int application_portNr = 8080;

    public void start() {
        try {
            ServerSocket serverSocket = new ServerSocket(application_portNr);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                serveClient(clientSocket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void serveClient(Socket socket) throws SocketException {
        socket.setSoTimeout(TIME_UNTIL_TIMEOUT);
        socket.setSoLinger(true, LINGER_TIME);
        ClientHandler clientHandler = new ClientHandler(socket);
        Thread dedicatedThread = new Thread(clientHandler);
        dedicatedThread.start();
    }
}
