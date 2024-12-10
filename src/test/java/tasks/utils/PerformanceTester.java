package tasks.utils;

import java.util.*;
import java.util.stream.Collectors;

public class PerformanceTester
{
  public PerformanceTester()
  {
    this(true);
  }

  public PerformanceTester(boolean printResults)
  {
    this.printResults = printResults;
  }

  public void startTiming()
  {
    t1 = System.nanoTime();
  }

  public void stopTiming()
  {
    long delta = System.nanoTime() - t1;
    deltas.add(delta);
  }

  public void printResults()
  {
    if(printResults)
    {
      PerformanceTester.printResults(deltas);
    }

    deltas.clear();
  }

  private boolean printResults;
  private long t1 = 0L;
  private List<Long> deltas = new ArrayList<>();

  public static void run(Runnable testFunction, int nRuns)
  {
    List<Long> deltas = new ArrayList<>();

    for(int i = 0; i < nRuns; i++)
    {
      long t1 = System.nanoTime();
      testFunction.run();
      long t2 = System.nanoTime();

      long delta = t2 - t1;
      deltas.add(delta);
    }
  }

  private static void printResults(List<Long> deltas)
  {
    System.out.println("Iterations: " + deltas.size());
    System.out.println("Total time: " + printTime(deltas.stream().mapToLong(o->o).sum()));
    System.out.println("Median time: " + printTime(calcMedian(deltas)));
    System.out.println("Average time: " + printTime(deltas.stream().mapToLong(o->o).average().orElse(0L)));
    System.out.println("Min time: " + printTime(deltas.stream().mapToLong(o->o).min().orElse(0L)));
    System.out.println("Max time: " + printTime(deltas.stream().mapToLong(o->o).max().orElse(0L)));
    System.out.println("Average Executions Per-Second: " + (int)(NS_IN_SEC / deltas.stream().mapToLong(o->o).average().orElse(0L)));
    System.out.println("");

    printGraph(deltas);
  }

  private static long calcMedian(List<Long> deltas)
  {
    return deltas.stream().sorted(Long::compareTo).collect(Collectors.toList()).get(deltas.size() / 2);
  }

  private static String printTime(double t)
  {
    if(t <= NS_IN_QS)
    {
      return String.format("%.2f ns", (t));
    }
    else if(t <= NS_IN_MS)
    {
      return String.format("%.2f qs", (t / NS_IN_QS));
    }
    else if(t <= NS_IN_SEC)
    {
      return String.format("%.2f ms", (t / NS_IN_MS));
    }
    else if(t <= NS_IN_MIN)
    {
      return String.format("%.2f sec", (t / NS_IN_SEC));
    }
    else if(t <= NS_IN_HOUR)
    {
      return String.format("%.2f min", (t / NS_IN_MIN));
    }
    else if(t <= NS_IN_DAY)
    {
      return String.format("%.2f hr", (t / NS_IN_HOUR));
    }
    else
    {
      return String.format("%.2f days", (t / NS_IN_DAY));
    }
  }

  private static void printGraph(List<Long> data)
  {
    if(data.size() > 0)
    {
      int graphWidth = Math.min(MAX_GRAPH_WIDTH, data.size());
      double[][] graphRatios = new double[GRAPH_HEIGHT][graphWidth];
      List<List<Long>> timeSplitData = splitGraphDataByTime(data, graphWidth);

      long min = data.stream().mapToLong(o -> o).min().orElse(0);
      long max = data.stream().mapToLong(o -> o).max().orElse(0);

      for(int i = 0; i < timeSplitData.size(); i++)
      {
        List<Long> columnData = timeSplitData.get(i);

        List<List<Long>> rows = boxData(columnData, GRAPH_HEIGHT, min, max);
        int maxDataInARow = largestSublistCount(rows);

        for(int j = rows.size() - 1; j != -1; j--)
        {
          int rowSize = rows.get(j).size();
          graphRatios[rows.size() - j - 1][i] = (double) rowSize / (double) maxDataInARow;
        }
      }

      for(int i = 0; i < graphRatios.length; i++)
      {
        System.out.print("|");
        int len = graphRatios[i].length;

        for(int j = 0; j < len; j++)
        {
          System.out.print(getGraphString(graphRatios[i][j]));
        }

        long range = max - min;
        double minIdxRatio = (double) (graphRatios.length - i - 1) / (double) graphRatios.length;
        double maxIdxRatio = (double) (graphRatios.length - i) / (double) graphRatios.length;
        double minInRange = (minIdxRatio * range) + min;
        double maxInRange = (maxIdxRatio * range) + min;
        System.out.println(" " + printTime(minInRange) + " - " + printTime(maxInRange));
      }

      System.out.print(" ");
      for(int x = 0; x < graphWidth; x++)
      {
        System.out.print("-");
      }
      System.out.println();
    }
  }

