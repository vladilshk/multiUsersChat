package ClientsForTest;

import ClientsForTest.Client;

import java.io.IOException;

public class SecondClient {
    public static void main(String[] args) throws IOException, InterruptedException {
        Client client = new Client();
        client.connection();
    }
}
