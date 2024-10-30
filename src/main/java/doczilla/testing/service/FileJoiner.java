package doczilla.testing.service;

import doczilla.testing.model.FileWithRequires;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

/**
 * Класс, объединяющий файлы
 */
public class FileJoiner {

    /**
     * Копирует содержимое файлов в заданный файл
     * @param files список файлов
     * @param rootPath путь до корневого каталога
     * @param outputPath путь до файла, в который нужно скопировать содержимое файлов из списка
     * @throws IOException ошибка чтения или записи в файл
     */
    public static void join(List<FileWithRequires> files, Path rootPath, Path outputPath)
            throws IOException {
        for (FileWithRequires file : files) {
            Path filePath = rootPath.resolve(file.getPath());
            List<String> lines = Files.readAllLines(filePath);
            Files.write(outputPath, lines, StandardOpenOption.APPEND);
        }
    }
}
