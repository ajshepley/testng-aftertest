package com.github.ajshepley;

import java.util.Collection;

public class CollectionTools {
  public static boolean isNullOrEmpty(final Collection<?> collection) {
    return collection == null || collection.isEmpty();
  }
}
