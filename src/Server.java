import java.io.DataInputStream;
import java.io.IOException;
import java.net.*;

public class Server implements Runnable{
    @Override
    public void run(){
        ServerSocket ss = null;
        int port = 0;

        for(int p : Ports.portsList){
            port = p;
            try {
                ss = new ServerSocket(port);
                break;
            } catch (IOException ignored) {
                //ignored
            }
        }

        System.out.println("Used Port: " + port);

        try {
            Socket s = ss.accept();//establishes connection
            DataInputStream dis = new DataInputStream(s.getInputStream());
            String str = (String) dis.readUTF();
            System.out.println("Client message = " + str);
            ss.close();
        } catch (SocketTimeoutException s) {
            System.out.println("Socket timed out!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start(){
        this.run();
    }


    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.start();
    }
}
