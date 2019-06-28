package SoftRouter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

public class UDPReceiver extends Thread {
  // 节点序号
  private int nodeId;
  // UDP Server 监听端口
  private int port;
  private String RouterPort[][] = new String[5][2];
  private int[][] nodeRouterTable = new int[5][3];

  private DatagramSocket socket;
  private InetAddress address;
  private boolean running;
  private byte[] buf = new byte[256];

  public UDPReceiver(int nodeId, int port, String[][] RouterPort, int[][] nodeRouterTable) throws SocketException, UnknownHostException {
    this.nodeId = nodeId;
    this.port = port;
    this.RouterPort = RouterPort;
    this.nodeRouterTable = nodeRouterTable;

    this.socket = new DatagramSocket(this.port);
    this.address = InetAddress.getByName("localhost");
  }

  private void listen()
      throws IOException, ClassNotFoundException, ParserConfigurationException, SAXException, TransformerException {
    DatagramPacket packet = new DatagramPacket(buf, buf.length);
    socket.receive(packet);

    ObjectInputStream iStream = new ObjectInputStream(new ByteArrayInputStream(buf));
    RouterPacket receivedPacket = (RouterPacket) iStream.readObject();
    iStream.close();

    System.out.println("UDP Receiver received: " + receivedPacket.toString());

    int ttl = receivedPacket.getTimeToLive();

    // Create packet information to XML
    Statistics receiverStatistics = new Statistics(nodeId, "receive", receivedPacket);
    receiverStatistics.saveToXML("SoftRouter/RouterStatistics/ReceiverStatistics.xml");

    int source = receivedPacket.getSourcePort();
    int des = receivedPacket.getTargetPort();
    String message = receivedPacket.getData();
    int id = 0;
    for (int i = 0; i < 5; i++) {
      if (des == Integer.parseInt(RouterPort[i][1])) {
        id = i;
      }
    }
    if (ttl == 0) {
      // Create packet information to XML
      Statistics lostStatistics = new Statistics(nodeId, "lost", receivedPacket);
      lostStatistics.saveToXML("SoftRouter/RouterStatistics/ReceiverStatistics.xml");
    }
    else if (nodeId != id) {
      if (nodeRouterTable[id][1] != -1) {
      
      int nextHop = Integer.parseInt(RouterPort[nodeRouterTable[id][2]][1]);
  
      RouterPacket packet1 = new RouterPacket(source, des, ttl-1, message);
      UDPSender udpSender1 = new UDPSender(nodeId, nextHop, packet1);
      udpSender1.run();
      }
    }

    if (receivedPacket.getData().equals("end")) {
      running = false;
    }
  }

  public void run() {
    System.out.println("[Server] Running Server at: " + address);

    running = true;

    while (running) {
      try {
        listen();
      } catch (IOException | ClassNotFoundException | ParserConfigurationException | SAXException
          | TransformerException e) {
        e.printStackTrace();
      }
    }

    socket.close();
  }
}