import com.luxiuyang.rpc_common.MySocketDecoder;

import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * \* Created with IntelliJ IDEA.
 * \* User: luxiuyang
 * \* Date: 2022/7/11
 * \* Time: 9:47
 * \* Description:
 * \
 */
public class TestSocketServer {
    ServerSocket serverSocket;

    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        Socket socket = null;
        InputStream inputStream = null;
        MySocketDecoder decoder = new MySocketDecoder();
        try {
            serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(10010));
            socket = serverSocket.accept();
            inputStream = socket.getInputStream();
            Object o = decoder.decode(inputStream);
            System.out.println(o);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try{
                serverSocket.close();
                socket.close();
                inputStream.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }


}
