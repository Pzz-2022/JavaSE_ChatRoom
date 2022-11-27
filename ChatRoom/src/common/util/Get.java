package common.util;

import java.security.MessageDigest;
import java.util.Random;

public class Get {
    //工具类，Get一个随机的验证码或者是MD5加密的数据
    public static String getCode() {
        StringBuilder code = new StringBuilder();
        String data = "qwertyuiopasdfghjkzxcvbnmQWERTYUOPASDFGHJKLZXCVBNM0123456789";
        //验证码取消了大写的i和小写的l，因为真的分不清，提高了用户的体验（我在测试的时候老来这两很烦）
        Random r = new Random();
        int k = 0;
        for (int i = 0; i < 5; i++) {
            int index = r.nextInt(data.length());
            char c = data.charAt(index);
            code.append(c);
            if (c > '0' && c < '9')
                k++;
        }
        if (k == 0)
            code = new StringBuilder(r.nextInt(10) + code.substring(1));
        return code.toString();
    }

    public static String getMD5(String str) throws Exception {

        StringBuilder MD5 = new StringBuilder();

        MessageDigest md5 = MessageDigest.getInstance("MD5");
        byte[] bytes = str.getBytes();
        byte[] digest = md5.digest(bytes);

        for (int b : digest) {
            //摘要字节数组中各个字节的"十六进制"形式.
            int j = b;
            j = j & 0x000000ff;
            String s1 = Integer.toHexString(j);

            if (s1.length() == 1) {
                s1 = "0" + s1;
            }
            MD5.append(s1);
        }
        return MD5.toString();
    }
}
