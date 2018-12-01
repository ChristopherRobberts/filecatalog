package common;

import server.model.ActionDeniedException;
import server.model.UserAccount;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ServerInterface extends Remote {
    String REMOTE_OBJECT_NAME = "SERVER_REGISTER_NAME";

    void registerAccount(ClientInterface ci, String username, String password)
            throws RemoteException, ActionDeniedException;

    UserAccountDTO loginUser(ClientInterface ci, String username, String password)
            throws RemoteException, ActionDeniedException;

    void logOut(UserAccountDTO ua) throws RemoteException, ActionDeniedException;

    void uploadFile(UserAccountDTO ua, String fileName, String owner,
                    int size, String readPermission, String writePermission)
            throws RemoteException, ActionDeniedException;

    List<? extends FileDTO> listUserFiles(UserAccountDTO ua)
            throws RemoteException, ActionDeniedException;

    FileDTO downloadFile(UserAccountDTO ua, String fileName) throws RemoteException, ActionDeniedException;

    void deleteFile(UserAccountDTO ua, String fileName) throws RemoteException, ActionDeniedException;

    void updateFileName(UserAccountDTO ua, String fileName, String newName)
            throws RemoteException, ActionDeniedException;
}