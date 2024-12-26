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
    int vIndex, //Vertex index
    boolean[] visited, // Whether vertex has been visited
    final List<T> vertices, //Vertex array, polled by vertex index
    Stack<T> sorted, //Sorted vertices
    final Map<T, Integer> itemIdxMap //Map of item index to map
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

  public static boolean isCyclic(List<? extends TopologicalSortable> elements) {
    Set<TopologicalSortable> visited = new HashSet<>();
    Map<TopologicalSortable, Boolean> recStack = new HashMap<>();
    elements.forEach(elem -> recStack.put(elem, false));

    for (TopologicalSortable element : elements)
    {
      if(!visited.contains(element) && isCyclicRecur(element, visited, recStack))
      {
        return true;
      }
    }

    return false;
  }

  public static boolean isCyclicRecur(
    TopologicalSortable elem,
    Set<TopologicalSortable> visited,
    Map<TopologicalSortable, Boolean> recStack
  )
  {
    if (!visited.contains(elem))
    {
      //Mark visited & in the stack
      visited.add(elem);
      recStack.put(elem, true);

      //Search all the neighbours
      for (TopologicalSortable dependency : elem.getDependencies())
      {
        //Identified a back edge of a neighbour
        if(!visited.contains(dependency) &&
          isCyclicRecur(dependency, visited, recStack))
        {
          return true;
        }
        //Identified a back edge
        else if (recStack.get(dependency))
        {
          return true;
        }
      }
    }

    //Pull out of stack
    recStack.put(elem, false);
    return false;
  }


}
