package org.example.utils;

import java.util.*;
import java.util.regex.*;

public class StringUtils
{
  private StringUtils()
  {

  }

  //Find all positions of a substring within an input (including overlaps)
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

  //Check if a substring is at a given position within an input string
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

  //Split an input by lines removing any newline characters
  public static List<String> splitIntoLines(String input)
  {
    return Arrays.stream(input.split("\n")).map(v -> v.replace("\r", "")).toList();
  }

  public static List<String> tokenize(String input, String delimiter)
  {
    StringTokenizer tokenizer = new StringTokenizer(input, delimiter);
    List<String> results = new ArrayList<>();

    while(tokenizer.hasMoreTokens())
    {
      results.add(tokenizer.nextToken());
    }

    return results;
  }

  //Got sick of doing the same random string parsing in every task so
  //I built a python-esque solution that just extracts the information
  //and skips past the delimiters so I can be lazy
  public static Object[] findVariables(String patternString, String input)
  {
    Pattern pattern = patternCache.get(patternString);
    List<Class<?>> captureGroups = new ArrayList<>();

    if(pattern == null)
    {
      StringBuilder regex = new StringBuilder();

      boolean lastCharPattern = false;

      for (char c : patternString.toCharArray())
      {
        if(lastCharPattern)
        {
          if(c == 'd')
          {
            //Find possible '-', followed by any number of digits
            regex.append("(\\-?\\d+)");
            captureGroups.add(Integer.class);
          }
          else if(c == 's')
          {
            //Find any character any number of times
            regex.append("(.*)");
            captureGroups.add(String.class);
          }
          else if(c == '%')
          {
            //Actually find the '%' character
            regex.append("%%");
          }
          lastCharPattern = false;
        }
        else if(REGEX_CONTROL_CHARS.contains(c))
        {
          regex.append("\\");
          regex.append(c);
        }
        else if(c == '%')
        {
          lastCharPattern = true;
          continue;
        }
        else
        {
          regex.append(c);
        }
      }

      pattern = Pattern.compile(regex.toString());
      patternCache.put(patternString, pattern);
    }

    Matcher matcher = pattern.matcher(input);
    if(matcher.find() && matcher.groupCount() == captureGroups.size())
    {
        List<Object> results = new ArrayList<>();

        for(int i = 0; i < captureGroups.size(); i++)
        {
          //Increment by 1 as group(0) is the entire match
          String groupCapture = matcher.group(i+1);
          Class<?> cls = captureGroups.get(i);
          if(cls.equals(Integer.class))
          {
            results.add(Integer.parseInt(groupCapture));
          }
          else if(cls.equals(String.class))
          {
            results.add(groupCapture);
          }
          else
          {
            throw new RuntimeException("BAD CAPTURE GROUP");
          }
        }

        return results.toArray();
    }

    throw new RuntimeException("bad pattern match for input '" + input + "' on pattern '" + patternString + "'");
  }

  //Parse a list of integers delimited by some delimiter
  public static List<Integer> parseDelimitedIntegers(String input, String delimiter)
  {
    List<Integer> results = new ArrayList<>();
    StringTokenizer tokenizer = new StringTokenizer(input.trim(), delimiter);

    while(tokenizer.hasMoreTokens())
    {
      int integer = Integer.parseInt(tokenizer.nextToken());
      results.add(integer);
    }

    return results;
  }

  private static final Map<String, Pattern> patternCache = new HashMap<>();
  private final static Set<Character> REGEX_CONTROL_CHARS;

  static
  {
    REGEX_CONTROL_CHARS = new HashSet<>();

    for (char c : "<([{\\^-=$!|]})?*+.>".toCharArray())
    {
      REGEX_CONTROL_CHARS.add(c);
    }
  }
}
