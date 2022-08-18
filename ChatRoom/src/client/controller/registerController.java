package client.controller;

import client.ClientMethod;
import common.entity.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import common.entity.User;

import java.net.URL;
import java.util.*;

public class registerController implements Initializable {
    //注册的控制类
    @FXML
    private TextField codeField;

    @FXML
    private TextField uidField;

    @FXML
    private ChoiceBox<Character> genderChoiceBox;

    @FXML
    private TextField nameField;

    @FXML
    private Button okButton;

    @FXML
    private Label messageLabel;

    @FXML
    private Button cancelButton;

    @FXML
    private PasswordField passwordField1;

    @FXML
    private PasswordField passwordField2;

    @FXML
    private TextField EmailField;

    @FXML
    private TextField ageField;

    @FXML
    private Button uidButton;

    @FXML
    private Button sendCodeButton;

    public String verificationCode = "";

    private String code = "-1";


    Map<EmailCode, Long> codeMap = new HashMap<>();
    //储存对应邮箱验证码的时间毫秒值

    private static final long TIME_OF_5_MIN = 5 * 60 * 1000;
    //五分钟的毫秒值

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //初始化
        ObservableList<Character> list = FXCollections.observableArrayList('男', '女');
        genderChoiceBox.setItems(list);
        //genderChoiceBox.getValue();

    }

    @FXML
    void okButtonOnAction(ActionEvent event) throws Exception {
        if (!checkAll()) return;
        if (uidButtonOnAction(null)) return;
        System.out.println(code);
        if (codeField.getText().length()==0){
            messageLabel.setText("验证码有误");
            return;
        }
        if (code.equalsIgnoreCase(codeField.getText())) {
            //调用注册的方法
            User user = new User(uidField.getText(), nameField.getText(), Get.getMD5(passwordField1.getText()), genderChoiceBox.getValue(), Integer.parseInt(ageField.getText()), EmailField.getText());
            user.setStates(0);
            if (ClientMethod.register(user)) {
                messageLabel.setText("注册成功！");
                Stage stage = (Stage) cancelButton.getScene().getWindow();
                stage.close();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("账号激活");
                alert.setHeaderText("亲爱的 " + user.getName()+":");
                alert.setContentText("您的账号为 " + user.getUid() + "，请妥善保管");

                alert.showAndWait();

                Parent root = FXMLLoader.load(getClass().getResource("../fxml/Login.fxml"));
                stage.setScene(new Scene(root, 520, 400));
                stage.show();
            }else{
                messageLabel.setText("未知错误，请联系管理员");
                System.out.println("未知错误，请联系管理员");
            }
        } else {
            Set<EmailCode> emailCodes = codeMap.keySet();
            for (EmailCode emailCode : emailCodes) {
                if (emailCode.getEmail() .equalsIgnoreCase( EmailField.getText())&&codeField.getText().equalsIgnoreCase(emailCode.getCode())) {
                    //调用注册的方法
                    User user = new User(uidField.getText(), nameField.getText(), Get.getMD5(passwordField1.getText()), genderChoiceBox.getValue(), Integer.parseInt(ageField.getText()), EmailField.getText());
                    user.setStates(0);
                    if (ClientMethod.register(user)) {
                        System.out.println("注册成功！");
                        messageLabel.setText("注册成功！");
                        Stage stage = (Stage) cancelButton.getScene().getWindow();
                        stage.close();

                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("账号激活");
                        alert.setHeaderText("亲爱的 " + user.getName()+":");
                        alert.setContentText("您的账号为 " + user.getUid() + "，请妥善保管");

                        alert.showAndWait();

                        Parent root = FXMLLoader.load(getClass().getResource("../fxml/Login.fxml"));
                        stage.setScene(new Scene(root, 520, 400));
                        stage.show();
                        break;
                    }else
                        messageLabel.setText("未知错误，请联系管理员");
                }
                else messageLabel.setText("验证码有误");
            }
        }
    }

    @FXML
    void cancelButtonOnAction(ActionEvent event) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }


    @FXML
    void sendCodeButtonOnAction(ActionEvent event) {
        if (!checkAll()) {
            return;
        } else if (uidButtonOnAction(null)) {
            return;
        } else if (checkEmailCode(EmailField.getText())) {
            messageLabel.setText("请勿重复发送验证码");
            return;
        } else if (ClientMethod.checkEmailExist(EmailField.getText()).length() >= 1) {
            messageLabel.setText("该邮箱已注册，账号为" + ClientMethod.checkEmailExist(EmailField.getText()));
            return;
        }
        if (Check.checkQQEmail(EmailField.getText())) {
            //发送验证码并将验证码和Email加入到map集合中
            //code = SendEmail.to(EmailField.getText(), nameField.getText());//应该放后端
            EmailCode emailCode = new EmailCode(EmailField.getText(), nameField.getText());
            //发送给后端
            code = ClientMethod.registerUserCode(emailCode);
            emailCode.setCode(code);
            if (Check.checkChinese(code)) {
                messageLabel.setText(code);
            } else {
                codeMap.put(emailCode, new Date().getTime());
                messageLabel.setText("验证码发送成功！");
            }
        } else {
            messageLabel.setText("QQ邮箱有误！");
        }
    }

    @FXML
    boolean uidButtonOnAction(ActionEvent event) {
        //有账号为true 无账号为false
        if (uidField.getText().length() == 0) {
            messageLabel.setText("账号不能为空！");
            return true;
        } else if (uidField.getText().length() > 15) {
            messageLabel.setText("账号过长！请输入不超过15个字符");
            return true;
        }

        //包装一个消息发给服务端进行处理
        boolean result = ClientMethod.checkUidExist(uidField.getText());
        if (result)
            messageLabel.setText("该账号已经被注册了，换一个吧~");
        else
            messageLabel.setText("这个账号还没被使用哦~~");
        return result;
    }


    private boolean checkAll() {
        //User正确返回true 不正确返回false
        if (Check.checkChinese(uidField.getText()) || Check.checkChinese(passwordField1.getText())) {
            messageLabel.setText("账号或密码不能含有中文！");
            return false;
        } else if (uidButtonOnAction(null)) {
            return false;
        }else if (uidField.getText().length() == 0) {
            messageLabel.setText("账号不能为空！");
            return false;
        } else if (uidField.getText().length() > 15) {
            messageLabel.setText("账号过长！请输入不超过15个字符");
            return false;
        } else if (passwordField1.getText().length() == 0) {
            messageLabel.setText("密码不能为空！");
            return false;
        }else if (!passwordField1.getText().equals(passwordField2.getText())){
            messageLabel.setText("两次密码不一致！");
            return false;
        } else if (nameField.getText().length() == 0 || nameField.getText().length() > 20) {
            messageLabel.setText("昵称有误");
            return false;
        } else if (genderChoiceBox.getValue() == null || genderChoiceBox.getValue().toString().length() < 1) {
            messageLabel.setText("未选择性别");
            return false;
        } else if (Integer.parseInt(ageField.getText()) < 1 || Integer.parseInt(ageField.getText()) > 120) {
            messageLabel.setText("年龄请填写1~120之间");
            return false;
        } else if (EmailField.getText().length()==0||!Check.checkQQEmail(EmailField.getText())){
            messageLabel.setText("邮箱有误");
            return false;
        }
        return true;
    }

    boolean checkEmailCode(String email) {
        //存在邮箱对应的验证码 返回true，不存在 返回false
        Set<EmailCode> emailCodes = codeMap.keySet();
        for (EmailCode emailCode : emailCodes) {
            if (emailCode.getEmail().equals(email)) {
                if (new Date().getTime() - codeMap.get(emailCode) < TIME_OF_5_MIN) {
                    return true;
                } else {
                    codeMap.remove(emailCode);
                }
            }
        }
        return false;
    }
}
