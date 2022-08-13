package common.entity;

import java.io.Serializable;
import java.util.Date;

public class Message implements Serializable {
    //客户端和服务端交互的主要信息类
    private String toUser;
    private String fromUser;
    private Type type;//用枚举类型将各种信息都有一个枚举的类去对应
    private Object object;//主要的信息，Type用来提示object里面装的是什么类型的数据
    private long sendTime;//会自动生成的创建时间，但是好像忘记用上去了

    public Message() {
        this.sendTime = new Date().getTime();
    }

    public Message(Type type, String fromUser) {
        this.sendTime = new Date().getTime();
        this.type = type;
        this.fromUser = fromUser;
    }

    public Message(String fromUser, String toUser, Type type, Object object) {
        this.toUser = toUser;
        this.fromUser = fromUser;
        this.type = type;
        this.object = object;
        this.sendTime = new Date().getTime();
    }

    public long getSendTime() {
        return sendTime;
    }

    public void setSendTime(long sendTime) {
        this.sendTime = sendTime;
    }

    public String getToUser() {
        return toUser;
    }

    public void setToUser(String toUser) {
        this.toUser = toUser;
    }

    public String getFromUser() {
        return fromUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    @Override
    public String toString() {
        return "Message{" +
                "toUser='" + toUser + '\'' +
                ", fromUser='" + fromUser + '\'' +
                ", type=" + type +
                ", object=" + object +
                '}';
    }
}
