package com.github.gordonforce.tutorials.java.baeldung.collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@FunctionalInterface
public interface ListOfMapsIterator {

  enum Type {
    FOR_WITH_KEY_SET,
    FOR_EACH,
    STREAM,
  }

  int iterate(List<Map<String, Object>> listOfMaps);

  static Map<Type, ListOfMapsIterator> defaultByType() {

    final Logger logger = LoggerFactory.getLogger(ListOfMapsIterator.class);

    final String logTemplate = "Visiting key='{}' value='{} impl='{}'";

    return new EnumMap<>(
        Map.of(
            Type.FOR_WITH_KEY_SET,
            lom -> {
              int visits = 0;
              for (Map<String, Object> map : lom) {
                visits++;

                for (String key : map.keySet()) { // NOSONAR - use of keySet() per requirements
                  visits++;
                  logger.info(logTemplate, key, map.get(key), Type.FOR_WITH_KEY_SET);
                }
              }
              return visits;
            },
            Type.FOR_EACH,
            lom -> {
              final AtomicInteger visits = new AtomicInteger(0);
              lom.forEach(
                  map -> {
                    visits.incrementAndGet();
                    map.forEach(
                        (key, value) -> {
                          visits.incrementAndGet();
                          logger.info(logTemplate, key, value, Type.FOR_EACH);
                        });
                  });

              return visits.get();
            },
            Type.STREAM,
            lom ->
                lom.stream()
                    .map(
                        map ->
                            map.entrySet().stream()
                                .map(
                                    entry -> {
                                      logger.info(
                                          logTemplate,
                                          entry.getKey(),
                                          entry.getValue(),
                                          Type.STREAM);
                                      return 1;
                                    })
                                .mapToInt(Integer::intValue)
                                .sum())
                    .mapToInt(Integer::intValue)
                    .map(pop -> pop + 1)
                    .sum()));
  }
}
