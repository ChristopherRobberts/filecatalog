package server.model;

import common.FileDTO;

public class File implements FileDTO {
    private String name;
    private int size;
    private boolean readPermission;
    private boolean writePermission;
    private String owner;

    public File(String name, String owner, int size, boolean read, boolean write) {
        this.name = name;
        this.owner = owner;
        this.size = size;
        this.readPermission = read;
        this.writePermission = write;
    }

    public String getFileName() {
        return this.name;
    }

    public String getFileOwner() {
        return this.owner;
    }

    public int getFileSize() {
        return this.size;
    }

    public boolean hasWritePermission() {
        return this.writePermission;
    }

    public boolean hasReadPermission() {
        return this.readPermission;
    }

    public void updateFileName(String name) {
        this.name = name;
    }

    public void updateFileSize(int size) {
        this.size = size;
    }
}
