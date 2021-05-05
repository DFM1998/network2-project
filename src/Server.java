import javax.sound.sampled.Port;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


public class Server implements Runnable {

    // storage contains the information from the set command <key> and <value>
    private HashMap<String, String> storage = new HashMap<>();
    private Set<Socket> servers = new HashSet<>();

    public String getValue(String key) {
        return storage.get(key);
    }

    public void putStorage(String key, String value) {
        this.storage.put(key, value);
    }

    private void connection(ServerSocket ss) throws IOException {

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
            } else if (str.substring(0, 6).equalsIgnoreCase("SERVER")) {
                //TODO: create connection between the nodes
                //TODO: broadcast command (get)
                System.out.println("TEST");
            }
            outputStream.flush();
            clientSocket.close();
            connection(ss);
        } catch (SocketTimeoutException s) {
            System.out.println("Socket timed out!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void serverConnection(ServerSocket ss) throws IOException {
        int index = (Ports.portsList.indexOf(ss.getLocalPort())) + 1;
        if (index >= Ports.portsList.size()){
            index = 0;
        }

        Socket node1 = new Socket("127.0.0.1", Ports.portsList.get(index));
        servers.add(node1);
        Ports.portsList.indexOf(ss.getLocalPort());

        int index2 = switch ((Ports.portsList.indexOf(ss.getLocalPort())) + 3) {
            case 10 -> 0;
            case 11 -> 1;
            case 12 -> 2;
            default -> (Ports.portsList.indexOf(ss.getLocalPort())) + 3;
        };

        Socket node2 = new Socket("127.0.0.1", Ports.portsList.get(index2));
        servers.add(node2);
        System.out.println(servers);

        try {
            Thread.sleep(10000);
            DataOutputStream output = new DataOutputStream(node1.getOutputStream());
            output.writeUTF("SERVER:hallo");
        } catch (InterruptedException e) {
            e.printStackTrace();
        };

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
                // ss.close();
                break;
            } catch (IOException ignored) {
                //ignored
            }
        }
        System.out.println("Used Port: " + port);

        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            serverConnection(ss);
        } catch (IOException e) {
            e.printStackTrace();
        }


        // check if the connection can be done
        try {
            // create connection and check used command
            connection(ss);
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
