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

public class Router {
  // 路由表文件位置（初始值）
  private static File routerTablePrefFile = new File("routerTable.txt");
  // 路由表数据结构
  private static List<List<String>> allRouterTable;
  // 路由节点名称对应表
  private static Map<String, Integer> routerNodeMap = Map.of("A", 0, "B", 1, "C", 2, "D", 3, "E", 4);
  // 节点的收敛状态：Converging / Steady
  private static String iterationStatus = "Converging";
  // 节点路由表
  private static RouterTable nodeRouterTable[];
  private static String[] routerName = { "A", "B", "C", "D", "E" };

  /**
   * Read router table
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

  public static void getNodeDistances(String node) {
    nodeRouterTable[routerNodeMap.get(node)].printRouterTable();
    // return allRouterTable.get(routerNodeMap.get(node));
  }

  public static void InitRouter(){
    int routerTableRow = allRouterTable.size();
    int routerTableCol = allRouterTable.get(0).size();
    nodeRouterTable = new RouterTable[5];
    for (int i = 0; i < routerTableRow; i++) {
      nodeRouterTable[i] = new RouterTable(i);
      nodeRouterTable[i].setNeighborRouter(allRouterTable.get(i));
      int des;
      for (int j = 0; j < routerTableCol; j++) {
        if (allRouterTable.get(i).get(j).equals("-")) {
          //do nothing
        } else {
          des = Integer.parseInt(allRouterTable.get(i).get(j));
          nodeRouterTable[i].setmyRouterTable(j, des, j);
        }
      }
    }
  }

  /**
   * 更新路由表某一节点的最短路径信息
   * @param source
   */
  public static void updateNode(int source) {
    int routerTableCol = allRouterTable.get(0).size();
    int[] neighbor = new int[5];
    neighbor = nodeRouterTable[source].getNeighborRouter();
    // 当前节点所存储的数据表
    List<String> sourceNodeInfo = allRouterTable.get(source);

    for (int i = 0; i < routerTableCol; i++) {
      if (neighbor[i]==0) {
        continue;
      }
      if (i != source) {
        if (!sourceNodeInfo.get(i).equals("-")) {
          if (!sourceNodeInfo.get(i).equals("0")) {
            int toAdjNodeDist = Integer.parseInt(sourceNodeInfo.get(i));

            // 邻居节点发来的数据表
            List<String> adjacentNodeInfo = allRouterTable.get(i);
            for (int j = 0; j < adjacentNodeInfo.size(); j++) {
              if (!adjacentNodeInfo.get(j).equals("-")) {
                if (!adjacentNodeInfo.get(j).equals("0")) {
                  if (j != source) {
                    int nodeToNextHopDist = Integer.parseInt(adjacentNodeInfo.get(j));
                    int newDist = nodeToNextHopDist + toAdjNodeDist;

                    if (sourceNodeInfo.get(j).equals("-")) {
                      nodeRouterTable[source].setmyRouterTable(j, newDist, i);
                      allRouterTable.get(source).set(j, Integer.toString(newDist));
                    } else {
                      if (newDist < Integer.parseInt(sourceNodeInfo.get(j))) {
                        nodeRouterTable[source].setmyRouterTable(j, newDist, i);
                        allRouterTable.get(source).set(j, Integer.toString(newDist));
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  /**
   * 更新整个路由表
   * Refresh whole routing table
   */
  public static void refreshNodes() {
    List<List<String>> originalRouterTable = allRouterTable;
    int routerTableRow = allRouterTable.size();

    for (int i = 0; i < routerTableRow; i++) {
      updateNode(i);
    }

    if (originalRouterTable.equals(allRouterTable)) {
      iterationStatus = "Steady";
    }
  }

  public static void main(String[] args) throws FileNotFoundException, IOException {
    System.out.println("[Usage] Input name of node to get node attributes.");
    System.out.println("        Input \"ALL\" to get router table.");
    System.out.println("        Input \"Q\" to quit.");
    System.out.println("        Input \"N\" to next.");
    System.out.println("        Available nodes: \"A, B, C, D, E\".\n");
    System.out.println("[INFO] Router Initialize\n");

    allRouterTable = Router.readRouterTable(routerTablePrefFile);
    printRouterTable(allRouterTable);
    InitRouter();

    Scanner scanner = new Scanner(System.in);
    int iterationIndex = 0;
    while (true) {
      System.out
          .println("[SYSTEM] Interactive console | Iterations: " + iterationIndex + " | Status: " + iterationStatus + "\n");

          // refreshNodes();

      String input = scanner.nextLine();

      if (input.equals("q") || input.equals("Q")) {
        break;
      }

      if ("ABCDEabcde".contains(input)) {
        getNodeDistances(input.toUpperCase());
        // System.out.println(nodeInfo);
      } else if (input.equals("ALL") || input.equals("all")) {
        printRouterTable(allRouterTable);
      } else if (input.equals("n") || input.equals("N")) {
        refreshNodes();
        iterationIndex++;
      }
      else {
        System.out.println("[INFO] Input n to refresh.");
        System.out.println("... Input name of node or \"ALL\" to get table of node(s).");
      }
    }
    scanner.close();
  }
}