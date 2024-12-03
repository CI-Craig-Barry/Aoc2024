package org.example;

import org.example.utils.StringUtils;

import java.util.*;

//I will die before I use regex
public class Day3
{
  public static int task1(String input)
  {
    int result = 0;
    List<MulResult> multiplyFunctions = findMultipliers(input);

    for (MulResult multiplyFunction : multiplyFunctions)
    {
      result += multiplyFunction.input1 * multiplyFunction.input2;
      System.out.println(multiplyFunction.toString());
    }

    return result;
  }

  public static int task2(String input)
  {
    int result = 0;
    List<MulResult> multiplyFunctions = findMultipliers(input);
    List<Boolean> enabledIndices = buildEnabledIndices(input);

    for (MulResult multiplyFunction : multiplyFunctions)
    {
      if(enabledIndices.get(multiplyFunction.index))
      {
        result += multiplyFunction.input1 * multiplyFunction.input2;
        multiplyFunction.enabled = true;
      }
      else
      {
        multiplyFunction.enabled = false;
      }

      System.out.println(multiplyFunction.toString());
    }

    return result;
  }

  public static List<Boolean> buildEnabledIndices(String input)
  {
    Set<Integer> doInstructionIndices = new HashSet<>(StringUtils.indexOfSubstrings(input, "do()"));
    Set<Integer> dontInstructionIndices = new HashSet<>(StringUtils.indexOfSubstrings(input,"don't()"));
    List<Boolean> enabledIndices = new ArrayList<>(input.length());
    boolean isEnabled = true;

    for(int i = 0; i < input.length(); i++)
    {
      if(isEnabled && dontInstructionIndices.contains(i))
      {
        isEnabled = false;
      }
      else if(!isEnabled && doInstructionIndices.contains(i))
      {
        isEnabled = true;
      }

      enabledIndices.add(isEnabled);
    }

    return enabledIndices;
  }

  public static List<MulResult> findMultipliers(String input)
  {
    List<MulResult> results = new ArrayList<>();
    List<Integer> indexOfMuls = StringUtils.indexOfSubstrings(input, "mul(");

    for (Integer indexOfMul : indexOfMuls)
    {
      int startingIndex = indexOfMul + MUL_PREFIX.length();
      boolean input1Found = false;
      StringBuilder input1Buffer = new StringBuilder();
      StringBuilder input2Buffer = new StringBuilder();

      int index = startingIndex;
      while(true)
      {
        if(index >= input.length())
        {
          break;
        }

        char c = input.charAt(index++);

        //Find the 1st input number
        if(!input1Found)
        {
          if(Character.isDigit(c))
          {
            input1Buffer.append(c);

            if(input1Buffer.length() > 3)
            {
              break;
            }
          }
          else if(c == ',')
          {
            input1Found = true;
          }
          else
          {
            break;
          }
        }
        //Find the 2nd input number
        else
        {
          if(Character.isDigit(c))
          {
            input2Buffer.append(c);

            if(input2Buffer.length() > 3)
            {
              break;
            }
          }
          else if(c == ')')
          {
            MulResult result = new MulResult();
            result.line = input.substring(indexOfMul, index);
            result.index = indexOfMul;
            result.input1 = Integer.parseInt(input1Buffer.toString());
            result.input2 = Integer.parseInt(input2Buffer.toString());

            results.add(result);
            break;
          }
          else
          {
            break;
          }
        }
      }
    }

    return results;
  }

  private static final String MUL_PREFIX = "mul(";


  private static class MulResult
  {
    @Override
    public String toString()
    {
      return "MulResult{" +
        "line='" + line + '\'' +
        ", enabled=" + enabled +
        ", index=" + index +
        ", input1=" + input1 +
        ", input2=" + input2 +
        '}';
    }

    private String line;
    private boolean enabled;
    private int index;
    private int input1;
    private int input2;
  }
}
