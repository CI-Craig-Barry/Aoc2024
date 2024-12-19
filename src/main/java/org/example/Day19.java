//
// Copyright (c) 2024, Conserve It Pty. Ltd.
//

package org.example;

import org.example.utils.StringInput;

import java.util.*;

public class Day19
{

  public static boolean debug;

  public static long task1(StringInput input, boolean debug)
  {
    Day18.debug = debug;

    var inputs = input.splitAtEmptyLine();
    PatternTree patternTree = makePatternTree(inputs.get(0));

    var designs = inputs.get(1);
    int possibleDesigns = 0;

    for (String line : designs.asLines())
    {
      PatternMatch match = findFirstMatch(line, patternTree);
      if(match != null)
      {
        possibleDesigns++;
      }

      if (debug)
      {
        if (match == null)
        {
          System.out.println(line + ", has no pattern matches\n");
        }
        else
        {
          System.out.println(line + ", has pattern match: ");
          System.out.println("\t" + match);
          System.out.println();
        }
      }
    }

    return possibleDesigns;
  }

  public static long task2(StringInput input, boolean debug)
  {
    Day18.debug = debug;

    var inputs = input.splitAtEmptyLine();
    PatternTree patternTree = makePatternTree(inputs.get(0));

    var designs = inputs.get(1);
    long totalMatches = 0;

    for (String line : designs.asLines())
    {
      long countMatches = getNumMatchingPatterns(line, patternTree);

      if (debug)
      {
        System.out.println(line + ", has " + countMatches + " matches");
      }

      totalMatches += countMatches;
    }

    return totalMatches;
  }

  //Construct a search tree for patterns
  private static PatternTree makePatternTree(StringInput input)
  {
    PatternTree tree = new PatternTree();
    String[] tokens = input.asString().split(", ");

    for (String token : tokens)
    {
      tree.addPattern(token.trim());
    }

    return tree;
  }

  //Find first match for a given pattern
  private static PatternMatch findFirstMatch(String pattern, PatternTree tree)
  {
    //Generate pattern matches starting from start of string
    List<PatternMatch> patternMatches = tree.matchPattern(pattern, 0)
      .stream()
      .map(v -> new PatternMatch(pattern, v))
      .toList();

    for (PatternMatch patternMatch : patternMatches)
    {
      PatternMatch result = findFirstMatchRecur(pattern, patternMatch, tree);
      if (result != null)
      {
        return result;
      }
    }

    return null;
  }

  //Recursively get best first match
  private static PatternMatch findFirstMatchRecur(String pattern, PatternMatch match, PatternTree tree)
  {
    if (match.isFullMatch())
    {
      return match;
    }

    List<PatternMatch> subMatches = tree.matchPattern(pattern, match.charsMatched)
      .stream()
      .map(match::copyAdd)
      .toList();

    for (PatternMatch subMatch : subMatches)
    {
      PatternMatch result = findFirstMatchRecur(pattern, subMatch, tree);
      if (result != null)
      {
        return result;
      }
    }

    return null;
  }

  //Counts the number of possible solutions for matching this pattern
  private static long getNumMatchingPatterns(String pattern, PatternTree tree)
  {
    //Calculate every possible next match if you started at every index of the string individually
    Map<Integer, List<String>> matchesAtIndex = new HashMap<>();
    int patternSize = pattern.length();
    for (int i = 0; i < patternSize; i++)
    {
      List<String> subMatches = tree.matchPattern(pattern, i);
      matchesAtIndex.put(i, subMatches);
    }

    Map<Integer, Long> pathsToEndCountByIndex = new HashMap<>();
    //Iterate backwards through matchesAtIndex & work out how many results you'd
    //get if you started at that index in the string. If the match does not get us to
    //the end of the string we can check how many routes there are at the index which
    //allows us to use the previously counted amount of paths to the end to efficiently
    //calculate the number of patterns
    for (int i = patternSize - 1; i >= 0; i--)
    {
      List<String> matches = matchesAtIndex.get(i);
      long indexCount = 0L;
      for (String match : matches)
      {
        int totalLen = i + match.length();
        //This pattern goes right to the end
        if(totalLen == patternSize)
        {
          indexCount++;
        }
        //This pattern doesn't get to the end, calculate how many paths
        //it can take to the end based on the previously calculated number of
        //paths to the end at that index
        else
        {
          long countAfterCurrentMatch = pathsToEndCountByIndex.get(i + match.length());
          indexCount += countAfterCurrentMatch;
        }
      }

      pathsToEndCountByIndex.put(i, indexCount);
    }

    return pathsToEndCountByIndex.get(0);
  }

