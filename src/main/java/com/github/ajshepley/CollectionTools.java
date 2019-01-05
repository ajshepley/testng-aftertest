package com.github.ajshepley;

import java.util.Collection;
import java.util.Objects;

public class CollectionTools {

  public static boolean isNullOrEmpty(final Collection<?> collection) {
    return collection == null || collection.isEmpty();
  }

  public static <T extends Throwable> Throwable expectException(
      final Class<T> expected,
      final Runnable toExecute
  ) {
    try {
      toExecute.run();
    } catch (final Throwable result) {
      if (!Objects.equals(result.getClass(), expected)) {
        throw new RuntimeException(
            "Unexpected exception returned. Expected: "
            + expected
            + ", found: "
            + result
        );
      } else {
        return result;
      }
    }

    return null;
  }
}
