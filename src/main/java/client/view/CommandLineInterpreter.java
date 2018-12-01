package client.view;

import client.FileExtractor;
import common.FileDTO;
import common.ServerInterface;
import common.UserAccountDTO;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.*;

public class CommandLineInterpreter implements Runnable {
    private static final String ALREADY_LOGGED_IN_ERROR_MSG = "you are already logged in to an account";
    private static final String NOT_LOGGED_IN_ERROR_MSG = "you must be logged in to perform this action";
    private static final String WRONG_INPUT_MESSAGE = "you must answer with [yes] or [no]";
    private CommandLineOutput commandLineOutput = new CommandLineOutput();
    private Scanner scanner = new Scanner(System.in);
    private ServerInterface server;
    private boolean connected = false;
    private boolean input = true;
    private ConnectedClient connectedClient;
    private boolean isLoggedIn = false;
    private UserAccountDTO userAccount;

    public void start(ConnectedClient connectedClient) {
        this.connectedClient = connectedClient;
        new Thread(this).run();
    }

    public void run() {
        informClient();

        while (this.input) {
            try {
                String command = scanner.nextLine().toUpperCase();
                CommandParser parser = new CommandParser(command);

                if ((!connected && !(parser.command == Commands.CONNECT))) {
                    commandLineOutput.println("you must first connect then login to interact with the catalog");
                    continue;
                }

                switch (parser.command) {
                    case CONNECT:
                        serverAccess();
                        commandLineOutput.println("connected");
                        this.connected = true;
                        break;
                    case REGISTER:
                        register();
                        break;
                    case LOGIN:
                        login();
                        break;
                    case LOGOUT:
                        if (!isLoggedIn) continue;
                        logout();
                        break;
                    case UPLOAD:
                        if (!isLoggedIn) continue;
                        uploadFile(parser);
                        break;
                    case DOWNLOAD:
                        if (!isLoggedIn) continue;
                        downloadFile();
                        break;
                    case DELETE:
                        if (!isLoggedIn) continue;
                        deleteFile();
                        break;
                    case UPDATE:
                        if (!isLoggedIn) continue;
                        updateFileName();
                        break;
                    case LIST:
                        if (!isLoggedIn) continue;
                        listFiles();
                        break;
                    case DISCONNECT:
                        this.connected = false;
                        this.input = false;
                        break;
                    case INFORM:
                        informClient();
                        break;
                    case UNKNOWN:
                        this.commandLineOutput.println("could not interpret command");
                        break;
                    default:
                        break;
                }

            } catch (Exception e) {
                this.commandLineOutput.println(e.getMessage());
            }
        }
    }

    private void serverAccess() throws RemoteException, MalformedURLException, NotBoundException {
        this.server = (ServerInterface) Naming.lookup("//localhost/" + ServerInterface.REMOTE_OBJECT_NAME);
    }

    private void informClient() {
        this.commandLineOutput.println("Following commands are legal\n" +
                "1.connect\n" +
                "2.register\n" +
                "3.login\n" +
                "4.upload *filename* *file size* *read permission [yes or no]* *write permission [yes or no]*\n" +
                "5.download *filename*\n" +
                "6.catalog\n" +
                "7.logout"
        );
    }

    private void register() throws Exception {
        commandLineOutput.println("user name: ");
        String username = scanner.nextLine();
        commandLineOutput.println("password: ");
        String password = scanner.nextLine();

        this.server.registerAccount(connectedClient, username, password);
    }

    private void login() throws Exception {
        this.commandLineOutput.println("user name: ");
        String username = scanner.nextLine();
        this.commandLineOutput.println("password: ");
        String password = scanner.nextLine();

        this.userAccount = this.server.loginUser(this.connectedClient, username, password);
        this.isLoggedIn = true;
    }

    private void logout() throws Exception {
        this.server.logOut(this.userAccount);
        this.userAccount = null;
        this.isLoggedIn = false;
    }

    private void uploadFile(CommandParser parser) throws Exception {
        commandLineOutput.println("please specify the file path -- Example: C://Users/Chris/test.txt");
        String path = scanner.nextLine();

        commandLineOutput.println("authorize read permissions: [yes/no]");
        String read = scanner.nextLine();
        if (!parser.isCorrectInput(read)) {
            commandLineOutput.println(WRONG_INPUT_MESSAGE);
            return;
        }

        commandLineOutput.println("authorize write permissions: [yes/no]");
        String write = scanner.nextLine();
        if (!parser.isCorrectInput(read)) {
            commandLineOutput.println(WRONG_INPUT_MESSAGE);
            return;
        }

        FileExtractor extractor = new FileExtractor(path);
        this.server.uploadFile(this.userAccount,
                extractor.getFileName(),
                this.userAccount.getUsername(),
                extractor.getFileSize(), read, write);
    }

    private void listFiles() throws Exception {
        List<? extends FileDTO> files = server.listUserFiles(this.userAccount);
        printFiles(files);
    }

    private void printFiles(List<? extends FileDTO> files) {
        if (files.isEmpty()) {
            commandLineOutput.println("no files to show");
            return;
        }

        for (FileDTO f : files) {
            commandLineOutput.println("owner: " + f.getFileOwner() +
                    "\t\tfile: " + f.getFileName() + "\t\tsize: " + f.getFileSize());
        }
    }

    private void downloadFile() throws Exception {
        this.commandLineOutput.println("Specify the name of the file you want to download, example: test.txt");
        String fileName = this.scanner.nextLine();
        FileDTO file = this.server.downloadFile(this.userAccount, fileName);
        commandLineOutput.println(file.getFileName());
    }

    private void deleteFile() throws Exception {
        this.commandLineOutput.println("Specify the name of the file you wish to delete, example: test.txt");
        String fileToDelete = this.scanner.nextLine();
        this.server.deleteFile(this.userAccount, fileToDelete);
    }

    private void updateFileName() throws Exception {
        this.commandLineOutput.println("Specify the name of the file you wish to update, example: test.txt");
        String fileToUpdate = this.scanner.nextLine();
        this.commandLineOutput.println("Specify the new file name: ");
        String newName = this.scanner.nextLine();
        this.server.updateFileName(this.userAccount, fileToUpdate, newName);
    }


    private class CommandParser {
        private Commands command;

        private CommandParser(String command) {
            if (command.isEmpty()) this.command = Commands.UNKNOWN;

            String[] parts = command.split(" ");
            if (parts.length > 1) {
                this.command = Commands.UNKNOWN;
                return;
            }

            try {
                this.command = Commands.valueOf(parts[0]);
            } catch (IllegalArgumentException e) {
                this.command = Commands.UNKNOWN;
            }
        }

        private boolean isCorrectInput(String readWrite) {
            return readWrite.equals("yes") || readWrite.equals("no");
        }
    }
}
