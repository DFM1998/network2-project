import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
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
        int portNumber=0;
        try {
            //establishes connection
            Socket clientSocket = ss.accept();
            //input is the information that we get from the client
            DataInputStream dis = new DataInputStream(clientSocket.getInputStream());

            //output is the information send back to the client
            DataOutputStream outputStream = new DataOutputStream(clientSocket.getOutputStream());
            // convert UTF to string
            String str = dis.readUTF();

            System.out.println("Client (" + clientSocket.getPort() + ") said to server: " + str);

            // check if was a get or a set
            if (str.substring(0, 3).equalsIgnoreCase("GET")) {
                String[] splitMessage = str.split(":");
                String key = splitMessage[2];
                String output;
                // check if key has been found
                if (getValue(key) == null) {
                    output = "Server ("+ss.getLocalPort()+"): Key does not exist";
                }
                output = "Server ("+ss.getLocalPort()+"): " + getValue(key);
                outputStream.writeUTF(output);
            } else if (str.substring(0, 3).equalsIgnoreCase("SET")) {
                String[] splitMessage = str.split(":");
                String key = splitMessage[2];
                String value = splitMessage[3];
                putStorage(key, value);
                outputStream.writeUTF("Server ("+ss.getLocalPort()+"): information has been saved.");
            } else if (str.substring(0, 5).equalsIgnoreCase("CHECK")) {
                // broadcast command
                String[] splitMessage = str.split(":");
                String key = splitMessage[1];
                int ttl = Integer.parseInt(splitMessage[2]);
                portNumber = Integer.parseInt(splitMessage[3]);
                ttl--;
                str = "CHECK:" + key + ":" + ttl + ":" +portNumber;
                // check if this server has the value for the corresponding key
                if (storage.containsKey(key)) {
                    // in case the first server has the information, then he will send it directly to the client
                    if ((ss.getLocalPort()) == portNumber) {
                        outputStream.writeUTF("Server ("+ss.getLocalPort()+"):"+getValue(key));
                    } else {
                        // if found the key, then let's send it the main server
                        Socket mainServer = new Socket("127.0.0.1", portNumber);
                        DataOutputStream mainServerOutput = new DataOutputStream(mainServer.getOutputStream());
                        mainServerOutput.writeUTF("FOUND:" + ss.getLocalPort() + ":" + getValue(key));
                        mainServerOutput.flush();
                        mainServerOutput.close();
                        mainServer.close();
                    }
                } else {
                    // check if packet should die or not
                    if (ttl > 0) {
                        // start a connection between the nodes
                        serverConnection(ss);
                        // send information to my nodes
                        for (Socket server : servers) {
                            System.out.println(server);
                            DataOutputStream output = new DataOutputStream(server.getOutputStream());
                            output.writeUTF(str);
                            server.close();
                        }
                        // only the server who got the check message from the client, should send an answer to the client
                        if (ss.getLocalPort() == portNumber) {
                            int i = 0;
                            String answerToClient = "The package died, because the TTL ran out of time...";
                            // while until 16 because the TTL is 4, so that we make sure that each server got asked if he has the key or not
                            while (i<16) {
                                // the server creates a new connection with a node and gets an answer from the node
                                // repeat for all the nodes
                                Socket waitNode = ss.accept();
                                DataInputStream dis2 = new DataInputStream(waitNode.getInputStream());
                                String str2 = dis2.readUTF();
                                i++;
                                // close the connection with the node
                                waitNode.close();
                                // as soon as the key is found, we break the while and give the client the information
                                if(str2.substring(0, 5).equalsIgnoreCase("FOUND")) {
                                    String[] splitMessage2 = str2.split(":");
                                    System.out.println("Information has been found on the server ("+splitMessage2[2]+")");
                                    answerToClient = "Server ("+splitMessage2[1]+"): "+splitMessage2[2];
                                    break;
                                }
                            }
                            outputStream.writeUTF(answerToClient);
                        }
                    } else {
                        //inform the server that asked for the information that the packet died
                        Socket mainServer = new Socket("127.0.0.1", portNumber);
                        DataOutputStream mainServerOutput = new DataOutputStream(mainServer.getOutputStream());
                        mainServerOutput.writeUTF("TTL ran out of time...");
                        mainServerOutput.flush();
                        mainServerOutput.close();
                        mainServer.close();
                    }
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
        // index to next node (in case it is the last index, it should start at 0 again)
        if (index1 >= Ports.portsList.size()) {
            index1 = 0;
        }

        Socket node1 = new Socket("127.0.0.1", Ports.portsList.get(index1));
        servers.add(node1);
        Ports.portsList.indexOf(ss.getLocalPort());

        // since we skip always 2, we need to check for an overflow and manage respectively to the corresponding port
        int index2 = switch ((Ports.portsList.indexOf(ss.getLocalPort())) + 3) {
            case 10 -> 0;
            case 11 -> 1;
            case 12 -> 2;
            default -> (Ports.portsList.indexOf(ss.getLocalPort())) + 3;
        };

        Socket node2 = new Socket("127.0.0.1", Ports.portsList.get(index2));
        servers.add(node2);
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
                break;
            } catch (IOException e) {
                //ignored
            }
        }

        System.out.println("Used Port: " + port);

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
