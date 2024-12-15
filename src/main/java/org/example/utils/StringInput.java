//
// Copyright (c) 2024, Conserve It Pty. Ltd.
//

package org.example.utils;

import java.util.*;

//Helpful wrapper for passing test input into a task without worrying
//if we need to process it by line or as a whole
public class StringInput
{
  public StringInput(String input)
  {
    this.input = input;
    this.lines = StringUtils.splitIntoLines(input);
  }

  public String asString()
  {
    return input;
  }

  public List<String> asLines()
  {
    return lines;
  }

  public List<StringInput> splitAtEmptyLine()
  {
    List<StringInput> inputs = new ArrayList<>();
    StringBuilder builder = new StringBuilder();

    for (String line : asLines())
    {
      if(!line.isEmpty())
      {
        builder.append(line);
        builder.append("\n");
      }
      else
      {
        inputs.add(new StringInput(builder.toString()));
        builder = new StringBuilder();
      }
    }

    inputs.add(new StringInput(builder.toString()));

    return inputs;
  }

  private final String input;
  private final List<String> lines;
}
