package server.model;

import common.UserAccountDTO;

public class UserAccount implements UserAccountDTO {
    private String username;
    private String password;

    public UserAccount(String userName, String passWord) {
        this.username = userName;
        this.password = passWord;
    }

    public String getUsername() {
        return this.username;
    }
}
