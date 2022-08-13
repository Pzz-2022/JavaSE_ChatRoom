package server.Mysql;
import server.MainServer;

import java.sql.*;
import java.util.Map;
import java.util.TreeMap;


public class FriendShip {
    public static Map<Long, Long> map = new TreeMap<>();
    public Map<Long, Long> getFriendShip(){
        String sql = "select uid1,uid2 from friend_ship";
        try {
            Statement statement = MainServer.conn.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            map.clear();
            while (rs.next()){
                long uid1 = rs.getLong("uid1");
                long uid2 = rs.getLong("uid2");
                map.put(uid1,uid2);
            }
            rs.close();
            statement.close();
            //释放资源
        } catch (SQLException e) {
            return null;
        }
        return map;
    }
}
//好像没有用上