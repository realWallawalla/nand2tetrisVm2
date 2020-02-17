package com.timonsarakinis.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

public class FileReaderWriter {

    public static final String DIR_PATH = "src/main/resources/asmfiles/";

    public static List<Path> getPaths(String path) {
        List<Path> filePaths = new ArrayList<>();

        try (Stream<Path> paths = Files.walk(Paths.get(path))) {
            filePaths = paths
                    .filter(p -> isPathVmFile(p.getFileName().toString()))
                    .collect(Collectors.toList());
        } catch (IOException | ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        return filePaths;
    }

    private static boolean isPathVmFile(String fileName) {
        return fileName.indexOf('.') != -1 && fileName.substring(fileName.lastIndexOf('.')).equals(".vm");
    }

    public static List<String> readFile(Path filePath) {
        try {
            BufferedReader reader = Files.newBufferedReader(filePath);
            System.out.println("Successfully read vm file");
            return reader.lines().collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public static void writeToFile(byte[] line, Path path) {
        try {
            Files.write(path, line, CREATE, APPEND);
            System.out.println("Successfully written line to output file");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createDirectory() {
        Path path = Paths.get(DIR_PATH);
        try {
            if (!Files.exists(path)) {
                Files.createDirectory(path);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

        public static Path getOutputPath(String fileName) {
            Path path = Paths.get(DIR_PATH + fileName);
            if (Files.exists(path)) {
                try {
                    Files.delete(path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return path;
        }
    }
