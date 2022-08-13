package client.controller;

import client.ClientMethod;
import common.entity.Message;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static client.controller.MainController.userLogin;


public class haoyouziliaoController implements Initializable {
    //好友资料的控制类
    @FXML
    private ImageView touxiang;

    @FXML
    private Label genderLabel;

    @FXML
    private Label gexingqianmingLabel;

    @FXML
    private Label uidLabel;

    @FXML
    private Label ageLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private Label nameLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        touxiang.setImage(new Image("common/image/head/head(" + userLogin.getSelectFriend().getHeadPortrait() + ").jpeg"));
        genderLabel.setText(String.valueOf(userLogin.getSelectFriend().getGender()));
        gexingqianmingLabel.setText(userLogin.getSelectFriend().getSignature());
        uidLabel.setText(userLogin.getSelectFriend().getUid());
        ageLabel.setText(String.valueOf(userLogin.getSelectFriend().getAge()));
        emailLabel.setText(userLogin.getSelectFriend().getEmail());
        nameLabel.setText(userLogin.getSelectFriend().getName());
    }

    @FXML
    public void shanchuhaoyouOnAction(ActionEvent event) {
        //删除好友的按钮
        List<Message> allFriendShipList = ClientMethod.getAllFriendShipList();
        if (allFriendShipList != null)
            MainController.allFriendShipList = allFriendShipList;
        for (Message message : MainController.allFriendShipList) {
            if (message.getToUser().equals(userLogin.getSelectFriend().getUid()) && message.getFromUser().equals(userLogin.getUid())
                    || message.getFromUser().equals(userLogin.getSelectFriend().getUid()) && message.getToUser().equals(userLogin.getUid())) {
                if ((int) message.getObject() == 2) {
                    message.setObject(0);
                    boolean b;
                    if (MainController.users.get(userLogin.getSelectFriend().getUid()).getEmail().equals(userLogin.getUid()))
                        b = ClientMethod.upDataFriendAtSqlOnGroup(message);
                    else
                        b = ClientMethod.upDataFriendAtSql(message);
                    if (b) {
                        System.out.println("删除成功。");
                    } else {
                        System.out.println("删除失败。");
                    }
                    Stage stage = (Stage) touxiang.getScene().getWindow();
                    stage.close();
                }
            }
        }
    }
}
