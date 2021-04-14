import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client {
    public static void main(String[] args) throws IOException {
        String serverName = "myServer";

        int port = 6666;


        try{
            Socket s=new Socket("127.0.0.1",port);
            DataOutputStream dout =new DataOutputStream(s.getOutputStream());
            dout.writeUTF("Hello " + serverName);
            dout.flush();
            dout.close();
            s.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
