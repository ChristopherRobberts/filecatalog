package server.TCPSocketCommunication;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private BufferedReader fromClient;
    private PrintWriter toClient;
    private boolean connected;
    private final String defaultPath =
            "C:\\Users\\Chris\\IdeaProjects\\filecatalog\\src\\main\\java\\server\\uploaded_files\\";

    ClientHandler(Socket socket) {
        this.clientSocket = socket;
        this.connected = true;
    }

    public void run() {
        try {
            boolean autoFlush = true;
            this.fromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.toClient = new PrintWriter(clientSocket.getOutputStream(), autoFlush);

            while (connected) {
                String msg = fromClient.readLine();
                ClientMessageParser command = new ClientMessageParser(msg);

                switch (command.messageType) {
                    case "upload":
                        uploadToDir(command.fileName, command.messageContent);
                        break;
                    case "download":
                        download(command.fileName);
                        break;
                    case "disconnect":
                        connected = false;
                    default:
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void uploadToDir(String fileName, String content) throws IOException {
        Path path = Paths.get(this.defaultPath + fileName);
        List<String> fileContent = new ArrayList<>(Arrays.asList(content.split(" ")));
        Files.write(path, fileContent, Charset.forName("UTF-8"));
    }

    private void download(String fileName) throws IOException {
        String content = getFileContent(fileName);
        this.toClient.println("downloading:" + fileName + ":" + content);
    }

    private String getFileContent(String fileName) throws IOException {
        File file = new File(this.defaultPath + fileName);
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String word;
        String content = "";
        while ((word = bufferedReader.readLine()) != null) {
            content += word;
        }
        return content;
    }

    private class ClientMessageParser {
        private String messageType;
        private String fileName;
        private String messageContent;

        private ClientMessageParser(String msg) {
            String[] parts = msg.split(":");

            this.messageType = parts[0];
            this.fileName = parts[1];
            this.messageContent = (parts.length > 2) ? parts[2] : " ";
        }
    }
}
