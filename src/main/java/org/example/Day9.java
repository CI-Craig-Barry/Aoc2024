//
// Copyright (c) 2024, Conserve It Pty. Ltd.
//

package org.example;

import org.example.utils.*;

import java.util.*;

public class Day9
{
  private static boolean debug = false;

  public static long task1(StringInput input, boolean debug)
  {
    Day9.debug = debug;
    DiskMap map = makeDiskmap(input);
    map.compressByBlock();
    return map.checksum();
  }

  public static long task2(StringInput input, boolean debug)
  {
    Day9.debug = debug;
    DiskMap map = makeDiskmap(input);
    map.compressByFile();
    return map.checksum();
  }

  //Construct diskmap, files & block encoding from original diskmap string encoding
  public static DiskMap makeDiskmap(StringInput input)
  {
    DiskMap diskMap = new DiskMap();
    diskMap.files = new ArrayList<>();

    String diskMapEncoded = input.asString();

    File currentFile = null;
    char[] diskMapBlockChars = diskMapEncoded.toCharArray();

    for(int i = 0; i < diskMapBlockChars.length; i++)
    {
      int fileId = i / 2;
      boolean isBlockSize = i % 2 == 0;
      int value = Integer.parseInt(String.valueOf(diskMapBlockChars[i]));

      if(isBlockSize)
      {
        currentFile = new File();
        currentFile.id = fileId;
        currentFile.blockSize = value;
      }
      else
      {
        currentFile.freeSpace = value;
        diskMap.files.add(currentFile);
        currentFile = null;
      }
    }

    if(currentFile != null)
    {
      currentFile.freeSpace = 0;
      diskMap.files.add(currentFile);
    }

    diskMap.calculateBlocks();
    diskMap.printBlocks();

    return diskMap;
  }

  private static class DiskMap
  {
    //Calculate block layout based on original diskmap
    public void calculateBlocks()
    {
      blocks = new ArrayList<>();

      for (File file : files)
      {
        //Keep track of where the file was initially assigned in the block map
        file.startingBlockPosition = blocks.size();

        //Add assigned blocks
        for(int i = 0; i < file.blockSize; i++)
        {
          blocks.add(file.id);
        }

        //Add trailing free space
        for(int i = 0; i < file.freeSpace; i++)
        {
          blocks.add(FREE_SPACE_ID);
        }
      }
    }

    //Debug print the diskmap bocks
    public void printBlocks()
    {
      StringBuilder builder = new StringBuilder();

      for (Integer block : blocks)
      {
        if(block == FREE_SPACE_ID)
        {
          builder.append('.');
        }
        else
        {
          builder.append(block);
        }

        //Note test inputs don't have a space but they only use single digit numbers, for printing
        //the actual puzzle inputs spaces will be required to delimit IDs
        builder.append(' ');
      }

      System.out.println(builder.toString());
    }

    //Compress disk map by compressing per-block
    public void compressByBlock()
    {
      int startPointer = 0;
      int endPointer = blocks.size() - 1;

      //Iterate from the end of the blocks, finding assigned blocks to be moved
      //to the front of the blocks
      for(; endPointer > startPointer; endPointer--)
      {
        int endBlock = blocks.get(endPointer);
        //If this block is assigned
        if(endBlock != FREE_SPACE_ID)
        {
          //Find next place starting from the front of the blocks to put it
          for(; startPointer < endPointer; startPointer++)
          {
            int startBlock = blocks.get(startPointer);

            //Found empty spot, can swap in the end block into the start block
            if(startBlock == FREE_SPACE_ID)
            {
              blocks.set(startPointer, endBlock);
              blocks.set(endPointer, FREE_SPACE_ID);

              break;
            }
          }
        }
      }
    }

