package client.controller;

import client.ClientMethod;
import common.util.Check;
import common.entity.EmailCode;
import common.util.Get;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class forgetPasswordController {
    //忘记即重置密码的控制类
    @FXML
    private TextField codeField;

    @FXML
    private Label messageLabel;

    @FXML
    private TextField passwordField1;

    @FXML
    private TextField passwordField2;

    @FXML
    private TextField emailField;

    @FXML
    private Button cancelButton;

    private String code;

    private static final Map<EmailCode, Long> emailCodeLongMap = new HashMap<>();

    private static final long TIME_OF_5_MIN = 5*60*1000;

    @FXML
    void sendCodeButtonOnAction(ActionEvent event) {
        if (Check.checkQQEmail(emailField.getText())) {
            Set<EmailCode> emailCodes = emailCodeLongMap.keySet();
            for (EmailCode emailCode : emailCodes) {
                if (emailCode.getEmail().equalsIgnoreCase(emailField.getText())){
                    if (new Date().getTime()-emailCodeLongMap.get(emailCode)<TIME_OF_5_MIN){
                        messageLabel.setText("请勿重复发送验证码！");
                        return;
                    }else
                        emailCodeLongMap.remove(emailCode);
                }
            }
            if (ClientMethod.checkEmailExist(emailField.getText()).length()<1) {
                messageLabel.setText("邮箱地址不存在！请先完成注册！");
                return;
            }

            EmailCode emailCode = new EmailCode(emailField.getText());
            code = ClientMethod.upDataSendCode(emailCode);
            emailCode.setCode(code);
            emailCodeLongMap.put(emailCode, new Date().getTime());
            messageLabel.setText("发送成功！");
        }else {
            messageLabel.setText("账号错误");
        }
    }

    @FXML
    void cancelButtonOnAction(ActionEvent event) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    void okButtonOnAction(ActionEvent event) throws Exception {
        if (!code.equalsIgnoreCase(codeField.getText())){
            messageLabel.setText("验证码错误");
        } else if ( Check.checkChinese(passwordField1.getText())) {
            messageLabel.setText("密码不能含有中文！");
        }  else if (passwordField1.getText().length() == 0) {
            messageLabel.setText("密码不能为空！");
        } else if(!passwordField1.getText().equals(passwordField2.getText())){
            messageLabel.setText("两次密码不一致");
        } else if (passwordField1.getText().length()>20){
            messageLabel.setText("密码过长，请输入不多于20个字母或数字。");
        }else{
            EmailCode emailCode = new EmailCode(emailField.getText(), Get.getMD5(passwordField1.getText()));

            if (ClientMethod.upPassword(emailCode)) {
                messageLabel.setText("修改成功！");
                Stage stage = (Stage) cancelButton.getScene().getWindow();
                stage.close();

                Parent root = FXMLLoader.load(getClass().getResource("../fxml/Login.fxml"));
                stage.setScene(new Scene(root, 520, 400));
                stage.show();
            } else {
                messageLabel.setText("未知错误，请联系管理员。");
            }
        }
    }
}
