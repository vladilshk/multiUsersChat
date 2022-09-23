package ClientsForTest;

import Server.Connection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    String name;
    Connection connection;
    Socket socket;
    InputStream inputStream;
    OutputStream outputStream;


    public void connection() throws IOException {
        socket = connect();
        outputStream = socket.getOutputStream();
        inputStream = socket.getInputStream();
        while (true) {
            this.name = setName();
            outputStream.write((name.length() + "/" + name).getBytes());
            byte[] buffer = new byte[64];
            inputStream.read(buffer);
            String message = new String(buffer);
            message = messageDecoding(message);
            if (message.startsWith("You are successfully connected")) {
                System.out.println("hello");
                break;
            }
            else {
                System.out.println(message);
            }
        }
        System.out.println("You have successfully connected.");
        startMessaging();

    }

    public String setName(){
        while (true) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Input your name: ");
            String userName = scanner.nextLine();
            if (userName.contains(" ")){
                System.out.println("Username shouldn't contain ' '");
            }
            else {
                return userName;
            }
        }
    }

    public Socket connect(){
        while (true){
            try {
                Scanner scanner = new Scanner(System.in);
                System.out.println("Input server IP: ");
                String host = scanner.nextLine();
                System.out.println("Input serever port: ");
                int port = scanner.nextInt();
                Socket clientSocket = new Socket(host, port);
                return clientSocket;
            } catch (Exception e){
                System.out.println("Wrong IP or port. Try again");
            }
        }
    }


    public void startMessaging(){
        Thread sendThread = new Thread() {
            @Override
            public void run() {
                while (true){
                    send();
                }
            }
        };
        Thread receiveThread = new Thread(){
            public void run(){
                while (true){
                    receive();
                }
            }
        };
        sendThread.start();
        receiveThread.start();
        try {
            receiveThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void send()  {
        try {
            Scanner scanner = new Scanner(System.in);
            String message = scanner.nextLine();
            message = codeMessage(message);
            byte[] buffer = message.getBytes();
            outputStream = socket.getOutputStream();
            outputStream.write(buffer);
            outputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void receive() {
        try {
            byte[] buffer = new byte[1024];
            inputStream = socket.getInputStream();
            inputStream.read(buffer);
            String message = new String(buffer);
            message = messageDecoding(message);
            System.out.println(message);
        } catch (IOException e){

        }
    }

    public String codeMessage(String message){
        StringBuilder editedMessage = new StringBuilder();

        if (message.startsWith("@sendUser ")){
            editedMessage.append(message.length() + name.length() + 2);
            editedMessage.append('/');
            editedMessage.append("@sendUser ");
            int idx = 0;
            for (int i = 10; i < message.indexOf(' ', 10); i++) {
                editedMessage.append(message.charAt(i));
                idx = i;
            }
            idx += 2;
            editedMessage.append(' ');
            editedMessage.append(name + ": ");
            for(int i = idx; i < message.length(); i++){
                editedMessage.append(message.charAt(i));
            }
        } else {
            editedMessage.append(message.length() + name.length() + 2);
            editedMessage.append('/');
            editedMessage.append(name + ": ");
            editedMessage.append(message);
        }
        return editedMessage.toString();
    }

    public static String messageDecoding(String message) {
        StringBuilder decodedMessage = new StringBuilder();
        int idx = 0;
        String str = new String();
        while (message.charAt(idx) != '/') {
            str += message.charAt(idx);
            idx++;
        }
        idx++;
        int messageLength = Integer.decode(str);
        for (int i = idx; i < messageLength + idx; i++) {
            decodedMessage.append(message.charAt(i));
        }
        return decodedMessage.toString();
    }
}
