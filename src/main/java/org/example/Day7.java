package org.example;

import org.example.utils.StringUtils;

import java.util.*;

public class Day7
{
  public static long task1(List<String> inputs)
  {
    return getResult(inputs, List.of(Operation.ADD, Operation.MULTIPLY));
  }

  public static long task2(List<String> inputs)
  {
    return getResult(inputs, List.of(Operation.ADD, Operation.MULTIPLY, Operation.COMBINE));
  }

  //Calculate the results given a set of possible operations
  public static long getResult(List<String> inputs,
                               List<Operation> operations)
  {
    long result = 0;

    for (String input : inputs)
    {
      //Build each calibration result
      CalibrationResult calibrationResult = makeCalibrationResult(input);
      //Build list of possible equations
      List<Equation> equations = generateEquations(calibrationResult.inputs, operations);

      //Find out if any of these equations match the result
      for (Equation equation : equations)
      {
        if(calibrationResult.result == equation.calculateResult())
        {
          result += calibrationResult.result;

          equation.printEquation(calibrationResult.result);
          break;
        }
      }
    }

    return result;
  }

  //Construct a line in the inputs into a calibration result
  private static CalibrationResult makeCalibrationResult(String line)
  {
    CalibrationResult result = new CalibrationResult();
    result.line = line;

    List<String> tokens = StringUtils.tokenize(line,":");
    assert tokens.size() == 2;

    result.result = Long.parseLong(tokens.get(0));

    tokens = StringUtils.tokenize(tokens.get(1), " ");
    result.inputs = tokens.stream()
      .map(Integer::parseInt)
      .toList();

    return result;
  }

  //Generate equations given a list of possible operations
  private static List<Equation> generateEquations(List<Integer> inputs, List<Operation> possibleOperations)
  {
    List<Equation> equations = new ArrayList<>();
    List<Operation> operations = new ArrayList<>();

    generateEquationsRecur(equations, possibleOperations, operations, inputs, 0);

    return equations;
  }

  //Recursively generate equations
  private static void generateEquationsRecur(
    List<Equation> equations,
    List<Operation> possibleOperations,
    List<Operation> operations,
    List<Integer> inputs,
    int index
  )
  {
    if(index == inputs.size() - 1)
    {
      Equation equation = new Equation();
      equation.inputs = inputs;
      equation.operations = operations;
      equations.add(equation);
      return;
    }

    for(int i = 0; i < possibleOperations.size(); i++)
    {
      Operation operation = possibleOperations.get(i);
      List<Operation> newOperations = new ArrayList<>(operations);
      newOperations.add(operation);
      generateEquationsRecur(equations, possibleOperations, newOperations, inputs ,index + 1);
    }
  }

  private static class Equation
  {
    private List<Integer> inputs;
    private List<Operation> operations;

    //Calculate the result of the equation
    public long calculateResult()
    {
      assert operations.size() == inputs.size() - 1;
      long result = inputs.getFirst();

      for(int index = 0; index < operations.size(); index++)
      {
        long input2 = inputs.get(index + 1);
        Operation operation = operations.get(index);
        result = doOperation(result, input2, operation);
      }

      return result;
    }

    private static long doOperation(long input1, long input2, Operation operation)
    {
      if(operation == Operation.ADD)
      {
        return input1 + input2;
      }
      else if(operation == Operation.MULTIPLY)
      {
        return input1 * input2;
      }
      else
      {
        String result = String.valueOf(input1) + String.valueOf(input2);
        return Long.parseLong(result);
      }
    }

    //Debug print the equation with the expected result
    public void printEquation(long result)
    {
      StringBuilder builder = new StringBuilder();
      builder.append(result);
      builder.append("= ");

      for(int i = 0; i < operations.size(); i++)
      {
        builder.append(inputs.get(i));

        Operation operation = operations.get(i);
        if(operation == Operation.ADD)
        {
          builder.append("+");
        }
        else if(operation == Operation.MULTIPLY)
        {
          builder.append("*");
        }
      }

      builder.append(inputs.getLast());
      System.out.println(builder.toString());
    }
  }

  //Possible operations for inputs
  private enum Operation {
    ADD,
    MULTIPLY,
    COMBINE
  }

  //Wraps each line in a class of its properties
  private static class CalibrationResult
  {
    private String line;

    private long result;
    private List<Integer> inputs;
  }

}
