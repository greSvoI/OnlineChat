package chat.server;

import chat.network.TcpConnection;
import chat.network.TcpConnectionObserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ChatServer implements TcpConnectionObserver {
    public static void main(String[] args) {
        new ChatServer();
    }
    private final ArrayList<TcpConnection> connections = new ArrayList<>();


    private ChatServer()
    {
        System.out.println("Server start!");
        try(ServerSocket serverSocket = new ServerSocket(8001))
        {
            while (true) {
                new TcpConnection(this,serverSocket.accept());
            }
        }
        catch (IOException e){

        }
    }

    @Override
    public synchronized void onConnection(TcpConnection tcpConnection) {
        connections.add(tcpConnection);
        BroadCastMsg(tcpConnection,"Connected : "+tcpConnection.toString());
    }

    @Override
    public synchronized void onDisconnect(TcpConnection tcpConnection) {
        connections.remove(tcpConnection);
        BroadCastMsg(tcpConnection,"Disconnect : "+tcpConnection.toString());
    }

    @Override
    public synchronized void onReceiveString(TcpConnection tcpConnection, String msg) {
            BroadCastMsg(tcpConnection,msg);
    }

    @Override
    public synchronized void onException(TcpConnection tcpConnection, Exception e) {
        System.out.println("Error"+e.getLocalizedMessage());
    }
    private void BroadCastMsg(TcpConnection tcpConnection,String msg) {
        System.out.println(msg);

        for (int i = 0; i < connections.size(); i++) {
            //if(connections.get(i).id!=tcpConnection.id)
                connections.get(i).sendMessage(msg);
        }
    }

}
