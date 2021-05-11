import javax.sound.sampled.Port;
import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.*;
import java.util.ArrayList;
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
        int portNumber=0;
        try {
            //establishes connection
            Socket clientSocket = ss.accept();
            //input is the information that we get from the client
            DataInputStream dis = new DataInputStream(clientSocket.getInputStream());

            //output is the information send back to the client
            DataOutputStream outputStream = new DataOutputStream(clientSocket.getOutputStream());
            // convert UTF to string
            String str = (String) dis.readUTF();

            System.out.println("Client (" + clientSocket.getPort() + ") said to server: " + str);

            boolean checkOtherServer = false;
            // check if was a get or a set
            if (str.substring(0, 3).equalsIgnoreCase("GET")) {
                String[] splitMessage = str.split(":");
                String key = splitMessage[2];
                String output = getValue(key);
                outputStream.writeUTF(output);
            } else if (str.substring(0, 3).equalsIgnoreCase("SET")) {
                // TODO Test if the same key overwrites the first one
                String[] splitMessage = str.split(":");
                String key = splitMessage[2];
                String value = splitMessage[3];
                putStorage(key, value);
                outputStream.writeUTF("Server: information has been saved.");
            } else if (str.substring(0, 5).equalsIgnoreCase("CHECK")) {
                // broadcast command
                String[] splitMessage = str.split(":");
                String key = splitMessage[1];
                int ttl = Integer.parseInt(splitMessage[2]);
                portNumber = Integer.parseInt(splitMessage[3]);
                ttl--;
                str = "CHECK:" + key + ":" + ttl + ":" +portNumber;
                outputStream.writeUTF("Server: searching on the servers...");
                if (storage.containsKey(key)) {
                    if ((ss.getLocalPort()) == portNumber) {
                        outputStream.writeUTF(getValue(key));
                    } else {
                        // if found the key, then let's send it the main server
                        Socket mainServer = new Socket("127.0.0.1", portNumber);
                        DataOutputStream mainServerOutput = new DataOutputStream(mainServer.getOutputStream());
                        mainServerOutput.writeUTF("FOUND:" + getValue(key));
                        mainServerOutput.flush();
                        mainServerOutput.close();
                        mainServer.close();
                    }
                } else {
                    if (ttl > 0) {
                        checkOtherServer = true;
                    } else {
                        Socket mainServer = new Socket("127.0.0.1", portNumber);
                        DataOutputStream mainServerOutput = new DataOutputStream(mainServer.getOutputStream());
                        mainServerOutput.writeUTF("TTL ran out of time...");
                        mainServerOutput.flush();
                        mainServerOutput.close();
                        mainServer.close();
                    }
                }
            }

            if (checkOtherServer) {
                serverConnection(ss);
                for (Socket server : servers) {
                    System.out.println(server);
                    DataOutputStream output = new DataOutputStream(server.getOutputStream());
                    output.writeUTF(str);
                    server.close();
                }
                if (ss.getLocalPort() == portNumber) {
                    int i = 0;
                    String answerToClient = "The package died, because the TTL ran out of time...";
                    while (i<16) {
                        Socket waitNode = ss.accept();
                        System.out.println("ECH SINN DOOOOOsadadad");
                        DataInputStream dis2 = new DataInputStream(waitNode.getInputStream());
                        String str2 = (String) dis2.readUTF();
                        System.out.println("Answer from server "+"(i) :"+i+" : " + str2);
                        i++;
                        waitNode.close();
                        if(str2.substring(0, 5).equalsIgnoreCase("FOUND")) {
                            System.out.println("ECH SINN DOOOOO");
                            String[] splitMessage2 = str2.split(":");
                            answerToClient = splitMessage2[1];
                            break;
                        }
                    }
                    outputStream.writeUTF(answerToClient);
                }
            }

            outputStream.flush();
            outputStream.close();
            clientSocket.close();
            connection(ss);
        } catch (SocketTimeoutException s) {
            System.out.println("Socket timed out!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // create connection between the nodes
    private void serverConnection(ServerSocket ss) throws IOException {
        servers.clear();
        int index1 = (Ports.portsList.indexOf(ss.getLocalPort())) + 1;
        if (index1 >= Ports.portsList.size()) {
            index1 = 0;
        }

        Socket node1 = new Socket("127.0.0.1", Ports.portsList.get(index1));
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

        /*try {
            Thread.sleep(1000);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

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
