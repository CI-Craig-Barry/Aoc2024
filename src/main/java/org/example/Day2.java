package org.example;

import java.util.*;

public class Day2
{
  public static int task1(List<String> lines)
  {
    int numSafe = 0;

    for (String line : lines)
    {
      ReportResult result = analyzeReport(line);
      calculateDiffs(result);
      if(calculateSafeReport(result.diffs))
      {
        result.safe = true;
        numSafe += 1;
      }

      System.out.println(result);
    }

    return numSafe;
  }

  public static int task2(List<String> lines)
  {
    int numSafe = 0;

    for (String line : lines)
    {
      ReportResult result = analyzeReport(line);
      calculateDiffs(result);
      if(calculateSafeReportOnSubDiffs(result))
      {
        numSafe += 1;
      }

      System.out.println(result);
    }

    return numSafe;
  }

  private static ReportResult analyzeReport(String report)
  {
    List<String> levelTokens = Arrays.stream(report.split(" ")).toList();
    List<Integer> levels = levelTokens.stream().map(Integer::parseInt).toList();

    ReportResult result = new ReportResult();
    result.levels = levels;
    result.reportString = report;

    return result;
  }

  private static void calculateDiffs(ReportResult result)
  {
    int nDiffs = result.levels.size() - 1;
    result.diffs = new ArrayList<>(result.levels.size());

    for(int i = 0; i < nDiffs; i++)
    {
      int value1 = result.levels.get(i);
      int value2 = result.levels.get(i + 1);

      int diff = value2 - value1;
      result.diffs.add(diff);
    }
  }

  private static boolean calculateSafeReport(List<Integer> diffs)
  {
    int minDiff = diffs.stream().min(Integer::compareTo).orElseThrow(RuntimeException::new);
    int maxDiff = diffs.stream().max(Integer::compareTo).orElseThrow(RuntimeException::new);

    boolean isSafe =
      //Always increasing and between values of 1 & 3
      minDiff >= 1 && maxDiff <= 3 ||
      //Always decreasing and between values of -1 & -3
      minDiff >= -3 && maxDiff <= -1;

    return isSafe;
  }

  private static void calculatePossibleDiffs(ReportResult result)
  {
    int nDiffs = result.levels.size() - 2;
    result.allPossibleDiffs = new ArrayList<>();

    for(int j = 0; j < result.levels.size(); j++)
    {
      List<Integer> possibleDiff = new ArrayList<>();
      List<Integer> modifiedLevels = new ArrayList<>(result.levels);
      //Remove on of the levels & then calculate the diff with this new list of values
      modifiedLevels.remove((int)j);

      for(int i = 0; i < nDiffs; i++)
      {
        int value1 = modifiedLevels.get(i);
        int value2 = modifiedLevels.get(i + 1);

        int diff = value2 - value1;
        possibleDiff.add(diff);
      }

      result.allPossibleDiffs.add(possibleDiff);
    }
  }

  private static boolean calculateSafeReportOnSubDiffs(ReportResult result)
  {
    calculatePossibleDiffs(result);

    result.safe = result.allPossibleDiffs.stream()
      .anyMatch(Day2::calculateSafeReport);

    return result.safe;
  }

  private static class ReportResult
  {
    @Override
    public String toString()
    {
      return "ReportResult{" +
        "reportString='" + reportString + '\'' +
        ", levels=" + levels +
        ", diffs=" + diffs +
        ", safe=" + safe +
        '}';
    }

    String reportString;
    List<Integer> levels;
    List<Integer> diffs;
    boolean safe = false;

    List<List<Integer>> allPossibleDiffs;
  }
}
