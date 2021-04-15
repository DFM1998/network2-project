import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;

public class Client implements Runnable {

    @Override
    public void run() {
        String message = "Hello server";
        int indexRandomPort = new Random().nextInt(Ports.portsList.length);

        while (true){
            try {
                Socket s = new Socket("127.0.0.1", Ports.portsList[indexRandomPort]);
                DataOutputStream dout = new DataOutputStream(s.getOutputStream());
                dout.writeUTF(message);
                dout.flush();
                dout.close();
                s.close();
                break;
            } catch (IOException e) {
                // ingored
            }
        }

    }

    public void start() {
        this.run();
    }

    public static void main(String[] args) throws IOException {
        Client client = new Client();
        client.start();
    }
}
