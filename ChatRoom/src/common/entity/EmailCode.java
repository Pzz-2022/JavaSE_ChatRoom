package common.entity;

import com.sun.xml.internal.ws.api.ha.StickyFeature;

import java.io.Serializable;

public class EmailCode implements Serializable {
    //储存对应邮箱的验证码
    //用于登录之前的一些不需要使用Message类来通信的替代品，也就是Message类的简化版
    private String email;
    private String code;
    private String uid;
    private String name;
    private long time;

    public EmailCode(String email) {
        this.email=email;
    }

    public EmailCode(String email, String code, String name) {
        this.email = email;
        this.code = code;
        this.name = name;
    }

    public EmailCode(String email, String name, long time) {
        //前端向后端申请发送验证码
        this.email = email;
        this.name = name;
        this.time = time;
    }

    public EmailCode(String email, String name) {
        //保存Email和验证码的关系
        this.email = email;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "EmailCode{" +
                "email='" + email + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}
