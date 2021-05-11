import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Client implements Runnable {
    public void connectToServer(int serverId, String outputString) {
        try {
            // create a connection
            Socket s = new Socket("127.0.0.1", serverId);

            // send the information to the server
            DataOutputStream output = new DataOutputStream(s.getOutputStream());

            // send the input to the server
            output.writeUTF(outputString);

            // get the information from the server
            DataInputStream input = new DataInputStream(s.getInputStream());

            // print the output that we got from the server
            String serverAnswer = input.readUTF();
            System.out.println(serverAnswer);

            // close and flush the connection
            output.flush();
            output.close();
            s.close();
        } catch (IOException e) {
            System.out.println("Server is not online.");
        }
    }

    @Override
    public void run() {
        while (true) {
            System.out.print("Enter your command:");
            // wait for user input
            Scanner scanner = new Scanner(System.in);
            String inputString = scanner.nextLine();

            if (inputString.length() < 3){
                System.out.println("Please enter any command: GET, SET or CHECK.");
            }
            // check the user input is valid
            else if (inputString.substring(0, 3).equalsIgnoreCase("GET")) {
                Pattern regex = Pattern.compile("(GET):[0-9]{1,7}:[a-zA-Z0-9_]+", Pattern.CASE_INSENSITIVE);
                Matcher message = regex.matcher(inputString);
                if (message.find()) {
                    String[] splitMessage = inputString.split(":");
                    int serverId = Integer.parseInt(splitMessage[1]);
                    // send the information to the server
                    connectToServer(serverId, inputString.toUpperCase());
                } else {
                    System.out.println("Not right format: GET:<port>:<key>");
                }
            } else if (inputString.substring(0, 3).equalsIgnoreCase("SET")) {
                Pattern regex = Pattern.compile("(SET):[0-9]{1,7}:[a-zA-Z0-9_]+:[a-zA-Z0-9_]+", Pattern.CASE_INSENSITIVE);
                Matcher message = regex.matcher(inputString);
                if (message.find()) {
                    String[] splitMessage = inputString.split(":");
                    int serverId = Integer.parseInt(splitMessage[1]);
                    if(serverId == 0000){
                        Random random = new Random(System.currentTimeMillis());
                        int index = random.nextInt(Ports.portsList.size());
                        serverId = Ports.portsList.get(index);
                    }
                    // send the information to the server
                    connectToServer(serverId, inputString.toUpperCase());
                } else {
                    System.out.println("Not right format: SET:<port>:<key>:<value> (Do not use ':' for the <value>)");
                }
            } else if (inputString.substring(0, 5).equalsIgnoreCase("CHECK")) {
                Pattern regex = Pattern.compile("(CHECK):[a-zA-Z0-9_]+", Pattern.CASE_INSENSITIVE);
                Matcher message = regex.matcher(inputString);
                if (message.find()) {
                    Random random = new Random(System.currentTimeMillis());
                    int index = random.nextInt(Ports.portsList.size());
                    int serverId = Ports.portsList.get(index);
                    //time to live is 5 inorder to check all the nodes for the information
                    int ttl = 5;
                    inputString += ":" + ttl + ":" + serverId;
                    System.out.println("Searching on the servers...");
                    // send the information to the server
                    connectToServer(serverId, inputString.toUpperCase());
                } else {
                    System.out.println("Not right format: CHECK:<key>:<TTL>");
                }
            } else {
                System.out.println("Not valid command, only GET, SET and CHECK (broadcast) allowed.");
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
