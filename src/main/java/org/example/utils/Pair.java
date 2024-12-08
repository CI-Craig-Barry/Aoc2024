package org.example.utils;

import java.util.*;

public class Pair<K, V>
  implements Map.Entry<K, V>
{
  public Pair(K key, V value)
  {
    this.key = key;
    this.value = value;
  }

  @Override
  public K getKey()
  {
    return key;
  }

  @Override
  public V getValue()
  {
    return value;
  }

  @Override
  public V setValue(V value)
  {
    this.value = value;
    return value;
  }

  public static <T> List<Pair<T, T>> makeAllPairs(List<T> items)
  {
    List<Pair<T, T>> pairs = new ArrayList<>();

    for(int i = 0; i < items.size(); i++)
    {
      for(int j = i + 1; j < items.size(); j++)
      {
        T item1 = items.get(i);
        T item2 = items.get(j);

        pairs.add(new Pair<>(item1, item2));
      }
    }

    return pairs;
  }

  public K getFirst()
  {
    return key;
  }

  public V getSecond()
  {
    return value;
  }

  private final K key;
  private V value;
}

