package chat.network;

public interface TcpConnectionObserver {
    void onConnection(TcpConnection tcpConnection);
    void onDisconnect(TcpConnection tcpConnection);
    void onReceiveString(TcpConnection tcpConnection,String msg);
    void onException(TcpConnection tcpConnection,Exception e);
}
