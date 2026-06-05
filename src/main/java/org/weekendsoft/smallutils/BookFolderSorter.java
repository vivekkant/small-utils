package org.weekendsoft.smallutils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BookFolderSorter {

    private static final Logger log = LoggerFactory.getLogger(BookFolderSorter.class);

    private static Path getDestinationPath(Path source) throws IOException {

        String filename = source.getFileName().toString();
        log.info("Filename: {}", filename);

        String foldername = getFolderNameFromFile(filename);
        Path folder = source.getParent().resolve(foldername);
        Files.createDirectories(folder);

        Path destination = folder.resolve(filename);
        log.info("Destination {}", destination);

        return destination;

    }

    private static String getFolderNameFromFile(String filename) {
        if (filename.indexOf('-') == -1) return "_default";
        String foldername = filename.substring(0, filename.indexOf('-'));
        return foldername.trim();
    }

    public static void main(String[] args) throws Exception {

        Path startPath = Paths.get("/Users/vivekkant/temp/Kindle2");

        try (Stream<Path> files = Files.list(startPath)) {
            files.filter(Files::isRegularFile)  // Only files, not directories
                .forEach(file -> {
                    try {
                        
                        Path destination = getDestinationPath(file);
                        log.info("Moving {} to destination", file.getFileName());
                        Files.move(file, destination, StandardCopyOption.REPLACE_EXISTING);


                    } catch (IOException e) {
                        log.error("Exception while moving", e);
                    }
                });
        }

    }

}
