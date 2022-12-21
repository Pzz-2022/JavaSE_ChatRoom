package client.controller;

import client.*;
import common.entity.Message;
import common.entity.Type;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import common.entity.User;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainController implements Initializable {
    //登录后的聊天界面的控制类
    @FXML
    public Label gerenBirthdayLabel;

    @FXML
    public ScrollPane emojiScrollPane;

    @FXML
    public SplitPane pane;

    @FXML
    private Label gerenqianmingLabel;

    @FXML
    private TextField messageField;

    @FXML
    private Button searchButton;

    @FXML
    private TextField searchField;

    @FXML
    public ListView<User> friendListView;

    @FXML
    private Label mainNameLabel;

    @FXML
    public ListView<Message> recordListView;

    @FXML
    private Button sendMessageButton;

    @FXML
    private Label warnLabel;

    @FXML
    private Label gerenNameLabel;

    @FXML
    private ImageView gerentouxiangImageView;

    @FXML
    private Label gerenUidLabel;

    @FXML
    private ChoiceBox<String> messageChoiceBox;


    public static Socket socketRead;

    public static List<Message> allFriendShipList;
    public static List<Message> allChatRecord;
    public static Map<String, List<String>> groupUid;
    public static Map<String, User> users;
    static ClientReadThread clientReadThread;
    public static final String ClientFileRecv = "D:\\Software\\FileRecv\\ChatRoom\\ClientFileRecv\\";

    {
        try {
            socketRead = new Socket("127.0.0.1", 7777);
            OutputStream outputStream = socketRead.getOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(outputStream);
            oos.writeObject(new Message(Type.SOCKET_SAVE, MainClient.userLogin.getUid()));
            oos.flush();
            ObjectInputStream ois = new ObjectInputStream(socketRead.getInputStream());

            allFriendShipList = (List<Message>) ois.readObject();
            System.out.println(allFriendShipList);
            allChatRecord = (List<Message>) ois.readObject();
            System.out.println(allChatRecord);
            groupUid = (Map<String, List<String>>) ois.readObject();
            System.out.println(groupUid);

            clientReadThread = new ClientReadThread(socketRead, this);
            new Thread(clientReadThread).start();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static UserLogin userLogin = MainClient.userLogin;
    public static Map<String, User> friendMap = userLogin.friendMap;
    public static Map<String, List<Message>> chatRecordMap = userLogin.chatRecordMap;
    public static List<Message> yanzhengList = new ArrayList<>();
    // 验证消息的数源
    public static ObservableList<User> itemsUser;
    public static ObservableList<Message> itemsRecord;
    // 作为可视化的数据源


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //初始化常用语和监听
        ObservableList<String> messageList = FXCollections.observableArrayList("你是怪物吧!", "有点难懂,但又很有道理。", "隐名免灾祸，隐身免烦恼。",
                "心无时不跳，我无处不在。", "日久不一定生情，但必定见人心。", "有你相伴的日子，即使平凡也浪漫!", "有没有一瞬间，你心疼过我的执着。");
        messageChoiceBox.setItems(messageList);
        messageChoiceBox.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                        messageField.setText(newValue);
                    }
                }
        );

        //将聊天记录的focus关闭
        recordListView.setFocusTraversable(false);

        //个人信息的初始化
        gerenNameLabel.setText(userLogin.getName());
        if (userLogin.getSignature() == null || userLogin.getSignature().length() == 0)
            userLogin.setSignature("这个少年很懒...");
        gerenqianmingLabel.setText(userLogin.getSignature());
        gerenUidLabel.setText(userLogin.getUid());
        gerentouxiangImageView.setImage(new Image("common/image/head/head(" + userLogin.getHeadPortrait() + ").jpeg"));
        gerenBirthdayLabel.setText(userLogin.getBirthday());


        //将好友列表初始化
        users = ClientMethod.allUser();
        itemsUser = friendListView.getItems();
        itemsRecord = recordListView.getItems();


        //去数据库查找好友并初始化聊天记录
        for (Message message : allFriendShipList) {
            if (message.getFromUser().equals(userLogin.getUid()) || message.getToUser().equals(userLogin.getUid())) {
                if ((int) message.getObject() == 1) {
                    if (message.getToUser().equals(userLogin.getUid())) {
                        //加入到验证消息
                        yanzhengList.add(message);
                    }
                } else if ((int) message.getObject() == 2) {
                    if (!message.getFromUser().equals(userLogin.getUid())) {
                        friendMap.put(message.getFromUser(), users.get(message.getFromUser()));
                        chatRecordMap.put(message.getFromUser(), new ArrayList<>());
                    } else if (!message.getToUser().equals(userLogin.getUid())) {
                        friendMap.put(message.getToUser(), users.get(message.getToUser()));
                        chatRecordMap.put(message.getToUser(), new ArrayList<>());
                    }
                }
            }
        }

        for (Message message : allChatRecord) {
            if (message.getToUser() == null || message.getFromUser() == null)
                continue;
            if (users.get(message.getToUser()).getHeadPortrait() == 100) {
                if (!chatRecordMap.containsKey(message.getToUser()))
                    chatRecordMap.put(message.getToUser(), new ArrayList<>());
                chatRecordMap.get(message.getToUser()).add(message);
            } else if (message.getFromUser().equals(userLogin.getUid())) {
                if (!chatRecordMap.containsKey(message.getToUser()))
                    chatRecordMap.put(message.getToUser(), new ArrayList<>());
                chatRecordMap.get(message.getToUser()).add(message);
            } else if (message.getToUser().equals(userLogin.getUid())) {
                if (!chatRecordMap.containsKey(message.getFromUser()))
                    chatRecordMap.put(message.getFromUser(), new ArrayList<>());
                chatRecordMap.get(message.getFromUser()).add(message);
            }
        }

        itemsUser.addAll(friendMap.values());
        //自定义的显示方式
        friendListView.setCellFactory(new Callback<ListView<User>, ListCell<User>>() {
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
                            touxiang.setOnMouseClicked(new EventHandler<MouseEvent>() {
                                @Override
                                public void handle(MouseEvent event) {
                                    try {
                                        tarenxingxi(null);
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            });

                            VBox vBox = new VBox(10);
                            vBox.setSpacing(5.0);
                            Label name = new Label(item.getName());
                            if (item.getSignature().isEmpty() || item.getSignature().length() == 0)
                                item.setSignature("这个少年很懒...");
                            Label signature = new Label(item.getSignature());
                            vBox.setAlignment(Pos.CENTER_LEFT);
                            vBox.getChildren().addAll(name, signature);

                            HBox hBox = new HBox(10);
                            hBox.setPrefWidth(240);
                            hBox.setPrefHeight(50);
                            hBox.setAlignment(Pos.CENTER_LEFT);
                            hBox.getChildren().addAll(touxiang, vBox);

                            this.setGraphic(hBox);
                        }
                    }
                };
            }
        });

        // messageChoiceBox.getSelectionModel().selectedItemProperty().addListener(
        friendListView.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<User>() {
                    @Override
                    public void changed(ObservableValue<? extends User> observable, User oldValue, User newValue) {
                        if (newValue != null) {

                            System.out.println(newValue.getName());
                            mainNameLabel.setText(newValue.getName());
                            userLogin.setSelectFriend(newValue);

                            changeChatRecord(newValue.getUid());
                        }
                    }
                }
        );

        //将放表情包的地方处理成隐藏，在点击emoji的时候再显示出来
        emojiScrollPane.setVisible(false);
        emojiScrollPane.setManaged(false);
        pane.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                emojiScrollPane.setVisible(false);
                emojiScrollPane.setManaged(false);
            }
        });
        int now = 1;
        VBox vBox = new VBox(10);
        vBox.setAlignment(Pos.CENTER_LEFT);
        for (int i = 1; i <= 20; i++) {
            HBox hBox = new HBox(10);
            hBox.setAlignment(Pos.CENTER_LEFT);
            for (; now <= i * 10; now++) {
                if (now > 188)
                    break;
                ImageView emoji = new ImageView(new Image("file:C:/Users/PP/IdeaProjects/ChatRoom/src/common/image/" +
                        "emoji/" + now + ".gif", 40, 40, true, true, true));
                int finalNow = now;
                emoji.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        messageField.setText(messageField.getText() + "[" + finalNow + "]");
                    }
                });
                hBox.getChildren().add(emoji);
            }
            vBox.getChildren().add(hBox);
        }
        emojiScrollPane.setContent(vBox);

        // 表情的快速删除
        messageField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (oldValue.equals(newValue + "]")) {
                    int length = oldValue.length() - 1 - oldValue.lastIndexOf('[');
                    if (length > 1 && length <= 4 && oldValue.lastIndexOf('[') != -1) {
                        Platform.runLater(() -> {
                            messageField.setText(oldValue.substring(0, oldValue.lastIndexOf('[')));
                        });
                    }
                }
            }
        });
    }


    void addFriend(User user) {
        if (user.getSignature() == null || user.getSignature().isEmpty() || user.getSignature().length() == 0)
            user.setSignature("这个少年很懒...");
        friendListView.getItems().add(user);


        friendListView.setCellFactory(new Callback<ListView<User>, ListCell<User>>() {
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
                            touxiang.setOnMouseClicked(new EventHandler<MouseEvent>() {
                                @Override
                                public void handle(MouseEvent event) {
                                    try {
                                        tarenxingxi(null);
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            });

                            VBox vBox = new VBox(10);
                            vBox.setSpacing(5.0);
                            Label name = new Label(item.getName());
                            Label signature = new Label(item.getSignature());
                            vBox.setAlignment(Pos.CENTER_LEFT);
                            vBox.getChildren().addAll(name, signature);

                            HBox hBox = new HBox(10);
                            hBox.setPrefWidth(240);
                            hBox.setPrefHeight(50);
                            hBox.setAlignment(Pos.CENTER_LEFT);
                            hBox.getChildren().addAll(touxiang, vBox);

                            this.setGraphic(hBox);
                        }
                    }
                };
            }
        });
    }


    @FXML
    void sendMessageButtonOnAction(ActionEvent event) {
        if (messageField.getText().length() == 0 || userLogin.getSelectFriend() == null) {
            return;
        }

        Message message = new Message();
        message.setType(Type.TEXT);
        message.setObject(messageField.getText());
        message.setFromUser(userLogin.getUid());
        message.setToUser(userLogin.getSelectFriend().getUid());
        messageField.setText("");

        ClientMethod.sendMessage(message);

        chatRecordMap.get(message.getToUser()).add(message);

        changeChatRecord(userLogin.getSelectFriend().getUid());
    }

    @FXML
    public void tarenxingxi(MouseEvent event) throws IOException {
        // 打开一个新的他人的个人信息窗口
        Parent root = FXMLLoader.load(getClass().getResource("../fxml/haoyouziliao.fxml"));
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("好友资料 - " + userLogin.getSelectFriend().getName());
        stage.showAndWait();
        refreshAll();
    }

    @FXML
    public void xiugaigerenxingxi(MouseEvent event) throws IOException {
        // 打开一个自己的修改信息窗口
        bianjiziliaoOnAction(null);
    }

    public static String search = "";

    @FXML
    void searchButtonOnAction(ActionEvent event) throws IOException {
        search = searchField.getText();
        tianjiahaoyouOnAction(null);
    }

    @FXML
    void biaoqingButtonOnAction(ActionEvent event) {
        emojiScrollPane.setVisible(true);
        emojiScrollPane.setManaged(true);
    }


    @FXML
    void tupianButtonOnAction(ActionEvent event) {
        if (userLogin.getSelectFriend() == null) {
            return;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择图片 - " + userLogin.getName());
        fileChooser.setInitialDirectory(new File("D:\\image"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("图片类型", "*.png", "*.gif", "*.jpg", "*.jpeg"));
        Stage stage = new Stage();
        File file = fileChooser.showOpenDialog(stage);
        if (file == null) {
            return;
        }
        System.out.println(file.getAbsoluteFile());

        Message message = new Message();
        message.setType(Type.IMAGE);
        message.setFromUser(userLogin.getUid());
        message.setToUser(userLogin.getSelectFriend().getUid());

        message.setObject(userLogin.getUid() + "_" + message.getSendTime() + "_" + file.getName());
        File file1 = new File(ClientFileRecv + message.getObject().toString());
        ClientMethod.copyFile(file, file1);

        ClientMethod.sendMessage(message);

        chatRecordMap.get(message.getToUser()).add(message);

        changeChatRecord(userLogin.getSelectFriend().getUid());

        ClientMethod.copyFileToServer(file1);
    }


    @FXML
    void wenjianButtonOnAction(ActionEvent event) {
        if (userLogin.getSelectFriend() == null) {
            return;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择文件 - " + userLogin.getName());
        fileChooser.setInitialDirectory(new File("D:\\image"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("文件类型", "*.*"));
        Stage stage = new Stage();
        File file = fileChooser.showOpenDialog(stage);
        if (file == null) {
            return;
        }
        System.out.println(file.getAbsoluteFile());

        Message message = new Message();
        message.setType(Type.FILE);
        message.setFromUser(userLogin.getUid());
        message.setToUser(userLogin.getSelectFriend().getUid());

        message.setObject(userLogin.getUid() + "_" + message.getSendTime() + "_" + file.getName());
        File file1 = new File(ClientFileRecv + message.getObject().toString());
        ClientMethod.copyFile(file, file1);

        ClientMethod.sendMessage(message);

        chatRecordMap.get(message.getToUser()).add(message);

        changeChatRecord(userLogin.getSelectFriend().getUid());

        ClientMethod.copyFileToServer(file1);
    }

    public void bianjiziliaoOnAction(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("../fxml/bianjiziliao.fxml"));
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("修改个人资料 - " + userLogin.getName());
        stage.showAndWait();
        refreshAll();
    }

    public void yanzhengxiaoxiOnAction(ActionEvent event) throws IOException {
        //添加好友和群的验证
        Parent root = FXMLLoader.load(getClass().getResource("../fxml/yanzhengxiaoxi.fxml"));
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("验证消息 - " + userLogin.getName());
        stage.showAndWait();
        refreshAll();
    }

    public void tianjiahaoyouOnAction(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("../fxml/searchFriend.fxml"));
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("添加好友 - " + userLogin.getName());
        stage.showAndWait();
        users = ClientMethod.allUser();
        refreshAll();
    }

    // 聊天记录的一个渲染到界面方法
    public void changeChatRecord(String uid) {
        // 设置为固定高度 好看一点
        // recordListView.setFixedCellSize(50);
        recordListView.setCellFactory(new Callback<ListView<Message>, ListCell<Message>>() {
            @Override
            public ListCell<Message> call(ListView<Message> param) {
                return new ListCell<Message>() {
                    @Override
                    protected void updateItem(Message item, boolean empty) {
                        super.updateItem(item, empty);

                        if (!empty)
                            if (item.getType() == Type.TEXT) {
                                //发送者的头像
                                ImageView touxiang = new ImageView("common/image/head/head(" + users.get(item.getFromUser()).getHeadPortrait() + ").jpeg");
                                touxiang.setPreserveRatio(true);
                                touxiang.setFitHeight(40);

                                //Label content = new Label((String) item.getObject());
                                //设置点击头像打开的资料界面
                                if (item.getFromUser().equals(userLogin.getUid())) {
                                    //content.setBackground(new Background(new BackgroundFill(new Color(0,0.8,0.9,0.6),null,null)));
                                    touxiang.setOnMouseClicked(new EventHandler<MouseEvent>() {
                                        @Override
                                        public void handle(MouseEvent event) {
                                            try {
                                                xiugaigerenxingxi(null);
                                            } catch (IOException e) {
                                                throw new RuntimeException(e);
                                            }
                                        }
                                    });
                                } else {
                                    //content.setBackground(new Background(new BackgroundFill(new Color(0.8,0.8,0,0.6),null,null)));
                                    touxiang.setOnMouseClicked(new EventHandler<MouseEvent>() {
                                        @Override
                                        public void handle(MouseEvent event) {
                                            try {
                                                tarenxingxi(null);
                                            } catch (IOException e) {
                                                throw new RuntimeException(e);
                                            }
                                        }
                                    });
                                }

                                HBox contentHBox = new HBox(3);
                                String content = (String) item.getObject();
                                String regex = "\\[1?\\d{1,2}\\]";
                                Pattern pattern = Pattern.compile(regex);
                                Matcher matcher = pattern.matcher(content);
                                while (matcher.find()) {
                                    String group = matcher.group();
                                    Label label;
                                    for (int i = 0; i < content.indexOf(group); i++) {
                                        label = new Label(content.charAt(i) + "");
                                        contentHBox.getChildren().add(label);
                                    }
                                    ImageView emoji = new ImageView(new Image("file:C:/Users/PP/IdeaProjects/ChatRoom/src/" +
                                            "common/image/emoji/" + MainController.getIndex(group.substring(1, group.length() - 1)) + ".gif",
                                            40, 40, true, true, true));
                                    contentHBox.getChildren().add(emoji);

                                    content = content.substring(content.indexOf(group));
                                    content = content.replaceFirst(regex, "");
                                }
                                //contentHBox.getChildren().add(new Label(content));
                                for (int i = 0; i < content.length(); i++) {
                                    contentHBox.getChildren().add(new Label(content.charAt(i) + ""));
                                }

                                VBox contentVBox = new VBox(10);

                                // 设置消息内容格式（换行）
                                {
                                    int count = contentHBox.getChildren().size();
                                    HBox hBox = new HBox();
                                    for (int i = 0; i < count; i++) {
                                        if (i % 50 == 0) {
                                            // 设置文字居中
                                            hBox.setAlignment(Pos.CENTER);
                                            contentVBox.getChildren().add(hBox);
                                            hBox = new HBox();
                                        }
                                        hBox.getChildren().add(contentHBox.getChildren().get(0));
                                        if (i == count - 1) {
                                            // 设置文字居中
                                            hBox.setAlignment(Pos.CENTER);
                                            contentVBox.getChildren().add(hBox);
                                        }
                                    }
                                }

                                HBox hBox = new HBox(15);
                                hBox.setPrefWidth(600);
                                if (item.getFromUser().equals(userLogin.getUid())) {
                                    hBox.setAlignment(Pos.CENTER_RIGHT);
                                    contentHBox.setAlignment(Pos.CENTER_RIGHT);
                                    contentVBox.setAlignment(Pos.CENTER_RIGHT);
                                    hBox.getChildren().addAll(contentVBox, touxiang);
                                    this.setGraphic(hBox);
                                } else {
                                    hBox.setAlignment(Pos.CENTER_LEFT);
                                    contentHBox.setAlignment(Pos.CENTER_LEFT);
                                    contentVBox.setAlignment(Pos.CENTER_LEFT);
                                    hBox.getChildren().addAll(touxiang, contentVBox);
                                    this.setGraphic(hBox);
                                }
                                Platform.runLater(() -> {
                                    recordListView.scrollTo(item);
                                });

                            } else if (item.getType() == Type.IMAGE) {
                                //发送者的头像
                                ImageView touxiang = new ImageView("common/image/head/head(" + users.get(item.getFromUser()).getHeadPortrait() + ").jpeg");
                                touxiang.setPreserveRatio(true);
                                touxiang.setFitHeight(40);
                                //设置点击头像打开的资料界面
                                if (item.getFromUser().equals(userLogin.getUid())) {
                                    touxiang.setOnMouseClicked(new EventHandler<MouseEvent>() {
                                        @Override
                                        public void handle(MouseEvent event) {
                                            try {
                                                xiugaigerenxingxi(null);
                                            } catch (IOException e) {
                                                throw new RuntimeException(e);
                                            }
                                        }
                                    });
                                } else {
                                    touxiang.setOnMouseClicked(new EventHandler<MouseEvent>() {
                                        @Override
                                        public void handle(MouseEvent event) {
                                            try {
                                                tarenxingxi(null);
                                            } catch (IOException e) {
                                                throw new RuntimeException(e);
                                            }
                                        }
                                    });
                                }

                                File file = new File(ClientFileRecv + item.getObject());
                                if (!file.exists()) {
                                    ClientMethod.copyFileFromServer(file);
                                }

                                ImageView contentImageView = new ImageView(new Image("file:" + ClientFileRecv + item.getObject(),
                                        200, 200, true, true, true));

                                HBox hBox = new HBox(15);
                                hBox.setPrefWidth(600);
                                //hBox.setPrefHeight(50);
                                if (item.getFromUser().equals(userLogin.getUid())) {
                                    hBox.setAlignment(Pos.CENTER_RIGHT);
                                    hBox.getChildren().addAll(contentImageView, touxiang);
                                    this.setGraphic(hBox);
                                } else {
                                    hBox.setAlignment(Pos.CENTER_LEFT);
                                    hBox.getChildren().addAll(touxiang, contentImageView);
                                    this.setGraphic(hBox);
                                }
                            } else if (item.getType() == Type.FILE) {
                                //发送者的头像
                                ImageView touxiang = new ImageView("common/image/head/head(" + users.get(item.getFromUser()).getHeadPortrait() + ").jpeg");
                                touxiang.setPreserveRatio(true);
                                touxiang.setFitHeight(40);
                                //设置点击头像打开的资料界面
                                if (item.getFromUser().equals(userLogin.getUid())) {
                                    touxiang.setOnMouseClicked(new EventHandler<MouseEvent>() {
                                        @Override
                                        public void handle(MouseEvent event) {
                                            try {
                                                xiugaigerenxingxi(null);
                                            } catch (IOException e) {
                                                throw new RuntimeException(e);
                                            }
                                        }
                                    });
                                } else {
                                    touxiang.setOnMouseClicked(new EventHandler<MouseEvent>() {
                                        @Override
                                        public void handle(MouseEvent event) {
                                            try {
                                                tarenxingxi(null);
                                            } catch (IOException e) {
                                                throw new RuntimeException(e);
                                            }
                                        }
                                    });
                                }

                                File file = new File(ClientFileRecv + item.getObject());
                                if (!file.exists()) {
                                    ClientMethod.copyFileFromServer(file);
                                }

                                ImageView contentImageView = new ImageView("common/image/wenjian.png");
                                contentImageView.setPreserveRatio(true);
                                contentImageView.setFitHeight(100);
                                VBox vBox = new VBox(20);
                                vBox.setAlignment(Pos.CENTER);
                                Label fileNameLabel = new Label(((String) item.getObject())
                                        .substring(((String) item.getObject()).lastIndexOf('_') + 1));
                                fileNameLabel.setMaxWidth(300);
                                Button openFileButton = new Button("打开文件");
                                openFileButton.setOnAction(new EventHandler<ActionEvent>() {
                                    @Override
                                    public void handle(ActionEvent event) {
                                        try {
                                            ClientMethod.openFile(file);
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                                });
                                vBox.getChildren().addAll(fileNameLabel, openFileButton);
                                HBox contentHBox = new HBox(0);
                                contentHBox.getChildren().addAll(contentImageView, vBox);

                                HBox hBox = new HBox(15);
                                hBox.setPrefWidth(600);
                                if (item.getFromUser().equals(userLogin.getUid())) {
                                    hBox.setAlignment(Pos.CENTER_RIGHT);
                                    contentHBox.setAlignment(Pos.CENTER_RIGHT);
                                    hBox.getChildren().addAll(contentHBox, touxiang);
                                    this.setGraphic(hBox);
                                } else {
                                    hBox.setAlignment(Pos.CENTER_LEFT);
                                    contentHBox.setAlignment(Pos.CENTER_LEFT);
                                    hBox.getChildren().addAll(touxiang, contentHBox);
                                    this.setGraphic(hBox);
                                }

                            } else {
                                System.out.println("未处理的消息" + item);
                            }
                    }
                };
            }
        });

        Platform.runLater(() -> {
            itemsRecord.clear();
            if (!chatRecordMap.containsKey(uid))
                chatRecordMap.put(uid, new ArrayList<>());
            itemsRecord.addAll(chatRecordMap.get(uid));
        });
    }

    public void refreshAll() {
        //个人信息的初始化
        gerenNameLabel.setText(userLogin.getName());
        if (userLogin.getSignature() == null || userLogin.getSignature().length() == 0)
            userLogin.setSignature("这个少年很懒...");
        gerenqianmingLabel.setText(userLogin.getSignature());
        gerenUidLabel.setText(userLogin.getUid());
        gerentouxiangImageView.setImage(new Image("common/image/head/head(" + userLogin.getHeadPortrait() + ").jpeg"));
        gerenBirthdayLabel.setText(userLogin.getBirthday());

        friendListView.setCellFactory(new Callback<ListView<User>, ListCell<User>>() {
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
                            touxiang.setOnMouseClicked(new EventHandler<MouseEvent>() {
                                @Override
                                public void handle(MouseEvent event) {
                                    try {
                                        tarenxingxi(null);
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            });

                            VBox vBox = new VBox(10);
                            vBox.setSpacing(5.0);
                            Label name = new Label(item.getName());
                            if (item.getSignature().isEmpty() || item.getSignature().length() == 0)
                                item.setSignature("这个少年很懒...");
                            Label signature = new Label(item.getSignature());
                            vBox.setAlignment(Pos.CENTER_LEFT);
                            vBox.getChildren().addAll(name, signature);

                            HBox hBox = new HBox(10);
                            hBox.setPrefWidth(240);
                            //hBox.setPrefHeight(50);
                            hBox.setAlignment(Pos.CENTER_LEFT);
                            hBox.getChildren().addAll(touxiang, vBox);

                            this.setGraphic(hBox);
                        }
                    }
                };
            }
        });
        friendListView.getItems().clear();
        friendListView.getItems().addAll(friendMap.values());
    }

    public void refreshButtonOnAction(ActionEvent event) {
        refreshAll();
    }

    public void chuanjianqunBuutonOnAction(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("../fxml/qun.fxml"));
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("创建群聊 - " + userLogin.getName());
        stage.showAndWait();
        refreshAll();
    }

    public static int getIndex(String group) {
        int index = 0;
        for (int i = 0; i < group.length(); i++) {
            index = index * 10 + group.charAt(i) - '0';
        }
        return index;
    }
}
