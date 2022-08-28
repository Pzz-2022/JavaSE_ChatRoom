package client;


import common.entity.Message;
import common.entity.Type;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import common.entity.User;

import java.util.Date;

public class test {
    //没啥用的测试
    /*
    private static UserLogin userLogin;
    {
        userLogin = new UserLogin();
        User user1 = new User("2733286080", "五十步者", "adsdasdasd", '男', 19, "2733286080@qq.com");
        User user2 = new User("951913164", "百步者", "adsdasdasd", '男', 19, "951913164@qq.com");
        user2.setHeadPortrait(2);
        userLogin.setGender('男');
        userLogin.setHeadPortrait(13);
        userLogin.setName("空");
        userLogin.setUid("123");
        userLogin.friendMap.put("2733286080", user1);
        userLogin.friendMap.put("951913164", user2);
        System.out.println(userLogin.getUid());
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("fxml/yanzhengxiaoxi.fxml"));
        primaryStage.initStyle(StageStyle.DECORATED);
        primaryStage.setTitle("添加好友-" + userLogin.getName());
        primaryStage.getIcons().add(new Image("common/image/派蒙.jpeg"));
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
    */
    public static void main(String[] args) {
        System.out.println("Holle World");

/*
CREATE DATABASE chat_room;

user chat_room;

CREATE DATABASE chat_room;

user chat_room;

CREATE TABLE `user` (
  `uid` varchar(15) NOT NULL COMMENT '唯一ID',
  `name` varchar(20) NOT NULL COMMENT '用户名',
  `password` varchar(35) NOT NULL COMMENT '密码',
  `gender` char(1) NOT NULL COMMENT '性别',
  `age` int NOT NULL COMMENT '年龄',
  `email` varchar(22) NOT NULL COMMENT '邮箱',
  `head_portrait` int NOT NULL COMMENT '头像',
  `states` tinyint DEFAULT NULL COMMENT '在线状态',
  `signature` varchar(60) DEFAULT NULL COMMENT '个性签名',
  `birthday` varchar(15)  DEFAULT NULL COMMENT '生日日期',
  PRIMARY KEY (`uid`)
);

CREATE TABLE `chat_record` (
  `chat_id` bigint NOT NULL COMMENT '聊天记录ID',
  `from_user` varchar(25) DEFAULT NULL COMMENT '发送人uid',
  `to_user` varchar(25) DEFAULT NULL COMMENT '接收人uid',
  `send_time` bigint DEFAULT NULL COMMENT '发送时间的时间戳',
  `content` text COMMENT '聊天内容',
  `type` tinyint DEFAULT NULL COMMENT '消息类型（0是文本，1是图片，2是文件）',
  PRIMARY KEY (`chat_id`)
);

CREATE TABLE `friend_ship` (
  `uid1` varchar(25) NOT NULL COMMENT '好友关系的id1',
  `uid2` varchar(25) NOT NULL COMMENT '好友关系的id2',
  `states` tinyint NOT NULL COMMENT '验证消息的状态，0为拒绝或删除，1为正在验证，2为好友'
);
 */
    }

}
