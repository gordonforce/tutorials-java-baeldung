package io.leftcoast.tutorials.baeldung.file;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;
import java.util.function.Supplier;

public interface FileOperations {

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

  static FileOperations defaults() {
    return new FileOperations() { };
  }
}
