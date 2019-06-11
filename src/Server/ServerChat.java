package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.Vector;

public class ServerChat {
    private Vector<ClientHandler> clients;

    public ServerChat() throws SQLException {
        ServerSocket server = null;
        Socket socket = null;
        clients = new Vector<>();
        try {
            AuthService.connection();
            server = new ServerSocket(8189);
            //System.out.println(AuthService.getNickByLoginAndPass("login1", "pass1"));
            System.out.println("The server is running!");

            while (true) {
                socket = server.accept();
                System.out.println("Client is connected!");
                new ClientHandler(socket, this);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println("Socket error!");
                e.printStackTrace();
            }
            try {
                server.close();
            } catch (IOException e) {
                System.out.println("Server error!");
                e.printStackTrace();
            }
            AuthService.disconnect();
        }
    }

    //Поиск пользователя в списке активных пользователей
    public synchronized boolean onlyOneUser(String nick) {
        for (ClientHandler o : clients) {
            if(o.getNick().equals(nick)) {
                return true;
            }
        }
        return false;
    }

    public void broadcastMsgPersonal(String msg, String nickSender, String nickRecipient) {
        for (ClientHandler o : clients) {
            if((o.getNick().equals(nickSender)) || (o.getNick().equals(nickRecipient))) {
                o.sendMsg(msg);
            }
        }
    }

    public void subscribe(ClientHandler client) {
        clients.add(client);
    }

    public void unsubscribe(ClientHandler client) {
        clients.remove(client);
    }

    public void broadcastMsg(String msg, String nickBoss) {
        for (ClientHandler c : clients) {
            if((AuthService.getNickInBlackList(c.getNick(), nickBoss)) == null) {  // поменял местами параметры на входе
                c.sendMsg(msg);
            }
        }
    }
    // для рассылки сервисных сообщений всем без учета черного списка
    public void broadcastMsgAll(String msg) {
        for (ClientHandler c : clients) {
                c.sendMsg(msg);
        }
    }
}
