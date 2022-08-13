package client.controller;

import client.ClientMethod;
import client.UserLogin;
import common.entity.Message;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import common.entity.User;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Predicate;

public class SearchFriendController implements Initializable {
    //搜索好友的控制类，用于添加好友时的查找用户
    @FXML
    public ListView<User> searchFriendListView;

    @FXML
    public Label messageLabel;
    @FXML
    public TextField searchField;

    private UserLogin userLogin = MainController.userLogin;

    Map<String, User> allUserMap = MainController.users;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //添加数据源
        ObservableList<User> friendList = searchFriendListView.getItems();

        allUserMap = ClientMethod.allUser();
        if (allUserMap != null){
            for (User value : allUserMap.values()) {
                if (!value.getUid().equals(userLogin.getUid())&&!userLogin.friendMap.containsKey(value.getUid())){
                    friendList.add(value);
                }
            }
        }


        searchFriendListView.setFocusTraversable(false);

        searchFriendListView.setCellFactory(new Callback<ListView<User>, ListCell<User>>() {
            @Override
            public ListCell<User> call(ListView<User> param) {
                return new ListCell<User>() {
                    @Override
                    protected void updateItem(User item, boolean empty) {
                        super.updateItem(item, empty);

                        if (!empty) {
                            ImageView touxiang = new ImageView("common/image/head/head(" + item.getHeadPortrait() + ").jpeg");
                            touxiang.setPreserveRatio(true);
                            touxiang.setFitHeight(40);

                            VBox vBox = new VBox(10);
                            vBox.setSpacing(5.0);
                            Label name = new Label(item.getName() + "  (" + item.getUid() + ")");
                            if (item.getSignature().isEmpty() || item.getSignature().length() == 0)
                                item.setSignature("这个少年很懒...");
                            Label signature = new Label(item.getSignature());
                            signature.setPrefWidth(360);
                            vBox.setAlignment(Pos.CENTER_LEFT);
                            vBox.getChildren().addAll(name, signature);

                            Button bu = new Button();
                            ImageView imageView = new ImageView("common/image/add.png");
                            imageView.setPreserveRatio(true);
                            imageView.setFitHeight(40);
                            bu.setGraphic(imageView);
                            bu.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent event) {
                                    //调用方法去发送给服务端加好友请求
                                    Message message = new Message();
                                    message.setFromUser(userLogin.getUid());
                                    message.setToUser(item.getUid());
                                    message.setObject(1);

                                    boolean result = ClientMethod.addFriendAtSql(message);
                                    if (result) {
                                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                        alert.setTitle("消息提示");
                                        alert.setHeaderText("亲爱的 " + userLogin.getName() + ":");
                                        alert.setContentText("已向账号为 " + item.getUid() + " 发送了好友申请！");

                                        alert.showAndWait();
                                    } else {
                                        messageLabel.setText("未知错误添加失败。");
                                    }
                                }
                            });

                            HBox hBox = new HBox(10);
                            hBox.setPrefWidth(240);
                            hBox.setPrefHeight(50);
                            hBox.setAlignment(Pos.CENTER_LEFT);
                            hBox.getChildren().addAll(touxiang, vBox, bu);

                            this.setGraphic(hBox);
                        }
                    }
                };
            }
        });

        searchField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                ObservableList<User> fl = friendList.filtered(new Predicate<User>() {
                    @Override
                    public boolean test(User user) {
                        return user.getUid().contains(newValue) || user.getName().contains(newValue);
                    }
                });
                searchFriendListView.setCellFactory(new Callback<ListView<User>, ListCell<User>>() {
                    @Override
                    public ListCell<User> call(ListView<User> param) {
                        return new ListCell<User>() {
                            @Override
                            protected void updateItem(User item, boolean empty) {
                                super.updateItem(item, empty);

                                if (!empty) {
                                    ImageView touxiang = new ImageView("common/image/head/head(" + item.getHeadPortrait() + ").jpeg");
                                    touxiang.setPreserveRatio(true);
                                    touxiang.setFitHeight(40);

                                    VBox vBox = new VBox(10);
                                    vBox.setSpacing(5.0);
                                    Label name = new Label(item.getName()+"  ("+item.getUid()+")");
                                    if (item.getSignature().isEmpty() || item.getSignature().length() == 0)
                                        item.setSignature("这个少年很懒...");
                                    Label signature = new Label(item.getSignature());
                                    signature.setPrefWidth(360);
                                    vBox.setAlignment(Pos.CENTER_LEFT);
                                    vBox.getChildren().addAll(name, signature);

                                    Button bu = new Button();
                                    ImageView imageView = new ImageView("common/image/add.png");
                                    imageView.setPreserveRatio(true);
                                    imageView.setFitHeight(40);

                                    bu.setGraphic(imageView);
                                    bu.setOnAction(new EventHandler<ActionEvent>() {
                                        @Override
                                        public void handle(ActionEvent event) {
                                            //调用方法去发送给服务端加好友请求
                                            Message message = new Message();
                                            message.setFromUser(userLogin.getUid());
                                            message.setToUser(item.getUid());
                                            message.setObject(1);

                                            boolean result = ClientMethod.addFriendAtSql(message);
                                            if (result) {
                                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                                alert.setTitle("消息提示");
                                                alert.setHeaderText("亲爱的 " + userLogin.getName() + ":");
                                                alert.setContentText("已向账号为 " + item.getUid() + " 发送了好友申请！");

                                                alert.showAndWait();
                                            } else {
                                                messageLabel.setText("未知错误添加失败。");
                                            }
                                        }
                                    });

                                    HBox hBox = new HBox(10);
                                    hBox.setPrefWidth(240);
                                    hBox.setPrefHeight(50);
                                    hBox.setAlignment(Pos.CENTER_LEFT);
                                    hBox.getChildren().addAll(touxiang, vBox, bu);

                                    this.setGraphic(hBox);
                                }
                            }
                        };
                    }
                });
                searchFriendListView.setItems(fl);
            }
        });

        searchField.setText(MainController.search);
    }


    public void searchFriendButtonOnAction(ActionEvent event) {/*无*/}

}
