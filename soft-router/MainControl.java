import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class MainControl {
  private static File RouterPortFile = new File("RouterPort.txt");
  private static String RouterPort[][] = new String[5][2];

  public static void ReadRouterPort(File file) {
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(new FileReader(file));
      String tempString = null;
      int line = 0;
      while ((tempString = reader.readLine()) != null) {
        System.out.println("line " + line + ": " + tempString);
        String[] arr = tempString.split("\\s+");
        RouterPort[line][0] = arr[0];
        RouterPort[line][1] = arr[1];
        line++;
      }
      reader.close();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (IOException e1) {
        }
      }
    }
  }

  public static void main(String[] args) {
    ReadRouterPort(RouterPortFile);
    
  }
}