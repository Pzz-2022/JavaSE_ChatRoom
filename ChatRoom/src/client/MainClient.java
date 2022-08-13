package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MainClient extends Application {
    //客户端的主方法，启动登录的窗口
    public static UserLogin userLogin = new UserLogin();

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("fxml/Login.fxml"));
        primaryStage.initStyle(StageStyle.DECORATED);
        primaryStage.setTitle("PP-聊天室");
        primaryStage.getIcons().add(new Image("common/image/派蒙.jpeg"));
        primaryStage.setScene(new Scene(root,520,400));
        primaryStage.show();
    }

    public static void main(String[] args){
        launch(args);
    }
}