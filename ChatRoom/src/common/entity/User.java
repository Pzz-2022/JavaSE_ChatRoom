package common.entity;

import server.MainServer;

import java.io.Serializable;
import java.sql.PreparedStatement;

public class User implements Serializable {
    //用户类
    private String uid;
    private String name;
    private String password;
    private char gender;
    private int age;
    private String email;
    private int headPortrait;
    private int states;
    private String signature;
    private String birthday;


    public User() {
        this.headPortrait = 1;
        this.states = 0;
    }


    public User(String uid, String name, String password, char gender, int age, String email) {
        this.uid = uid;
        this.name = name;
        this.password = password;
        this.gender = gender;
        this.age = age;
        this.email = email;
        this.headPortrait = 1;
        this.signature = "这个少年很懒...";
    }

    public User(String uid, String name, String password, char gender, int age, String email, int headPortrait, int states, String signature, String birthday) {
        this.uid = uid;
        this.name = name;
        this.password = password;
        this.gender = gender;
        this.age = age;
        this.email = email;
        this.headPortrait = headPortrait;
        this.states = states;
        this.signature = signature;
        this.birthday = birthday;
    }

    public int addUser(User user) throws Exception {
        //好像也没用上
        String sql = "insert into user(uid, name, gender, password, age, e_mail, head_portrait) values(?,?,?,?,?,?,?)";
        PreparedStatement statement = MainServer.conn.prepareStatement(sql);

        //设置参数


        int result = statement.executeUpdate();
        return result;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public char getGender() {
        return gender;
    }

    public void setGender(char gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getHeadPortrait() {
        return headPortrait;
    }

    public void setHeadPortrait(int headPortrait) {
        this.headPortrait = headPortrait;
    }

    public int getStates() {
        return states;
    }

    public void setStates(int states) {
        this.states = states;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    @Override
    public String toString() {
        return "User{" +
                "uid='" + uid + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", gender=" + gender +
                ", age=" + age +
                ", email='" + email + '\'' +
                ", headPortrait=" + headPortrait +
                ", states=" + states +
                ", signature='" + signature + '\'' +
                ", birthday='" + birthday + '\'' +
                '}';
    }
}
