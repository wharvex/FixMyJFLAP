package org.wharvex.fixjflap;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileLoader {
  private String rootDirPath;
  private File[] rootDirFiles;

  public FileLoader(String rootDirAbsolutePath) {
    this.rootDirPath = rootDirAbsolutePath;
    File rootDir = new File(rootDirAbsolutePath);
    if (rootDir.isDirectory()) {
      rootDirFiles = rootDir.listFiles();
    } else {
      System.out.println("The provided path is not a directory.");
    }
  }

  // Search recursively through rootDirFiles and all subdirectories, returning a flattened list of all *.java files.
  public File[] getJavaFiles() {
    if (rootDirFiles == null) {
      return new File[0];
    }
    return getJavaFilesRecursively(rootDirFiles);
  }

  // Recursive method to search for *.java files in the directory and its subdirectories.
  private File[] getJavaFilesRecursively(File[] files) {
    File[] javaFiles = new File[0];
    for (File file : files) {
      if (file.isDirectory()) {
        File[] subDirFiles = file.listFiles();
        if (subDirFiles != null) {
          javaFiles =
              concat(javaFiles, getJavaFilesRecursively(subDirFiles));
        }
      } else if (file.getName().endsWith(".java")) {
        javaFiles = concat(javaFiles, new File[]{file});
      }
    }
    return javaFiles;
  }

  // Helper method to concatenate two arrays of File objects.
  private File[] concat(File[] array1, File[] array2) {
    File[] result = new File[array1.length + array2.length];
    System.arraycopy(array1, 0, result, 0, array1.length);
    System.arraycopy(array2, 0, result, array1.length, array2.length);
    return result;
  }

  // Helper method to get all lines from a file.
  public static List<String> getAllLines(File file) {
    try {
      return Files.readAllLines(file.toPath());
    } catch (IOException e) {
      throw new RuntimeException(
          "Error reading file: " + file.getAbsolutePath(), e);
    }
  }

  public static void replaceLine(Path filePath, int lineNumber,
                                 String newLine) {
    List<String> lines = null;
    try {
      lines = Files.readAllLines(filePath);
      if (lineNumber > 0 && lineNumber <= lines.size()) {
        lines.set(lineNumber - 1, newLine);
        Files.write(filePath, lines);
      } else {
        throw new IllegalArgumentException("Invalid line number");
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static void replaceLinesInFiles(File[] files,
                                         String targetLineBase,
                                         String newLineBase) {
    for (File file : files) {
      var lines = FileLoader.getAllLines(file);
      var targetLineNums = new ArrayList<Integer>();
      var lastLineNum = lines.size();
      for (int i = 0; i < lastLineNum; i++) {
        String line = lines.get(i);
        if (line.startsWith(targetLineBase)) {
          targetLineNums.add(i + 1);
        }
      }
      for (int targetLineNum : targetLineNums) {
        var targetLine = lines.get(targetLineNum - 1);
          var newLine = targetLine.replace(targetLineBase, newLineBase);
          FileLoader.replaceLine(file.toPath(), targetLineNum, newLine);
        }
      }
    }
  }


}
