package common.entity;

import java.util.Date;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendEmail {
    //发送验证码的工具类
    public static String to(String toEmail, String url) {
        // 收件人电子邮箱
        //String to = "951913164@qq.com";

        // 发件人电子邮箱
        String from = "PP-2022@qq.com";

        // 指定发送邮件的主机为 smtp.qq.com
        String host = "smtp.qq.com";  //QQ 邮件服务器

        // 获取系统属性
        Properties properties = System.getProperties();

        // 设置邮件服务器
        properties.setProperty("mail.smtp.host", host);

        properties.put("mail.smtp.auth", "true");
        // 获取默认session对象
        Session session = Session.getDefaultInstance(properties, new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("PP-2022@qq.com", "vpywzneeazrtdcjh");
                //发件人邮件用户名、授权码
            }
        });

        try {
            // 创建默认的 MimeMessage 对象
            MimeMessage message = new MimeMessage(session);

            // Set From: 头部头字段
            message.setFrom(new InternetAddress(from));

            // Set To: 头部头字段
            message.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(toEmail));

            // Set Subject: 头部头字段
            message.setSubject("[PP聊天室] 账户激活");

            // 设置消息体
            String code = Get.getCode();
            message.setText("亲爱的 " + url + ":\r\n感谢您注册 PP聊天室。为保证用户正常使用我们的系统，请您输入验证码以帮助我们确认您的电子邮件地址有效：\r\n"
                    + code +"\r\n此邮件由系统自动发送，请勿回复。\r\n" + new Date());

            // 发送消息
            Transport.send(message);
            System.out.println("Sent message successfully....");
            return code;
        } catch (MessagingException mex) {
            mex.printStackTrace();
            return null;
        }
    }
    public static String to(String toEmail) {
        // 收件人电子邮箱
        //String to = "951913164@qq.com";

        // 发件人电子邮箱
        String from = "PP-2022@qq.com";

        // 指定发送邮件的主机为 smtp.qq.com
        String host = "smtp.qq.com";  //QQ 邮件服务器

        // 获取系统属性
        Properties properties = System.getProperties();

        // 设置邮件服务器
        properties.setProperty("mail.smtp.host", host);

        properties.put("mail.smtp.auth", "true");
        // 获取默认session对象
        Session session = Session.getDefaultInstance(properties, new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("PP-2022@qq.com", "vpywzneeazrtdcjh");
                //发件人邮件用户名、授权码
            }
        });

        try {
            // 创建默认的 MimeMessage 对象
            MimeMessage message = new MimeMessage(session);

            // Set From: 头部头字段
            message.setFrom(new InternetAddress(from));

            // Set To: 头部头字段
            message.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(toEmail));

            // Set Subject: 头部头字段
            message.setSubject("[PP聊天室] 密码重置");

            // 设置消息体
            String code = Get.getCode();
            message.setText("亲爱的 使用者:\r\n感谢您使用 PP聊天室。为保证用户正常重置密码，请您输入验证码以帮助我们确认您的电子邮件地址正确：\r\n"
                    + code +"\r\n此邮件由系统自动发送，请勿回复。\r\n" + new Date());

            // 发送消息
            Transport.send(message);
            System.out.println("Sent message successfully....");
            return code;
        } catch (MessagingException mex) {
            mex.printStackTrace();
            return null;
        }
    }
}
