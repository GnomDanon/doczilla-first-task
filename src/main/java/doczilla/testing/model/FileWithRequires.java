package doczilla.testing.model;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileWithRequires {
    private final Path path;
    private final List<FileWithRequires> children = new ArrayList<>();

    public FileWithRequires(Path path) {
        this.path = path;
    }

    public Path getPath() {
        return path;
    }

    public void setChild(FileWithRequires file) {
        children.add(file);
    }

    public List<FileWithRequires> getChildren() {
        return new ArrayList<>(children);
    }
}
