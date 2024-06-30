package com.github.gordonforce.tutorials.java.baeldung.file.linuxpaths;

import java.util.regex.Pattern;

public interface PathMatcher {

  default Pattern pattern() {

    // Modified from Baeldung's original solution to avoid stack overflow errors with large inputs
    return Pattern.compile("^/$|^/(?:[\\w-]++/)*+[\\w-]++$");
  }
}
