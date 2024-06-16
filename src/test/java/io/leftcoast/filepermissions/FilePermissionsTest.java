package io.leftcoast.filepermissions;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class FilePermissionsTest {

  private static final Map<PosixFilePermission, String> FILE_PATHS =
      Map.of(
          PosixFilePermission.OWNER_READ, ".owner_read",
          PosixFilePermission.OWNER_WRITE, ".owner_write");

  private static final Operations DEFAULT_OPS = new Operations() {};

  private static final Map<ImplementationType, Operations> OPERATIONS =
      DEFAULT_OPS.implementations();

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
  @EnumSource(ImplementationType.class)
  void verifyIsReadableDirectory(final ImplementationType type) {

    final Operations ops = OPERATIONS.get(type);
    final String dirPath = FILE_PATHS.get(PosixFilePermission.OWNER_READ);

    try {
      setUp();
      assertThat(ops.isReadableDirectory(dirPath)).isTrue();
    } finally {
      tearDown();
    }
  }

  @ParameterizedTest
  @EnumSource(ImplementationType.class)
  void verifyIsWriteableDirectory(final ImplementationType type) {

    final Operations ops = OPERATIONS.get(type);
    final String dirPath = FILE_PATHS.get(PosixFilePermission.OWNER_WRITE);

    try {
      setUp();
      assertThat(ops.isWriteableDirectory(dirPath)).isTrue();
    } finally {
      tearDown();
    }
  }
}
