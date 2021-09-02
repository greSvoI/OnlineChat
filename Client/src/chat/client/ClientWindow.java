package chat.client;

import chat.network.TcpConnection;
import chat.network.TcpConnectionObserver;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.*;


public class ClientWindow extends JFrame implements ActionListener, TcpConnectionObserver {
    private static String IP_ADDR = "localhost";
    private static int PORT = 8001;
    private static int WIDTH = 600;
    private static int HEIGHT = 600;


    public static void main(String[] args) {
        new ClientWindow();
    }
    private static JTextArea messageBox = new JTextArea();
    private static JTextField name = new JFormattedTextField();
    private static JTextField message = new JFormattedTextField();
    private static TcpConnection connection;


    private ClientWindow()
    {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WIDTH,HEIGHT);
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);
        messageBox.setEditable(false);
        messageBox.setLineWrap(true);
        add(messageBox,BorderLayout.CENTER);
        add(name,BorderLayout.NORTH);
        add(message,BorderLayout.SOUTH);

        message.addActionListener(this);
        try {
            connection = new TcpConnection(this,IP_ADDR,PORT);
        } catch (IOException e) {
            printMessage("Error connection"+e);
        }


        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String msg = message.getText();
        if(msg.equals(""))return;
        message.setText(null);
        connection.sendMessage(name.getText()+": "+msg);
    }

    @Override
    public void onConnection(TcpConnection tcpConnection) {
        printMessage("Connection ready...");
    }

    @Override
    public void onDisconnect(TcpConnection tcpConnection) {
        printMessage("Connection close!");
    }

    @Override
    public void onReceiveString(TcpConnection tcpConnection, String msg) {
        printMessage(msg);
    }

    @Override
    public void onException(TcpConnection tcpConnection, Exception e) {
        printMessage("Error connection"+e);
    }
    private synchronized void printMessage(String msg) {
        messageBox.append(msg+"\n");
        messageBox.setCaretPosition(messageBox.getDocument().getLength());
    }
}
