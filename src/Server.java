import java.io.DataInputStream;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Server implements Runnable{

    private HashMap<String, String> storage = new HashMap<>();

    public HashMap<String, String> getStorage() {
        return storage;
    }

    public void setStorage(String key,String value) {
        this.storage.put(key, value);
    }

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
