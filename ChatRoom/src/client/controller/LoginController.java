package client.controller;

import client.ClientMethod;
import common.entity.Check;
import common.entity.Get;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import common.entity.User;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    //登录的控制类
    @FXML
    private Button forgetButton;

    @FXML
    private TextField codeField;

    @FXML
    private TextField uidField;

    @FXML
    private Button cancelButton;

    @FXML
    private Button registerButton;

    @FXML
    private Button refreshButton;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label codeLabel;

    @FXML
    private Button okButton;

    @FXML
    private Label messageLabel;

    private String verificationCode = "";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        refreshButtonOnAction(null);
    }

    public void okButtonOnAction(ActionEvent event) throws Exception {
        messageLabel.setText("Login ing...");
        String uid = uidField.getText();
        String passWorld = passwordField.getText();
        if (uid.isEmpty() || passWorld.isEmpty()) {
            messageLabel.setText("账号或密码为空！");
        } else if (!verificationCode.equalsIgnoreCase(codeField.getText())) {
            //判断验证码是否输入正确
            messageLabel.setText("验证码错误 请重新输入验证码");
            refreshButtonOnAction(null);
        } else if (uid.length() > 15) {
            messageLabel.setText("账号过长，请输入不多于十五个字符");
        } else if (passWorld.length() > 20) {
            messageLabel.setText("密码错误");
            passwordField.setText("");
        } else if (Check.checkChinese(uid) || Check.checkChinese(passWorld)) {
            messageLabel.setText("账号和密码不能含有中文！");
        } else {
            //下面将用户输入的账号和密码传给服务端，服务端在MySQL里检查是否有该账号，并回应客户端
            User user = new User();
            user.setUid(uidField.getText());
            user.setPassword(Get.getMD5(passwordField.getText()));
            System.out.println(Get.getMD5(passwordField.getText()));
            int login = ClientMethod.login(user);
            if(login==1){
                System.out.println("登录成功！");

                Parent root = FXMLLoader.load(getClass().getResource("../fxml/main.fxml"));
                Stage primaryStage = new Stage();
                primaryStage.initStyle(StageStyle.DECORATED);
                primaryStage.setTitle("PP-聊天室");
                primaryStage.getIcons().add(new Image("common/image/派蒙.jpeg"));
                primaryStage.setScene(new Scene(root,1330,700));
                primaryStage.show();

                //关闭登录页面进入聊天界面
                cancelButtonOnAction(null);
            }else if (login==10){
                messageLabel.setText("账号已登录。");
            }else {
                messageLabel.setText("账号或密码错误.");
            }
        }
    }

    public void cancelButtonOnAction(ActionEvent event) throws Exception {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    public void forgetButtonOnAction(ActionEvent event) throws Exception {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
        Parent root = FXMLLoader.load(getClass().getResource("../fxml/forgetPassword.fxml"));
        stage.setScene(new Scene(root, 520, 400));
        stage.show();
    }

    public void registerButtonOnAction(ActionEvent event) throws Exception {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
        Parent root = FXMLLoader.load(getClass().getResource("../fxml/register.fxml"));
        stage.setScene(new Scene(root, 520, 600));
        stage.show();
    }


    public void refreshButtonOnAction(ActionEvent event) {
        verificationCode = Get.getCode();
        codeLabel.setText(verificationCode);
        codeField.setText("");
    }
}
