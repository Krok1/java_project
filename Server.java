import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class Server {
    private static final int PORT = 2500;
    private static final int MAX_CLIENTS = 4;
    private static final Map<String, Object> objectsMap = new ConcurrentHashMap<>();
    private static final Set<Integer> connectedClients = ConcurrentHashMap.newKeySet();

    public static void main(String[] args) {
        createObjects();
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new ClientHandler(clientSocket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createObjects() {
        for (int i = 1; i <= 4; i++) {
            objectsMap.put("book_" + i, new Book("Title_" + i));
            objectsMap.put("car_" + i, new Car("Model_" + i));
            objectsMap.put("house_" + i, new House("Address_" + i));
        }
    }

    private static class ClientHandler extends Thread {
        private final Socket clientSocket;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try (ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
                 ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream())) {

                int clientId = in.readInt();
                if (connectedClients.size() < MAX_CLIENTS) {
                    connectedClients.add(clientId);
                    out.writeObject("OK");
                    System.out.println("Client " + clientId + " connected.");

                    for (int i = 0; i < 3; i++) {
                        String request = (String) in.readObject();
                        String className = request.split("_")[0];
                        List<Object> responseList = new ArrayList<>();

                        for (Map.Entry<String, Object> entry : objectsMap.entrySet()) {
                            if (entry.getKey().startsWith(className)) {
                                responseList.add(entry.getValue());
                            }
                        }

                        if (responseList.isEmpty()) {
                            responseList.add(new Book("default"));
                        }

                        out.writeObject(responseList);
                        System.out.println("Sent " + responseList + " to client " + clientId);
                        Thread.sleep(new Random().nextInt(1500)); // random delay
                    }

                    connectedClients.remove(clientId);
                } else {
                    out.writeObject("REFUSED");
                    System.out.println("Client " + clientId + " refused due to max client limit.");
                }
            } catch (IOException | ClassNotFoundException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
