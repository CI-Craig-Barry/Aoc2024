package org.example;

import org.example.utils.*;

import java.util.*;

public class Day25
{
  private static boolean debug = false;

  public static long task1(StringInput input, boolean debug)
  {
    Day25.debug = debug;

    var parsedEntries = parseInputs(input);
    var locks = parsedEntries.getFirst();
    var keys = parsedEntries.getSecond();

    int matches = 0;

    for (Lock lock : locks)
    {
      for (Key key : keys)
      {
        if(lock.fitsKey(key))
        {
          matches++;
        }
      }
    }

    return matches;
  }

  public static long task2(StringInput input, boolean debug)
  {
    Day25.debug = debug;

    return 0L;
  }

  private static Pair<List<Lock>, List<Key>> parseInputs(StringInput input)
  {
    List<Lock> locks = new ArrayList<>();
    List<Key> keys = new ArrayList<>();

    for (StringInput stringInput : input.splitAtEmptyLine())
    {
      boolean isKey = stringInput.asString().charAt(0) == '.';
      int width = stringInput.asLines().getFirst().trim().length();

      Item item = isKey ? new Key() : new Lock();
      item.heights = new ArrayList<>();
      for(int i = 0; i < width; i++)
      {
        item.heights.add(-1);
      }

      for (String line : stringInput.asLines())
      {
        char[] chars = line.toCharArray();
        for (int i = 0; i < chars.length; i++)
        {
          char c = chars[i];
          if(c == '#')
          {
            item.heights.set(i, item.heights.get(i) + 1);
          }
        }
      }

      if(isKey)
      {
        keys.add((Key) item);
      }
      else
      {
        ((Lock)item).maxHeight = stringInput.asLines().size() - 2;
        locks.add((Lock) item);
      }
    }

    return new Pair<>(locks, keys);
  }

  private static class Item
  {
    public List<Integer> heights = new ArrayList<>();
  }

  private static class Lock
    extends Item
  {
    public boolean fitsKey(Key key)
    {
      for(int i = 0; i < key.heights.size(); i++)
      {
        int diff = maxHeight - key.heights.get(i) - heights.get(i);
        if(diff < 0)
        {
          return false;
        }
      }
      return true;
    }

    private int maxHeight;
  }

  private static class Key
    extends Item
  {
  }
}
