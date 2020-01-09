package cn.edu.nuc.article.controller;



import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.AbstractQueue;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SocketClient {

  private String host = "localhost";
    
  public static int port = 3533;

  public static String user = "ervergrande";

  public static String password = "password";

  private static Map<String, AbstractQueue<SocketClient>> qs = new HashMap<String, AbstractQueue<SocketClient>>();

  private static Thread t = null;

  private Socket socket = null;
  
  private static byte [] toBytes(String text) throws IOException {
    return text.getBytes("UTF-8");
  }

  private static String br(String text) {
    if (text == null)
      return "\"\"";
    text = text.trim();
    if (text.length() == 0)
      return "\"\"";
    if (text.indexOf('"') != -1)
      text = text.replace('"', '\'');
    return "\"" + text + "\"";
  }

  private void closeSocket() {
    try {
//System.out.println("closeing connection from " + host);
      socket.close();
    }
    catch (IOException e) {
    }
  }

  private String invoke(String cmd) throws IOException {
    IOException ex = null;
    String err = null;
    if (socket.isClosed()) {
      return "Error not connected";
    }
    for (int i = 0; i < 2; i++) {
      try {
//System.out.print("invoke: " + cmd);
        err = invokeCommand(cmd);
//System.out.println("return: " + err);
        if (!err.isEmpty()) {
          if (!err.startsWith("Error"))
            return err;
        }
        if (!err.isEmpty()) {
          if (!err.startsWith("Error OOM"))
            return err;
        }
      }

      catch (IOException e) {
        ex = e;
        e.printStackTrace();
      }
      try {
//        System.out.println("invoke sleep thread name" + Thread.currentThread().getName()+"\n"+
//                "##########################################");
        Thread.sleep(100);
      }
      catch (Exception e) {
        e.printStackTrace();
      }
      closeSocket();
      connect(host, port);
    }
    if (err == null) {
      throw ex;
    }
    return err;
  }

  private String invokeCommand(String cmd) throws IOException {
//    System.out.print("invokeCommand: " + cmd);
    OutputStream out = socket.getOutputStream();
//    System.out.print("socket HostAddress: " + socket.getInetAddress().getHostAddress() +
//            "\n socket HostAddress: " + socket.getInetAddress().getHostName() +
//            "\n socket Port: " + socket.getPort() + "\n");
    out.write(toBytes(cmd));
    int len;
    byte [] bytes = new byte[256];
    StringBuffer sb = new StringBuffer();
  	InputStream in = socket.getInputStream();
    while ((len = in.read(bytes)) != -1) {
      String str = new String(bytes, 0, len, "UTF-8");
      int sp = str.indexOf('\n');
      if (sp != -1) {
        sb.append(str.substring(0, sp));
        break;
      }
      sb.append(str);
    }
    return sb.toString();
  }

  public String prepareHiddenWatermark(String fileId, String watermark) throws IOException {
    OutputStream out = socket.getOutputStream();
    StringBuffer sb = new StringBuffer();
    sb.append("$").append("prepareHiddenWatermark");
    sb.append(" ").append(br(fileId));
    sb.append(" ").append(br(watermark));
    sb.append("\n");
    return invoke(sb.toString());
  }


  public String generateOutputFile(String fileId, String watermark, boolean withHiddenWatermark, String text, String background, String imgType) throws IOException {
    StringBuffer sb = new StringBuffer();
    sb.append("$").append("generateOutputFile");
    sb.append(" ").append(br(fileId));
    sb.append(" ").append(br(watermark));
    sb.append(" ").append(br(withHiddenWatermark ? "embedWM" : ""));
    sb.append(" ").append(br(text));
    sb.append(" ").append(br(background));
    sb.append(" ").append(br(imgType));
    sb.append("\n");
    return invoke(sb.toString());
  }

  public String cleanupSourceFile(String fileId) throws IOException {
    StringBuffer sb = new StringBuffer();
    sb.append("$").append("cleanupSourceFile");
    sb.append(" ").append(br(fileId));
    sb.append("\n");
    return invoke(sb.toString());
  }

  public String cleanupHiddenWatermark(String fileId, String watermark) throws IOException {
    StringBuffer sb = new StringBuffer();
    sb.append("$").append("cleanupHiddenWatermark");
    sb.append(" ").append(br(fileId));
    sb.append(" ").append(br(watermark));
    sb.append("\n");
    return invoke(sb.toString());
  }

  private String login(String user, String password) throws IOException {
    StringBuffer sb = new StringBuffer();
    sb.append("$").append("login");
    sb.append(" ").append(br(user));
    sb.append(" ").append(br(password));
    sb.append("\n");
    return invokeCommand(sb.toString());
  }

  public String keepAlive() throws Exception {
    return invoke("$keepAlive\n");
  }

  private void connect(String host, int port) throws IOException {
    this.host = host;
    SocketException ex = null;
    for (int i = 0; i < 4; i++) {
      try {
//      System.out.println("reconnecting to watermark server " + host +"第" + i +"次 \n");
        socket = new Socket(host, port);
//        InetAddress inetAddress = InetAddress.getByName(host);
//        socket = new Socket(inetAddress,port);
        try {
          String rs = login(user, password);
          if (rs.startsWith("OK")) {

            return;
          }
          socket.close();
          throw new IOException("Fail when login to watermark server!");
        }
        catch (IOException e) {
          socket.close();
          throw e;
        }
      }
      catch (SocketException e) {
        ex = e;
      }
      try {
//        System.out.println("connect sleep thread name" + Thread.currentThread().getName() +"\n"+
//                "##########################################");
        Thread.sleep(1000 * i);
      }
      catch (Exception e) {
      }
    }
    throw ex;
  }

  public void close() throws IOException {
    if (!socket.isClosed()) {
      AbstractQueue<SocketClient> queue;
      synchronized (qs) {
        if (qs.containsKey(host)) {
          queue = qs.get(host);
        } else {
          queue = new ConcurrentLinkedQueue<SocketClient>();
          qs.put(host, queue);
        }
      }
      queue.add(this);
    }
  }
   static class KeepAlive implements Runnable {
     public void run() {
       for (;;) {
         try {
           Thread.sleep(200000);
         }
         catch (Exception e) {
         }
         for (String host : qs.keySet()) {
           try {
             SocketClient sc = SocketClient.connect(host);
             String ret = sc.invokeCommand("$keepAlive\n");
             if (ret.startsWith("OK")) {
//               System.out.println("Keep alive returns " + ret);
               sc.close();
             }
             else {
//               System.out.println("Keep alive returns " + ret);
               sc.socket.close();
//               System.out.println("Close the socket for fail of keep alive.");
             }
           }
           catch (Exception e) {
             e.printStackTrace();
           }
         }
       }
     }
   }
  public static SocketClient connect(String host) throws IOException {
    AbstractQueue<SocketClient> queue;
    synchronized (qs) {
      if (qs.containsKey(host)) {
        queue = qs.get(host);
      } else {
        queue = new ConcurrentLinkedQueue<SocketClient>();
        qs.put(host, queue);
      }
      if (t == null) {
        t = new Thread(new KeepAlive());
        t.start();
      }
    }
    SocketClient sc = queue.poll();
    if (sc == null) {
      sc = new SocketClient();
      sc.connect(host, port);
      return sc;
    }
    return sc;
  }
}
