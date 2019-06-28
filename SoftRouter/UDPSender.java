package SoftRouter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

public class UDPSender extends Thread {
  // 节点序号
  private int nodeId;
  // 下一跳的端口
  private int nextHopPort;
  // 分组待发送包内容
  private RouterPacket routerPacket;

  private DatagramSocket socket;
  private InetAddress address;
  private byte[] buf;

  public UDPSender(int nodeId, int nextHopPort, RouterPacket routerPacket)
      throws UnknownHostException, SocketException {
    // 节点序号
    this.nodeId = nodeId;
    // 当前端口和下一跳端口
    this.nextHopPort = nextHopPort;
    // 发送包
    this.routerPacket = routerPacket;

    this.socket = new DatagramSocket();
    this.address = InetAddress.getByName("localhost");
  }

  public void send() throws IOException, ParserConfigurationException, SAXException, TransformerException {
    // 将分组包转化为 byte 数组，方便发送
    ByteArrayOutputStream bStream = new ByteArrayOutputStream();
    ObjectOutput objectOutput = new ObjectOutputStream(bStream);
    objectOutput.writeObject(routerPacket);
    objectOutput.close();

    buf = bStream.toByteArray();

    // Create packet information to XML
    Statistics statistics = new Statistics(nodeId, "send", routerPacket);
    statistics.saveToXML("SoftRouter/RouterStatistics/SenderStatistics.xml");

    // 创建发送包
    DatagramPacket packet = new DatagramPacket(buf, buf.length, address, nextHopPort);
    socket.send(packet);
  }

  public void close() {
    socket.close();
  }

  public void run() {
    try {
      send();
    } catch (IOException | ParserConfigurationException | SAXException | TransformerException e) {
      e.printStackTrace();
    }
    close();
  }
}