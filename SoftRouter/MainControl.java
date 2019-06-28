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
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

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
  private static Timer timer;

  /**
   * Read router table
   * 
   * @param file 路由表文件
   * @return List<List<String>> 路由表数据结构（二维列表）
   */
  public static List<List<String>> readRouterTable(File file) throws FileNotFoundException, IOException {
    final List<List<String>> allRouterTable = new ArrayList<List<String>>();
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

  public static void main(String[] args)
      throws FileNotFoundException, IOException, ParserConfigurationException, SAXException {
    System.out.println("[Usage] Input name of node to get node attributes.");
    System.out.println("        Input \"RT\" to get all router table.");
    System.out.println("        Input \"SP\" to send message.");
    System.out.println("        Input \"ST\" to show statistice.");
    System.out.println("        Available nodes: \"A, B, C, D, E\".\n");
    System.out.println("[INFO] Router Initialize\n");
    allRouterTable = SoftRouter.MainControl.readRouterTable(routerTablePrefFile);
    printRouterTable(allRouterTable);
    ReadRouterPort(RouterPortFile);
    RouterThread[] rt;
    Thread[] routerId;
    rt = new RouterThread[5];
    routerId = new Thread[5];

    for (int i = 0; i < 5; i++) {
      rt[i] = new RouterThread(i, RouterPort);
      routerId[i] = new Thread(rt[i]);
      routerId[i].start();
      rt[i].setNeighborRouter(allRouterTable.get(0));
      rt[i].initNodeRouterTable(allRouterTable);
    }

    // rt[0] = new RouterThread(0);
    // routerId[0] = new Thread(rt[0]);
    // routerId[0].start();
    // rt[1] = new RouterThread(1);
    // routerId[1] = new Thread(rt[1]);
    // routerId[1].start();

    // rt[0].initNodeRouterTable();
    // rt[0].printNodeRouterTable();
    // rt[0].updateAllRouterTable(allRouterTable);
    // rt[0].printNodeRouterTable();
    // printRouterTable(rt[0].getAllRouterTable());
    // printRouterTable(allRouterTable);
    // rt[1].printNodeRouterTable();

    TimerTask task = new TimerTask() {

      @Override
      public void run() {
        for (int i = 0; i < 5; i++) {
          rt[i].updateAllRouterTable(allRouterTable);
        }
      }
    };
    timer = new Timer();
    long tt = 500;
    timer.schedule(task, tt, tt);

    // rt[0].printNodeRouterTable();
    Scanner scanner = new Scanner(System.in);
    while (true) {
      String input = scanner.nextLine();
      if (input.equals("q") || input.equals("Q")) {
        timer.cancel();
        // for (int i = 0; i < 5; i++) {
        // routerId[i].stop();
        // }
        break;
      }
      if (input.equals("RT") || input.equals("rt")) {
        printRouterTable(allRouterTable);
      }
      if (input.equals("SP") || input.equals("sp")) {
        String m_source = scanner.nextLine();
        if ("ABCDEabcde".contains(m_source)) {
          String m_target = scanner.nextLine();
          if ("ABCDEabcde".contains(m_target)) {
            String m_ttl = scanner.nextLine();
            int ttl = Integer.parseInt(m_ttl);
            if (ttl >= 0) {
              String m_data = scanner.nextLine();
              int source = routerNodeMap.get(m_source.toUpperCase());
              int des = routerNodeMap.get(m_target.toUpperCase());
              int ACK = rt[source].sendPacket(source, des, ttl, m_data);
              if (ACK == 1) {
                System.out.println("Send success");
              } else {
                System.out.println("No path for this message");
              }
            }
          } else {
            System.out.println("Wrong destination");
          }
        } else {
          System.out.println("Wrong source address");
        }
      }
      if (input.equals("ST") || input.equals("st")) {
        Statistics statistics = new Statistics();
        statistics.printStats();
      }
    }
    scanner.close();
  }
}