package client;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import common.entity.User;

public class test {
    //没啥用的测试产品
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
    }

}


