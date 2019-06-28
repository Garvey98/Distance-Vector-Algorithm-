package SoftRouter;

import java.util.List;

public class RouterTable {
  private int id;
  private int[][] myRouterTable = new int[5][3];
  private int[] neighborRouter = new int[5];
  private static String[] routerName = { "A", "B", "C", "D", "E" };

  public void setmyRouterTable(int des, int dis, int name) {
    myRouterTable[des][0] = des;
    myRouterTable[des][1] = dis;
    myRouterTable[des][2] = name;
  }

  public void printRouterTable(){
    for (int i = 0; i < myRouterTable.length; i++) {
      for (int j = 0; j < myRouterTable[0].length; j++) {
        if (myRouterTable[i][0] == -1) {
          System.out.print(myRouterTable[i][j]);         
        } 
        else if (j!=1) {
          System.out.print(routerName[myRouterTable[i][j]]);  
        }
        else {
          System.out.print(myRouterTable[i][j]);  
        }
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
      } else {
        neighborRouter[i] = 0;
      }
    }
  }

  public int[] getNeighborRouter() {
    return neighborRouter;
  } 

  public RouterTable(int id) {
    this.id = id;
    for (int i = 0; i < myRouterTable.length; i++) {
      for (int j = 0; j < myRouterTable[0].length; j++) {
        myRouterTable[i][j] = -1;
      }
    }
  }
}