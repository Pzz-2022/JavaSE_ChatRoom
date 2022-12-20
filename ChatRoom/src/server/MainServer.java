package server;

import common.entity.Message;
import common.entity.Type;
import server.Mysql.GetMysql;
import common.entity.User;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class MainServer {
    // 定义一个线程池 处理来自客户端的线程任务
    private static ExecutorService pool = new ThreadPoolExecutor(20,
            20, 6, TimeUnit.SECONDS, new ArrayBlockingQueue<>(5),
            Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());

    // 与数据库的连接 conn
    public static Connection conn;
    public static Map<String, User> users = new HashMap<>();//储存所有user信息
    public static Map<String, Socket> socketMap = new HashMap<>();//储存socket管道
    public static List<Message> allFriendShipList = new ArrayList<>();//储存好友关系
    public static List<Message> allChatRecord = new ArrayList<>();//储存聊天数据
    public static Map<String, List<String>> groupUid= new HashMap<>();//储存群中成员列表

    public static final String ServerFileRecv = "D:\\Software\\FileRecv\\ChatRoom\\ServerFileRecv\\";

    static {
        //初始化所有数据
        try {
            // 得到数据库连接 在数据库操作时使用的conn
            conn = GetMysql.getConnection();

            ResultSet allUser = GetMysql.selectAllUser();
            while (allUser.next()) {
                addUserToServer(allUser);
            }
            System.out.println(users);

            ResultSet friendShip = GetMysql.selectAllFriendShip();
            while (friendShip.next()) {
                addFriendShipToServer(friendShip);
                // 如果是群 先判断是否在group中存在 将群的成员全部放进group里面
                if (users.get(friendShip.getString("uid2")).getHeadPortrait()==100){
                    if (!groupUid.containsKey(friendShip.getString("uid2")))
                        groupUid.put(friendShip.getString("uid2"), new ArrayList<>());
                    groupUid.get(friendShip.getString("uid2")).add(friendShip.getString("uid1"));
                }
            }
            System.out.println(allFriendShipList);
            System.out.println(groupUid);

            ResultSet chatRecord = GetMysql.selectAllChatRecord();
            while (chatRecord.next()) {
                addChatRecordToServer(chatRecord);
            }
            System.out.println(allChatRecord);
        } catch (Exception e) {
            System.out.println("连接数据库失败！");
        }
    }

    public static void main(String[] args) {
        try {
            //创建一个服务端口，设置端口号为 7777
            ServerSocket serverSocket = new ServerSocket(7777);
            System.out.println("========服务器启动========");
            while (true) {
                //循环接受socket管道，来一条管道就new一个自定义的任务对象，将任务对象放进线程池子处理
                Socket socket = serverSocket.accept();
                Runnable target = new ServerRunnable(socket);
                pool.execute(target);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static void addUserToServer(ResultSet resultSet) throws SQLException {
        //初始化user数据的方法  将数据库中的数据取出来 存进内存
        User user = new User();
        user.setUid(resultSet.getString("uid"));
        user.setName(resultSet.getString("name"));
        user.setPassword(resultSet.getString("password"));
        user.setGender(resultSet.getString("gender").charAt(0));
        user.setAge(resultSet.getInt("age"));
        user.setEmail(resultSet.getString("email"));
        user.setHeadPortrait(resultSet.getInt("head_portrait"));
        user.setSignature(resultSet.getString("signature"));
        user.setBirthday(resultSet.getString("birthday"));
        users.put(user.getUid(), user);
    }

    static void addFriendShipToServer(ResultSet resultSet) throws SQLException {
        // 初始化好友数据的方法
        Message message = new Message();
        message.setFromUser(resultSet.getString("uid1"));
        message.setToUser(resultSet.getString("uid2"));
        message.setObject(resultSet.getInt("states"));
        message.setType(Type.FRIEND_SHIP);
        allFriendShipList.add(message);
    }

    static void addChatRecordToServer(ResultSet resultSet) throws SQLException {
        //初始化聊天记录的方法
        Message message = new Message();
        message.setFromUser(resultSet.getString("from_user"));
        message.setToUser(resultSet.getString("to_user"));
        message.setSendTime(resultSet.getLong("send_time"));
        message.setObject(resultSet.getString("content"));
        message.setType(Type.TEXT);
        if (resultSet.getInt("type")==1)
            message.setType(Type.IMAGE);
        else if (resultSet.getInt("type")==2)
            message.setType(Type.FILE);
        allChatRecord.add(message);
    }
}

