import java.io.*;
import java.net.*;
import java.util.*;

public class Client {
    private final int clientId;
    private final String host;
    private final int port;

    public Client(int clientId, String host, int port) {
        this.clientId = clientId;
        this.host = host;
        this.port = port;
    }

    public void start() {
        try (Socket socket = new Socket(host, port);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            out.writeInt(clientId);
            out.flush();
            String status = (String) in.readObject();
            if ("OK".equals(status)) {
                System.out.println("Client " + clientId + ": Connection OK");

                String[] requests = {"book_1", "car_2", "house_3"};
                for (String request : requests) {
                    out.writeObject(request);
                    out.flush();
                    List<?> response = (List<?>) in.readObject();
                    System.out.println("Client " + clientId + ": Received " + response);
                }
            } else {
                System.out.println("Client " + clientId + ": Connection REFUSED");
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        List<Thread> clients = new ArrayList<>();
        for (int i = 1; i <= 6; i++) {
            int id = i;
            Thread clientThread = new Thread(() -> new Client(id, "localhost", 2500).start());
            clients.add(clientThread);
            clientThread.start();
        }
        for (Thread client : clients) {
            try {
                client.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
