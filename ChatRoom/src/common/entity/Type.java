package common.entity;

public enum Type {
    //枚举类
    //用下划线分割每一个单词
    //用户发送信息类
    TEXT,//文本类型
    FILE,//文件类型
    AGREE,//同意
    REFUSE,//拒绝
    USER,//用户类
    FRIEND_SHIP,//申请好友
    UP_FRIEND_SHIP,

    //系统消息类
    CHECK_UID,//检查数据库中是否含有该UID
    CHECK_EMAIL,//检查数据库中是否含有该Email
    APPLY,//申请类，申请好友或群聊
    APPLY_CODE,//前端向后端申请发送验证码
    REGISTER,//注册
    APPLY_CODE_FOR_UP_PASSWORD,
    UP_PASSWORD,
    UP_DATA_USER,
    LOGIN,//登录
    ALL_USER,
    ALL_FRIEND_SHIP,
    SOCKET_SAVE,
    DELETE_GROUP,//解散群聊
    DOWNLINE//下线
}
