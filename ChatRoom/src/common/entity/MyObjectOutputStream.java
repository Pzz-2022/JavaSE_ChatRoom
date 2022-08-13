package common.entity;

import java.io.*;

public class MyObjectOutputStream extends ObjectOutputStream {

    public MyObjectOutputStream(OutputStream out) throws IOException {
        super(out);
    }

    @Override
    protected void writeStreamHeader() throws IOException {
        //重写读取头部信息方法：不写入头部信息
        super.reset();
    }
}

