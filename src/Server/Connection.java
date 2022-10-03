package Server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Connection {
    String name;
    Socket socket;
    OutputStream outputStream;
    InputStream inputStream;

    public Connection(Socket socket, String name) throws IOException {
        this.name = name;
        this.socket = socket;
        outputStream = socket.getOutputStream();
        inputStream = socket.getInputStream();
        Thread thread = new Thread() {
            @Override
            public void run() {
                //startListening();
                while (true) {
                    try {
                        while (serverReceive()) ;
                        outputStream.close();
                        inputStream.close();
                        socket.close();
                        Server.clients.remove(name);
                        System.out.println(name + "disconected");
                        for (String key : Server.clients.keySet()) {
                            System.out.println(key);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                }
            }
        };
        thread.start();

    }

    public boolean serverReceive() {
        try {
            byte[] buffer = new byte[1024];
            inputStream = socket.getInputStream();
            inputStream.read(buffer);
            String message = new String(buffer);
            message = messageDecoding(message);
            if (message.startsWith("@quit")) {
                for (String key : Server.clients.keySet()) {
                    if (Server.clients.get(key).equals(this)) {
                        serverSend(key + " was disconnected.");
                    }
                }
                return false;
            } else if (message.startsWith("@sendUser ")) {
                sendToCurrentUser(message);
            } else {
                serverSend(message);
            }
        } catch (IOException e) {

        }
        return true;
    }

    public void serverSend(String message) {
        message = message.length() + "/" + message;
        for (Connection con : Server.clients.values()) {
            if (!this.equals(con))
                con.send(message);
        }
    }

    public void sendToCurrentUser(String message) {
        StringBuilder name = new StringBuilder();
        for (int i = 10; i < message.indexOf(' ', 10); i++) {
            name.append(message.charAt(i));
        }
        if (Server.clients.containsKey(name.toString())) {
            StringBuilder editedMessage = new StringBuilder();
            editedMessage.append(message.length() - 11 - name.length());
            editedMessage.append('/');
            for (int i = message.indexOf(' ', 10) + 1; i < message.length(); i++) {
                editedMessage.append(message.charAt(i));
            }
            System.out.println(editedMessage.toString());
            Server.clients.get(name.toString()).send(editedMessage.toString());
        } else {
            //send(messageDecoding("Error: there is no user " + name.toString() + " in server"));
        }
    }

    public void send(String message) {
        try {
            outputStream = socket.getOutputStream();
            byte[] buffer = message.getBytes();
            outputStream.write(buffer);
            outputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized static String messageDecoding(String message) {
        System.out.println(message);
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
