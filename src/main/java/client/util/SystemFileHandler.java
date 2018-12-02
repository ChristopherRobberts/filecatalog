package client.util;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;

public class SystemFileHandler {
    private BasicFileAttributes basicFileAttributes;
    private Path path;
    private String spath;
    private String content = "";
    private int fileSize;
    private String fileName;

    public SystemFileHandler(String path) throws IOException {
        try {
            this.spath = path;
            this.path = Paths.get(path);
            basicFileAttributes = Files.readAttributes(this.path, BasicFileAttributes.class);
            this.fileSize = (int) this.basicFileAttributes.size();
            this.fileName = this.path.getFileName().toString();
        } catch (IOException e) {
            throw new NoSuchFileException("no such file, perhaps the wrong path was given");
        }
    }

    public int getFileSize() {
        return this.fileSize;
    }

    public String getFileName() {
        return this.fileName;
    }

    public String getContent() throws Exception {
        File file = new File(spath);
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String word;

        while ((word = bufferedReader.readLine()) != null) {
            this.content += word;
        }
        return content;
    }
}