package io.leftcoast.tutorials.java.baeldung.file.linuxpaths;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.regex.Pattern;

@Execution(ExecutionMode.CONCURRENT)
class PathMatcherTest {

  private static final Pattern PATH_PATTERN = new PathMatcher() {}.pattern();

  @ParameterizedTest
  @ValueSource(strings = {"/", "/foo", "/foo/0", "/foo/0/bar", "/f_o_o/-/bar"})
  @DisplayName("Verify if a path matches the pattern")
  void verifyPathMatches(final String path) {

    assertThat(path).matches(PATH_PATTERN);
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "  ", "foo", "/foo/", "/foo/bar/", "/fo o/bar", "/foo/b@ar"})
  @DisplayName("Verify if a path does not match the pattern")
  void verifyPathDoesNotMatch(final String path) {

    assertThat(path).doesNotMatch(PATH_PATTERN);
  }
}
