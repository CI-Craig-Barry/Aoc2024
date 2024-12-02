package org.example;

import java.util.*;

public class Day1Task2Parser
{
  public static int run(List<String> lines)
  {
    var result = Day1Task1Parser.processList(lines);
    Collections.sort(result.rightList);
    Collections.sort(result.leftList);

    Map<Integer, Integer> similarityMap = new TreeMap<>();
    for (Integer value : result.leftList)
    {
      similarityMap.put(value, 0);
    }

    for (Integer value : result.rightList)
    {
      Integer numValues = similarityMap.get(value);

      if(numValues != null)
      {
        similarityMap.put(value, numValues + 1);
      }
    }

    for (Map.Entry<Integer, Integer> entry : similarityMap.entrySet())
    {
      System.out.println(entry.getKey() + ", " + entry.getValue());
    }

    return calculateSimularityScore(similarityMap);
  }

  private static int calculateSimularityScore(Map<Integer, Integer> simularityMap)
  {
    int simularityScore = 0;

    for (var entry : simularityMap.entrySet())
    {
      int score = entry.getKey() * entry.getValue();
      simularityScore += score;
    }

    return simularityScore;
  }
}
