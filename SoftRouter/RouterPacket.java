package SoftRouter;

import java.io.Serializable;

public class RouterPacket implements Serializable {
  // For object serialization
  private static final long serialVersionUID = 1L;

  // Source port 出发节点
  private int sourcePort;
  // Target port 目标节点
  private int targetPort;
  // TTL 每经过一个节点，TLL 自减 1
  private int timeToLive;
  // 发送数据
  private String data;

  public RouterPacket(int sourcePort, int targetPort, int timeToLive, String data) {
    this.sourcePort = sourcePort;
    this.targetPort = targetPort;
    this.timeToLive = timeToLive;
    this.data = data;
  }

  public int getSourcePort() {
    return this.sourcePort;
  }

  public void setSourcePort(int sourcePort) {
    this.sourcePort = sourcePort;
  }

  public int getTargetPort() {
    return this.targetPort;
  }

  public void setTargetPort(int targetPort) {
    this.targetPort = targetPort;
  }

  public int getTimeToLive() {
    return this.timeToLive;
  }

  public void setTimeToLive(int timeToLive) {
    this.timeToLive = timeToLive;
  }

  public String getData() {
    return this.data;
  }

  public void setData(String data) {
    this.data = data;
  }

  @Override
  public String toString() {
    return "sourcePort='" + getSourcePort() + "'" + ", targetPort='" + getTargetPort() + "'" + ", timeToLive='"
        + getTimeToLive() + "'" + ", data='" + getData() + "'";
  }
}