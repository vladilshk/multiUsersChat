package Server;

import java.io.IOException;

public class TestServer {
    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = new Server();
        server.startServer();
    }
}
