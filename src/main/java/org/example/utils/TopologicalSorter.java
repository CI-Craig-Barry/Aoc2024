//
// Copyright (c) 2024, Conserve It Pty. Ltd.
//

package org.example.utils;

import java.util.*;

public class TopologicalSorter
{
  public static <T extends TopologicalSortable> List<T> sort(final List<T> items)
  {
    int nVertices = items.size();

    Stack<T> stack = new Stack<>();

    boolean[] visited = new boolean[nVertices];

    HashMap<T, Integer> itemIdxMap = new HashMap<>();
    ListIterator<T> itemIter = items.listIterator();
    while (itemIter.hasNext())
    {
      int idx = itemIter.nextIndex();
      itemIdxMap.put(itemIter.next(), idx);
    }

    for (int i = 0; i < nVertices; i++)
    {
      if (!visited[i])
      {
        topologicalSortRecursive(i, visited, items, stack, itemIdxMap);
      }
    }


    List<T> results = new ArrayList<>(nVertices);
    while (!stack.empty())
    {
      //Add in reverse order
      results.add(0, stack.pop());
    }
    return results;
  }

  private static <T extends TopologicalSortable> void topologicalSortRecursive(
    int vIndex,
    boolean visited[],
    final List<T> vertices,
    Stack<T> sorted,
    final Map<T, Integer> itemIdxMap
  )
  {
    //Mark the current node as visited.
    visited[vIndex] = true;

    //Iterate all the vertices adjacent to this vertex
    T item = vertices.get(vIndex);
    Iterator<? extends TopologicalSortable> iter = item.getDependencies().iterator();

    while (iter.hasNext())
    {
      TopologicalSortable nextVertex = iter.next();

      int idx = itemIdxMap.getOrDefault(nextVertex, -1);

      if (idx == -1)
      {
        //If this dependency is not in the vertices input, simply ignore it
        continue;
      }

      if (!visited[idx])
      {
        topologicalSortRecursive(idx, visited, vertices, sorted, itemIdxMap);
      }
    }

    //Push current vertex to sorted which stores result
    sorted.push(item);
  }
}
