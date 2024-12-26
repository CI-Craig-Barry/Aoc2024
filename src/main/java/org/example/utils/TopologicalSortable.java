package org.example.utils;

import java.util.*;
import java.util.stream.Collectors;

public class TopologicalSortable
{
  public TopologicalSortable(int item)
  {
    this.item = item;
  }

  public int getItem()
  {
    return item;
  }

  public Set<TopologicalSortable> getDependencies()
  {
    return dependencies;
  }

  public Set<Integer> getDependencyItems()
  {
    return dependencies.stream().map(TopologicalSortable::getItem).collect(Collectors.toSet());
  }

  public void clearDependencies()
  {
    dependencies.clear();
  }

  public void addDependency(TopologicalSortable dependency)
  {
    this.dependencies.add(dependency);
  }

  private final int item;
  private final Set<TopologicalSortable> dependencies = new HashSet<>();
}


