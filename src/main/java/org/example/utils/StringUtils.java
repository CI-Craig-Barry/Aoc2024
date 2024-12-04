package org.example.utils;

import java.util.*;

public class StringUtils
{
  public static List<Integer> indexOfSubstrings(String input, String search)
  {
    char[] inputChars = input.toCharArray();
    char charZero = search.charAt(0);
    List<Integer> results = new ArrayList<>();

    for(int searchIdx = 0; searchIdx < inputChars.length; searchIdx++)
    {
      char inputChar = input.charAt(searchIdx);

      if(inputChar == charZero)
      {
        if(isStringAtIndex(input, searchIdx, search))
        {
          results.add(searchIdx);
        }
      }
    }

    return results;
  }

  public static boolean isStringAtIndex(String input, int index, String search)
  {
    if(index + search.length() > input.length())
    {
      return false;
    }

    int searchIdx = index;

    for(int iter = 0; iter < search.length(); iter++,searchIdx++)
    {
      //TODO - Check this is within bounds
      char inputChar = input.charAt(searchIdx);
      char searchChar = search.charAt(iter);

      if(inputChar != searchChar)
      {
        return false;
      }
    }

    return true;
  }

  public static List<String> splitIntoLines(String input)
  {
    return Arrays.stream(input.split("\n")).toList();
  }
}
