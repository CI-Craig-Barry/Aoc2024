//
// Copyright (c) 2024, Conserve It Pty. Ltd.
//

package org.example.utils;

import java.util.List;

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

  private final String input;
  private final List<String> lines;
}