    //Compress disk map by compressing files in a single pass
    public void compressByFile()
    {
      //Get list of files that haven't been moved
      List<File> unmovedFiles = new ArrayList<>(files);
      //Reverse list to order them in the priority order of when they should be moved
      Collections.reverse(unmovedFiles);

      //List of pairs that contain <index, size> of all free spaces in the block map
      List<Pair<Integer, Integer>> freeSpaceMapping = mapFreeSpace();

      //Iterate over files in reverse file ID order
      for (int i = 0; i < unmovedFiles.size(); i++)
      {
        File file = unmovedFiles.get(i);
        ListIterator<Pair<Integer, Integer>> freeSpaceIter = freeSpaceMapping.listIterator();

        while (freeSpaceIter.hasNext())
        {
          var freeSpaceEntry = freeSpaceIter.next();
          int freeSpaceIndex  = freeSpaceEntry.getFirst();
          int freeSpaceSize = freeSpaceEntry.getSecond();

          //Moving file right of its starting position makes no sense, we can stop
          //searching as all remaining free space is worse than its current position
          if(freeSpaceIndex > file.startingBlockPosition)
          {
            break;
          }

          //Can be moved into this space, then move it there
          if(file.blockSize <= freeSpaceSize)
          {
            //Assign blocks & clear out original blocks
            clearBlocks(file.startingBlockPosition, file.blockSize);
            assignBlocks(freeSpaceIndex, file.blockSize, file.id);

            //For debugging
            if(Day9.debug)
            {
              printBlocks();
            }

            //Update free space entry
            Pair<Integer, Integer> newEntry = new Pair<>(
              freeSpaceIndex + file.blockSize, //New free block will start following assigned block
              freeSpaceSize - file.blockSize //Calculate remaining free size
            );

            //If there is actually any free space left, overrwrite entry
            if(newEntry.getSecond() > 0)
            {
              freeSpaceIter.set(newEntry);
            }
            //No space left, remove entry
            else
            {
              freeSpaceIter.remove();
            }

            //No need to move this file again. Break to attempt moving next file
            break;
          }
        }
      }

      //Print resulting disk-map
      System.out.println("*** RESULT ***");
      printBlocks();
    }

    //Maps the index of a free space to the length of the free space
    private List<Pair<Integer, Integer>> mapFreeSpace()
    {
      List<Pair<Integer, Integer>> result = new ArrayList<>();

      for(int i = 0; i < blocks.size(); i++)
      {
        if(blocks.get(i) == FREE_SPACE_ID)
        {
          int freeBlockSize = calculateFreeSpaceSize(i);
          result.add(new Pair<>(i, freeBlockSize));

          //Iterate past remaining free space to not count it multiple times
          i += freeBlockSize - 1;
        }
      }

      return result;
    }

    //Clear out an amount of blocks at a given index
    private void clearBlocks(int index, int amount)
    {
      assignBlocks(index, amount, FREE_SPACE_ID);
    }

    //Assign an amount of blocks with a given value starting at a given index
    private void assignBlocks(int index, int amount, int value)
    {
      for(int i = 0; i < amount; i++)
      {
        blocks.set(index + i, value);
      }
    }

    //Calculate the amount of free space starting a given index
    private int calculateFreeSpaceSize(int index)
    {
      int freeSpace = 0;

      while(index < blocks.size())
      {
        if(blocks.get(index) != FREE_SPACE_ID)
        {
          break;
        }

        freeSpace++;
        index++;
      }

      return freeSpace;
    }

    //Calculate checksum of diskmap
    public long checksum()
    {
      long checksum = 0L;

      for(int i = 0; i < blocks.size(); i++)
      {
        int fileId = blocks.get(i);

        if(fileId == FREE_SPACE_ID)
        {
          //Can't break due to part 2 leaving some free spaces
          continue;
        }

        checksum += (long)i * (long)fileId;
      }

      return checksum;
    }

    //Defines the files
    private List<File> files;

    //Maps disk block [ID] to file ID
    private List<Integer> blocks;
  }

  //Record for file attributes
  private static class File
  {
    //Unique file identifier assigned at parsing (pre-compression)
    private int id;
    //Starting position (pre-compression)
    private int startingBlockPosition;
    //Size of blocks
    private int blockSize;
    //Amount of free space initially following this block
    private int freeSpace;
  }

  private static final int FREE_SPACE_ID = -1;
}
