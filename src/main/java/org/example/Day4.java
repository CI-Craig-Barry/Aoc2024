//
// Copyright (c) 2024, Conserve It Pty. Ltd.
//

package org.example;

import org.example.utils.*;

import java.util.List;

public class Day4
{
  public static int task1(String input)
  {
    WordSearch wordSearch = makeWordsearch(input.trim());
    int result = 0;

    for(int i = 0; i < wordSearch.getNumLetters(); i++)
    {
      Pair<Integer, Integer> rowColPair = wordSearch.getRowColFromIndex(i);
      int row = rowColPair.getFirst();
      int col = rowColPair.getSecond();
      result += wordSearch.findFrom(row, col, "XMAS");
    }

    return result;
  }

  public static int task2(String input)
  {
    WordSearch wordSearch = makeWordsearch(input.trim());
    int result = 0;

    for(int i = 0; i < wordSearch.getNumLetters(); i++)
    {
      Pair<Integer, Integer> rowColPair = wordSearch.getRowColFromIndex(i);
      int row = rowColPair.getFirst();
      int col = rowColPair.getSecond();
      result += wordSearch.findXFrom(row, col, "MAS");
    }

    return result / 2;
  }

  private static WordSearch makeWordsearch(String input)
  {
    List<String> lines = StringUtils.splitIntoLines(input);
    int height = lines.size();
    int width = lines.getFirst().trim().length();

    char[][] letters = new char[lines.size()][];

    for(int i = 0; i < lines.size(); i++)
    {
      String line = lines.get(i).trim();
      letters[i] = line.toCharArray();
    }

    WordSearch wordSearch = new WordSearch();
    wordSearch.width = width;
    wordSearch.height = height;
    wordSearch.letters = letters;

    return wordSearch;
  }


  private static class WordSearch
  {
    public int findFrom(int row, int col, String text)
    {
      char startLetter = letters[row][col];
      if(text.charAt(0) == startLetter)
      {
        int count = 0;
        count = search(row, col, 1, 0, text) ? count + 1 : count;
        count = search(row, col, 1, 1, text) ? count + 1 : count;
        count = search(row, col, 0, 1, text) ? count + 1 : count;
        count = search(row, col, -1, 1, text) ? count + 1 : count;
        count = search(row, col, -1, 0, text) ? count + 1 : count;
        count = search(row, col, -1, -1, text) ? count + 1 : count;
        count = search(row, col, 0, -1, text) ? count + 1 : count;
        count = search(row, col, 1, -1, text) ? count + 1 : count;
        return count;
      }

      return 0;
    }

    public int findXFrom(int row, int col, String text)
    {
      char startLetter = letters[row][col];

      if(text.charAt(0) == startLetter)
      {
        int count = 0;
//        count = searchForX(row, col, 1, 0, text) ? count + 1 : count;
        count = searchForX(row, col, 1, 1, text) ? count + 1 : count;
//        count = searchForX(row, col, 0, 1, text) ? count + 1 : count;
        count = searchForX(row, col, -1, 1, text) ? count + 1 : count;
//        count = searchForX(row, col, -1, 0, text) ? count + 1 : count;
        count = searchForX(row, col, -1, -1, text) ? count + 1 : count;
//        count = searchForX(row, col, 0, -1, text) ? count + 1 : count;
        count = searchForX(row, col, 1, -1, text) ? count + 1 : count;
        return count;
      }

      return 0;
    }

    public Pair<Integer, Integer> getRowColFromIndex(int index)
    {
      int row = index / width;
      int col = index % width;

      return new Pair<>(row, col);
    }

    public int getNumLetters()
    {
      return width * height;
    }

    private boolean searchForX(int startRow, int startCol, int rowDirection, int colDirection, String text)
    {
      assert text.length() % 2 == 1;
      final int halfTextLength = text.length() / 2; // Rounded down always

      int midpointRow = startRow + (rowDirection * halfTextLength);
      int midpointCol = startCol + (colDirection * halfTextLength);

      //If we found one diagnoal, we need to check the opposite diagonal
      if(search(startRow, startCol, rowDirection, colDirection, text))
      {
        int firstDiagonalRowDirection = colDirection;
        int firstDiagonalColDirection = -rowDirection;

        int firstDiagonalRow = midpointRow - (firstDiagonalRowDirection * halfTextLength);
        int firstDiagonalCol = midpointCol - (firstDiagonalColDirection * halfTextLength);

        if(search(firstDiagonalRow, firstDiagonalCol, firstDiagonalRowDirection, firstDiagonalColDirection, text))
        {
          System.out.println("Found X at: [" + startRow + ", " + startCol + "]");
          return true;
        }

        int secondDiagonalRowDirection = -colDirection;
        int secondDiagonalColDirection = rowDirection;

        int secondDiagonalRow = midpointRow - (secondDiagonalRowDirection * halfTextLength);
        int secondDiagonalCol = midpointCol - (secondDiagonalColDirection * halfTextLength);

        if(search(secondDiagonalRow, secondDiagonalCol, secondDiagonalRowDirection, secondDiagonalColDirection, text))
        {
          System.out.println("Found X at: [" + startRow + ", " + startCol + "]");
          return true;
        }
      }

      return false;
    }

    private boolean search(int startRow, int startCol, int rowDirection, int colDirection, String text)
    {
      int row = startRow;
      int col = startCol;
      char c;

      for(int i = 0; i < text.length(); i++)
      {
        if(row < 0 || row >= height)
        {
          return false;
        }
        else if(col < 0 || col >= width)
        {
          return false;
        }

        c = letters[row][col];
        if(c == text.charAt(i))
        {
          row += rowDirection;
          col += colDirection;
          continue;
        }
        else
        {
          return false;
        }
      }

      return true;
    }

    private int width;
    private int height;
    private char[][] letters;
  }
}
