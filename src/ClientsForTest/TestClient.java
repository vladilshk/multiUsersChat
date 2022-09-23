package ClientsForTest;

import java.io.IOException;

public class TestClient {
    public static void main(String[] args) throws IOException, InterruptedException {
        Client client = new Client();
        client.connection();
    }
}
