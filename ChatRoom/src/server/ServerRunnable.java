package server;


import client.UserLogin;
import common.entity.*;
import common.util.SendEmail;
import common.util.SocketUtil;
import server.Mysql.GetMysql;
import common.entity.User;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;

public class ServerRunnable implements Runnable {
    // 自定义的任务类，处理来自客户端的消息
    private Socket socket;

    public ServerRunnable(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (
                //建立客户端与服务端的连接管道
                InputStream inputStream = socket.getInputStream();
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                OutputStream outputStream = socket.getOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);) {

            while (true) {
                //等待客户端写入信息
                Message message = (Message) objectInputStream.readObject();

                Type type = message.getType();
                System.out.println(type);

                switch (type) {
                    case APPLY_CODE:
                        //申请发送验证码
                        EmailCode emailCode = (EmailCode) message.getObject();
                        String code = SendEmail.to(emailCode.getEmail(), emailCode.getName());
                        objectOutputStream.writeObject(code);
                        objectOutputStream.flush();
                        break;

                    case CHECK_UID:
                        //查询数据库中是否有这个ID，有返回true
                        String uid = (String) message.getObject();

                        //返回结果
                        objectOutputStream.writeObject(GetMysql.selectUID(uid));
                        objectOutputStream.flush();
                        break;

                    case CHECK_EMAIL:
                        //查看数据库里面有没有这个账号，并返回这个账号，没有返回""
                        String email = (String) message.getObject();
                        String result = GetMysql.selectEmail(email);

                        //返回结果
                        objectOutputStream.writeObject(result);
                        objectOutputStream.flush();
                        break;

                    case REGISTER:
                        //注册一个用户类
                        User user = (User) message.getObject();
                        boolean judge = GetMysql.register(user);

                        //返回结果
                        objectOutputStream.writeObject(judge);
                        objectOutputStream.flush();

                        if (judge) {
                            if (user.getHeadPortrait() == 100)
                                MainServer.groupUid.put(user.getUid(), new ArrayList<>());
                            MainServer.users.put(user.getUid(), user);
                        }
                        break;

                    case LOGIN:
                        //用户登录
                        User user1 = (User) message.getObject();
                        if (MainServer.users.get(user1.getUid()).getStates() == 0) {
                            UserLogin userLogin = GetMysql.login(user1);
                            if (userLogin.getPassword().length() > 30) {
                                MainServer.users.get(userLogin.getUid()).setStates(1);
                            }
                            objectOutputStream.writeObject(userLogin);
                            objectOutputStream.flush();
                        } else {
                            UserLogin userLogin = new UserLogin();
                            userLogin.setPassword("s");
                            userLogin.setStates(1);
                            objectOutputStream.writeObject(userLogin);
                            objectOutputStream.flush();
                        }
                        break;

                    case APPLY_CODE_FOR_UP_PASSWORD:
                        //申请发送验证码为了更新密码
                        EmailCode emailCode1 = (EmailCode) message.getObject();
                        String code1 = SendEmail.to(emailCode1.getEmail());
                        objectOutputStream.writeObject(code1);
                        objectOutputStream.flush();
                        break;

                    case UP_PASSWORD:
                        //用新的密码去替换掉原来的密码
                        EmailCode emailCode2 = (EmailCode) message.getObject();

                        objectOutputStream.writeObject(GetMysql.upPassword(emailCode2));
                        objectOutputStream.flush();
                        break;

                    case UP_DATA_USER:
                        //更新个人信息
                        User upUser = (User) message.getObject();
                        objectOutputStream.writeObject(GetMysql.upDataUser(upUser));
                        objectOutputStream.flush();

                        MainServer.users.get(upUser.getUid()).setName(upUser.getName());
                        MainServer.users.get(upUser.getUid()).setSignature(upUser.getSignature());
                        MainServer.users.get(upUser.getUid()).setAge(upUser.getAge());
                        MainServer.users.get(upUser.getUid()).setGender(upUser.getGender());
                        MainServer.users.get(upUser.getUid()).setHeadPortrait(upUser.getHeadPortrait());
                        MainServer.users.get(upUser.getUid()).setBirthday(upUser.getBirthday());
                        break;

                    case FRIEND_SHIP:
                        //增加一个好友关系
                        boolean b = GetMysql.addFriend(message);
                        objectOutputStream.writeObject(b);
                        objectOutputStream.flush();
                        //看有没有好友验证
                        if (b) {
                            MainServer.allFriendShipList.add(message);
                            //发送验证消息给对应的客户端
                            if ((int) message.getObject() == 1 && MainServer.socketMap.containsKey(message.getToUser())) {
                                OutputStream outputStream1 = MainServer.socketMap.get(message.getToUser()).getOutputStream();
                                ObjectOutputStream oos = new MyObjectOutputStream(outputStream1);
                                oos.writeObject(message);
                            } else if (MainServer.users.get(message.getToUser()).getHeadPortrait() == 100) {
                                if ((int) message.getObject() == 1 && MainServer.socketMap.containsKey(MainServer.users.get(message.getToUser()).getEmail())) {
                                    OutputStream outputStream1 = MainServer.socketMap.get(MainServer.users.get(message.getToUser()).getEmail()).getOutputStream();
                                    ObjectOutputStream oos = new MyObjectOutputStream(outputStream1);
                                    oos.writeObject(message);
                                }
                                if ((int) message.getObject() == 2)
                                    MainServer.groupUid.get(message.getToUser()).add(MainServer.users.get(message.getToUser()).getEmail());
                            }
                        }
                        break;

                    case ALL_USER:
                        //请求所有用户信息
                        objectOutputStream.writeObject(MainServer.users);
                        objectOutputStream.flush();
                        break;

                    case ALL_FRIEND_SHIP:
                        //请求好友关系
                        objectOutputStream.writeObject(MainServer.allFriendShipList);
                        objectOutputStream.flush();
                        break;

                    case SOCKET_SAVE:
                        //登录后在服务端保存socket管道
                        MainServer.socketMap.put(message.getFromUser(), socket);
                        //看有没有好友验证
                        objectOutputStream.writeObject(MainServer.allFriendShipList);
                        objectOutputStream.flush();
                        objectOutputStream.writeObject(MainServer.allChatRecord);
                        objectOutputStream.flush();
                        objectOutputStream.writeObject(MainServer.groupUid);
                        objectOutputStream.flush();
                        break;

                    case UP_FRIEND_SHIP:
                        //更新好友关系
                        boolean b1 = GetMysql.upFriend(message);
                        objectOutputStream.writeObject(b1);
                        objectOutputStream.flush();
                        for (Message message1 : MainServer.allFriendShipList) {
                            if (message1.getFromUser().equals(message.getFromUser()) && message1.getToUser().equals(message.getToUser())) {
                                message1.setObject(message.getObject());
                                break;
                            }
                        }
                        //更新好友状态
                        if (b1) {
                            if (MainServer.users.get(message.getToUser()).getHeadPortrait() == 100 && (int) message.getObject() == 2) {
                                MainServer.groupUid.get(message.getToUser()).add(message.getFromUser());
                            }
                            if (MainServer.socketMap.containsKey(message.getFromUser()) && (int) message.getObject() == 2) {
                                OutputStream outputStream1 = MainServer.socketMap.get(message.getFromUser()).getOutputStream();
                                ObjectOutputStream objectOutputStream1 = new MyObjectOutputStream(outputStream1);
                                objectOutputStream1.writeObject(message);
                                objectOutputStream1.flush();
                            } else if ((int) message.getObject() == 0) {
                                if (MainServer.socketMap.containsKey(message.getFromUser())) {
                                    OutputStream outputStream1 = MainServer.socketMap.get(message.getFromUser()).getOutputStream();
                                    ObjectOutputStream objectOutputStream1 = new MyObjectOutputStream(outputStream1);
                                    objectOutputStream1.writeObject(message);
                                    objectOutputStream1.flush();
                                }
                                if (MainServer.socketMap.containsKey(message.getToUser())) {
                                    OutputStream outputStream1 = MainServer.socketMap.get(message.getToUser()).getOutputStream();
                                    ObjectOutputStream objectOutputStream1 = new MyObjectOutputStream(outputStream1);
                                    objectOutputStream1.writeObject(message);
                                    objectOutputStream1.flush();
                                }
                            }
                        }
                        break;

                    case DELETE_GROUP:
                        //解散群
                        boolean b2 = GetMysql.deleteGroup(message);
                        message.setType(Type.UP_FRIEND_SHIP);
                        objectOutputStream.writeObject(b2);
                        objectOutputStream.flush();
                        for (Message message1 : MainServer.allFriendShipList) {
                            if (message1.getToUser().equals(message.getToUser())) {
                                message1.setObject(message.getObject());
                                break;
                            }
                        }

                        if (b2) {
                            for (String s : MainServer.groupUid.get(message.getToUser())) {
                                if (MainServer.socketMap.containsKey(s)) {
                                    OutputStream outputStream1 = MainServer.socketMap.get(s).getOutputStream();
                                    ObjectOutputStream objectOutputStream1 = new MyObjectOutputStream(outputStream1);
                                    message.setFromUser(s);
                                    objectOutputStream1.writeObject(message);
                                    objectOutputStream1.flush();
                                }
                            }
                            MainServer.groupUid.get(message.getToUser()).clear();
                            MainServer.groupUid.remove(message.getToUser());
                        }
                        break;

                    case TEXT:
                        //文本类型消息
                    case IMAGE:
                        //图片类型消息
                    case FILE:
                        //文件类型
                        GetMysql.addChatRecord(message);
                        MainServer.allChatRecord.add(message);
                        if (MainServer.socketMap.containsKey(message.getToUser())) {
                            OutputStream outputStream1 = MainServer.socketMap.get(message.getToUser()).getOutputStream();
                            ObjectOutputStream objectOutputStream1 = new MyObjectOutputStream(outputStream1);
                            objectOutputStream1.writeObject(message);
                            objectOutputStream1.flush();
                            objectOutputStream.writeObject(true);
                            objectOutputStream.flush();
                            break;
                        } else if (MainServer.users.get(message.getToUser()).getHeadPortrait() == 100) {
                            System.out.println(MainServer.groupUid.get(message.getToUser()).toString());
                            for (String groupUid : MainServer.groupUid.get(message.getToUser())) {
                                if (MainServer.socketMap.containsKey(groupUid) && !groupUid.equals(message.getFromUser())) {
                                    OutputStream outputStream1 = MainServer.socketMap.get(groupUid).getOutputStream();
                                    ObjectOutputStream objectOutputStream1 = new MyObjectOutputStream(outputStream1);
                                    objectOutputStream1.writeObject(message);
                                    objectOutputStream1.flush();
                                    System.out.println(groupUid);
                                }
                            }

                            objectOutputStream.writeObject(true);
                            objectOutputStream.flush();
                            break;
                        }
                        objectOutputStream.writeObject(false);
                        objectOutputStream.flush();
                        break;

                    case APPLY_FILE:
                        File file = new File(MainServer.ServerFileRecv + message.getObject());
                        InputStream fileInputStream = new FileInputStream(file);
                        byte[] bytes = new byte[1024 * 8];
                        int len;
                        int sum = 0;
                        while ((len = fileInputStream.read(bytes)) != -1) {
                            outputStream.write(bytes, 0, len);
                            sum += len;
                        }
                        System.out.println(sum);
                        fileInputStream.close();
                        socket.shutdownOutput();
                        break;

                    case APPLY_FILE_TO_SERVER:
                        File file1 = new File(MainServer.ServerFileRecv + message.getObject());
                        OutputStream fileOutputStream = new FileOutputStream(file1);
                        byte[] bytes1 = new byte[1024 * 8];
                        int len1;
                        int sum1 = 0;
                        while ((len1 = inputStream.read(bytes1)) != -1) {
                            fileOutputStream.write(bytes1, 0, len1);
                            sum1 += len1;
                        }
                        System.out.println(sum1);
                        fileOutputStream.close();
                        break;


                    default:
                        System.out.println("未经处理的消息：" + message);
                        break;
                }
            }
        } catch (EOFException e) {
            SocketUtil.close(socket);
            if (MainServer.socketMap.containsValue(socket)) {
                for (Map.Entry<String, Socket> stringSocketEntry : MainServer.socketMap.entrySet()) {
                    if (stringSocketEntry.getValue().equals(socket)) {
                        MainServer.users.get(stringSocketEntry.getKey()).setStates(0);
                        MainServer.socketMap.remove(stringSocketEntry.getKey(), stringSocketEntry.getValue());
                    }
                }
            }
        } catch (RuntimeException e) {
            System.out.println(socket.getRemoteSocketAddress() + " 已经离开");
            if (MainServer.socketMap.containsValue(socket)) {
                for (Map.Entry<String, Socket> stringSocketEntry : MainServer.socketMap.entrySet()) {
                    if (stringSocketEntry.getValue().equals(socket)) {
                        MainServer.users.get(stringSocketEntry.getKey()).setStates(0);
                        MainServer.socketMap.remove(stringSocketEntry.getKey(), stringSocketEntry.getValue());
                    }
                }
            }
            SocketUtil.close(socket);
        } catch (Exception e) {
            SocketUtil.close(socket);
            if (MainServer.socketMap.containsValue(socket)) {
                for (Map.Entry<String, Socket> stringSocketEntry : MainServer.socketMap.entrySet()) {
                    if (stringSocketEntry.getValue().equals(socket)) {
                        MainServer.users.get(stringSocketEntry.getKey()).setStates(0);
                        MainServer.socketMap.remove(stringSocketEntry.getKey(), stringSocketEntry.getValue());
                    }
                }
            }
            throw new RuntimeException(e);
        }
    }
}
