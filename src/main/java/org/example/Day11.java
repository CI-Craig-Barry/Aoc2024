package org.example;

import org.example.utils.StringInput;

import java.util.*;

public class Day11
{
  private static boolean debug = false;

  public static long task1(StringInput input, int count, boolean debug)
  {
    Day11.debug = debug;

    State state = makeState(input);

    for(int i = 0; i < count; i++)
    {
      state.blink();

      if(debug)
      {
        state.drawState();
      }
    }

    return state.stones.size();
  }

  public static long task2(StringInput input, int count, boolean debug)
  {
    Day11.debug = debug;

    State state = makeState(input);
    ResultCache cache = new ResultCache();
    StoneCounter counter = new StoneCounter();
    counter.stoneCounts = new HashMap<>();
    for (Long stone : state.stones)
    {
      long stoneCount = counter.stoneCounts.getOrDefault(stone, 0L);
      counter.stoneCounts.put(stone, ++stoneCount);
    }

    for(int i = 0; i < count; i++)
    {
      counter.nextBlink(cache);
    }

    return counter.getTotalStones();
  }

  //Parse state object from input
  public static State makeState(StringInput input)
  {
    String encoded = input.asString().trim();
    String[] tokens = encoded.split(" ");

    State state = new State();
    state.stones = Arrays.stream(tokens).map(Long::parseLong).toList();

    return state;
  }

  //State object
  public static class State
  {
    //Blink rocks one iteration
    public void blink()
    {
      List<Long> newStones = new LinkedList<>(stones);

      var stoneIter = newStones.listIterator();
      while(stoneIter.hasNext())
      {
        BlinkResult result = blinkRock(stoneIter.next());

        stoneIter.set(result.result1);
        if(result.nResults == 2)
        {
          stoneIter.add(result.result2);
        }
      }

      this.stones = newStones;
    }

    //Draw current rock state
    public void drawState()
    {
      StringJoiner joiner = new StringJoiner(" ");
      stones.forEach(stone -> joiner.add(String.valueOf(stone)));
      System.out.println(joiner.toString());
    }

    private List<Long> stones;
  }

  //Blink a singular rock & give the results
  public static BlinkResult blinkRock(long number)
  {
    BlinkResult result = new BlinkResult();
    long nDigits = (long)Math.floor(Math.log10(number)) + 1;

    if(number == 0)
    {
      result.nResults = 1;
      result.result1 = 1;
    }
    else if(nDigits % 2 == 0)
    {
      result.nResults = 2;
      String str = String.valueOf(number);
      int strLen = str.length();
      result.result1 = Long.parseLong(str.substring(0, strLen / 2));
      result.result2 = Long.parseLong(str.substring(strLen / 2));
    }
    else
    {
      result.nResults = 1;
      result.result1 = number * 2024L;
    }
    return result;
  }

  //Caches results of blinking a rock with a given number
  public static class ResultCache
  {
    //Get result, calculating it if the input number has never been seen
    public BlinkResult getResult(long number)
    {
      BlinkResult result = cache.get(number);

      if(result == null)
      {
        result = blinkRock(number);
        cache.put(number, result);
      }

      return result;
    }

    private final Map<Long, BlinkResult> cache = new HashMap<>();
  }

  //Performs blinks without tracking rock positions, simply the number
  //of rocks of each number that would be present after each blink
  private static class StoneCounter
  {
    //Perform a blink & store the number of each rock
    public void nextBlink(ResultCache cache)
    {
      Map<Long, Long> newStoneCounts = new HashMap<>();

      for (var entry : stoneCounts.entrySet())
      {
        long stoneNumber = entry.getKey();
        long stoneCount = entry.getValue();
        BlinkResult blinkResult = cache.getResult(stoneNumber);

        long newStoneCount = newStoneCounts.getOrDefault(blinkResult.result1, 0l);
        newStoneCounts.put(blinkResult.result1, newStoneCount + stoneCount);

        if(blinkResult.nResults == 2)
        {
          newStoneCount = newStoneCounts.getOrDefault(blinkResult.result2, 0l);
          newStoneCounts.put(blinkResult.result2, newStoneCount + stoneCount);
        }
      }

      this.stoneCounts = newStoneCounts;
    }

    //Retrieve the number of stones at the current iteration
    public long getTotalStones()
    {
      long total = 0;
      for(long value : stoneCounts.values())
      {
        total += value;
      }

      return total;
    }

    //Maps the number to total stones with that number
    private Map<Long, Long> stoneCounts;
  }

  //Result of a blink
  private static class BlinkResult
  {
    private int nResults;
    private long result1;
    private long result2;
  }
}
