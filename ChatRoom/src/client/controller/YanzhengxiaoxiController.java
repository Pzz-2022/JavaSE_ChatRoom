package client.controller;

import client.ClientMethod;
import client.UserLogin;
import common.entity.Message;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;


public class YanzhengxiaoxiController implements Initializable {
    //验证好友的控制类
    @FXML
    public ListView<Message> yanzhengListView;

    List<Message> yanzhengList = MainController.yanzhengList;

    private static Map<String, User> users = MainController.users;
    private static UserLogin userLogin = MainController.userLogin;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<Message> items = yanzhengListView.getItems();
        items.addAll(yanzhengList);
        System.out.println(yanzhengList);

        yanzhengListView.setCellFactory(new Callback<ListView<Message>, ListCell<Message>>() {
            @Override
            public ListCell<Message> call(ListView<Message> param) {
                return new ListCell<Message>() {
                    @Override
                    protected void updateItem(Message item, boolean empty) {
                        super.updateItem(item, empty);

                        if (!empty) {
                            ImageView touxiang = new ImageView("common/image/head/head(" + users.get(item.getFromUser()).getHeadPortrait() + ").jpeg");
                            touxiang.setPreserveRatio(true);
                            touxiang.setFitHeight(40);

                            VBox vBox = new VBox(10);
                            vBox.setSpacing(5.0);
                            Label name = new Label(users.get(item.getFromUser()).getName() + "  (" + users.get(item.getFromUser()).getUid() + ")");
                            if (users.get(item.getFromUser()).getSignature().isEmpty() || users.get(item.getFromUser()).getSignature().length() == 0)
                                users.get(item.getFromUser()).setSignature("这个少年很懒...");
                            Label signature;
                            if (users.get(item.getToUser()).getHeadPortrait() == 100) {
                                signature = new Label(users.get(item.getFromUser()).getSignature()+"              请求加入你的群聊:"+users.get(item.getToUser()).getName());
                            } else {
                                signature = new Label(users.get(item.getFromUser()).getSignature());
                            }
                            signature.setPrefWidth(300);
                            vBox.setAlignment(Pos.CENTER_LEFT);
                            vBox.getChildren().addAll(name, signature);

                            Button bu2 = new Button();
                            ImageView imageView = new ImageView("common/image/ok_hover.png");
                            imageView.setPreserveRatio(true);
                            imageView.setFitHeight(40);
                            bu2.setGraphic(imageView);
                            bu2.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent event) {
                                    //调用方法去发送给服务端加好友请求
                                    item.setObject(2);
                                    boolean result = ClientMethod.upDataFriendAtSql(item);
                                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                    alert.setTitle("消息提示");
                                    alert.setHeaderText("亲爱的 " + userLogin.getName() + ":");
                                    if (result) {
                                        alert.setContentText("添加成功");
                                        MainController.allFriendShipList.add(item);
                                        if (users.get(item.getToUser()).getHeadPortrait() != 100)
                                            MainController.friendMap.put(item.getFromUser(), users.get(item.getFromUser()));
                                        else
                                            MainController.groupUid.get(item.getToUser()).add(item.getFromUser());
                                    } else {
                                        alert.setContentText("发生错误");
                                    }
                                    alert.showAndWait();
                                    yanzhengList.remove(item);
                                    fun(items, item);
                                }
                            });

                            Button bu0 = new Button();
                            ImageView imageView0 = new ImageView("common/image/cancel_hover.png");
                            imageView0.setPreserveRatio(true);
                            imageView0.setFitHeight(40);
                            bu0.setGraphic(imageView0);
                            bu0.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent event) {
                                    //调用方法去发送给服务端加好友请求
                                    item.setObject(0);
                                    boolean result = ClientMethod.upDataFriendAtSql(item);
                                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                    alert.setTitle("消息提示");
                                    alert.setHeaderText("亲爱的 " + userLogin.getName() + ":");
                                    if (result) {
                                        alert.setContentText("拒绝成功");
                                    } else {
                                        alert.setContentText("发生错误");
                                    }
                                    alert.showAndWait();
                                    yanzhengList.remove(item);
                                    fun(items, item);
                                }
                            });

