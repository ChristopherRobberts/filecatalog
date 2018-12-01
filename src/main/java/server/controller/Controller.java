package server.controller;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import common.ClientInterface;
import common.FileDTO;
import common.ServerInterface;
import common.UserAccountDTO;
import server.integration.FileCatalogDOA;
import server.model.*;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;


public class Controller extends UnicastRemoteObject implements ServerInterface {
    private final static String INTERNAL_ERROR_MSG = "Internal error, try again";
    private final static String NOT_LOGGED_IN_MSG = "you must be logged in to perform this action";
    private FileCatalogDOA dbHandler = new FileCatalogDOA();
    private HashMap<String, ClientInterface> currentlyLoggedInUsers = new HashMap<>();

    public Controller() throws RemoteException {
        super();
        dbHandler.connect();
    }

    @Override
    public synchronized void registerAccount(ClientInterface ci, String username, String password)
            throws ActionDeniedException {
        try {
            dbHandler.registerUser(username, password);
            ci.receiveMessage("user " + username + " registered");
        } catch (MySQLIntegrityConstraintViolationException e) {
            throw new ActionDeniedException("Username already taken");
        } catch (Exception e) {
            throw new ActionDeniedException(INTERNAL_ERROR_MSG);
        }
    }

    @Override
    public synchronized UserAccountDTO loginUser(ClientInterface ci, String username, String password)
            throws ActionDeniedException {
        try {
            if (isLoggedIn(username)) {
                throw new ActionDeniedException("already logged in on this device or another");
            }

            if (dbHandler.userIsValidated(username, password)) {
                currentlyLoggedInUsers.put(username, ci);
                ci.receiveMessage("logged in");
                return new UserAccount(username, password);
            }

            throw new ActionDeniedException("wrong password or username");
        } catch (SQLException | RemoteException e) {
            e.printStackTrace();
            throw new ActionDeniedException(INTERNAL_ERROR_MSG);
        }
    }

    @Override
    public void logOut(UserAccountDTO ua) throws ActionDeniedException {
        try {
            String username = ua.getUsername();
            ClientInterface ci = currentlyLoggedInUsers.get(username);
            currentlyLoggedInUsers.remove(username);
            ci.receiveMessage("successfully logged out, bye bye " + username);

        } catch (RemoteException e) {
            e.printStackTrace();
            throw new ActionDeniedException(INTERNAL_ERROR_MSG);
        }
    }

    @Override
    public synchronized void uploadFile(UserAccountDTO ua, String fileName,
                                        String owner, int size,
                                        String readPermission, String writePermission) throws ActionDeniedException {
        try {
            String username = ua.getUsername();
            if (!isLoggedIn(username)) {
                throw new ActionDeniedException(NOT_LOGGED_IN_MSG);
            }

            dbHandler.uploadFile(fileName, owner, size, readPermission, writePermission);
            currentlyLoggedInUsers.get(username).receiveMessage("file uploaded");

        } catch (SQLException | RemoteException e) {
            throw new ActionDeniedException(INTERNAL_ERROR_MSG);
        }
    }

    @Override
    public synchronized List<? extends FileDTO> listUserFiles(UserAccountDTO ua) throws ActionDeniedException {
        try {
            return dbHandler.getUserSpecificFiles();
        } catch (SQLException e) {
            throw new ActionDeniedException(INTERNAL_ERROR_MSG);
        }
    }

    @Override
    public synchronized FileDTO downloadFile(UserAccountDTO ua, String fileName) throws ActionDeniedException {
        try {
            String username = ua.getUsername();
            File file = dbHandler.getFile(fileName);

            if (file == null) {
                throw new ActionDeniedException("invalid file name");
            }

            if (!file.getFileOwner().equals(username)) {
                if (!file.hasReadPermission()) {
                    throw new ActionDeniedException("No permission to obtain this document");
                }

                String fileOwner = file.getFileOwner();

                if (isLoggedIn(fileOwner)) {
                    currentlyLoggedInUsers.get(fileOwner).receiveMessage(username +
                            " has downloaded your file " + file.getFileName());
                }

                return file;
            }

            return file;
        } catch (SQLException | RemoteException e) {
            throw new ActionDeniedException(INTERNAL_ERROR_MSG);
        }
    }

    @Override
    public void deleteFile(UserAccountDTO ua, String fileName) throws ActionDeniedException {
        try {
            String username = ua.getUsername();
            String action = "delete";

            File file = dbHandler.getFile(fileName);
            performPermissionsCheck(file, action, username);

            dbHandler.deleteFile(fileName);
            alertOwner(file, action, username);

        } catch (SQLException | RemoteException e) {
            throw new ActionDeniedException(INTERNAL_ERROR_MSG);
        }
    }

    @Override
    public void updateFileName(UserAccountDTO ua, String fileName, String newName) throws ActionDeniedException {
        try {
            String username = ua.getUsername();
            String action = "name-update";

            File file = dbHandler.getFile(fileName);
            performPermissionsCheck(file, action, username);

            dbHandler.updateFileName(fileName, newName);
            alertOwner(file, action, username);

        } catch (SQLException | RemoteException e) {
            e.printStackTrace();
            throw new ActionDeniedException(INTERNAL_ERROR_MSG);
        }
    }

    private void performPermissionsCheck(File file, String action, String username)
            throws RemoteException, ActionDeniedException, SQLException {
        if (file == null) {
            throw new ActionDeniedException("invalid file name");
        }

        if (!file.getFileOwner().equals(username) && !file.hasWritePermission()) {
            throw new ActionDeniedException("no permission to " + action + " this file");
        }
    }

    private void alertOwner(File file, String action, String username) throws RemoteException {
        if (!file.getFileOwner().equals(username) && isLoggedIn(file.getFileOwner())) {
            currentlyLoggedInUsers
                    .get(file.getFileOwner())
                    .receiveMessage(username + " has performed a " + action + " on your file " + file.getFileName());
        }
        currentlyLoggedInUsers.get(username).receiveMessage(action + " performed");
    }

    private boolean isLoggedIn(String username) throws RemoteException {
        System.out.println(username);
        return currentlyLoggedInUsers.containsKey(username);
    }
}