  private static class PatternMatch
  {
    private PatternMatch(String input)
    {
      this.input = input;
      this.patterns = new LinkedList<>();
    }

    public PatternMatch(String input, String firstMatch)
    {
      this.input = input;
      this.charsMatched = firstMatch.length();
      this.patterns = new LinkedList<>();
      this.patterns.add(firstMatch);
    }

    public PatternMatch copy()
    {
      PatternMatch match = new PatternMatch(input);
      match.charsMatched = this.charsMatched;
      match.patterns.addAll(this.patterns);
      return match;
    }

    public PatternMatch copyAdd(String subPattern)
    {
      PatternMatch match = copy();
      match.add(subPattern);
      return match;
    }

    public void add(String subPattern)
    {
      this.charsMatched += subPattern.length();
      this.patterns.add(subPattern);
    }

    public boolean isFullMatch()
    {
      return charsMatched == input.length();
    }

    @Override
    public String toString()
    {
      StringJoiner builder = new StringJoiner(", ");
      patterns.forEach(builder::add);
      return builder.toString();
    }

    private final String input;
    private final List<String> patterns;
    private int charsMatched = 0;
  }

  //Tree which splits the possible patterns into a tree where each node is a character
  //added to its parent characters to form a string. This allows us to efficiently track
  //all possible matching patterns for a string by simply iterating down the tree using
  //the individual characters of the string & every endNode we hit represents a matching
  //pattern
  private static class PatternTree
    extends PatternNode
  {
    public PatternTree()
    {
      super(false, "");
    }

    //Add a pattern to the tree, used during construction of the tree
    public void addPattern(String pattern)
    {
      PatternNode iter = this;
      char[] patternChars = pattern.toCharArray();

      for (int i = 0; i < patternChars.length; i++)
      {
        char c = patternChars[i];
        PatternNode parent = iter;
        iter = iter.getChild(c);

        if (iter == null)
        {
          boolean isEndNode = i == patternChars.length - 1;
          PatternNode childNode = new PatternNode(isEndNode, pattern.substring(0, i + 1));
          parent.subPatterns.put(c, childNode);
          iter = childNode;
        }
      }

      iter.endNode = true;
    }

    //Given an input string, start at the given index & find all patterns that
    //match the string
    public List<String> matchPattern(String input, int index)
    {
      char[] inputChars = input.toCharArray();
      PatternNode iter = this;
      List<String> patternMatches = new LinkedList<>();

      for (int i = index; i < input.length(); i++)
      {
        char c = inputChars[i];
        iter = iter.getChild(c);

        if (iter == null)
        {
          break;
        }

        if (iter.endNode)
        {
          //Add in reverse order so biggest matches occur first in the list
          patternMatches.addFirst(iter.patternString);
        }
      }

      return patternMatches;
    }
  }

  //A node in the pattern tree, representing a single character of a larger pattern. The pattern
  //can be calculated by appending all characters from the root of the tree to this node
  private static class PatternNode
  {
    public PatternNode(boolean endNode, String patternString)
    {
      this.endNode = endNode;
      this.patternString = patternString;
      this.subPatterns = new HashMap<>();
    }

    public PatternNode getChild(char colorChar)
    {
      return subPatterns.get(colorChar);
    }

    private boolean endNode; // If true this is one of our input patterns
    private final String patternString;
    protected final Map<Character, PatternNode> subPatterns;
  }
}
