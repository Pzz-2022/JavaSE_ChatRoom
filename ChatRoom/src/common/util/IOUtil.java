package common.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class IOUtil {
    //字节输入流的关闭
    public static void close(InputStream inputStream){
        if(null != inputStream){
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    //字节输出流的关闭
    public static void close(OutputStream outputStream){
        if(null != outputStream){
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //重载方法 一起关闭
    public static void close(InputStream inputStream, OutputStream outputStream){
        close(inputStream);
        close(outputStream);
    }
}