                            HBox hBox = new HBox(10);
                            hBox.setPrefWidth(240);
                            hBox.setPrefHeight(50);
                            hBox.setAlignment(Pos.CENTER_LEFT);
                            hBox.getChildren().addAll(touxiang, vBox, bu2, bu0);

                            this.setGraphic(hBox);
                        }
                    }
                };
            }
        });
    }

    void fun(ObservableList<Message> items, Message item) {
        items.remove(item);

        yanzhengListView.setCellFactory(new Callback<ListView<Message>, ListCell<Message>>() {
            @Override
            public ListCell<Message> call(ListView<Message> param) {
                return new ListCell<Message>() {
                    @Override
                    protected void updateItem(Message item, boolean empty) {
                        super.updateItem(item, empty);

                        if (!empty) {
                            ImageView touxiang = new ImageView("common/image/head/head(" + users.get(item.getFromUser()).getHeadPortrait() + ").jpeg");
                            touxiang.setPreserveRatio(true);
                            touxiang.setFitHeight(40);

                            VBox vBox = new VBox(10);
                            vBox.setSpacing(5.0);
                            Label name = new Label(users.get(item.getFromUser()).getName() + "  (" + users.get(item.getFromUser()).getUid() + ")");
                            if (users.get(item.getFromUser()).getSignature().isEmpty() || users.get(item.getFromUser()).getSignature().length() == 0)
                                users.get(item.getFromUser()).setSignature("这个少年很懒...");
                            Label signature = new Label(users.get(item.getFromUser()).getSignature());
                            signature.setPrefWidth(300);
                            vBox.setAlignment(Pos.CENTER_LEFT);
                            vBox.getChildren().addAll(name, signature);

                            Button bu2 = new Button();
                            ImageView imageView = new ImageView("common/image/ok_hover.png");
                            imageView.setPreserveRatio(true);
                            imageView.setFitHeight(40);
                            bu2.setGraphic(imageView);
                            bu2.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent event) {
                                    //调用方法去发送给服务端加好友请求
                                    item.setObject(2);
                                    boolean result = ClientMethod.upDataFriendAtSql(item);
                                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                    alert.setTitle("消息提示");
                                    alert.setHeaderText("亲爱的 " + userLogin.getName() + ":");
                                    if (result) {
                                        alert.setContentText("添加成功");
                                        MainController.allFriendShipList.add(item);
                                        MainController.friendMap.put(item.getFromUser(), users.get(item.getFromUser()));
                                        MainController.chatRecordMap.put(item.getFromUser(), new ArrayList<>());
                                    } else {
                                        alert.setContentText("发生错误");
                                    }
                                    alert.showAndWait();
                                    yanzhengList.remove(item);
                                    items.remove(item);
                                }
                            });

                            Button bu0 = new Button();
                            ImageView imageView0 = new ImageView("common/image/cancel_hover.png");
                            imageView0.setPreserveRatio(true);
                            imageView0.setFitHeight(40);
                            bu0.setGraphic(imageView0);
                            bu0.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent event) {
                                    //调用方法去发送给服务端加好友请求
                                    item.setObject(0);
                                    boolean result = ClientMethod.upDataFriendAtSql(item);
                                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                    alert.setTitle("消息提示");
                                    alert.setHeaderText("亲爱的 " + userLogin.getName() + ":");
                                    if (result) {
                                        alert.setContentText("拒绝成功");
                                    } else {
                                        alert.setContentText("发生错误");
                                    }
                                    alert.showAndWait();
                                    yanzhengList.remove(item);
                                    items.remove(item);
                                }
                            });

                            HBox hBox = new HBox(10);
                            hBox.setPrefWidth(240);
                            hBox.setPrefHeight(50);
                            hBox.setAlignment(Pos.CENTER_LEFT);
                            hBox.getChildren().addAll(touxiang, vBox, bu2, bu0);

                            this.setGraphic(hBox);
                        }
                    }
                };
            }
        });
    }
}









