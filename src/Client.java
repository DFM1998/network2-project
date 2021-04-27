import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Client implements Runnable {

    public void connectToServer(int indexRandomPort, String inputString){
        while (true){
            try {
                Socket s = new Socket("127.0.0.1", Ports.portsList[indexRandomPort]);
                DataOutputStream dout = new DataOutputStream(s.getOutputStream());
                dout.writeUTF(inputString);
                dout.flush();
                dout.close();
                s.close();
                break;
            } catch (IOException e) {
                // ingored
            }
        }
    }

    @Override
    public void run() {
        int indexRandomPort = new Random().nextInt(Ports.portsList.length);
        while (true){
            System.out.print("Enter your command:");
            Scanner scanner = new Scanner(System.in);
            String inputString = scanner.nextLine();

            if (inputString.substring(0,3).equalsIgnoreCase("GET")){
                Pattern regex = Pattern.compile("(GET):[0-9]{1,7}:[a-zA-Z0-9_]+", Pattern.CASE_INSENSITIVE);
                Matcher message = regex.matcher(inputString);
                if (message.find()){
                    String[] splitMessage = inputString.split(":");
                    int serverId = Integer.parseInt(splitMessage[1]);
                    String key = splitMessage[2];
                    // TODO: check because of the inputString and check with the indexRandomPort
                    connectToServer(indexRandomPort, inputString);
                }else {
                    System.out.println("Not right format: GET:<id>:<key>");
                }
            }else if(inputString.substring(0,3).equalsIgnoreCase("SET")){
                Pattern regex = Pattern.compile("(SET):[0-9]{1,7}:[a-zA-Z0-9_]+:[a-zA-Z0-9_]+", Pattern.CASE_INSENSITIVE);
                Matcher message = regex.matcher(inputString);
                if (message.find()){
                    String[] splitMessage = inputString.split(":");
                    int serverId = Integer.parseInt(splitMessage[1]);
                    String key = splitMessage[2];
                    String value = splitMessage[3];
                    // TODO: check with the indexRandomPort
                    connectToServer(indexRandomPort, value);
                }else {
                    System.out.println("Not right format: SET:<id>:<key>:<value> (Do not use ':' for the <value>)");
                }
            }
            else {
                System.out.println("Not valid command, only GET and SET allowed.");
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
