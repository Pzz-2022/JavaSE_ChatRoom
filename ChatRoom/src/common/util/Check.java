package common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Check {
    //正则表达式，用于各种输入账号和密码的场景，不允许用户输入中文
    public static boolean checkChinese(String checkStr){
        String rule = "\\W";
        //定义匹配规则为\W,可以进行中文的匹配
        Pattern pattern = Pattern.compile(rule);
        //将规则解析，让计算机明白
        Matcher matcher = pattern.matcher(checkStr);
        //得到一个匹配器对象去找
        return matcher.find();
    }
    public static boolean checkUid(String uid){
        return uid.matches("[1-9]\\d{4,15}");
    }
    public static boolean checkQQEmail(String Email){
        return Email.matches("[1-9]\\d{4,11}@qq\\.com");
    }
}
