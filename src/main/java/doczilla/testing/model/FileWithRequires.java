package doczilla.testing.model;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс с информацией о файле.
 */
public class FileWithRequires {

    /**
     * Путь до файла.
     */
    private final Path path;

    /**
     * Список файлов, зависящих от данного
     */
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
