package client.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ServerConnection {
    private final String host = "localhost";
    private final int port = 8080;
    private BufferedReader fromServer;
    private PrintWriter toServer;
    private String path;

    public void connect() {
        try {
            boolean autoFlush = true;
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(host, port));
            this.toServer = new PrintWriter(socket.getOutputStream(), autoFlush);
            this.fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Listener listener = new Listener();
            new Thread(listener).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void uploadFileContent(String fileName, String content) {
        String message = "upload" + ":" + fileName + ":" + content;
        toServer.println(message);
    }

    public void downloadFileContent(String fileName) {
        String message = "download" + ":" + fileName;
        toServer.println(message);
    }

    private class Listener implements Runnable {

        public void run() {
            try {
                while (true) {
                    String message = fromServer.readLine();
                    ServerMessageParser serverMessageParser = new ServerMessageParser(message);

                    if (serverMessageParser.description.equals("downloading")) {
                        createFile(serverMessageParser.fileName ,serverMessageParser.content);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void createFile(String fileName, String content) throws IOException {
            Path filePath = Paths.get(path + fileName);
            List<String> fileContent = new ArrayList<>(Arrays.asList(content.split(" ")));
            Files.write(filePath, fileContent, Charset.forName("UTF-8"));
        }

        private class ServerMessageParser {
            private String description;
            private String content;
            private String fileName;

            private ServerMessageParser(String entireCommand) {
                String[] messageParts = entireCommand.split(":");
                System.out.println(Arrays.toString(messageParts));
                this.description = messageParts[0];
                this.fileName = messageParts[1];
                this.content = messageParts[2];
            }
        }
    }
}
