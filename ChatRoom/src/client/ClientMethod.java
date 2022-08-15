package client;

import common.entity.EmailCode;
import common.entity.Message;
import common.entity.Type;
import common.util.IOUtil;
import common.util.SocketUtil;
import common.entity.User;

import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Map;

public class ClientMethod {
    //客户端的工具类，客户端的方法都写在这里了
    public static String registerUserCode(EmailCode emailCode) {
        try (
                Socket socket = new Socket("127.0.0.1", 7777);
                OutputStream outputStream = socket.getOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                InputStream inputStream = socket.getInputStream();
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);) {
            String code = "";

            Message message = new Message();
            message.setObject(emailCode);
            message.setType(Type.APPLY_CODE);

            objectOutputStream.writeObject(message);

            code = (String) objectInputStream.readObject();

            IOUtil.close(objectInputStream, objectOutputStream);
            IOUtil.close(inputStream, outputStream);
            SocketUtil.close(socket);

            return code;
        } catch (IOException e) {
            System.out.println("连接服务器失败！");
            return "连接服务器失败！";
        } catch (ClassNotFoundException e) {
            System.out.println("发送失败.");
            return "发送失败.";
        }
    }

    public static String checkEmailExist(String email) {
        try (
                Socket socket = new Socket("127.0.0.1", 7777);
                OutputStream outputStream = socket.getOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                InputStream inputStream = socket.getInputStream();
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);) {

            //包装一个消息发给服务端进行处理,email存在返回 账号 ,不存在返回""
            Message message = new Message();
            message.setType(Type.CHECK_EMAIL);
            message.setObject(email);

            objectOutputStream.writeObject(message);

            //接受一个消息
            Object readObject = objectInputStream.readObject();

            return (String) readObject;
        } catch (Exception e) {
            System.out.println("连接服务器失败！");
            return "";
        }
    }

    public static boolean checkUidExist(String uid) {
        //uid 存在返回"true",不存在返回"false"
        try (
                Socket socket = new Socket("127.0.0.1", 7777);
                OutputStream outputStream = socket.getOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                InputStream inputStream = socket.getInputStream();
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);) {

            //包装一个消息发给服务端进行处理
            Message message = new Message();
            message.setType(Type.CHECK_UID);
            message.setObject(uid);

            objectOutputStream.writeObject(message);

            //接受一个消息
            Object readObject = objectInputStream.readObject();

            return (boolean) readObject;
        } catch (Exception e) {
            System.out.println("连接服务器失败！");
            return false;
        }
    }

    public static boolean register(User user) {
        //前端于后端的通信在注册的期间的方法类
        try (
                Socket socket = new Socket("127.0.0.1", 7777);
                OutputStream outputStream = socket.getOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                InputStream inputStream = socket.getInputStream();
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);) {

            //包装一个消息发给服务端进行处理
            Message message = new Message();
            message.setType(Type.REGISTER);
            message.setObject(user);

            objectOutputStream.writeObject(message);
            //接受一个消息
            boolean readObject = (boolean) objectInputStream.readObject();
            if (readObject)
                System.out.println(user.getName() + " 注册成功！");
            return readObject;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static int login(User user) {
        try (
                Socket socket = new Socket("127.0.0.1", 7777);
                OutputStream outputStream = socket.getOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                InputStream inputStream = socket.getInputStream();
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);) {

            //包装一个消息发给服务端进行处理
            Message message = new Message();
            message.setType(Type.LOGIN);
            message.setObject(user);

            objectOutputStream.writeObject(message);

            //接受一个消息
            MainClient.userLogin = (UserLogin) objectInputStream.readObject();

            if (MainClient.userLogin.getPassword().length() > 30) {
                System.out.println(user.getUid() + " 登录成功！");

                System.out.println(MainClient.userLogin.getGender());
                return 1;
            } else if (MainClient.userLogin.getStates() == 1) {
                System.out.println("已经登录了");
                return 10;
            }
            return 0;
        } catch (Exception e) {
            System.out.println("服务停止。");
            return 0;
        }
    }


    public static String upDataSendCode(EmailCode emailCode) {
        try (
                Socket socket = new Socket("127.0.0.1", 7777);
                OutputStream outputStream = socket.getOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                InputStream inputStream = socket.getInputStream();
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);) {
            String code = "";

            Message message = new Message();
            message.setObject(emailCode);
            message.setType(Type.APPLY_CODE_FOR_UP_PASSWORD);

            objectOutputStream.writeObject(message);

            code = (String) objectInputStream.readObject();

            IOUtil.close(objectInputStream, objectOutputStream);
            IOUtil.close(inputStream, outputStream);
            SocketUtil.close(socket);

            return code;
        } catch (IOException e) {
            System.out.println("连接服务器失败！");
            return "连接服务器失败！";
        } catch (ClassNotFoundException e) {
            System.out.println("发送失败.");
            return "发送失败.";
        }
    }

    public static boolean upPassword(EmailCode emailCode) {
        //前端于后端的通信在注册的期间的方法类
        try (
                Socket socket = new Socket("127.0.0.1", 7777);
                OutputStream outputStream = socket.getOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                InputStream inputStream = socket.getInputStream();
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);) {

            //包装一个消息发给服务端进行处理
            Message message = new Message();
            message.setType(Type.UP_PASSWORD);
            message.setObject(emailCode);

            objectOutputStream.writeObject(message);

            //接受一个消息
            boolean readObject = (boolean) objectInputStream.readObject();
            if (readObject)
                System.out.println(emailCode.getEmail() + " 重置成功！");
            return readObject;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean upDataUser(User user) {
        //前端于后端的通信在注册的期间的方法类
        try (
                Socket socket = new Socket("127.0.0.1", 7777);
                OutputStream outputStream = socket.getOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                InputStream inputStream = socket.getInputStream();
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);) {

            //包装一个消息发给服务端进行处理
            Message message = new Message();
            message.setType(Type.UP_DATA_USER);
            message.setObject(user);

            objectOutputStream.writeObject(message);

            //接受一个消息
            boolean result2 = (boolean) objectInputStream.readObject();

            if (result2) {
                System.out.println(user.getName() + " 编辑成功！");
            } else
                System.out.println("编辑失败。");
            return result2;
        } catch (Exception e) {
            System.out.println("连接服务端失败。");
            return false;
        }
    }

    public static boolean addFriendAtSql(Message message) {
        //前端于后端的通信在注册的期间的方法类
        try (
                Socket socket = new Socket("127.0.0.1", 7777);
                OutputStream outputStream = socket.getOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                InputStream inputStream = socket.getInputStream();
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);) {

            //包装一个消息发给服务端进行处理
            message.setType(Type.FRIEND_SHIP);
            objectOutputStream.writeObject(message);

            //接受一个消息
            boolean result2 = (boolean) objectInputStream.readObject();

            if (result2) {
                System.out.println("申请成功！");
            } else
                System.out.println("申请失败。");
            return result2;
        } catch (Exception e) {
            System.out.println("连接服务端失败。");
            return false;
        }
    }

    public static Map<String, User> allUser() {
        //前端于后端的通信在注册的期间的方法类
        try (
                Socket socket = new Socket("127.0.0.1", 7777);
                OutputStream outputStream = socket.getOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                InputStream inputStream = socket.getInputStream();
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);) {

            //包装一个消息发给服务端进行处理
            Message message = new Message();
            message.setType(Type.ALL_USER);

            objectOutputStream.writeObject(message);

            //接受一个消息
            Map<String, User> users = (Map<String, User>) objectInputStream.readObject();

            return users;
        } catch (Exception e) {
            System.out.println("连接服务端失败。");
            return null;
        }
    }

    public static List<Message> getAllFriendShipList() {
        try (
                Socket socket = new Socket("127.0.0.1", 7777);
                OutputStream outputStream = socket.getOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                InputStream inputStream = socket.getInputStream();
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);) {

            //包装一个消息发给服务端进行处理
            Message message = new Message();
            message.setType(Type.ALL_FRIEND_SHIP);

            objectOutputStream.writeObject(message);

            //接受一个消息
            List<Message> allFriendShipList = (List<Message>) objectInputStream.readObject();

            return allFriendShipList;
        } catch (Exception e) {
            System.out.println("连接服务端失败。");
            return null;
        }
    }

    public static boolean upDataFriendAtSql(Message message) {
        //前端于后端的通信在注册的期间的方法类
        try (
                Socket socket = new Socket("127.0.0.1", 7777);
                OutputStream outputStream = socket.getOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                InputStream inputStream = socket.getInputStream();
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);) {

            //包装一个消息发给服务端进行处理
            message.setType(Type.UP_FRIEND_SHIP);
            objectOutputStream.writeObject(message);

            //接受一个消息
            boolean result2 = (boolean) objectInputStream.readObject();

            if (result2) {
                System.out.println("操作成功！");
            } else
                System.out.println("操作失败。");
            return result2;
        } catch (Exception e) {
            System.out.println("连接服务端失败。");
            return false;
        }
    }

    public static boolean upDataFriendAtSqlOnGroup(Message message) {
        //前端于后端的通信在注册的期间的方法类
        try (
                Socket socket = new Socket("127.0.0.1", 7777);
                OutputStream outputStream = socket.getOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                InputStream inputStream = socket.getInputStream();
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);) {

            //包装一个消息发给服务端进行处理
            message.setType(Type.DELETE_GROUP);
            objectOutputStream.writeObject(message);

            //接受一个消息
            boolean result2 = (boolean) objectInputStream.readObject();

            if (result2) {
                System.out.println("操作成功！");
            } else
                System.out.println("操作失败。");
            return result2;
        } catch (Exception e) {
            System.out.println("连接服务端失败。");
            return false;
        }
    }

    public static void sendMessage(Message message) {
        try (
                Socket socket = new Socket("127.0.0.1", 7777);
                OutputStream outputStream = socket.getOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                InputStream inputStream = socket.getInputStream();
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);) {
            objectOutputStream.writeObject(message);
            boolean result3 = (boolean) objectInputStream.readObject();

            if (result3) {
                System.out.println("发送成功！");
            } else
                System.out.println("对方未在线。");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void copyFile(File from, File to) {
        try (
                InputStream inputStream = new FileInputStream(from);
                OutputStream outputStream = new FileOutputStream(to);
        ) {
            byte[] bytes = new byte[1024 * 8];
            int len;
            while ((len = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void copyFileFromServer(File file) {
        try (
                Socket socket = new Socket("127.0.0.1", 7777);
                OutputStream outputStream = socket.getOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                InputStream inputStream = socket.getInputStream();
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                OutputStream fileOutputStream = new FileOutputStream(file);
        ) {
            Message message = new Message();
            message.setType(Type.APPLY_FILE);
            message.setObject(file.getName());
            objectOutputStream.writeObject(message);

            byte[] bytes = new byte[1024 * 8];
            int len;
            int sum =0;
            while ((len = inputStream.read(bytes))!=-1) {
                fileOutputStream.write(bytes, 0, len);
                sum+=len;
            }
            System.out.println(sum);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void copyFileToServer(File file) {
        try (
                Socket socket = new Socket("127.0.0.1", 7777);
                OutputStream outputStream = socket.getOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                InputStream inputStream = socket.getInputStream();
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                InputStream fileInputStream = new FileInputStream(file);
        ) {
            Message message = new Message();
            message.setType(Type.APPLY_FILE_TO_SERVER);
            message.setObject(file.getName());
            objectOutputStream.writeObject(message);
            objectOutputStream.flush();

            byte[] bytes = new byte[1024 *8];
            int len;
            int sum =0;
            while ((len = fileInputStream.read(bytes))!=-1){
                outputStream.write(bytes, 0, len);
                sum+=len;
            }
            socket.shutdownOutput();
            System.out.println(sum);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void openFile(File file) throws IOException {
        if (!Desktop.isDesktopSupported()) {
            System.out.println("Desktop is not supported");
            return;
        }

        Desktop desktop = Desktop.getDesktop();
        //let's try to open file
        if (file.exists())
            desktop.open(file);
        else
            System.out.println("文件不存在，无法打开。");
    }
}
