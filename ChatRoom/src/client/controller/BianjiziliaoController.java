package client.controller;

import client.ClientMethod;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import common.entity.User;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.ResourceBundle;

import static client.controller.MainController.userLogin;

public class BianjiziliaoController implements Initializable {
    //编辑个人信息的控制类
    @FXML
    public ImageView touxinagImageView;

    @FXML
    private Button cancelButton;

    @FXML
    private TextField ageField;

    @FXML
    private DatePicker birthdayDatePicker;

    @FXML
    private ChoiceBox<Integer> touxiangChoiceBox;

    @FXML
    private TextArea qianmingArea;

    @FXML
    private TextField nameField;

    @FXML
    private ChoiceBox<Character> genderChoiceBox;

    @FXML
    private Button okButton;

    @FXML
    private Label uidLabel;
    @FXML
    private Label messageLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //初始化时间
        String birthday = userLogin.getBirthday();
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            if (birthday != null && birthday.length() == 10) {
                Date date = simpleDateFormat.parse(birthday);//2022-08-10
                System.out.println(date.toString());
                String format = simpleDateFormat.format(date);
                System.out.println(format);
                String[] splits = format.split("-");
                birthdayDatePicker.setValue(LocalDate.of(getIntByString(splits[0]), getIntByString(splits[1]), getIntByString(splits[2])));
            }
        } catch (ParseException e) {
            System.out.println("无日期");
        }

        //初始化个人信息
        nameField.setText(userLogin.getName());
        qianmingArea.setText(userLogin.getSignature());
        if (userLogin.getUid() != null)
            uidLabel.setText(userLogin.getUid());
        ageField.setText(String.valueOf(userLogin.getAge()));

        touxinagImageView.setImage(new Image("common/image/head/head(" + userLogin.getHeadPortrait() + ").jpeg"));

        System.out.println(userLogin.getHeadPortrait());
        System.out.println(userLogin);

        //初始化
        ObservableList<Character> list = FXCollections.observableArrayList('男', '女');
        genderChoiceBox.setItems(list);
        char gender = userLogin.getGender();
        if (gender == '男' || gender == '女') {
            genderChoiceBox.setValue(gender);
        }

        ObservableList<Integer> headList = FXCollections.observableArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18);
        touxiangChoiceBox.setItems(headList);
        touxiangChoiceBox.setValue(userLogin.getHeadPortrait());
        touxiangChoiceBox.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<Integer>() {
                    @Override
                    public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                        touxinagImageView.setImage(new Image("common/image/head/head(" + touxiangChoiceBox.getValue() + ").jpeg"));
                    }
                }
        );
    }

    @FXML
    void okButtonOnAction(ActionEvent event) {
        if (getIntByString(ageField.getText()) < 1 || getIntByString(ageField.getText()) > 120) {
            messageLabel.setText("年龄请填写1~120之间");
            return;
        } else if (nameField.getText().length() == 0 || nameField.getText().length() > 20) {
            messageLabel.setText("昵称有误");
            return;
        } else if (qianmingArea.getText().length() >= 58) {
            messageLabel.setText("签名过长");
            return;
        } else if (birthdayDatePicker.getValue() == null || birthdayDatePicker.getValue().toString().length() == 0) {
            messageLabel.setText("未选择生日");
            return;
        }

        User user = new User();
        user.setName(nameField.getText());
        user.setSignature(qianmingArea.getText());
        user.setAge(getIntByString(ageField.getText()));
        user.setGender(genderChoiceBox.getValue());
        user.setHeadPortrait(touxiangChoiceBox.getValue());
        user.setUid(userLogin.getUid());
        System.out.println(birthdayDatePicker.getValue());
        user.setBirthday(birthdayDatePicker.getValue().toString());

        if (ClientMethod.upDataUser(user)) {
            messageLabel.setText("编辑成功！！");
            Stage stage = (Stage) okButton.getScene().getWindow();
            stage.close();

            userLogin.setName(nameField.getText());
            userLogin.setSignature(qianmingArea.getText());
            userLogin.setAge(getIntByString(ageField.getText()));
            userLogin.setGender(genderChoiceBox.getValue());
            userLogin.setHeadPortrait(touxiangChoiceBox.getValue());
            userLogin.setBirthday(birthdayDatePicker.getValue().toString());

            //MainController.refreshAll();
        } else {
            messageLabel.setText("未知错误，请联系管理员！");
        }
    }

    @FXML
    void cancelButtonOnAction(ActionEvent event) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    private int getIntByString(String ageStr) {
        int age = 0;
        for (int i = 0; i < ageStr.length(); i++) {
            age = age * 10 + ageStr.charAt(i) - '0';
        }
        return age;
    }
}
