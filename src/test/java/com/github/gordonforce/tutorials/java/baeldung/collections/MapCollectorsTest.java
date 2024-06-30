package com.github.gordonforce.tutorials.java.baeldung.collections;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.*;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

@Execution(ExecutionMode.CONCURRENT)
class MapCollectorsTest {

  private static final City ACCHEN = new City("Aachen", "Germany");
  private static final City BERLIN = new City("Berlin", "Germany");
  private static final City HAMBURG = new City("Hamburg", "Germany");
  private static final City NEW_YORK = new City("New York", "USA");
  private static final City NICE = new City("Nice", "France");
  private static final City PARIS = new City("Paris", "France");
  private static final City TOKYO = new City("Tokyo", "Japan");
  private static final City UNKNOWN = new City("Unknown", null);

  private static  final City FRANCE_UNKNOWN = new City(null, "France");

  private static final Collection<City> ONE_PER_COUNTRY_CITIES =
      List.of(NEW_YORK, PARIS, BERLIN, TOKYO);

  private static final Collection<City> MULTIPLE_PER_COUNTRY_CITIES =
      Stream.concat(ONE_PER_COUNTRY_CITIES.stream(), Stream.of(ACCHEN, HAMBURG, NICE)).toList();

  private static final MapCollector MAP_COLLECTOR = new MapCollector() {};

  @Test
  @DisplayName("Verify toMap with non-null countries")
  void verifyToMapWithNonNullCountries() {

    final Map<String, City> byCountry = MAP_COLLECTOR.toMap(ONE_PER_COUNTRY_CITIES.stream());

    Assertions.assertThat(byCountry)
        .isEqualTo(
            Map.of(
                "Germany", BERLIN,
                "France", PARIS,
                "Japan", TOKYO,
                "USA", NEW_YORK));
  }

  @Test
  @DisplayName("Verify toMap with one null country")
  void verifyToMapWithOneNullCountry() {

    final Map<String, City> byCountry =
        MAP_COLLECTOR.toMap(Stream.concat(ONE_PER_COUNTRY_CITIES.stream(), Stream.of(UNKNOWN)));

    Assertions.assertThat(byCountry)
        .isEqualTo(
            new HashMap<>(
                Map.of(
                    "Germany", BERLIN,
                    "France", PARIS,
                    "Japan", TOKYO,
                    "USA", NEW_YORK)) {
              {
                put(null, UNKNOWN);
              }
            });
  }

  @Test
  @DisplayName(
      "Verify toMap throws an IllegalStateException with duplicate countries in the stream")
  void verifyToMapThrowExceptionWithDuplicateCountries() {

    final Stream<City> cities = MULTIPLE_PER_COUNTRY_CITIES.stream();

    assertThatThrownBy(() -> MAP_COLLECTOR.toMap(cities))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("Duplicate key");
  }

  @Test
  @DisplayName("Verify toMap with merge function can handle duplicate countries in the stream")
  void verifyToMapWithMergeFunctionCanDuplicateCountries() {

    final Stream<City> cities = MULTIPLE_PER_COUNTRY_CITIES.stream();

    assertThat(MAP_COLLECTOR.toMapWithMerge(cities))
        .isEqualTo(
            Map.of(
                "Germany", ACCHEN,
                "France", NICE,
                "Japan", TOKYO,
                "USA", NEW_YORK));
  }

  @Test
  @DisplayName("Verify toMap with city name value")
  void verifyToMapWithCityNameValue() {

    final Map<String, String> byCountry =
        MAP_COLLECTOR.toMapWithWithCityNameValue(ONE_PER_COUNTRY_CITIES.stream());

    assertThat(byCountry)
        .isEqualTo(
            Map.of(
                "Germany", BERLIN.name(),
                "France", PARIS.name(),
                "Japan", TOKYO.name(),
                "USA", NEW_YORK.name()));
  }

  @Test
  @DisplayName("Verify toMap with city name value throws exception with a null city name value")
  void verifyToMapWithANullCityNameValueThrowsException() {

    final Stream<City> cities = Stream.of(PARIS, FRANCE_UNKNOWN);

    assertThatThrownBy(() -> MAP_COLLECTOR.toMapWithWithCityNameValue(cities))
        .isInstanceOf(NullPointerException.class)
        .hasNoCause();
  }

  @Test
  @DisplayName("Verify groupingBy with non-null countries")
  void verifyGroupingByWithNonNullCountries() {

    final Map<String, List<City>> byCountry =
        MAP_COLLECTOR.groupingBy(ONE_PER_COUNTRY_CITIES.stream());

    assertThat(byCountry)
        .isEqualTo(
            Map.of(
                "Germany", List.of(BERLIN),
                "France", List.of(PARIS),
                "Japan", List.of(TOKYO),
                "USA", List.of(NEW_YORK)));
  }

  @Test
  @DisplayName("Verify groupingBy with non-null countries and some with multiple cities")
  void verifyGroupingByWithNonNullCountriesAndSomeWithMultipleCities() {

    assertThat(MAP_COLLECTOR.groupingBy(MULTIPLE_PER_COUNTRY_CITIES.stream()))
        .isEqualTo(
            Map.of(
                "Germany", List.of( BERLIN, ACCHEN,HAMBURG),
                "France", List.of(PARIS, NICE),
                "Japan", List.of(TOKYO),
                "USA", List.of(NEW_YORK)));
  }

  @Test
  @DisplayName("Verify groupingBy with non-null countries and one null country")
  void verifyGroupingByWithNullCountryThrowsException() {

    final Stream<City> withNullCountry = Stream.concat(ONE_PER_COUNTRY_CITIES.stream(), Stream.of(UNKNOWN));


    assertThatThrownBy(() -> MAP_COLLECTOR.groupingBy(withNullCountry))
        .isInstanceOf(NullPointerException.class)
        .hasNoCause();
  }
}
