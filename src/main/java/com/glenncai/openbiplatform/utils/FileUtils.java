package com.glenncai.openbiplatform.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.Optional;

/**
 * This class is for file common utils
 *
 * @author Glenn Cai
 * @version 1.0 07/28/2023
 */
public class FileUtils {

  private FileUtils() {
  }

  /**
   * To check if file size is less than max size
   *
   * @param file    file
   * @param maxSize max size
   * @param unit    unit (B, K, M, G)
   * @return true if file size is less than max size
   */
  public static boolean validFileSize(File file, int maxSize, String unit) {
    long bytes = file.length();
    double multiplier = switch (unit.toUpperCase()) {
      case "B" -> 1; // Bytes
      case "K" -> 1024; // Kilobytes
      case "M" -> 1024 * 1024; // Megabytes
      case "G" -> 1024 * 1024 * 1024; // Gigabytes
      default -> throw new IllegalArgumentException("Invalid unit: " + unit);
    };

    double fileSizeInUnits = bytes / multiplier;

    return fileSizeInUnits <= maxSize;
  }

  /**
   * To check if multipart file size is less than max size
   *
   * @param multipartFile multipart file
   * @param maxSize       max size
   * @param unit          unit (B, K, M, G)
   * @return true if multipart file size is less than max size
   */
  public static boolean validFileSize(MultipartFile multipartFile, int maxSize, String unit) {
    long bytes = multipartFile.getSize();
    double multiplier = switch (unit.toUpperCase()) {
      case "B" -> 1; // Bytes
      case "K" -> 1024; // Kilobytes
      case "M" -> 1024 * 1024; // Megabytes
      case "G" -> 1024 * 1024 * 1024; // Gigabytes
      default -> throw new IllegalArgumentException("Invalid unit: " + unit);
    };

    double fileSizeInUnits = bytes / multiplier;

    return fileSizeInUnits <= maxSize;
  }

  /**
   * To check if file extension is valid
   *
   * @param filename            file name
   * @param validFileExtensions valid file extensions
   * @return true if file extension is valid
   */
  public static boolean validFileExtension(String filename, List<String> validFileExtensions) {
    String extension = Optional.ofNullable(filename)
                               .filter(f -> f.contains("."))
                               .map(f -> f.substring(filename.lastIndexOf(".") + 1))
                               .orElse("");

    return validFileExtensions.contains(extension);
  }
}
