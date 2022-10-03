package Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Server {

    private final int serverPort = 50002;

    private static Map<String, String> users;

    public static Map<String, Connection> clients = new HashMap<>();
    ServerSocket serverSocket;

    public void startServer() throws IOException, InterruptedException {
        getLogins();
        Thread waitConnectionThread = new Thread() {
            @Override
            public void run() {
                try {
                    waitForConnection();
                    //logWithPassword();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        waitConnectionThread.start();
        waitConnectionThread.join();
    }

    public void waitForConnection() throws IOException {
        ServerSocket serverSocket = new ServerSocket(serverPort);
        System.out.println("Server starts in port: " + serverPort);
        while (true) {
            Socket clientSocket = serverSocket.accept();
            InputStream inputStream = clientSocket.getInputStream();
            byte[] buffer = new byte[64];
            inputStream.read(buffer);
            String name = new String(buffer);
            name = Connection.messageDecoding(name);
            OutputStream outputStream = clientSocket.getOutputStream();
            while (clients.containsKey(name) || name.equals("")) {
                String message = "This name is already binded, choose another one.";
                message = message.length() + "/" + message;
                outputStream.write(message.getBytes());
                inputStream.read(buffer);
                name = new String(buffer);
                name = Connection.messageDecoding(name);
            }

            String message = "You are successfully connected to server";
            message = message.length() + "/" + message;
            outputStream.write(message.getBytes());
            System.out.println(name + " connected");
            clients.put(name, new Connection(clientSocket, name));
        }
    }

    public void logWithPassword() throws IOException {
        ServerSocket serverSocket = new ServerSocket(serverPort);
        System.out.println("Server starts in port: " + serverPort);
        while (true) {
            Socket clientSocket = serverSocket.accept();
            InputStream inputStream = clientSocket.getInputStream();
            byte[] buffer = new byte[64];
            inputStream.read(buffer);
            String name = new String(buffer);
            name = Connection.messageDecoding(name);
            OutputStream outputStream = clientSocket.getOutputStream();
            while (clients.containsKey(name) || name.equals("") || !users.containsKey(name)) {
                String message = "Wrong name, or this name is already used";
                message = message.length() + "/" + message;
                outputStream.write(message.getBytes());
                buffer = new byte[64];
                inputStream.read(buffer);
                name = new String(buffer);
                name = Connection.messageDecoding(name);
            }

            String message = "Ok";
            //message = message.length() + "/" + message;
            outputStream.write(message.getBytes());

            buffer = new byte[64];
            inputStream.read(buffer);
            String password = new String(buffer);
            password = Connection.messageDecoding(password);
            while (!users.get(name).equals(password)){
                message = "Wrong password";
                message = message.length() + "/" + message;
                outputStream.write(message.getBytes());
                buffer = new byte[64];
                inputStream.read(buffer);
                password = new String(buffer);
                password = Connection.messageDecoding(password);
            }

            message = "Ok";
            message = message.length() + "/" + message;
            outputStream.write(message.getBytes());

            System.out.println(name + " connected");
            clients.put(name, new Connection(clientSocket, name));
        }
    }


    public static void getLogins() throws IOException {
        users = new HashMap<>();
        FileReader fileReader = new FileReader("logins.txt");
        char[] buffer = new char[64];
        fileReader.read(buffer);
        String logins = new String(buffer);
        //System.out.println(logins);

        int i = 0;
        while (logins.charAt(i) != '/'){
            StringBuilder sb = new StringBuilder();
            while (logins.charAt(i) != '\n'){
                sb.append(logins.charAt(i));
                i++;
            }
            i++;
            StringBuilder password = new StringBuilder();
            while (logins.charAt(i) != '\n'){
                password.append(logins.charAt(i));
                i++;
                if (logins.charAt(i) == '/'){
                    break;
                }
            }

            if(!sb.isEmpty() && !password.isEmpty()){
                users.put(sb.toString(), password.toString());
            }

            i++;
        }
        System.out.println(users);
    }


}