  private static List<List<Long>> splitGraphDataByTime(List<Long> data, int nPortions)
  {
    List<List<Long>> splitData = new ArrayList<>();
    int dataSize = data.size();

    for(int i = 0; i < nPortions; i++)
    {
      splitData.add(new ArrayList<>());
    }

    int splitIndex = 0;
    for(int i = 1; i <= nPortions; i++)
    {
      int maxIndex = (int)Math.ceil(dataSize * ((double)i / (double)nPortions));

      for(int j = splitIndex; j < maxIndex; j++)
      {
        splitData.get(i-1).add(data.get(j));
        splitIndex++;
      }
    }

    return splitData;
  }

  private static List<List<Long>> boxData(List<Long> data, int nPortions, long min, long max)
  {
    long range = max - min;
    List<Long> dataCopy = new ArrayList<>(data);
    List<List<Long>> splitData = new ArrayList<>();
    for(int i = 0; i < nPortions; i++)
    {
      splitData.add(new ArrayList<>());
    }

    for(int i = 0; i < nPortions; i++)
    {
      double idxRatio = (double)(i + 1) / (double)nPortions;
      double maxInRange = idxRatio * range + min;

      Iterator<Long> iter = dataCopy.iterator();
      while(iter.hasNext())
      {
        long value = iter.next();

        if(value <= maxInRange)
        {
          splitData.get(i).add(value);
          iter.remove();
        }
      }
    }

    return splitData;
  }

  private static int largestSublistCount(List<List<Long>> data)
  {
    return data.stream().mapToInt(List::size).max().orElse(0);
  }

  private static String getGraphString(double ratio)
  {
    if(ratio == 0d)
    {
      return " ";
    }
    if(ratio <= 1d/6d)
    {
      return RED_BACKGROUND + " " + RESET;
    }
    else if(ratio <= 2d/6d)
    {
      return RED_BACKGROUND_BRIGHT + " " + RESET;
    }
    else if(ratio <= 3d/6d)
    {
      return YELLOW_BACKGROUND + " " + RESET;
    }
    else if(ratio <=  4d/6d)
    {
      return YELLOW_BACKGROUND_BRIGHT + " " + RESET;
    }
    else if(ratio <= 5d/6d)
    {
      return GREEN_BACKGROUND + " " + RESET;
    }
    else
    {
      return GREEN_BACKGROUND_BRIGHT + " " + RESET;
    }
  }

  private static final int GRAPH_HEIGHT = 8;
  private static final int MAX_GRAPH_WIDTH = 60;

  private static final Long NS_IN_QS = 1000L;
  private static final Long NS_IN_MS = 1000L * 1000L;
  private static final Long NS_IN_SEC = 1000L * 1000L * 1000L;
  private static final Long NS_IN_MIN = 60L * 1000L * 1000L * 1000L;
  private static final Long NS_IN_HOUR = 60L * 60L * 1000L * 1000L * 1000L;
  private static final Long NS_IN_DAY = 24L * 60L * 60L * 1000L * 1000L * 1000L;

  public static final String RESET = "\033[0m";  // Text Reset

  public static final String RED_BACKGROUND = "\033[41m";    // RED
  public static final String GREEN_BACKGROUND = "\033[42m";  // GREEN
  public static final String YELLOW_BACKGROUND = "\033[43m"; // YELLOW

  public static final String RED_BACKGROUND_BRIGHT = "\033[0;101m";// RED
  public static final String GREEN_BACKGROUND_BRIGHT = "\033[0;102m";// GREEN
  public static final String YELLOW_BACKGROUND_BRIGHT = "\033[0;103m";// YELLOW
}

