package utils;

import java.io.IOException;
import java.net.*;
import java.nio.file.*;
import java.util.List;

public class FileUtils
{
  public static List<String> getLines(String fileName)
  {
    URL resourceUrl = FileUtils.class.getResource(fileName);
    try
    {
      return Files.readAllLines(Path.of(resourceUrl.toURI()));
    }
    catch (IOException e)
    {

      throw new RuntimeException(e);
    }
    catch (URISyntaxException e)
    {
      throw new RuntimeException(e);
    }
  }

  public static String getFileAsString(String fileName)
  {
    URL resourceUrl = FileUtils.class.getResource(fileName);
    try
    {
      return Files.readString(Path.of(resourceUrl.toURI()));
    }
    catch (IOException e)
    {

      throw new RuntimeException(e);
    }
    catch (URISyntaxException e)
    {
      throw new RuntimeException(e);
    }
  }
}
