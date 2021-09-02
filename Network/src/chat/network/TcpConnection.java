package chat.network;
import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

public class TcpConnection {
    private final Socket socket;
    private final Thread thread;
    private final BufferedReader in;
    private final BufferedWriter out;
    private final TcpConnectionObserver eventObserver;
    public String id;

    public TcpConnection(TcpConnectionObserver eventObserver,String ipAddr,int port)throws IOException{
        this(eventObserver,new Socket(ipAddr,port));
        this.id = ipAddr;
    }


    public TcpConnection(TcpConnectionObserver eventObserver,Socket socket) throws IOException {
        this.eventObserver = eventObserver;
        this.socket = socket;
        this.id = ""+socket.getInetAddress();
       in = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));
       out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),Charset.forName("UTF-8")));
       thread = new Thread(new Runnable() {
           @Override
           public void run() {
               try {
                   eventObserver.onConnection(TcpConnection.this);
                   while (!thread.isInterrupted())
                   {
                        eventObserver.onReceiveString(TcpConnection.this,in.readLine());
                   }
               } catch (IOException e) {
                   eventObserver.onException(TcpConnection.this,e);
               }
               finally {
                    eventObserver.onDisconnect(TcpConnection.this);
               }
           }
       });
       thread.start();
    }
    public synchronized void sendMessage(String msg)
    {
        try {
            out.write(msg+"\r\n");
            out.flush();
        } catch (IOException e) {
            eventObserver.onException(TcpConnection.this,e);
            Disconnect();
        }
    }
    public synchronized void Disconnect(){
        thread.isInterrupted();
        try {
            socket.close();
        } catch (IOException e) {
           eventObserver.onException(TcpConnection.this,e);
        }
    }
    @Override
    public String toString()
    {
        return socket.getInetAddress()+": "+socket.getPort();
    }

}
