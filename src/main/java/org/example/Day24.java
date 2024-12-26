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

    var parseEntry = parseGraph(input);
    var graph = parseEntry.getFirst();
    var variableMap = parseEntry.getSecond();

    long x = getNumber(variableMap, "x");
    long y = getNumber(variableMap, "y");

    return graph.resolve(x, y);
  }

  public static String task2(StringInput input, boolean debug)
  {
    Day24.debug = debug;

    Graph graph = parseGraph(input).getFirst();

    //Perform the following swaps to prevent findAdders having issues
    graph.swap(
      graph.getNode("z05"),
      graph.getNode("frn")
    );

    graph.swap(
      graph.getNode("vtj"),
      graph.getNode("wnf")
    );

    graph.swap(
      graph.getNode("z21"),
      graph.getNode("gmq")
    );

    graph.swap(
      graph.getNode("z39"),
      graph.getNode("wtt")
    );

    //If this throws errors then the swaps are not correct & the
    findAdders(graph, 45);

    return "";
  }

  private static String translateVariableName(String prefix, int idx)
  {
    if(idx < 10)
    {
      return prefix + "0" + idx;
    }

    return prefix + idx;
  }

  private static long getNumber(Map<String, Variable> variableMap, String prefix)
  {
    long result = 0;
    int idx = 0;
    while(true)
    {
      String outputName = translateVariableName(prefix, idx);
      var outputVariable = variableMap.get(outputName);
      if(outputVariable == null)
      {
        return result;
      }

      if(outputVariable.input)
      {
        result = result | (1L << idx);
      }
      idx++;
    }
  }

  public static class Variable
  {
    public Variable(String name)
    {
      this.name = name;
      this.input = null;
    }

    public Variable(String name, boolean input)
    {
      this.name = name;
      this.input = input;
    }

    @Override
    public boolean equals(Object o)
    {
      if (!(o instanceof Variable variable))
      {
        return false;
      }
      return Objects.equals(name, variable.name);
    }

    @Override
    public int hashCode()
    {
      return Objects.hashCode(name);
    }

    @Override
    public String toString()
    {
      return name;
    }

    private final String name;
    private final Boolean input;
  }

  private static Pair<Graph, Map<String, Variable>> parseGraph(StringInput input)
  {
    Map<String, GraphNode> nodesByName = new HashMap<>();
    Map<String, Variable> variableMap = new HashMap<>();

    StringInput section1 = input.splitAtEmptyLine().get(0);
    StringInput section2 = input.splitAtEmptyLine().get(1);

    for (String line : section1.asLines())
    {
      Object[] vars = StringUtils.findVariables("%s: %d", line.trim());
      String variableName = (String)vars[0];
      boolean result = ((long)vars[1] == 1);
      var variable = new Variable(variableName, result);
      variableMap.put(variableName, variable);

      nodesByName.put(variableName, new GraphNode(variable, null));
    }

    //Build the graph nodes
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

      nodesByName.put(outputName, new GraphNode(output, gateOperation));
    }

    //Link graph nodes to their incoming & outgoing
    for (String line : section2.asLines())
    {
      Object[] tokens = StringUtils.findVariables("%s %s %s -> %s", line.trim());
      String input1Name = (String) tokens[0];
      String input2Name = (String) tokens[2];
      String outputName = (String) tokens[3];

      var variable1 = nodesByName.get(input1Name);
      var variable2 = nodesByName.get(input2Name);
      var output = nodesByName.get(outputName);

      output.incoming.add(variable1);
      output.incoming.add(variable2);
      variable1.outgoing.add(output);
      variable2.outgoing.add(output);
    }

    //Calculate all dependencies & depth
    for (GraphNode node : nodesByName.values())
    {
      Set<GraphNode> dependencies = new HashSet<>(node.incoming);
      if(dependencies.isEmpty())
      {
        continue;
      }

      int depth = 0;
      int size = 0;
      while(size != dependencies.size())
      {
        depth++;
        size = dependencies.size();
        Set<GraphNode> temp = new HashSet<>();
        for (GraphNode dependency : dependencies)
        {
          temp.addAll(dependency.incoming);
        }
        dependencies.addAll(temp);
      }

      node.depth = depth;
    }

    Graph graph = new Graph();
    nodesByName.values().forEach(graph::add);
    return new Pair<>(graph, variableMap);
  }

  private static void findAdders(Graph graph, int maxIdx)
  {
    AdderCircuit prevCircuit = null;

    for(int i = 0; i < maxIdx; i++)
    {
      var xNode = graph.getInputNode(translateVariableName("x", i));
      var yNode = graph.getInputNode(translateVariableName("y", i));
      var zNode = graph.getOutputNode(translateVariableName("z", i));

      AdderCircuit circuit = new AdderCircuit();
      circuit.xInput = xNode;
      circuit.yInput = yNode;
      if(prevCircuit != null)
      {
        circuit.carry = prevCircuit.or1;
      }

      Set<GraphNode> xyOutgoing = new HashSet<>();
      xyOutgoing.addAll(yNode.outgoing);
      xyOutgoing.addAll(xNode.outgoing);
      for (GraphNode node : xyOutgoing)
      {
        if(node.incoming.contains(yNode))
        {
          if(node.operation == GateOperation.XOR)
          {
            circuit.xor1 = node;

            //If this is a full adder it should output to XOR2 & AND2
            if(prevCircuit != null && circuit.xor1.outgoing.size() != 2)
            {
              System.err.println(i + ": Expected 2 outputs for XOR1, but found: " + circuit.xor1.outgoing.size());
            }
            //If this is a half adder is should output directly to an output node
            else if(prevCircuit == null && !circuit.xor1.equals(zNode))
            {
              System.err.println(i + ": Expected XOR1 to link to output " + zNode + ", but found: " + node);
            }
          }
          else if(node.operation == GateOperation.AND)
          {
            circuit.and1 = node;

            //If this is a full circuit and1 should link to OR1
            if(prevCircuit != null && circuit.and1.outgoing.size() != 1)
            {
              System.err.println(i + ": Expected 1 output for AND1, but found: " + circuit.and1.outgoing.size());
            }
            //If this is a half-added will link to XOR1 & AND2 of next circuit
            else if(prevCircuit == null && circuit.and1.outgoing.size() != 2)
            {
              System.err.println(i + ": Expected 2 outputs for AND1, but found: " + circuit.and1.outgoing.size());
            }
          }
          else
          {
            System.err.println("Full adder operation 'OR' found fir first layer on idx: " + i);
          }
        }
      }

      if(circuit.xor1 == null)
      {
        System.err.println(i + ":  missing XOR1");
      }

      if(circuit.and1 == null)
      {
        System.err.println(i + ":  missing AND1");
      }

      if(prevCircuit == null)
      {
        //Set the carry bit circuit as the and circuit as this only a half-adder
        circuit.or1 = circuit.and1;
        prevCircuit = circuit;
        continue;
      }

      //Attempt to find XOR2, should link from carry input & xor1
      for (GraphNode node : circuit.xor1.outgoing)
      {
        //Attempt to find XOR2, it link in the carry pit as well as output to z[i] output
        if((circuit.carry == null || node.incoming.contains(circuit.carry)) && node.operation == GateOperation.XOR)
        {
          if(circuit.carry == null)
          {
            System.out.println(i + ": Assumed carry for circuit");
            circuit.carry = node.incoming.stream().filter(n -> !node.equals(n)).findAny().orElse(null);
          }
          if(circuit.xor2 != null)
          {
            System.err.println(i + ": Found duplicate XOR2");
          }

          circuit.xor2 = node;

          if(!node.equals(zNode))
          {
            System.err.println(i + ": Expected XOR2 to output to " + zNode + " but instead it outputs to: " + node);
          }
        }
        //Attempt to find AND2, it should link in the carry bit as well
        else if ((circuit.carry == null || node.incoming.contains(circuit.carry)) && node.operation == GateOperation.AND)
        {
          if(circuit.carry == null)
          {
            System.out.println(i + ": Assumed carry for circuit");
            circuit.carry = node.incoming.stream().filter(n -> !node.equals(n)).findAny().orElse(null);
          }
          if(circuit.and2 != null)
          {
            System.err.println(i + ": Found duplicate AND2");
          }

          circuit.and2 = node;

          if(node.outgoing.size() != 1)
          {
            System.err.println(i + ": AND2 has more than 1 output for index: " + i);
          }
        }
      }

      //Check we found an XOR2 & AND2
      if(circuit.xor2 == null)
      {
        System.err.println(i + ":  missing XOR2");
      }
      if(circuit.and2 == null)
      {
        System.err.println(i + ":  missing AND2");
      }

      if(circuit.and1 != null && circuit.and2 != null)
      {
        for (GraphNode node : circuit.and1.outgoing)
        {
          if(node.incoming.contains(circuit.and2) && node.operation == GateOperation.OR)
          {
            if(circuit.or1 != null)
            {
              System.err.println(i + ": Found duplicate OR1");
            }

            circuit.or1 = node;
          }
        }
      }
      else
      {
        System.err.println(i + ": Cannot attempt to find OR1 as missing pre-requisites");
      }

      if(circuit.or1 == null)
      {
        System.err.println(i + ": Circuit missing OR1");
      }

      prevCircuit = circuit;
    }
  }

  private static class AdderCircuit
  {
    private GraphNode xInput;
    private GraphNode yInput;
    private GraphNode carry;

    private GraphNode xor1;
    private GraphNode and1;

    private GraphNode xor2;
    private GraphNode and2;
    private GraphNode or1;
  }

  private enum GateOperation
  {
    AND,
    OR,
    XOR
  }

  private static class Graph
  {
    public long resolve(long x, long y)
    {
      int idx = 0;
      while(true)
      {
        Set<GraphNode> nodes = nodesByDepth.getOrDefault(idx, null);

        if(nodes == null)
        {
          break;
        }

        for (GraphNode node : nodes)
        {
          node.resolve(x, y);
        }

        idx++;
      }

      long result = 0;
      idx = 0;
      while(true)
      {
        var outputNode = getNode(translateVariableName("z", idx));
        if(outputNode == null)
        {
          return result;
        }

        if(outputNode.result)
        {
          result |= (1L << idx);
        }

        idx++;
      }
    }

    public void add(GraphNode node)
    {
      nodes.add(node);

      if(node.isOutput())
      {
        outputNodes.add(node);
      }

      if(node.isInput())
      {
        inputNodes.add(node);
      }

      Set<GraphNode> depthSet = nodesByDepth.computeIfAbsent(node.depth, k -> new HashSet<>());
      depthSet.add(node);
    }

    public void swap(GraphNode node1, GraphNode node2)
    {
      swapImpl(node1, node2);
    }

    private void swapImpl(GraphNode node1, GraphNode node2)
    {
      var temp = node1.variable;
      node1.variable = node2.variable;
      node2.variable = temp;

      //Modify outgoing
      var tempOutgoing = node1.outgoing;
      node1.outgoing = node2.outgoing;
      node2.outgoing = tempOutgoing;

      for (GraphNode node : node1.outgoing)
      {
        node.incoming.remove(node2);
        node.incoming.add(node1);
      }

      for (GraphNode node : node2.outgoing)
      {
        node.incoming.remove(node1);
        node.incoming.add(node2);
      }
    }

    public GraphNode getNode(String name)
    {
      return nodes.stream()
        .filter(node -> node.variable.name.equals(name))
        .findAny()
        .orElse(null);
    }

    public GraphNode getInputNode(String name)
    {
      return inputNodes.stream()
        .filter(node -> node.variable.name.equals(name))
        .findAny()
        .orElse(null);
    }

    public GraphNode getOutputNode(String name)
    {
      GraphNode output = outputNodes.stream()
        .filter(node -> node.variable.name.equals(name))
        .findAny()
        .orElse(null);

      if(output == null)
      {
        return getNode(name);
      }

      return output;
    }

    private final Set<GraphNode> nodes = new HashSet<>();

    private final List<GraphNode> inputNodes = new ArrayList<>();
    private final List<GraphNode> outputNodes = new ArrayList<>();

    private final Map<Integer, Set<GraphNode>> nodesByDepth = new HashMap<>();
  }

  private static class GraphNode
  {
    public GraphNode(Variable variable, GateOperation operation)
    {
      this.variable = variable;
      this.operation = operation;
    }

    public void resolve(long x, long y)
    {
      if(isInput())
      {
        String variableName = this.variable.name;
        String input = variableName.substring(1);
        int idx = Integer.parseInt(input);

        long mask = (1L << idx);
        if(variableName.startsWith("x"))
        {
          result = (x & mask) > 0 ;
          return;
        }
        else if(variableName.startsWith("y"))
        {
          result = (y & mask) > 0;
          return;
        }
      }

      var iter = this.incoming.iterator();
      var node1 = iter.next();
      var node2 = iter.next();

      result = doOperation(node1.result, node2.result);
    }

    private boolean doOperation(boolean xInput, boolean yInput)
    {
      return switch(operation)
      {
        case OR -> xInput || yInput;
        case AND -> xInput && yInput;
        case XOR -> xInput ^ yInput;
      };
    }

    public boolean isInput()
    {
      return incoming.isEmpty();
    }

    public boolean isOutput()
    {
      return outgoing.isEmpty();
    }

    @Override
    public String toString()
    {
      if(!isInput())
      {
        var iter = incoming.iterator();

        return iter.next().variable.name + " "
          + operation + " "
          + iter.next().variable.name + " => "
          + variable.name;
      }
      else
      {
        return variable.name;
      }
    }

    private boolean result;

    private Variable variable;
    // Can be null for input & output nodes. Value does not change between swaps
    private final GateOperation operation;

    // Dependencies, always 2, or zero (for depth 1). These are static during swaps
    private final Set<GraphNode> incoming = new HashSet<>();
    // Dependents, at least 1, or zero (for z outputs). These are changed during swaps
    private Set<GraphNode> outgoing = new HashSet<>();

    // The number of layers of dependencies above this node
    // i.e. a depth 0 is an input node, a depth 1 requires the values of 2 input nodes
    // A depth 2 requires the values of 4 input nodes & 2 intermediate nodes, so on.
    // This is static between swaps
    private int depth;
  }
}
