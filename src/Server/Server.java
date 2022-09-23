package Server;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Server {

    public static Map<String, Connection> clients = new HashMap<>();
    ServerSocket serverSocket;
    public void startServer() throws IOException, InterruptedException {
        Thread waitConnectionThread = new Thread() {
            @Override
            public void run() {
                try {
                    waitForConnection();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        waitConnectionThread.start();
        waitConnectionThread.join();
    }
    public void waitForConnection() throws IOException {
        ServerSocket serverSocket = new ServerSocket(50001);
        while (true) {
            Socket clientSocket = serverSocket.accept();
            InputStream inputStream = clientSocket.getInputStream();
            byte[] buffer = new byte[64];
            inputStream.read(buffer);
            String name = new String(buffer);
            name = Connection.messageDecoding(name);
            OutputStream outputStream = clientSocket.getOutputStream();
            if (clients.containsKey(name)){
                String message = "This name is already binded, choose another one.";
                message = message.length() + "/" + message;
                outputStream.write(message.getBytes());
            }
            else {
                String message = "You are successfully connected to server";
                message = message.length() + "/" + message;
                outputStream.write(message.getBytes());
                System.out.println(name + " connected");
                clients.put(name, new Connection(clientSocket));

            }

        }
    }



}

