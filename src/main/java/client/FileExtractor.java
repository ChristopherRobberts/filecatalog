package client;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;

public class FileExtractor {
    private BasicFileAttributes basicFileAttributes;
    private Path path;

    public FileExtractor(String path) throws IOException {
        try {
            this.path = Paths.get(path);
            basicFileAttributes = Files.readAttributes(this.path, BasicFileAttributes.class);
        } catch (IOException e) {
            throw new NoSuchFileException("no such file, perhaps the wrong path was given");
        }
    }

    public int getFileSize() {
        return (int) this.basicFileAttributes.size();
    }

    public String getFileName() {
        return this.path.getFileName().toString();
    }
}
