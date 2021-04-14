import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class Server {
    public static void main(String[] args) throws IOException {

        int port = 15113;

        ServerSocket welcomingSocket = new ServerSocket(port);

        welcomingSocket.setSoTimeout(10000);

        try{
            ServerSocket ss=new ServerSocket(port);
            Socket s=ss.accept();//establishes connection
            DataInputStream dis=new DataInputStream(s.getInputStream());
            String  str=(String)dis.readUTF();
            System.out.println("message= "+str);
            ss.close();
        }catch (SocketTimeoutException s){
            System.out.println("Socket timed out!");
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
