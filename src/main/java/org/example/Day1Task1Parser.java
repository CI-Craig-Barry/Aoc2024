package org.example;

import java.util.*;
import java.util.stream.Collectors;

public class Day1Task1Parser
{
  public static int parseLines(List<String> lines)
  {
    ListProcessingResult result = new ListProcessingResult();

    Collections.sort(result.leftList);
    Collections.sort(result.rightList);

    return calculateDistance(result.leftList, result.rightList);
  }

  public static ListProcessingResult processList(List<String> lines)
  {
    List<Integer> leftList = new ArrayList<>();
    List<Integer> rightList = new ArrayList<>();

    for (String line : lines)
    {
      String[] tokens = line.split(" ");

      String leftToken = tokens[0];
      String rightToken = tokens[tokens.length - 1];

      leftList.add(Integer.parseInt(leftToken));
      rightList.add(Integer.parseInt(rightToken));
    }

    ListProcessingResult result = new ListProcessingResult();
    result.leftList = leftList;
    result.rightList = rightList;

    return result;
  }

  private static int calculateDistance(List<Integer> list1, List<Integer> list2)
  {
    assert list1.size() == list2.size();

    int totalDistance = 0;
    int length = list1.size();

    for(int i = 0; i < length; i++)
    {
      int leftValue = list1.get(i);
      int rightValue = list2.get(i);
      int distance = Math.abs(rightValue - leftValue);
      totalDistance += distance;
    }

    return totalDistance;
  }

  public static class ListProcessingResult
  {
    List<Integer> leftList;
    List<Integer> rightList;
  }
}
