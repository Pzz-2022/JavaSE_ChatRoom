package test;

import common.entity.Message;
import common.entity.User;
import org.junit.Test;
import server.MainServer;
import server.Mysql.GetMysql;

import java.net.Socket;
import java.sql.Connection;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

public class testDate {
    // 定义一个线程池 处理来自客户端的线程任务
    private static ExecutorService pool = new ThreadPoolExecutor(20,
            20, 6, TimeUnit.SECONDS, new ArrayBlockingQueue<>(5),
            Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());

    // 与数据库的连接 conn
    public static Connection conn;

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT1 = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Test
    public void testDate1() {
        System.out.println(SIMPLE_DATE_FORMAT1.format(new Date().getTime()));
        System.out.println();
    }

    @Test
    public void testMain() {
        //初始化所有数据
        try {
            // 得到数据库连接 在数据库操作时使用的conn
            conn = GetMysql.getConnection();

            ResultSet allUser = GetMysql.selectAllUser();
            while (allUser.next()) {
                MainServer.addUserToServer(allUser);
            }
            System.out.println("连接数据库成功。");
        } catch (Exception e) {
            System.out.println("连接数据库失败！");
            throw new RuntimeException(e);
        }
    }
}
