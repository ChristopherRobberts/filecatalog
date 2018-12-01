package server.integration;

import server.model.File;
import server.model.UserAccount;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FileCatalogDOA {
    private Connection connection;
    private PreparedStatement registerUser;
    private PreparedStatement addFile;
    private ResultSet resultSet;

    public void connect() {
        try {
            this.connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/file_catalog?useSSL=false",
                    "root", "root");
            initPrepStatements();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void registerUser(String username, String password) throws SQLException {
        this.registerUser.setString(1, username);
        this.registerUser.setString(2, password);
        this.registerUser.executeUpdate();
    }

    public boolean userIsValidated(String username, String password) throws SQLException {
        Statement statement = connection.createStatement();
        resultSet = statement.executeQuery("SELECT username, password FROM users WHERE username = '" +
                username + "' AND password = '" + password + "'");
        return resultSet.next();
    }

    private void initPrepStatements() throws SQLException {
        this.registerUser =
                this.connection.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)");
        this.addFile =
                this.connection.prepareStatement("INSERT INTO userfiles " +
                        "(owner, name, filesize, readpermission, writepermission) VALUES (?, ?, ?, ?, ?)");
    }

    public void uploadFile(String name, String owner, int size, String read, String write) throws SQLException {
        System.out.println("here we are");
        this.addFile.setString(1, owner);
        this.addFile.setString(2, name);
        this.addFile.setInt(3, size);
        this.addFile.setString(4, read);
        this.addFile.setString(5, write);
        this.addFile.executeUpdate();
    }

    public List<File> getUserSpecificFiles() throws SQLException {
        String fileName;
        int fileSize;
        boolean read;
        boolean write;
        String owner;

        List<File> files = new ArrayList<>();
        Statement st = this.connection.createStatement();
        resultSet = st.executeQuery("SELECT owner, name, filesize, readpermission, writepermission FROM userfiles");

        while (resultSet.next()) {
            owner = resultSet.getString(1);
            fileName = resultSet.getString(2);
            fileSize = resultSet.getInt(3);
            read = (resultSet.getString(4).equals("yes") || resultSet.getString(4).equals("YES"));
            write = (resultSet.getString(5).equals("yes") || resultSet.getString(5).equals("YES"));
            files.add(new File(fileName, owner, fileSize, read, write));
        }

        return files;
    }

    public File getFile(String fileName) throws SQLException {
        Statement statement = this.connection.createStatement();
        resultSet = statement.executeQuery("SELECT * FROM userfiles WHERE name = '" + fileName + "'");

        if (resultSet.next()) {
            boolean read = (resultSet.getString(4).equals("yes") || resultSet.getString(4).equals("YES"));
            boolean write = (resultSet.getString(5).equals("yes") || resultSet.getString(5).equals("YES"));
            return new File(resultSet.getString(2),
                    resultSet.getString(1),
                    resultSet.getInt(3),
                    read, write);
        }

        return null;
    }

    public void deleteFile(String fileName) throws SQLException {
        Statement statement = this.connection.createStatement();
        statement.executeUpdate("DELETE FROM userfiles WHERE name = '" + fileName + "'");
    }

    public void updateFileName(String fileName, String newName) throws SQLException {
        Statement statement = this.connection.createStatement();
        statement.executeUpdate("UPDATE userfiles SET name = '" + newName + "' WHERE name = '" + fileName + "'");
    }
}
