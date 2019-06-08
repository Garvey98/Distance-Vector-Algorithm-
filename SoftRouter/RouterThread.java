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

public class RouterThread implements Runnable {
  private int id;
  private int portNUm;
  private int[][] nodeRouterTable = new int[5][3];
  private List<List<String>> allRouterTable;
  private int[] neighborRouter = new int[5];
  private static String[] routerName = { "A", "B", "C", "D", "E" };
  private Timer timer;

  TimerTask task = new TimerTask(){
  
    @Override
    public void run() {
      updateAllRouterTable();
      printNodeRouterTable();
    }
  };


  public RouterThread(int id, List<List<String>> allRouterTable){
    this.id = id;
    this.allRouterTable = allRouterTable;
    timer = new Timer();
    for (int i = 0; i < nodeRouterTable.length; i++) {
      for (int j = 0; j < nodeRouterTable[0].length; j++) {
        nodeRouterTable[i][j] = -1;
      }
    }
  }

  public void initNodeRouterTable(){
      int des;
      for (int j = 0; j < allRouterTable.get(0).size(); j++) {
        if (allRouterTable.get(id).get(j).equals("-")) {
          //do nothing
        } else {
          des = Integer.parseInt(allRouterTable.get(id).get(j));
          nodeRouterTable[j][0] = j;
          nodeRouterTable[j][1] = des;
          nodeRouterTable[j][2] = j;
        }
      }
  }

  public void updateAllRouterTable(){
    List<String> sourceNodeInfo = allRouterTable.get(id);
    for (int i = 0; i < allRouterTable.get(0).size(); i++) {
      if (neighborRouter[i] == 0) {
        continue;
      }
      if (i!=id) {
        if (!sourceNodeInfo.get(i).equals("-")) {
          if (!sourceNodeInfo.get(i).equals("0")) {
            int toAdjNodeDist = Integer.parseInt(sourceNodeInfo.get(i));

            // 邻居节点发来的数据表
            List<String> adjacentNodeInfo = allRouterTable.get(i);
            for (int j = 0; j < adjacentNodeInfo.size(); j++) {
              if (!adjacentNodeInfo.get(j).equals("-")) {
                if (!adjacentNodeInfo.get(j).equals("0")) {
                  if (j != id) {
                    int nodeToNextHopDist = Integer.parseInt(adjacentNodeInfo.get(j));
                    int newDist = nodeToNextHopDist + toAdjNodeDist;

                    if (sourceNodeInfo.get(j).equals("-")) {
                      setNodeRouterTable(j, newDist, i);
                      allRouterTable.get(id).set(j, Integer.toString(newDist));
                    } else {
                      if (newDist < Integer.parseInt(sourceNodeInfo.get(j))) {
                        setNodeRouterTable(j, newDist, i);
                        allRouterTable.get(id).set(j, Integer.toString(newDist));
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

  public List<List<String>> getAllRouterTable(){
    return allRouterTable;
  }

  public void setNeighborRouter(List<String> s){
    for (int i = 0; i < s.size(); i++) {
      if ((!s.get(i).equals("0")) && (!s.get(i).equals("-"))) {
        // System.out.println(s.get(i));
        neighborRouter[i] = 1;
      } else {
        neighborRouter[i] = 0;
      }
    }
  }

  public void setNodeRouterTable(int des, int dis, int name) {
    nodeRouterTable[des][0] = des;
    nodeRouterTable[des][1] = dis;
    nodeRouterTable[des][2] = name;
  }

  public void printNodeRouterTable(){
    for (int i = 0; i < nodeRouterTable.length; i++) {
      for (int j = 0; j < nodeRouterTable[0].length; j++) {
        if (nodeRouterTable[i][0] == -1) {
          System.out.print(nodeRouterTable[i][j]);         
        } 
        else if (j!=1) {
          System.out.print(routerName[nodeRouterTable[i][j]]);  
        }
        else {
          System.out.print(nodeRouterTable[i][j]);  
        }
        System.out.print(" ");
      }
      System.out.println();
    }
  }

  public void run(){
    System.out.println(id);
    // long tt = 5000;
    // timer.schedule(task, tt);
    
  }
}