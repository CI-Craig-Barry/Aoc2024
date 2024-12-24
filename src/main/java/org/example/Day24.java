//
// Copyright (c) 2024, Conserve It Pty. Ltd.
//

package org.example;

import org.example.utils.*;

import java.util.*;

public class Day24
{
  private static boolean debug = false;

  public static long task1(StringInput input, boolean debug)
  {
    Day24.debug = debug;

    parseInputs(input);

    for (LogicGate logicGate : logicGates)
    {
      logicGate.resolve();
    }

    long result = 0L;
    int idx = 0;
    while(true)
    {
      String outputName = "";
      if(idx < 10)
      {
        outputName = "z0" + idx;
      }
      else
      {
        outputName = "z" + idx;
      }

      var outputVariable = variableMap.get(outputName);
      if(outputVariable == null)
      {
        return result;
      }

      if(outputVariable.result)
      {
        result = result | (1L << idx);
      }
      idx++;
    }
  }

  public static long task2(StringInput input, boolean debug)
  {
    Day24.debug = debug;

    return 0L;
  }

  private static void parseInputs(StringInput input)
  {
    StringInput section1 = input.splitAtEmptyLine().get(0);
    StringInput section2 = input.splitAtEmptyLine().get(1);

    for (String line : section1.asLines())
    {
      Object[] vars = StringUtils.findVariables("%s: %d", line.trim());
      String variableName = (String)vars[0];
      boolean result = ((long)vars[1] == 1);
      variableMap.put(variableName, new Variable(variableName, result));
    }

    for (String line : section2.asLines())
    {
      Object[] tokens = StringUtils.findVariables("%s %s %s -> %s", line.trim());
      String input1Name = (String) tokens[0];
      String operation = (String) tokens[1];
      String input2Name = (String) tokens[2];
      String outputName = (String) tokens[3];

      var variable1 = variableMap.getOrDefault(input1Name, new Variable(input1Name));
      var variable2 = variableMap.getOrDefault(input2Name, new Variable(input2Name));
      var output = variableMap.getOrDefault(outputName, new Variable(outputName));

      variableMap.put(input1Name, variable1);
      variableMap.put(input2Name, variable2);
      variableMap.put(outputName, output);

      GateOperation gateOperation = switch(operation)
      {
        case "XOR" -> GateOperation.XOR;
        case "AND" -> GateOperation.AND;
        case "OR" -> GateOperation.OR;
        default -> throw new RuntimeException("Bad operation");
      };

      LogicGate gate = new LogicGate(variable1, variable2, output, gateOperation);
      logicGates.add(gate);
    }

    //Now that all logic gates are parsed, link the dependencies
    logicGates.forEach(LogicGate::linkDependencies);

    //Now sort the list in dependency order
    logicGates = TopologicalSorter.sort(logicGates);

  }

  private static Map<String, Variable> variableMap = new HashMap<>();
  private static List<LogicGate> logicGates = new ArrayList<>();

  public static class LogicGate
    extends TopologicalSortable
  {
    public LogicGate(Variable input1, Variable input2, Variable output, GateOperation operation)
    {
      super(0);

      this.input1 = input1;
      this.input2 = input2;
      this.output = output;

      this.input1.addInputGate(this);
      this.input2.addInputGate(this);
      this.output.setOutputGate(this);
      this.operation = operation;
    }

    public void linkDependencies()
    {
//      Set<LogicGate> input1Dependencies = input1.getInputGat4Les();
//      Set<LogicGate> input2Dependencies = input2.getInputGates();
//
//      for (LogicGate input1Dependency : input1Dependencies)
//      {
//        this.addDependency(input1Dependency);
//      }
//
//      for (LogicGate input2Dependency : input2Dependencies)
//      {
//        this.addDependency(input2Dependency);
//      }

      if(input1.outputGate != null)
      {
        this.addDependency(input1.outputGate);
      }

      if(input2.outputGate != null)
      {
        this.addDependency(input2.outputGate);
      }
    }

    public void resolve()
    {
      assert input1.hasResult() && input2.hasResult();

      boolean result = false;
      switch (operation)
      {
        case OR:
          result = input1.result || input2.result;
          break;
        case XOR:
          result = input1.result ^ input2.result;
          break;
        case AND:
          result = input1.result && input2.result;
          break;
      }

      output.setResult(result);
    }

    private final Variable input1;
    private final Variable input2;
    private final Variable output;
    private final GateOperation operation;
  }

  public static class Variable
    extends TopologicalSortable
  {
    public Variable(String name)
    {
      super(0);

      this.name = name;
    }

    public Variable(String name, boolean result)
    {
      this(name);
      this.result = result;
    }

    protected void addInputGate(LogicGate gate)
    {
      inputGates.add(gate);
    }

    protected Set<LogicGate> getInputGates()
    {
      return inputGates;
    }

    protected void setOutputGate(LogicGate gate)
    {
      assert this.outputGate == null;
      this.outputGate = gate;
    }

    public void setResult(boolean result)
    {
      this.result = result;
    }

    public boolean hasResult()
    {
      return result != null;
    }

    private final String name;
    private Boolean result = null;
    private Set<LogicGate> inputGates = new HashSet<>();
    private LogicGate outputGate = null;
  }

  private enum GateOperation
  {
    AND,
    OR,
    XOR
  }
}
