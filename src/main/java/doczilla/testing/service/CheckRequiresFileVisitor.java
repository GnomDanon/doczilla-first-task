package doczilla.testing.service;

import doczilla.testing.model.FileWithRequires;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class CheckRequiresFileVisitor extends SimpleFileVisitor<Path> {
    private final Path rootPath;
    private final Map<String, FileWithRequires> filesByName = new HashMap<>();

    public CheckRequiresFileVisitor(Path rootPath) {
        this.rootPath = rootPath;
    }

    private void updateFilesDependencies(Path childPath, String parentName) {
        FileWithRequires child, parent;
        Path parentPath = Path.of(parentName);

        if (filesByName.containsKey(childPath.toString())) {
            child = filesByName.get(childPath.toString());
        } else {
            child = new FileWithRequires(childPath);
            filesByName.put(childPath.toString(), child);
        }

        if (filesByName.containsKey(parentPath.toString())) {
            parent = filesByName.get(parentPath.toString());
        } else {
            parent = new FileWithRequires(parentPath);
            filesByName.put(parentPath.toString(), parent);
        }

        parent.setChild(child);
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        try (Stream<String> lines = Files.lines(file)) {
            lines.filter(line -> line.startsWith("require"))
                    .map(line -> {
                        String require = line.split(" ", 2)[1];
                        return require.substring(1, require.length() - 1);
                    })
                    .forEach(require -> updateFilesDependencies(rootPath.relativize(file), require));

        } catch (IOException exception) {
            throw new IOException(String.format("Ошибка чтения файла %s", file));
        }

        Path relativizePath = rootPath.relativize(file);
        if (!filesByName.containsKey(relativizePath.toString())) {
            filesByName.put(relativizePath.toString(), new FileWithRequires(relativizePath));
        }

        return FileVisitResult.CONTINUE;
    }

    public List<FileWithRequires> getFiles() {
        return filesByName.values().stream().toList();
    }
}
