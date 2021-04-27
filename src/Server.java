import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.HashMap;


public class Server implements Runnable {

    // storage contains the information from the set command <key> and <value>
    private HashMap<String, String> storage = new HashMap<>();

    public String getValue(String key) {
        return storage.get(key);
    }

    public void putStorage(String key, String value) {
        this.storage.put(key, value);
    }

    //TODO: create connection between the nodes
    //TODO: broadcast command (get)

    private void connection(int port) throws IOException {
        ServerSocket ss = new ServerSocket(port);
        try {
            //establishes connection
            Socket clientSocket = ss.accept();
            //input is the information that we get from the client
            DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
            //output is the information send back to the client
            DataOutputStream outputStream = new DataOutputStream(clientSocket.getOutputStream());
            // convert UTF to string
            String str = (String) dis.readUTF();

            // check if was a get or a set
            if (str.substring(0, 3).equalsIgnoreCase("GET")) {
                String[] splitMessage = str.split(":");
                String key = splitMessage[2];
                String output = getValue(key);
                outputStream.writeUTF(output);
            } else if (str.substring(0, 3).equalsIgnoreCase("SET")) {
                String[] splitMessage = str.split(":");
                String key = splitMessage[2];
                String value = splitMessage[3];
                putStorage(key, value);
                outputStream.writeUTF("Server: information has been saved.");
            }
            outputStream.flush();
            ss.close();
            connection(port);
        } catch (SocketTimeoutException s) {
            System.out.println("Socket timed out!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        ServerSocket ss = null;
        int port = 0;
        //try to find a free port
        for (int p : Ports.portsList) {
            port = p;
            try {
                ss = new ServerSocket(port);
                // closing the connection so that a new connection can be established later
                ss.close();
                break;
            } catch (IOException ignored) {
                //ignored
            }
        }
        System.out.println("Used Port: " + port);

        // check if the connection can be done
        try {
            // create connection and check used command
            connection(port);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void start() {
        this.run();
    }


    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.start();
    }
}
