package org.example;//
// Copyright (c) 2024, Conserve It Pty. Ltd.
//

import org.example.utils.*;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class Day17
{
  public static boolean debug;

  public static String task1(StringInput input, boolean debug)
  {
    Day17.debug = debug;

    Program program = parseProgram(input);
    String output = program.run();

    return output;
  }

  public static long task2(StringInput input, boolean debug)
  {
    Day17.debug = debug;

    Program program = parseProgram(input);
    Set<Long> result = getPossibleInputs(program.instructions);

    return result.stream().mapToLong(v -> v).min().getAsLong();
  }

  public static Program parseProgram(StringInput input)
  {
    List<String> lines = input.asLines();
    Program program = new Program();

    program.registerA = (long)StringUtils.findVariables("Register A: %d", lines.get(0))[0];
    program.registerB = (long)StringUtils.findVariables("Register B: %d", lines.get(1))[0];
    program.registerC = (long)StringUtils.findVariables("Register C: %d", lines.get(2))[0];

    String instructions = (String)StringUtils.findVariables("Program: %s", lines.get(4))[0];
    String[] tokens = instructions.split(",");
    program.instructions = Arrays.stream(tokens)
      .map(Integer::parseInt)
      .collect(Collectors.toList());

    return program;
  }

  public static class Program
  {
    public String run()
    {
      StringJoiner output = new StringJoiner(",");
      int instructionIdx = 0;

      while(instructionIdx < instructions.size())
      {
        int instruction = instructions.get(instructionIdx);

        if(debug)
        {
          System.out.println("PC: " + instructionIdx + ", A: " + registerA + ", B: " + registerB + ", C: " + registerC);
        }

        switch(instruction)
        {
          case 0: //Adv
            registerA = adv(instructionIdx);
            break;
          case 1: //BXL
            registerB = registerB ^ getLiteralOperand(instructionIdx);
            break;
          case 2: //BST
            registerB = getComboOperand(instructionIdx) % 8;
            break;
          case 3: //JNZ
            if(registerA == 0L)
            {
              break;
            }
            instructionIdx = (int)getLiteralOperand(instructionIdx);
            continue;
          case 4: //BXC
            registerB  = registerB ^ registerC;
            break;
          case 5: //Out
            output.add(String.valueOf(getComboOperand(instructionIdx) % 8));
            break;
          case 6: //BDV
            registerB = adv(instructionIdx);
            break;
          case 7: //CDV
            registerC = adv(instructionIdx);
            break;
        }

        instructionIdx += 2;
      }

      return output.toString();
    }

    private long adv(int programCounter)
    {
      long numerator = registerA;
      long denominator = (long)Math.pow(2, getComboOperand(programCounter));

      return numerator / denominator;
    }

    private long getComboOperand(int programCounter)
    {
      int param = instructions.get(programCounter + 1);

      return switch (param)
      {
        case 0 -> 0;
        case 1 -> 1;
        case 2 -> 2;
        case 3 -> 3;
        case 4 -> registerA;
        case 5 -> registerB;
        case 6 -> registerC;
        default -> throw new RuntimeException("Combo operand can't be " + param);
      };
    }

    private long getLiteralOperand(int programCounter)
    {
      int param = instructions.get(programCounter + 1);
      return (long)param;
    }

    public long registerA;
    public long registerB;
    public long registerC;

    public List<Integer> instructions;
  }

  //Reverse step 7 to find all possible inputs
  private static Set<Long> reverseStep7(long aResult)
  {
    //Step 7 => a / 8 = result. Therefore result * 8 <= a.
    //As 8/8==1, but so is 10/8==1, up until 15/8==1
    long minInput = aResult * 8;

    return Set.of(
      minInput,
      minInput + 1,
      minInput + 2,
      minInput + 3,
      minInput + 4,
      minInput + 5,
      minInput + 6,
      minInput + 7
    );
  }

  //Find all inputs to the Day2 program that produced the given output result
  public static Set<Long> getPossibleInputs(List<Integer> results)
  {
    Set<Long> possibleAValues = new HashSet<>();
    //We know the final A value must be zero to end the program. So we start from there
    //And we attempt to calculate the A value at the previous iteration
    possibleAValues.add(0L);

    var iter = results.listIterator(results.size());
    while(iter.hasPrevious())
    {
      assert !possibleAValues.isEmpty();
      int result = iter.previous();

      //All possible A values given the current A value. We know that AResult => AInput / 8. For any AResult
      //there are 8 possible AInput values that could produce that result (due to integer rounding)
      possibleAValues = possibleAValues.stream()
        .map(Day17::reverseStep7)
        .flatMap(Collection::stream)
        .collect(Collectors.toSet());

      var aValueIter = possibleAValues.iterator();

      //Move forwards throw the program to see if the generated B & C values
      //generate the output for this iteration. If they do then these could be
      //a possible A value for this iteration
      while (aValueIter.hasNext())
      {
        long a = aValueIter.next();

        long b = a % 8;
        b = b ^ 2;
        long c = (long) (a / Math.pow(2, b));
        b = b ^ 3;
        b = b ^ c;

        if(b % 8 != result)
        {
          aValueIter.remove();
        }
      }
    }

    return possibleAValues;
  }
}
