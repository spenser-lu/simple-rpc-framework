import com.luxiuyang.rpc_common.MySocketEncoder;
import com.luxiuyang.rpc_common.RPCRequest;
import com.luxiuyang.rpc_common.serialize.impl.HessianSerializer;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * \* Created with IntelliJ IDEA.
 * \* User: luxiuyang
 * \* Date: 2022/7/11
 * \* Time: 9:54
 * \* Description:
 * \
 */
public class TestClient {
    public static void main(String[] args) {
        Socket socket = new Socket();
        OutputStream outputStream = null;
        MySocketEncoder encoder = new MySocketEncoder(new HessianSerializer());
        try {
            socket.connect(new InetSocketAddress("localhost", 10010));
            outputStream = socket.getOutputStream();
            RPCRequest rpcRequest = new RPCRequest()
                    .setInterfaceName("com.hahah")
                    .setMethodName("hello")
                    .setParams(new Object[]{"sasda"})
                    .setParamTypes(new Class[]{String.class});
            outputStream.write(encoder.encode(rpcRequest));
            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
