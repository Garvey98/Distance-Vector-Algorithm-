package SoftRouter;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Statistics {
  private int nodeId;
  private String packetType;
  private RouterPacket routerPacket;

  public Statistics() {

  }

  public Statistics(int nodeId, String packetType, RouterPacket routerPacket) {
    this.nodeId = nodeId;
    this.packetType = packetType;
    this.routerPacket = routerPacket;
  }

  public void saveToXML(String filePath)
      throws ParserConfigurationException, SAXException, IOException, TransformerException {
    File xmlFile = new File(filePath);

    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
    Document doc = dBuilder.parse(xmlFile);

    doc.getDocumentElement().normalize();

    NodeList nlist = doc.getElementsByTagName("node");

    for (int i = 0; i < nlist.getLength(); i++) {
      Node nnode = nlist.item(i);
      String id = ((Element) nnode).getAttribute("id");

      if (id.equals(String.valueOf(nodeId))) {
        Node sendNode = ((Element) nnode).getElementsByTagName(packetType).item(0);
        Element appendPacket = doc.createElement("packet");
        appendPacket.setTextContent(routerPacket.toString());
        sendNode.appendChild(appendPacket);
      }
    }

    DOMSource source = new DOMSource(doc);
    TransformerFactory transformerFactory = TransformerFactory.newInstance();
    Transformer transformer = transformerFactory.newTransformer();
    StreamResult result = new StreamResult(filePath);
    transformer.transform(source, result);
  }

  public void printStats() throws ParserConfigurationException, SAXException, IOException {
    File xmlSenderFile = new File("SoftRouter/RouterStatistics/SenderStatistics.xml");
    File xmlReceiverFile = new File("SoftRouter/RouterStatistics/ReceiverStatistics.xml");

    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

    Document sendDoc = dBuilder.parse(xmlSenderFile);
    // sendDoc.getDocumentElement().normalize();

    Document receiveDoc = dBuilder.parse(xmlReceiverFile);
    // receiveDoc.getDocumentElement().normalize();

    NodeList senderNlist = sendDoc.getElementsByTagName("node");
    NodeList receiverNlist = receiveDoc.getElementsByTagName("node");

    for (int i = 0; i < senderNlist.getLength(); i++) {
      System.out.println("[SYSTEM] Node " + i);

      System.out.println("Sender Packets:");
      Node senderNNode = senderNlist.item(i);
      Node senderSendNode = ((Element) senderNNode).getElementsByTagName("send").item(0);
      NodeList senderNodePacketList = senderSendNode.getChildNodes();
      for (int j = 1; j < senderNodePacketList.getLength(); j++) {
        System.out.println(j + ": " + senderNodePacketList.item(j).getTextContent().trim());
      }

      System.out.println("Receiver Packets:");
      Node receiverNNode = receiverNlist.item(i);
      Node receiverReceiveNode = ((Element) receiverNNode).getElementsByTagName("receive").item(0);
      NodeList receiverNodePacketList = receiverReceiveNode.getChildNodes();
      for (int j = 1; j < receiverNodePacketList.getLength(); j++) {
        System.out.println(j + ": " + receiverNodePacketList.item(j).getTextContent().trim());
      }

      System.out.println("Lost Packets:");
      Node lostNNode = receiverNlist.item(i);
      Node lostLostNode = ((Element) lostNNode).getElementsByTagName("lost").item(0);
      NodeList lostNodePacketList = lostLostNode.getChildNodes();
      for (int j = 1; j < lostNodePacketList.getLength(); j++) {
        System.out.println(j + ": " + lostNodePacketList.item(j).getTextContent().trim());
      }

      System.out.println();
    }
  }
}