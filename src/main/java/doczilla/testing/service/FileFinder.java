package doczilla.testing.service;

import doczilla.testing.exception.CyclicalDependencyException;
import doczilla.testing.model.FileWithRequires;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Класс, обнаруживающий и сортирующий файлы.
 */
public class FileFinder {

    /**
     * Путь до корневого каталога.
     */
    private final Path rootPath;

    /**
     * Список файлов.
     */
    private List<FileWithRequires> files;

    public FileFinder(Path rootPath) {
        this.rootPath = rootPath;
    }

    /**
     * Запускает процесс поиска и сортировки файлов
     * @return отсортированный список файлов
     * @throws IOException ошибка чтения файла
     * @throws CyclicalDependencyException ошибка, возникающая при обнаружении циклической зависимости
     */
    public List<FileWithRequires> run() throws IOException, CyclicalDependencyException {
        findFiles();
        return getSortedFiles();
    }

    /**
     * Запускает процесс поиска файлов
     * @throws IOException ошибка чтения файла
     */
    private void findFiles() throws IOException {
        CheckRequiresFileVisitor fileVisitor = new CheckRequiresFileVisitor(rootPath);
        Files.walkFileTree(rootPath, fileVisitor);
        files = fileVisitor.getFiles();
    }

    /**
     * Сортирует найденные файлы с помощью алгоритма топологической сортировки.
     * @return Отсортированный список файлов
     * @throws CyclicalDependencyException ошибка, возникающая при обнаружении циклической зависимости.
     */
    private List<FileWithRequires> getSortedFiles() throws CyclicalDependencyException {
        Map<FileWithRequires, Integer> degreesOfOccurrence = new HashMap<>();
        for (FileWithRequires file : files) {
            degreesOfOccurrence.put(file, 0);
        }

        for (FileWithRequires file : files) {
            for (FileWithRequires child : file.getChildren()) {
                degreesOfOccurrence.put(child, degreesOfOccurrence.get(child) + 1);
            }
        }

        Queue<FileWithRequires> queue = new PriorityQueue<>(Comparator.comparing(file -> file.getPath().toString()));
        for (FileWithRequires file : files) {
            if (degreesOfOccurrence.get(file) == 0) {
                queue.add(file);
            }
        }

        List<FileWithRequires> sortedList = new ArrayList<>();

        while (!queue.isEmpty()) {
            FileWithRequires current = queue.poll();
            sortedList.add(current);

            for (FileWithRequires child : current.getChildren()) {
                degreesOfOccurrence.put(child, degreesOfOccurrence.get(child) - 1);
                if (degreesOfOccurrence.get(child) == 0) {
                    queue.add(child);
                }
            }
        }

        if (sortedList.size() != files.size()) {
            throw new CyclicalDependencyException("Обнаружен цикл");
        }

        return sortedList;
    }
}
