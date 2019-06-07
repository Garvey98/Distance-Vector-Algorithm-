import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class WsbRouterTable {
  private int id;
  private int[][] myRouterTable = new int[5][3];
  private int[] neighborRouter = new int[5];

  public void setmyRouterTable(int des, int dis, int name) {
    myRouterTable[des][0] = des;
    myRouterTable[des][1] = dis;
    myRouterTable[des][2] = name;
  }

  public void printRouterTable(){
    for (int i = 0; i < myRouterTable.length; i++) {
      for (int j = 0; j < myRouterTable[0].length; j++) {
        System.out.print(myRouterTable[i][j]);
        System.out.print(" ");
      }
      System.out.println();
    }
  }

  public void setNeighborRouter(List<String> s) {
    for (int i = 0; i < s.size(); i++) {
      if ((!s.get(i).equals("0")) && (!s.get(i).equals("-"))) {
        // System.out.println(s.get(i));
        neighborRouter[i] = 1;
        System.out.println(i);
      } else {
        neighborRouter[i] = 0;
      }
    }
  }

  public int[] getNeighborRouter() {
    return neighborRouter;
  } 

  public WsbRouterTable(int id) {
    this.id = id;
    for (int i = 0; i < myRouterTable.length; i++) {
      for (int j = 0; j < myRouterTable[0].length; j++) {
        myRouterTable[i][j] = -1;
      }
    }
  }
}