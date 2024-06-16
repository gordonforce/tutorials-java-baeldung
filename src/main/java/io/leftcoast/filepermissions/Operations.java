package io.leftcoast.filepermissions;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

public interface Operations {

  default Runnable createDirectory(final Path path) {
    return () -> {
      try {
        java.nio.file.Files.createDirectory(path);
      } catch (java.io.IOException e) {
        throw new IllegalStateException(
            String.format("Should not occur when creating a directory %s%n", path.toAbsolutePath()),
            e);
      }
    };
  }

  default Runnable destroyDirectory(final Path path) {
    return () -> {
      try {
        java.nio.file.Files.delete(path);
      } catch (java.io.IOException e) {
        throw new IllegalStateException(
            String.format("Should not occur when deleting a directory %s%n", path.toAbsolutePath()),
            e);
      }
    };
  }

  default Runnable setPermission(final Path path, PosixFilePermission permission) {

    return () -> {
      try {

        final Supplier<Set<PosixFilePermission>> permissions =
            () -> {
              try {
                final Set<PosixFilePermission> permissionsSet = Files.getPosixFilePermissions(path);
                permissionsSet.add(permission);
                return permissionsSet;
              } catch (java.io.IOException e) {
                throw new IllegalStateException(
                    String.format(
                        "Should not occur when getting permissions for directory %s%n",
                        path.toAbsolutePath()),
                    e);
              }
            };

        Files.setPosixFilePermissions(path, permissions.get());

      } catch (java.io.IOException e) {
        throw new IllegalStateException(
            String.format(
                "Should not occur when setting permission %s on directory %s%n",
                permission, path.toAbsolutePath()),
            e);
      }
    };
  }

  default boolean hasPermission(final Path path, PosixFilePermission permission) {

    try {
      return Files.getPosixFilePermissions(path).contains(permission);
    } catch (java.io.IOException e) {
      throw new IllegalStateException(
          String.format(
              "Should not occur when getting permissions for directory %s%n",
              path.toAbsolutePath()),
          e);
    }
  }

  default boolean isReadableDirectory(final String pathAsString) {
    return Optional.of(pathAsString)
        .map(Paths::get)
        .map(path -> Files.isDirectory(path) && hasPermission(path, PosixFilePermission.OWNER_READ))
        .orElse(false);
  }

  default boolean isWriteableDirectory(final String pathAsString) {
    return Optional.of(pathAsString)
        .map(Paths::get)
        .map(
            path -> Files.isDirectory(path) && hasPermission(path, PosixFilePermission.OWNER_WRITE))
        .orElse(false);
  }

  default Map<ImplementationType, Operations> implementations() {
    return new EnumMap<>(
        Map.of(
            ImplementationType.JAVA_IO,
                new Operations() {

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
            ImplementationType.JAVA_NIO, new Operations() {}));
  }
}
