package com.github.gordonforce.tutorials.java.baeldung.file.permissions;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.gordonforce.tutorials.java.baeldung.file.FileOperations;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@Execution(ExecutionMode.SAME_THREAD)
class FilePermissionsTest {

  private static final Map<PosixFilePermission, String> FILE_PATHS =
      Map.of(
          PosixFilePermission.OWNER_READ, ".owner_read",
          PosixFilePermission.OWNER_WRITE, ".owner_write");

  private static final FileOperations DEFAULT_OPS = FileOperations.defaults();

  private static final Map<PermissionsChecks.ImplementationType, PermissionsChecks> CHECKS_BY_TYPE =
      PermissionsChecks.implementations();

  private void setUp() {

    FILE_PATHS.forEach(
        (permission, pathAsString) ->
            Optional.of(pathAsString)
                .map(Paths::get)
                .ifPresent(
                    path ->
                        Stream.of(
                                DEFAULT_OPS.createDirectory(path),
                                DEFAULT_OPS.setPermission(path, permission))
                            .forEach(Runnable::run)));
  }

  private void tearDown() {

    FILE_PATHS.values().stream()
        .map(Paths::get)
        .map(DEFAULT_OPS::destroyDirectory)
        .forEach(Runnable::run);
  }

  @ParameterizedTest
  @EnumSource(PermissionsChecks.ImplementationType.class)
  @DisplayName("Verify if a directory is readable")
  void verifyIsReadableDirectory(final PermissionsChecks.ImplementationType type) {

    final PermissionsChecks ops = CHECKS_BY_TYPE.get(type);
    final String dirPath = FILE_PATHS.get(PosixFilePermission.OWNER_READ);

    try {
      setUp();
      assertThat(ops.isReadableDirectory(dirPath)).isTrue();
    } finally {
      tearDown();
    }
  }

  @ParameterizedTest
  @EnumSource(PermissionsChecks.ImplementationType.class)
  @DisplayName("Verify if a directory is writeable")
  void verifyIsWriteableDirectory(final PermissionsChecks.ImplementationType type) {

    final PermissionsChecks ops = CHECKS_BY_TYPE.get(type);
    final String dirPath = FILE_PATHS.get(PosixFilePermission.OWNER_WRITE);

    try {
      setUp();
      assertThat(ops.isWriteableDirectory(dirPath)).isTrue();
    } finally {
      tearDown();
    }
  }
}
