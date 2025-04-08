package org.wharvex.fixjflap;

import java.io.File;

public class FileLoader {
  private File[] rootDirFiles;

  public FileLoader(String rootDirAbsolutePath) {
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


}
