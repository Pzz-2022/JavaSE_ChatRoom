package client.controller;

import client.ClientMethod;
import common.util.Check;
import common.entity.Message;
import common.entity.Type;
import common.entity.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.ArrayList;


public class QunController {
    //创建群的控制类
    @FXML
    private TextField uidField;

    @FXML
    private TextField nameField;

    @FXML
    private TextArea gexingArea;

    @FXML
    private Label messageLabel;

    @FXML
    boolean refreshButtonOnAction(ActionEvent event) {
        //有账号为true 无账号为false
        if (uidField.getText().length() == 0) {
            messageLabel.setText("ID不能为空！");
            return false;
        } else if (uidField.getText().length() > 15) {
            messageLabel.setText("ID过长！请输入不超过15个字符");
            return false;
        } else if (Check.checkChinese(uidField.getText())){
            messageLabel.setText("含有中文");
            return false;
        } else if (!Check.checkUid(uidField.getText())){
            messageLabel.setText("含有英文");
            return false;
        }

        //包装一个消息发给服务端进行处理
        boolean result = ClientMethod.checkUidExist(uidField.getText());
        if (result)
            messageLabel.setText("该ID已经被注册了，换一个吧~");
        else
            messageLabel.setText("这个ID还没被使用哦~~");
        return result;
    }

    @FXML
    void okButtonOnAction(ActionEvent event) {
        if (refreshButtonOnAction(null)){
            return;
        } else if (nameField.getText().length()>15){
            messageLabel.setText("name过长");
            return;
        } else if (gexingArea.getText().length()>58){
            messageLabel.setText("群简介过长");
            return;
        }
        User user = new User();
        user.setUid(uidField.getText());
        user.setName(nameField.getText());
        user.setPassword("100");
        user.setGender('群');
        user.setHeadPortrait(100);
        user.setSignature(gexingArea.getText());
        user.setStates(100);
        user.setAge(100);
        user.setEmail(MainController.userLogin.getUid());

        if (ClientMethod.register(user)) {
            messageLabel.setText("创建群成功！");
            cancelButtonOnAction(null);

            Message message = new Message();
            message.setFromUser(MainController.userLogin.getUid());
            message.setToUser(user.getUid());
            message.setType(Type.FRIEND_SHIP);
            message.setObject(2);
            ClientMethod.addFriendAtSql(message);

            MainController.users.put(user.getUid(), user);
            MainController.allFriendShipList.add(message);
            MainController.friendMap.put(message.getToUser(), user);
            MainController.chatRecordMap.put(message.getToUser(), new ArrayList<>());
            MainController.groupUid.put(message.getToUser(), new ArrayList<>());
        }else{
            messageLabel.setText("未知错误，请联系管理员");
            System.out.println("未知错误，请联系管理员");
        }
    }

    @FXML
    void cancelButtonOnAction(ActionEvent event) {
        Stage stage = (Stage) gexingArea.getScene().getWindow();
        stage.close();
    }
}
