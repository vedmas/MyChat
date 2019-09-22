package Server;

import java.sql.*;
import java.util.ArrayList;

public class AuthService {
    private static Connection connection;
    private static Statement stmn;
    private static ArrayList<String> log = new ArrayList<>();

    public static ArrayList<String> getLog() {
        return log;
    }

    public static void connection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:DBUsers.db");
            stmn = connection.createStatement();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static String getNickByLoginAndPass(String login, String password) {
        String sql = String.format("select nick from main where login = '%s' and password = '%s'", login, password);
        try {
            ResultSet rs = stmn.executeQuery(sql);
            if (rs.next()) {
                return rs.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    //Поиск пользователя в таблице main
    public static boolean getNickInMain(String nick) {
        String sql = String.format("select nick from main where nick = '%s'", nick);
        String n = null;
        try {
            ResultSet serchNick = stmn.executeQuery(sql);
            if (serchNick.next()) {
                n = serchNick.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (n != null) {
            return true;
        } else return false;
    }

    //Поиск связки в таблице Blacklist
    public static String getNickInBlackList(String nickBoss, String blackNick) {
        String sql = String.format("select BlackNick from Blacklist where NickBoss = '%s' and BlackNick = '%s'", nickBoss, blackNick);
        ResultSet blackN = null;
        try {
            blackN = stmn.executeQuery(sql);
            if (blackN.next()) {
                return blackN.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    //Добавление записи в Blacklist
    public static int getInsertUserInBlacklist(String nickBoss, String blackNick) {
        String sql = String.format("insert into Blacklist(NickBoss, BlackNick) values('%s', '%s')", nickBoss, blackNick);
        int create = 0;
        try {
            create = stmn.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return create;
    }

    //Удаление записи из Blacklist
    public static int getRemoveUserInBlacklist(String nickBoss, String blackNick) {
        String sql = String.format("delete from Blacklist where nickBoss = '%s' and BlackNick = '%s'", nickBoss, blackNick);
        int create = 0;
        try {
            create = stmn.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return create;
    }

    //добавление записи в таблицу Chatlog
    public static int setInsertTextChatlog(String textChat) {
        String sql = String.format("insert into Chatlog(Text) values('%s')", textChat);
        int create = 0;
        try {
            create = stmn.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return create;
    }

    //добавление в таблицу Chatlog персональных сообщений
    public static int setInsertTextChatlogPersonal(String textChat, String nickSender, String nickRecipient) {
        String sql = String.format("insert into Chatlog(Text, nickSender, nickRecipient) values('%s', '%s', '%s')", textChat, nickSender, nickRecipient);
        int create = 0;
        try {
            create = stmn.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return create;
    }

    //вывод из таблици Chatlog всех записей из поля TEXT
    public static ArrayList<String> getChatlog() {
        String sql = String.format("select [Text], [nickSender], [nickRecipient] from Chatlog");
        ResultSet rs = null;
        try {
            rs = stmn.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            while (rs.next()) {
                log.add((rs.getString(2)) + " " + (rs.getString(3)) + " " + (rs.getString(1))); // + " " + rs.getString(2) + " " + rs.getString(3)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return log;
    }

    //очистка списка log для избежания дублирования записей
    public static void clearLog() {
        log.clear();
    }

    public static void disconnect() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

//    public static void main(String[] args) {
//        try {
//            connection();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        //System.out.println(getInsertUserInBlacklist("Nick1", "Nick2"));
//        //System.out.println(getNickInMain("nick4"));
//        //System.out.println(getRemoveUserInBlacklist("nick2", "nick1"));
//        //setInsertTextChatlog("Привет всем. Как дела? Кто чем сегодня занимается?");
//        clearLog();
////        for (String s : getChatlog()) {
////            System.out.println(s);
////        }
//        //setInsertTextChatlogPersonal("nick1: Yelow submarine", "nick1", "nick2");
//        disconnect();
//    }
}