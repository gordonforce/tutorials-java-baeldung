package io.leftcoast.tutorials.java.baeldung.collections;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.List;
import java.util.Map;

@Execution(ExecutionMode.CONCURRENT)
class ListOfMapsIteratorTest {

  private static final Map<ListOfMapsIterator.Type, ListOfMapsIterator> ITERATORS =
      ListOfMapsIterator.defaultByType();

  private static final List<Map<String, Object>> LIST_OF_MAPS =
      List.of(Map.of("name", "Jack", "age", 30), Map.of("name", "Jones", "age", 25));

  private static final int EXPECTED_VISITS =
      LIST_OF_MAPS.size() + LIST_OF_MAPS.stream().map(Map::size).mapToInt(Integer::intValue).sum();

  @BeforeAll
  static void  sanityCheck() {
    assertThat(EXPECTED_VISITS).isEqualTo(6);
  }

  @ParameterizedTest
  @EnumSource(ListOfMapsIterator.Type.class)
  void verifyIterationVisits(final ListOfMapsIterator.Type type) {

    final ListOfMapsIterator iterator = ITERATORS.get(type);

    assertThat(iterator.iterate(LIST_OF_MAPS)).isEqualTo(EXPECTED_VISITS);
  }
}
