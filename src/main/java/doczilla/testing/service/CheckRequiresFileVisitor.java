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

/**
 * Класс для обхода дерева файлов.
 */
public class CheckRequiresFileVisitor extends SimpleFileVisitor<Path> {

    /**
     * Путь до корневого каталога
     */
    private final Path rootPath;

    /**
     * Map, где ключ - путь до файла от корневого каталога,
     * значение - {@link FileWithRequires} с файлом, лежащим по этому пути.
     */
    private final Map<String, FileWithRequires> filesByName = new HashMap<>();

    public CheckRequiresFileVisitor(Path rootPath) {
        this.rootPath = rootPath;
    }

    /**
     * Обновляет структуру filesByName.
     * Добавляет в структуру два файла, где child зависит от parent и устанавливает эту зависимость.
     *
     * @param childPath путь до файла, который зависит от parent, от корневого каталога.
     * @param parentName путь до файла, от которого зависит child, от корневого каталога
     *                   в строковом представлении.
     */
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

    /**
     * Переопределяет метод {@link SimpleFileVisitor}.
     * <p>
     * При обходе дерева файлов находит файл со строкой формата
     * 'require '<путь к другому файлу от корневого каталога>'',
     * обновляет коллекцию файлов, устанавливая зависимости между ними.
     * <p>
     * В случае ненахождения в файле строки с зависимостями, обновляет коллекцию, если в ней нет данного файла.
     *
     * @param file
     *          a reference to the file
     * @param attrs
     *          the file's basic attributes
     *
     * @return CONTINUE
     * @throws IOException ошибка чтения файла
     */
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

    /**
     * @return список файлов с установленными зависимостями
     */
    public List<FileWithRequires> getFiles() {
        return filesByName.values().stream().toList();
    }
}
