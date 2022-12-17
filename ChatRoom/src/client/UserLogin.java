package client;

import common.entity.Message;
import common.entity.User;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserLogin extends User implements Serializable {
    //服务端的登录后的主要的信息来源，继承User类，user储存自己的信息，继承之后还有与自己相关的信息


    public Map<String, List<Message>> chatRecordMap = new HashMap<>();
    //聊天记录的map，key为用户id，value为对应的聊天记录(包括群的聊天记录）

    public Map<String, User> friendMap = new HashMap<>();
    //好友的信息

    private User selectFriend = new User();
    //打开好友面板的好友资料

    public UserLogin() {
    }

    public UserLogin(String uid, String name, String password, char gender, int age, String email, int headPortrait, int states, String signature, String birthday) {
        super(uid, name, password, gender, age, email, headPortrait, states, signature, birthday);
    }

    public User getSelectFriend() {
        return selectFriend;
    }

    public void setSelectFriend(User selectFriend) {
        this.selectFriend = selectFriend;
    }
}
