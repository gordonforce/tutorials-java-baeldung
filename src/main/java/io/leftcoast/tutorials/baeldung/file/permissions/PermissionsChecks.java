package io.leftcoast.tutorials.baeldung.file.permissions;

import io.leftcoast.tutorials.baeldung.file.FileOperations;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public interface PermissionsChecks {

  boolean isReadableDirectory(final String pathAsString);

  boolean isWriteableDirectory(final String pathAsString);

  static Map<ImplementationType, PermissionsChecks> implementations() {
    return new EnumMap<>(
        Map.of(
            ImplementationType.JAVA_IO,
                new PermissionsChecks() {

                  private boolean isPathMatching(
                      String path, final Function<File, Boolean> conditions) {
                    return Optional.of(path).map(File::new).map(conditions).orElse(false);
                  }

                  @Override
                  public boolean isReadableDirectory(final String path) {

                    return isPathMatching(
                        path, fileHandle -> fileHandle.isDirectory() && fileHandle.canRead());
                  }

                  @Override
                  public boolean isWriteableDirectory(final String path) {

                    return isPathMatching(
                        path, fileHandle -> fileHandle.isDirectory() && fileHandle.canWrite());
                  }
                },
            ImplementationType.JAVA_NIO,
                new PermissionsChecks() {

                  private static final FileOperations FILE_OPS = FileOperations.defaults();

                  @Override
                  public boolean isReadableDirectory(final String pathAsString) {

                    return Optional.of(pathAsString)
                        .map(Paths::get)
                        .filter(Files::isDirectory)
                        .map(path -> FILE_OPS.hasPermission(path, PosixFilePermission.OWNER_READ))
                        .orElse(false);
                  }

                  @Override
                  public boolean isWriteableDirectory(final String pathAsString) {

                    return Optional.of(pathAsString)
                        .map(Paths::get)
                        .filter(Files::isDirectory)
                        .map(path -> FILE_OPS.hasPermission(path, PosixFilePermission.OWNER_WRITE))
                        .orElse(false);
                  }
                }));
  }
}
