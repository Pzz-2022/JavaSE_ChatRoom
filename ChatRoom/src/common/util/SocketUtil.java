package common.util;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketUtil {
    //socket的关闭
    public static void close(Socket socket) {
        if (socket != null && !socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //serverSocket的关闭
    public static void close(ServerSocket serverSocket) {
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
