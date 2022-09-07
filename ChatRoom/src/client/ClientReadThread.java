package client;

import client.controller.MainController;
import common.entity.Message;
import common.entity.MyObjectInputStream;
import common.entity.Type;
import common.entity.User;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static client.controller.MainController.userLogin;

public class ClientReadThread extends Thread {
    //客户端线程的监听，处理来自服务端的信息
    private Socket socket;
    private MainController mainController;

    public ClientReadThread(Socket socket) {
        this.socket = socket;
    }

    public ClientReadThread(Socket socketRead, MainController mainController) {
        this.socket = socketRead;
        this.mainController = mainController;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (
                InputStream inputStream = socket.getInputStream();
                ObjectInputStream objectInputStream = new MyObjectInputStream(inputStream);) {
            while (true) {
                Message message = (Message) objectInputStream.readObject();
                Type type = message.getType();
                System.out.println(type);
                switch (type) {
                    case FRIEND_SHIP:
                        MainController.yanzhengList.add(message);
                        break;

                    case UP_FRIEND_SHIP:
                        if ((int) message.getObject() == 2) {
                            MainController.friendMap.put(message.getToUser(), MainController.users.get(message.getToUser()));
                            addFriendToList(message);
                            MainController.chatRecordMap.put(message.getToUser(), new ArrayList<>());
                        } else if ((int) message.getObject() == 0) {
                            if (MainController.friendMap.containsKey(message.getToUser())) {
                                MainController.friendMap.remove(message.getToUser());
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        //更新JavaFX的主线程的代码放在此处
                                        MainController.itemsUser.remove(MainController.users.get(message.getToUser()));
                                    }
                                });
                            } else {
                                MainController.friendMap.remove(message.getFromUser());
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        //更新JavaFX的主线程的代码放在此处
                                        MainController.itemsUser.remove(MainController.users.get(message.getFromUser()));
                                    }
                                });
                            }
                        }
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                //更新JavaFX的主线程的代码放在此处
                                mainController.refreshAll();
                                if ((int) message.getObject() == 0)
                                    mainController.recordListView.getItems().clear();
                            }
                        });
                        break;

                    case TEXT:
                    case IMAGE:
                        if (MainController.users.get(message.getToUser()).getHeadPortrait() != 100) {
                            MainController.chatRecordMap.get(message.getFromUser()).add(message);
                            if (userLogin.getSelectFriend().getUid().equals(message.getFromUser())) {
                                changeChatRecord(userLogin.getSelectFriend().getUid(), message);
                            }
                        } else {
                            MainController.chatRecordMap.get(message.getToUser()).add(message);
                            if (userLogin.getSelectFriend().getUid().equals(message.getToUser())) {
                                changeChatRecord(userLogin.getSelectFriend().getUid(), message);
                            }
                        }
                        break;

                    default:
                        System.out.println("收到了未经处理的消息：" + message);
                        break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void changeChatRecord(String uid, Message message) {
        Platform.runLater(() -> {
            MainController.itemsRecord.add(message);
        });

        mainController.recordListView.setFixedCellSize(50);
        mainController.recordListView.setCellFactory(new Callback<ListView<Message>, ListCell<Message>>() {
            @Override
            public ListCell<Message> call(ListView<Message> param) {
                return new ListCell<Message>() {
                    @Override
                    protected void updateItem(Message item, boolean empty) {
                        super.updateItem(item, empty);

                        if (!empty)
                            if (item.getType() == Type.TEXT) {
                                ImageView touxiang = new ImageView("common/image/head/head(" + MainController.users.get(item.getFromUser()).getHeadPortrait() + ").jpeg");
                                touxiang.setPreserveRatio(true);
                                touxiang.setFitHeight(40);

                                //Label content = new Label((String) item.getObject());
                                if (item.getFromUser().equals(userLogin.getUid())) {
                                    //content.setBackground(new Background(new BackgroundFill(new Color(0,0.8,0.9,0.7),null,null)));
                                    touxiang.setOnMouseClicked(new EventHandler<MouseEvent>() {
                                        @Override
                                        public void handle(MouseEvent event) {
                                            try {
                                                mainController.xiugaigerenxingxi(null);
                                            } catch (IOException e) {
                                                throw new RuntimeException(e);
                                            }
                                        }
                                    });
                                } else {
                                    //content.setBackground(new Background(new BackgroundFill(new Color(0.8, 0.8, 0, 0.9), null, null)));
                                    touxiang.setOnMouseClicked(new EventHandler<MouseEvent>() {
                                        @Override
                                        public void handle(MouseEvent event) {
                                            try {
                                                mainController.tarenxingxi(null);
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
//                                contentHBox.getChildren().add(new Label(content));
                                for (int i = 0; i < content.length(); i++) {
                                    contentHBox.getChildren().add(new Label(content.charAt(i) + ""));
                                }

                                VBox contentVBox = new VBox(10);
                                {
                                    int count = contentHBox.getChildren().size();
                                    HBox hBox = new HBox();
                                    for (int i = 0; i < count; i++) {
                                        if (i % 50 == 0) {
                                            contentVBox.getChildren().add(hBox);
                                            hBox = new HBox();
                                        }
                                        hBox.getChildren().add(contentHBox.getChildren().get(0));
                                        if (i == count - 1) {
                                            contentVBox.getChildren().add(hBox);
                                        }
                                    }
                                }

                                HBox hBox = new HBox(15);
                                hBox.setPrefWidth(600);
                                //hBox.setPrefHeight(50);
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
                                    mainController.recordListView.scrollTo(item);
                                });
                            } else if (item.getType() == Type.IMAGE) {

                                ImageView touxiang = new ImageView("common/image/head/head(" + MainController.users.get(item.getFromUser()).getHeadPortrait() + ").jpeg");
                                touxiang.setPreserveRatio(true);
                                touxiang.setFitHeight(40);

                                if (item.getFromUser().equals(userLogin.getUid())) {
                                    touxiang.setOnMouseClicked(new EventHandler<MouseEvent>() {
                                        @Override
                                        public void handle(MouseEvent event) {
                                            try {
                                                mainController.xiugaigerenxingxi(null);
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
                                                mainController.tarenxingxi(null);
                                            } catch (IOException e) {
                                                throw new RuntimeException(e);
                                            }
                                        }
                                    });
                                }

                                File file = new File(MainController.ClientFileRecv + item.getObject());
                                if (!file.exists()) {
                                    ClientMethod.copyFileFromServer(file);
                                }

                                ImageView contentImageView = new ImageView(new Image("file:" + MainController.ClientFileRecv + item.getObject(),
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
                                ImageView touxiang = new ImageView("common/image/head/head(" + MainController.users.get(item.getFromUser()).getHeadPortrait() + ").jpeg");
                                touxiang.setPreserveRatio(true);
                                touxiang.setFitHeight(40);
                                //设置点击头像打开的资料界面
                                if (item.getFromUser().equals(userLogin.getUid())) {
                                    touxiang.setOnMouseClicked(new EventHandler<MouseEvent>() {
                                        @Override
                                        public void handle(MouseEvent event) {
                                            try {
                                                mainController.xiugaigerenxingxi(null);
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
                                                mainController.tarenxingxi(null);
                                            } catch (IOException e) {
                                                throw new RuntimeException(e);
                                            }
                                        }
                                    });
                                }

                                File file = new File(MainController.ClientFileRecv + item.getObject());
                                if (!file.exists()) {
                                    ClientMethod.copyFileFromServer(file);
                                }

                                ImageView contentImageView = new ImageView("common/image/wenjian.png");
                                contentImageView.setPreserveRatio(true);
                                contentImageView.setFitHeight(100);
                                VBox vBox = new VBox(20);
                                vBox.setAlignment(Pos.CENTER);
                                Label fileNameLabel = new Label(((String) item.getObject())
                                        .substring(((String) item.getObject()).lastIndexOf('_')+1));
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
                                //hBox.setPrefHeight(50);
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
    }


    private void addFriendToList(Message message) {
        mainController.friendListView.setCellFactory(new Callback<ListView<User>, ListCell<User>>() {
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
                                        mainController.tarenxingxi(null);
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
        if (MainController.users.get(message.getToUser()) == null) {
            MainController.users = ClientMethod.allUser();
            MainController.friendMap.put(message.getToUser(), MainController.users.get(message.getToUser()));
        }
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                //更新JavaFX的主线程的代码放在此处
                MainController.itemsUser.add(MainController.users.get(message.getToUser()));
            }
        });
    }
}