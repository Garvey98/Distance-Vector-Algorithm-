package SoftRouter;

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
  private static File RouterPortFile = new File("SoftRouter/RouterPort.txt");
  private static String RouterPort[][] = new String[5][2];
  // 路由表文件位置（初始值）
  private static File routerTablePrefFile = new File("SoftRouter/routerTable.txt");
  // 路由表数据结构
  private static List<List<String>> allRouterTable;
  // 路由节点名称对应表
  private static Map<String, Integer> routerNodeMap = Map.of("A", 0, "B", 1, "C", 2, "D", 3, "E", 4);
  // 节点的收敛状态：Converging / Steady
  private static String iterationStatus = "Converging";
  // 节点路由表
  private static int[][] nodeRouterTable;
  private static String[] routerName = { "A", "B", "C", "D", "E" };

  /**
   * Read router table
   * 
   * @param file 路由表文件
   * @return List<List<String>> 路由表数据结构（二维列表）
   */
  public static List<List<String>> readRouterTable(File file) throws FileNotFoundException, IOException {
    List<List<String>> allRouterTable = new ArrayList<List<String>>();
    BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

    String fileString;
    while ((fileString = bufferedReader.readLine()) != null) {
      List<String> strRow = Arrays.asList(fileString.split(" "));
      allRouterTable.add(strRow);
    }
    bufferedReader.close();

    return allRouterTable;
  }

  /**
   * 打印路由表
   * 
   * @param allRouterTable
   */
  public static void printRouterTable(List<List<String>> allRouterTable) {
    int routerTableRow = allRouterTable.size();
    int routerTableCol = allRouterTable.get(0).size();

    System.out.println("-------- Router Table ---------");
    System.out.println("|    | A  | B  | C  | D  | E  |");
    for (int i = 0; i < routerTableRow; i++) {
      System.out.print(String.format("| %-2s |", routerName[i]));
      for (int j = 0; j < routerTableCol; j++) {
        System.out.print(String.format(" %-2s |", allRouterTable.get(i).get(j)));
      }
      System.out.println();
    }
    System.out.println("-------- Router Table ---------");
  }

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

  public static void main(String[] args) throws FileNotFoundException, IOException {
    allRouterTable = SoftRouter.MainControl.readRouterTable(routerTablePrefFile);
    printRouterTable(allRouterTable);
    ReadRouterPort(RouterPortFile);
    RouterThread[] rt;
    Thread[] routerId;
    rt = new RouterThread[5];
    routerId = new Thread[5];
    for (int i = 0; i < 5; i++) {
    rt[i] = new RouterThread(i, allRouterTable);
    routerId[i] = new Thread(rt[i]);
    routerId[i].start();
    }
    // rt[0] = new RouterThread(0, allRouterTable);
    // routerId[0] = new Thread(rt[0]);
    // routerId[0].start();
    // rt[1] = new RouterThread(1, allRouterTable);
    // routerId[1] = new Thread(rt[1]);
    // routerId[1].start();

    rt[0].initNodeRouterTable();
    rt[0].setNeighborRouter(allRouterTable.get(0));
    rt[0].printNodeRouterTable();
    rt[1].printNodeRouterTable();

    // rt[0].printNodeRouterTable();
  }
}