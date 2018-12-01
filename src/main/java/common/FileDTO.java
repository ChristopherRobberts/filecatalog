package common;

import java.io.Serializable;
import java.util.List;

public interface FileDTO extends Serializable {

    boolean hasReadPermission();

    boolean hasWritePermission();

    String getFileName();

    String getFileOwner();

    int getFileSize();


}
