//
// Copyright (c) 2024, Conserve It Pty. Ltd.
//

package org.example;

import org.example.utils.StringInput;

import java.util.*;
import java.util.stream.Collectors;

public class Day23
{
  private static boolean debug;

  public static long task1(StringInput input, boolean debug)
  {
    Day23.debug = debug;
    Map<String, Computer> computerMap = parseComputers(input);
    var threeSets = findThreeSets(computerMap);
    threeSets = filterNamesStartingWithT(threeSets);

    return threeSets.size();
  }

  public static String task2(StringInput input, boolean debug)
  {
    Day23.debug = debug;

    Map<String, Computer> computerMap = parseComputers(input);

    //Find the biggest set which each computer belongs to
    Set<Set<Computer>> biggestSets = new HashSet<>();
    for (Computer value : computerMap.values())
    {
      Set<Computer> biggestSet = findLargestSetFrom(value);
      biggestSets.add(biggestSet);
    }

    //Find the biggest set from that group (i.e. the biggest set for all computers)
    Set<Computer> biggestSet = biggestSets.stream()
      .max(Comparator.comparingInt(Set::size))
      .orElse(null);

    String password = biggestSet.stream()
      .map(comp -> comp.name)
      .sorted()
      .collect(Collectors.joining(","));

    System.out.println(password);

    return password;
  }

  //Filter a set of computers to only include sets where atleast one name starts with 't'
  private static Set<Set<Computer>> filterNamesStartingWithT(Set<Set<Computer>> comps)
  {
    var copy = new HashSet<>(comps);
    var iter = copy.iterator();
    while (iter.hasNext())
    {
      var compSet = iter.next();
      if (compSet.stream().noneMatch(comp -> comp.name.startsWith("t")))
      {
        iter.remove();
      }
    }

    return copy;
  }

  //Find all sets of three from the given computers
  private static Set<Set<Computer>> findThreeSets(Map<String, Computer> computerMap)
  {
    Set<Set<Computer>> results = new HashSet<>();

    for (Computer computer : computerMap.values())
    {
      for (Computer linkedComputer1 : computer.linkedComputers)
      {
        for (Computer linkedComputer2 : computer.linkedComputers)
        {
          if (linkedComputer1 == linkedComputer2)
          {
            continue;
          }

          if (linkedComputer1.linkedComputers.contains(linkedComputer2))
          {
            results.add(Set.of(computer, linkedComputer1, linkedComputer2));
          }
        }
      }
    }

    return results;
  }

  //Find the largest set that a given computer belongs to
  private static Set<Computer> findLargestSetFrom(Computer computer)
  {
    Set<Computer> largestSet = new HashSet<>();

    for (Computer linkedComputer : computer.linkedComputers)
    {
      Set<Computer> currentSet = new HashSet<>();
      currentSet.add(computer);
      currentSet.add(linkedComputer);

      for (Computer additionalComp : linkedComputer.linkedComputers)
      {
        if(additionalComp.linkedComputers.containsAll(currentSet))
        {
          currentSet.add(additionalComp);
        }
      }

      if(currentSet.size() > largestSet.size())
      {
        largestSet = currentSet;
      }
    }

    return largestSet;
  }

  //Parse computer strings & link them together
  private static Map<String, Computer> parseComputers(StringInput input)
  {
    Map<String, Computer> results = new HashMap<>();

    for (String line : input.asLines())
    {
      String[] tokens = line.split("-");
      String comp1Name = tokens[0];
      String comp2Name = tokens[1];

      if (!results.containsKey(comp1Name))
      {
        results.put(comp1Name, new Computer(comp1Name));
      }
      if (!results.containsKey(comp2Name))
      {
        results.put(comp2Name, new Computer(comp2Name));
      }

      Computer comp1 = results.get(comp1Name);
      Computer comp2 = results.get(comp2Name);

      comp1.link(comp2);
      comp2.link(comp1);
    }

    return results;
  }

  //Stores a computer name & what computer it links to
  public static class Computer
  {
    public Computer(String name)
    {
      this.name = name;
    }

    public void link(Computer computer)
    {
      linkedComputers.add(computer);
    }

    private final Set<Computer> linkedComputers = new HashSet<>();
    private final String name;
  }
}
