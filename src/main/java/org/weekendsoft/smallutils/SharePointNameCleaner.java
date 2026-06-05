package org.weekendsoft.smallutils;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.regex.Pattern;

public class SharePointNameCleaner {

    // Characters prohibited in SharePoint names (except & which we treat separately)
    private static final Pattern INVALID_CHARS = Pattern.compile("[\"#%*:<>{}?/\\\\|~]");
    private static final Pattern AMPERSAND = Pattern.compile("&");

    // Windows reserved names (case-insensitive)
    private static final String[] RESERVED_NAMES = {
        "CON", "PRN", "AUX", "NUL",
        "COM1","COM2","COM3","COM4","COM5","COM6","COM7","COM8","COM9",
        "LPT1","LPT2","LPT3","LPT4","LPT5","LPT6","LPT7","LPT8","LPT9"
    };

    private static BufferedWriter csvWriter;

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: java SharePointNameCleanerCSV <folder-path>");
            System.exit(1);
        }

        Path root = Paths.get(args[0]);
        if (!Files.isDirectory(root)) {
            System.err.println("Provided path is not a directory: " + root);
            System.exit(1);
        }

        Path csvFile = root.resolve("rename_audit.csv");
        csvWriter = Files.newBufferedWriter(csvFile);
        csvWriter.write("Original Path,New Path");
        csvWriter.newLine();

        // Walk bottom-up so files are renamed before their parent directories
        Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                renameIfInvalid(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                renameIfInvalid(dir);
                return FileVisitResult.CONTINUE;
            }
        });

        csvWriter.close();
        System.out.println("Cleaning completed. Audit log: " + csvFile.toAbsolutePath());
    }

    private static void renameIfInvalid(Path path) throws IOException {
        Path parent = path.getParent();
        String name = path.getFileName().toString();

        String cleaned = cleanName(name);

        if (!name.equals(cleaned)) {
            Path newPath = parent.resolve(cleaned);
            int counter = 1;

            // Prevent overwriting existing file/folder
            while (Files.exists(newPath)) {
                String base = removeExtension(cleaned);
                String ext = getExtension(cleaned);
                newPath = parent.resolve(base + "_" + counter + ext);
                counter++;
            }

            Files.move(path, newPath);
            logRename(path, newPath);
            System.out.println("Renamed: " + path + " -> " + newPath);
        }
    }

    private static String cleanName(String name) {
        // Replace '&' with "and"
        String cleaned = AMPERSAND.matcher(name).replaceAll("and");

        // Replace other invalid chars with "_"
        cleaned = INVALID_CHARS.matcher(cleaned).replaceAll("_");

        // Remove trailing spaces or periods
        cleaned = cleaned.replaceAll("[\\s.]+$", "");

        // Avoid reserved Windows names
        for (String reserved : RESERVED_NAMES) {
            if (cleaned.equalsIgnoreCase(reserved) || cleaned.matches("(?i)" + reserved + "\\..*")) {
                cleaned = "_" + cleaned;
                break;
            }
        }

        // Handle empty names
        if (cleaned.isBlank()) {
            cleaned = "_";
        }
        return cleaned;
    }

    private static void logRename(Path original, Path renamed) throws IOException {
        csvWriter.write("\"" + original.toAbsolutePath() + "\",\"" + renamed.toAbsolutePath() + "\"");
        csvWriter.newLine();
    }

    private static String removeExtension(String name) {
        int dotIndex = name.lastIndexOf('.');
        return (dotIndex > 0) ? name.substring(0, dotIndex) : name;
    }

    private static String getExtension(String name) {
        int dotIndex = name.lastIndexOf('.');
        return (dotIndex > 0) ? name.substring(dotIndex) : "";
    }
}
