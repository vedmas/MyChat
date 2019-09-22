package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ClientHandler {
    private DataInputStream in;
    private DataOutputStream out;
    private Socket socket;
    private ServerChat server;
    private String nick;

    public String getNick() {
        return nick;
    }

    public ClientHandler(Socket socket, ServerChat server) {
        try {
            this.socket = socket;
            this.server = server;
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        String str = in.readUTF();
                        if(str.startsWith("/auth")) {
                            String [] tokens = str.split(" ");
                            String newNick = AuthService.getNickByLoginAndPass(tokens[1], tokens[2]);
                            if(newNick != null) {
                                if(!server.onlyOneUser(newNick)) {
                                    sendMsg("/authok!");
                                    Date data = new Date();
                                    SimpleDateFormat newData = new SimpleDateFormat("hh:mm:ss");
                                    server.broadcastMsgAll(newData.format(data) + ": " + "User " + newNick + " connected");
                                    for (String s : AuthService.getChatlog()) {      //вывод истории чата в окно чата после регистрации
                                        String [] massage = s.split(" ", 3);
                                        if((massage[0].equals(newNick)) || (massage[1].equals(newNick)) || (massage[1].equals("All"))) {
                                            sendMsg(massage[2]);
                                        }
                                    }
                                    AuthService.clearLog();
                                    nick = newNick;
                                    server.subscribe(ClientHandler.this);
                                    break;
                                } else { sendMsg("User is already connected!");}
                            } else sendMsg("Authorization filed!");
                        }
                    }
                    while (true) {
                        String str = in.readUTF();
                        Date data = new Date();
                        SimpleDateFormat newData = new SimpleDateFormat("hh:mm:ss");
                        // проверка на команду выход из чата
                        if(str.equals("/end")) {
                            server.broadcastMsgAll(nick + " disconnected!");

                            break;
                            // Отправка персональных сообщений
                        } else if(str.startsWith("/w ")) {
                            String [] personalChat = str.split(" ", 3);
                            server.broadcastMsgPersonal((newData.format(data) + ": "+ nick + ": " + personalChat[2]), personalChat[1], nick);
                            AuthService.setInsertTextChatlogPersonal(nick + ": " + personalChat[2], nick, personalChat[1]);
                            // Черный список add/remove
                        } else if(str.startsWith("/Add Black ") || str.startsWith("/add black ")) {
                            String[] blackUser = str.split(" ", 3);
                            if(AuthService.getNickInMain(blackUser[2])) {
                                if (AuthService.getNickInBlackList(nick, blackUser[2]) == null) {
                                    if (AuthService.getInsertUserInBlacklist(nick, blackUser[2]) == 1) {
                                        sendMsg("User " + blackUser[2] + " added in Black List");
                                    } else sendMsg("Error added Black List");
                                } else sendMsg("User is already enabled in Black List");
                            } else sendMsg(blackUser[2] + " is not registered!");
                        } else if(str.startsWith("/remove black ")) {
                            String[] dellist = str.split(" ", 3);
                            if(AuthService.getNickInBlackList(nick, dellist[2]) != null) {
                                if(AuthService.getRemoveUserInBlacklist(nick, dellist[2]) != 0) {
                                    sendMsg(dellist[2] + " remove in Black List");
                                } else sendMsg("Error remove " + dellist[2] + " in Black List!");
                            } else sendMsg(dellist[2] + " is not in Black List!");
                        } else {
                            server.broadcastMsg(newData.format(data) + ": " + nick + ": " + str, nick);
                            AuthService.setInsertTextChatlogPersonal(nick + ": " + str, nick, "All");
                        }
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                finally {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    server.unsubscribe(ClientHandler.this);
                }
            }
        }).start();

    }

    public void sendMsg(String msg) {
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
