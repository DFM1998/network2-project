import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// commands to test:
// SET:8000:HALLO:TEST
// GET:8000:HALLO
// SET:8001:YES:TEST2
// CHECK:YES:5

public class Client implements Runnable {

    public void connectToServer(int serverId, String outputString) {
        try {
            // create a connection
            Socket s = new Socket("127.0.0.1", serverId);
            // add client port to the string
            outputString += ":" + s.getLocalPort();
            // send the information to the server
            DataOutputStream output = new DataOutputStream(s.getOutputStream());
            // send the input to the server
            output.writeUTF(outputString);
            // get the information from the server
            DataInputStream input = new DataInputStream(s.getInputStream());
            // print the output that we got from the server
            String serverAnswer = input.readUTF();
            System.out.println(serverAnswer);
            String checkServerAnswer = serverAnswer.substring(0, 17);
            if (checkServerAnswer.equals("Server: searching")) {
                
            }
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

            // check the user input is valid
            if (inputString.substring(0, 3).equalsIgnoreCase("GET")) {
                Pattern regex = Pattern.compile("(GET):[0-9]{1,7}:[a-zA-Z0-9_]+", Pattern.CASE_INSENSITIVE);
                Matcher message = regex.matcher(inputString);
                if (message.find()) {
                    String[] splitMessage = inputString.split(":");
                    int serverId = Integer.parseInt(splitMessage[1]);
                    // send the information to the server
                    connectToServer(serverId, inputString);
                } else {
                    System.out.println("Not right format: GET:<id>:<key>");
                }
            } else if (inputString.substring(0, 3).equalsIgnoreCase("SET")) {
                Pattern regex = Pattern.compile("(SET):[0-9]{1,7}:[a-zA-Z0-9_]+:[a-zA-Z0-9_]+", Pattern.CASE_INSENSITIVE);
                Matcher message = regex.matcher(inputString);
                if (message.find()) {
                    String[] splitMessage = inputString.split(":");
                    int serverId = Integer.parseInt(splitMessage[1]);
                    // send the information to the server
                    connectToServer(serverId, inputString);
                } else {
                    System.out.println("Not right format: SET:<id>:<key>:<value> (Do not use ':' for the <value>)");
                }
            } else if (inputString.substring(0, 5).equalsIgnoreCase("CHECK")) {
                Pattern regex = Pattern.compile("(CHECK):[a-zA-Z0-9_]+", Pattern.CASE_INSENSITIVE);
                Matcher message = regex.matcher(inputString);
                if (message.find()) {
                    int serverId = 8000;
                    // send the information to the server
                    connectToServer(serverId, inputString);
                } else {
                    System.out.println("Not right format: CHECK:<key>");
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
