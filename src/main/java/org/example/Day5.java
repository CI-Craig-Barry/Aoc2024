package org.example;

import org.example.utils.*;

import java.util.*;

public class Day5
{
  public static int task1(List<String> lines)
  {
    Map<Integer, TopologicalSortable> dependencies = mapDependencies(lines);
    List<ManualOrder> manualOrders = makeOrderings(lines);

    int result = 0;
    for (ManualOrder manualOrder : manualOrders)
    {
      if(manualOrder.isOrderedCorrectly(dependencies))
      {
        System.out.println(manualOrder.toString() + " is correct");
        result += manualOrder.getMiddlePage();
      }
      else
      {
        System.out.println(manualOrder.toString() + " is incorrect");
      }
    }

    return result;
  }

  public static int task2(List<String> lines)
  {
    Map<Integer, TopologicalSortable> dependencies = mapDependencies(lines);
    List<ManualOrder> manualOrders = makeOrderings(lines);

    int result = 0;
    for (ManualOrder manualOrder : manualOrders)
    {
      if(!manualOrder.isOrderedCorrectly(dependencies))
      {
        List<Integer> orderedPages = manualOrder.orderPages(dependencies);
        System.out.println(manualOrder.ordering + " becomes " + orderedPages);

        int middleNumber = orderedPages.get(orderedPages.size() / 2);
        result += middleNumber;
      }
    }

    return result;
  }

  public static Map<Integer, TopologicalSortable> mapDependencies(List<String> lines)
  {
    Map<Integer, TopologicalSortable> results = new HashMap<>();

    for (String line : lines)
    {
      if(line.trim().isEmpty())
      {
        return results;
      }

      String[] tokens = line.split("\\|");
      assert tokens.length == 2;

      //Y depends on X
      System.out.println(Arrays.toString(tokens));
      int x = Integer.parseInt(tokens[0]);
      int y = Integer.parseInt(tokens[1]);

      TopologicalSortable ySortable = results.getOrDefault(y, new TopologicalSortable(y));
      TopologicalSortable xSortable = results.getOrDefault(x, new TopologicalSortable(x));

      ySortable.addDependency(xSortable);
      results.put(y, ySortable);
      results.put(x, xSortable);
    }

    throw new RuntimeException("Should have hit empty line");
  }

  public static List<ManualOrder> makeOrderings(List<String> lines)
  {
    List<ManualOrder> results = new ArrayList<>();

    Iterator<String> iter = lines.iterator();
    //Iterate until blank line
    while(true)
    {
      if(iter.next().trim().isEmpty())
      {
        break;
      }
    }

    while(iter.hasNext())
    {
      String orderString = iter.next();
      ManualOrder order = new ManualOrder();
      order.line = orderString;

      String[] tokens = orderString.split(",");
      List<Integer> ordering = new ArrayList<>();
      for (String token : tokens)
      {
        ordering.add(Integer.parseInt(token));
      }
      order.ordering = ordering;
      results.add(order);
    }

    return results;
  }

  public static class ManualOrder
  {
    public boolean isOrderedCorrectly(Map<Integer, TopologicalSortable> dependencyMap)
    {
      Set<Integer> usedPages = new HashSet<>();

      var iter = ordering.listIterator(ordering.size());
      while(iter.hasPrevious())
      {
        int page = iter.previous();

        TopologicalSortable dependencies = dependencyMap.getOrDefault(page, new TopologicalSortable(page));
        //If this page has already been used it is not in the correct order
        if(usedPages.stream().anyMatch(usedPage -> dependencies.getDependencyItems().contains(usedPage)))
        {
          return false;
        }

        usedPages.add(page);
      }

      return true;
    }

    public int getMiddlePage()
    {
      return ordering.get(ordering.size() / 2);
    }

    public List<Integer> orderPages(Map<Integer, TopologicalSortable> dependencyMap)
    {
      List<TopologicalSortable> topologicalItems = new ArrayList<>();

      for (Integer page : ordering)
      {
        topologicalItems.add(dependencyMap.getOrDefault(page, new TopologicalSortable(page)));
      }

      return TopologicalSorter.sort(topologicalItems)
        .stream()
        .map(TopologicalSortable::getItem)
        .toList();
    }

    @Override
    public String toString()
    {
      return "ManualOrder{" +
        "line='" + line + '\'' +
        ", ordering=" + ordering +
        '}';
    }

    private String line;
    private List<Integer> ordering;
  }
}
