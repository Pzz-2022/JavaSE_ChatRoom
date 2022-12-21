package server.Mysql;

import client.UserLogin;
import common.entity.EmailCode;
import common.entity.Message;
import common.entity.User;
import server.MainServer;

import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class GetMysql {
    public static String url;
    public static String username;
    public static String password;

    static {
        // 反射的知识
        InputStream inputStream = GetMysql.class.getClassLoader().getResourceAsStream("server/Mysql/db.properties");
        // 读取Properties配置文件
        Properties properties = new Properties();
        try {
            properties.load(inputStream);

            url = properties.getProperty("url");
            username = properties.getProperty("username");
            password = properties.getProperty("password");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 存放所有关于数据库的操作方法(JDBC)
    public static Connection getConnection() throws Exception {
        // 连接MySQL数据库
        Connection conn = DriverManager.getConnection(url, username, password);
        return conn;
    }

    public static String selectEmail(String email) throws Exception {
        // 通过Email查询用户id
        String sql = "select uid from user where email = ?";
        PreparedStatement preparedStatement = MainServer.conn.prepareStatement(sql);

        preparedStatement.setString(1, email);

        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getString("uid");
        }
        return "";
    }

    public static boolean selectUID(String uid) throws Exception {
        // 查看数据库是否有这个id唯一键存在
        String sql = "select uid from user where uid = ?";
        PreparedStatement preparedStatement = MainServer.conn.prepareStatement(sql);

        preparedStatement.setString(1, uid);

        ResultSet resultSet = preparedStatement.executeQuery();
        // 有就返回真 没有返回假
        return resultSet.next();
    }

    public static boolean register(User user) throws Exception {
        // 注册 给数据库增加一个用户（记录）
        String sql = "insert into user(uid,name,password,gender,age,email,head_portrait,signature) values(?,?,?,'" + user.getGender() + "',?,?,?,?)";
        PreparedStatement preparedStatement = MainServer.conn.prepareStatement(sql);

        preparedStatement.setString(1, user.getUid());
        preparedStatement.setString(2, user.getName());
        preparedStatement.setString(3, user.getPassword());
        preparedStatement.setInt(4, user.getAge());
        preparedStatement.setString(5, user.getEmail());
        preparedStatement.setInt(6, user.getHeadPortrait());
        if (user.getSignature() == null || user.getSignature().length() == 0) {
            user.setSignature("这个少年很懒...");
        }
        preparedStatement.setString(7, user.getSignature());

        int count = preparedStatement.executeUpdate();
        return count == 1;
    }

    public static UserLogin login(User user) throws Exception {
        // 登录 查询用户输入的账号密码是否正确
        UserLogin userLogin = new UserLogin();
        String sql = "select * from user where uid = ? && password = ?";
        PreparedStatement preparedStatement = MainServer.conn.prepareStatement(sql);

        preparedStatement.setString(1, user.getUid());
        preparedStatement.setString(2, user.getPassword());

        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            userLogin.setUid(resultSet.getString("uid"));
            userLogin.setName(resultSet.getString("name"));
            userLogin.setPassword(resultSet.getString("password"));
            userLogin.setGender(resultSet.getString("gender").charAt(0));
            userLogin.setAge(resultSet.getInt("age"));
            userLogin.setEmail(resultSet.getString("email"));
            userLogin.setHeadPortrait(resultSet.getInt("head_portrait"));
            userLogin.setSignature(resultSet.getString("signature"));
            userLogin.setBirthday(resultSet.getString("birthday"));
        }
        return userLogin;
    }

    public static boolean upPassword(EmailCode emailCode) throws Exception {
        // 重置密码，更新用户的密码
        String sql = "update user set password = ? where email = ?";
        PreparedStatement preparedStatement = MainServer.conn.prepareStatement(sql);

        preparedStatement.setString(1, emailCode.getName());//因为emailCode类中没有设置密码变量，故使用name来传递密码
        preparedStatement.setString(2, emailCode.getEmail());


        int count = preparedStatement.executeUpdate();
        System.out.println(count);
        return count == 1;
    }

    public static boolean upDataUser(User user) throws Exception {
        //修改个人信息，在数据库中update信息
        String sql = "update user set name = ? , signature = ? , age = ? , head_portrait = ? " +
                ", birthday = ? , gender = '" + user.getGender() + "' where uid = ?";
        PreparedStatement preparedStatement = MainServer.conn.prepareStatement(sql);

        preparedStatement.setString(1, user.getName());
        preparedStatement.setString(2, user.getSignature());
        preparedStatement.setInt(3, user.getAge());
        preparedStatement.setInt(4, user.getHeadPortrait());
        preparedStatement.setString(5, user.getBirthday());
        preparedStatement.setString(6, user.getUid());

        int count = preparedStatement.executeUpdate();
        return count == 1;
    }

    public static ResultSet selectAllUser() throws Exception {
        //让服务端获取所有的用户信息
        String sql = "SELECT * FROM user";
        PreparedStatement preparedStatement = MainServer.conn.prepareStatement(sql);

        return preparedStatement.executeQuery();
    }

    public static ResultSet selectAllFriendShip() throws Exception {
        //让服务端获取所有的好友关系
        String sql = "SELECT * FROM friend_ship";
        PreparedStatement preparedStatement = MainServer.conn.prepareStatement(sql);

        return preparedStatement.executeQuery();
    }

    public static ResultSet selectAllChatRecord() throws Exception {
        //查询聊天记录（所有的）
        String sql = "SELECT * FROM chat_record";
        PreparedStatement preparedStatement = MainServer.conn.prepareStatement(sql);

        return preparedStatement.executeQuery();
    }

    public static boolean addFriend(Message message) throws SQLException {
        //添加好友，数据库中好友关系表加一条数据
        String sql = "insert into friend_ship(uid1,uid2,states) values(?, ?, ?);";
        PreparedStatement preparedStatement = MainServer.conn.prepareStatement(sql);

        preparedStatement.setString(1, message.getFromUser());
        System.out.println(message.getFromUser());
        preparedStatement.setString(2, message.getToUser());
        preparedStatement.setInt(3, (int) message.getObject());

        int count = preparedStatement.executeUpdate();
        return count == 1;
    }

    public static boolean upFriend(Message message) throws SQLException {
        //同意或拒绝好友申请，更新数据库中的数据
        String sql = "update friend_ship set states = ? where uid2 = ? and uid1 = ? limit 1";
        PreparedStatement preparedStatement = MainServer.conn.prepareStatement(sql);

        preparedStatement.setInt(1, (Integer) message.getObject());
        preparedStatement.setString(2, message.getToUser());
        preparedStatement.setString(3, message.getFromUser());

        int count = preparedStatement.executeUpdate();
        return count > 0;
    }

    public static boolean deleteGroup(Message message) throws SQLException {
        //群的解散，更新数据库中的数据
        String sql = "delete from friend_ship where uid2 = ?";
        PreparedStatement preparedStatement = MainServer.conn.prepareStatement(sql);
        preparedStatement.setString(1, message.getToUser());

        int count = preparedStatement.executeUpdate();

        String sql2 = "delete from chat_record where to_user = ?";
        PreparedStatement preparedStatement2 = MainServer.conn.prepareStatement(sql2);
        preparedStatement2.setString(1, message.getToUser());
        preparedStatement2.executeUpdate();

        String sql3 = "delete from user where uid = ?";
        PreparedStatement preparedStatement3 = MainServer.conn.prepareStatement(sql3);
        preparedStatement3.setString(1, message.getToUser());
        preparedStatement3.executeUpdate();

        return count > 0;
    }


    public static boolean addChatRecord(Message message) throws SQLException {
        //加一条聊天记录
        String sql = "insert into chat_record(from_user,to_user,send_time,content,type) values(?, ?, ?, ?, ?);";
        PreparedStatement preparedStatement = MainServer.conn.prepareStatement(sql);

        preparedStatement.setString(1, message.getFromUser());
        preparedStatement.setString(2, message.getToUser());
        preparedStatement.setLong(3, message.getSendTime());
        preparedStatement.setString(4, (String) message.getObject());
        int type = 0;
        switch (message.getType()) {
            case TEXT:
                //type=0;
                break;

            case IMAGE:
                type = 1;
                break;

            case FILE:
                type = 2;
                break;
        }
        preparedStatement.setInt(5, type);

        int count = preparedStatement.executeUpdate();
        return count == 1;
    }
}
