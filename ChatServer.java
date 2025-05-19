package chat;

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {

    private static List<ClientHandler> clients = new ArrayList<>();

    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket();
            serverSocket.setReuseAddress(true);
            serverSocket.bind(new InetSocketAddress(6000));
            System.out.println("Server running.");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket);

                ClientHandler clientThread = new ClientHandler(clientSocket, clients);
                clients.add(clientThread);
                new Thread(clientThread).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null && !serverSocket.isClosed()) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

class ClientHandler implements Runnable {

    private Socket clientSocket;
    private List<ClientHandler> clients;
    private PrintWriter out;
    private BufferedReader in;
    private BufferedWriter fileWriter; // Add a BufferedWriter for writing to a file
    private static final String HISTORY_FILE = "chatlog.txt"; // File name for saving the chat history

    public ClientHandler(Socket socket, List<ClientHandler> clients) throws IOException {
        this.clientSocket = socket;
        this.clients = clients;
        this.out = new PrintWriter(clientSocket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        // Open the file for writing chat history
        this.fileWriter = new BufferedWriter(new FileWriter(HISTORY_FILE, true));
    }

    public void run() {
        try {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                // Decode the received message using Hamming code and Caesar cipher
                String decodedMessage = Encryption.decode(inputLine);

                // Write the decoded message to the chat history file
                fileWriter.write(decodedMessage);
                fileWriter.newLine();
                fileWriter.flush(); // Flush the writer to ensure data is written to the file

                // Broadcast the decoded message to all clients
                for (ClientHandler aClient : clients) {
                    // Encode the message again for sending
                    String encodedMessage = Encryption.encode(decodedMessage);
                    aClient.out.println(encodedMessage);
                }
            }
        } catch (IOException e) {
            System.out.println("An error occurred: " + e.getMessage());
        } finally {
            try {
                in.close();
                out.close();
                clientSocket.close();
                fileWriter.close(); // Close the file writer
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

