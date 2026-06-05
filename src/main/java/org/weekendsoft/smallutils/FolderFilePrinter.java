package org.weekendsoft.smallutils;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class FolderFilePrinter {

    public static final char TAB = '\t';

    public static String getFileExtension(Path filePath) {
        String fileName = filePath.getFileName().toString();
        int lastDot = fileName.lastIndexOf('.');
        
        if (lastDot > 0 && lastDot < fileName.length() - 1) {
            return fileName.substring(lastDot + 1).toLowerCase();
        }
        return ""; // No extension
    }

    public static boolean isEBook(String ext) {
        Set<String> ebookExt = new HashSet<>(Arrays.asList(
            "epub", "pdf", "mobi", "azw", "azw3", "fb2", "txt", "rtf"
        ));

        return ebookExt.contains(ext);
    }

    public static boolean isEBook(Path filePath) {
        return isEBook(getFileExtension(filePath));
    }
    public static void main(String[] args) throws Exception {

        Path startPath = Paths.get("/Users/vivekkant/temp/Final");
        List<String> records = new ArrayList<>();
        records.add("\"Author\",\"Book\",\"Type\"");

        try (Stream<Path> files = Files.walk(startPath)) {
            files.filter(Files::isRegularFile)
                .forEach(file -> {

                    if (isEBook(file)) {
                        Path relativePath = startPath.relativize(file);
                        String firstSubfolder = relativePath.getName(0).toString();

                        String fileName = file.getFileName().toString();
                        String fileNameWithoutExtension = fileName.substring(0, fileName.lastIndexOf('.'));

                        String ext = getFileExtension(file);
                        String line = "\"" +firstSubfolder + "\",\"" + fileNameWithoutExtension + "\",\"" + ext + "\"";
                        
                        System.out.println(line);
                        records.add(line);
                    }

                });
        }

        Path parentFolder = startPath.getParent();
        Path csv = parentFolder.resolve("books.csv");

        try (OutputStream os = Files.newOutputStream(csv)) {
            for (String line : records) {
                os.write(line.getBytes());
                os.write("\n".getBytes());
            }
            os.flush();
        }

    }

}
