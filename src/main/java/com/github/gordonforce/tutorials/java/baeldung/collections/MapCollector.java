package com.github.gordonforce.tutorials.java.baeldung.collections;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface MapCollector {

  default Map<String, City> toMap(Stream<City> cities) {
    return cities.collect(Collectors.toMap(
        City::country,
        Function.identity()));
  }

  default Map<String, String> toMapWithWithCityNameValue(Stream<City> cities) {
    return cities.collect(Collectors.toMap(
        City::country,
        City::name));
  }

  default Map<String, City> toMapWithMerge(Stream<City> cities) {
    return cities.collect(Collectors.toMap(
        City::country,
        Function.identity(),
        (c1, c2) -> c1.name().compareTo(c2.name()) < 0 ? c1 : c2));
  }

  default Map<String, List<City>> groupingBy(Stream<City> cities) {
    return cities
        .collect(Collectors.groupingBy(City::country));
  }
}
