package doczilla.testing;

import doczilla.testing.model.FileWithRequires;
import doczilla.testing.service.FileFinder;
import doczilla.testing.service.FileJoiner;

import java.nio.file.Path;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Path rootPath = Path.of(args[0]);
        Path outputPath = Path.of(args[1]);
        FileFinder fileFinder = new FileFinder(rootPath);
        try {
            List<FileWithRequires> files = fileFinder.run();
            for (FileWithRequires file : files) {
                System.out.println(file.getPath());
            }
            FileJoiner.join(files, rootPath, outputPath);
        } catch (Exception exception) {
            System.out.printf("%s: %s", exception.getClass().getName(), exception.getMessage());
        }
    }
}