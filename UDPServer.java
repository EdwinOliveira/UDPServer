import java.net.*;
import java.io.*;
import java.util.HashMap;

public class UDPServer {
    static private int indexCounter = 1;
    static private HashMap<Integer, String> storedHashMap = new HashMap<Integer, String>();
    static private HashMap<Integer, String> tempHashMap = new HashMap<Integer, String>();

    public static void main(String args[]) {

        DatagramSocket aSocket = null;
        String message[], requestClient, responseError;

        try {

            aSocket = new DatagramSocket(6789);
            byte[] buffer = new byte[1000], currentMessage;

            while (true) {
                //Getting the Message
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                aSocket.receive(request);

                //Transforming the message in a string
                requestClient = new String(request.getData());

                //Splitting the Message
                message = requestClient.split(",", 2);

                //Checking if Message is in Order compared to the server counter
                if (Integer.parseInt(message[0]) == indexCounter) {
                    indexCounter +=1;

                    storedHashMap.put(Integer.parseInt(message[0]), message[1]);

                    processDeliveredMessages();

                } else {
                    tempHashMap.put(Integer.parseInt(message[0]), message[1]);
                }

                if(tempHashMap.size() > 0) {
                    responseError = "waitingfor" + "," + indexCounter;
                    currentMessage = responseError.getBytes();
                } else {
                    currentMessage = message[1].getBytes();
                }

                DatagramPacket reply = new DatagramPacket(currentMessage, currentMessage.length,request.getAddress(), request.getPort());
                aSocket.send(reply);
            }
        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
        } finally {
            if (aSocket != null) aSocket.close();
        }
    }

    /**
     * Processes delivered messages
     *
     * @return the last message processed in order
     */
    static private void  processDeliveredMessages() {
        while(tempHashMap.size() > 0) {
            if(tempHashMap.containsKey((indexCounter))) {
                storedHashMap.put(((indexCounter)), tempHashMap.get((indexCounter)));
                tempHashMap.remove((indexCounter++));
            } else {
                break;
            }
        }
    }
}
