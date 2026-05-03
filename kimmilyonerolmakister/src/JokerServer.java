package joker;

import java.io.*;
import java.net.*;
import java.util.*;
import model.GameConstants; // model paketini import et

public class JokerServer {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(GameConstants.JOKER_SERVER_PORT)) {
            System.out.println("Joker Server is running on port " + GameConstants.JOKER_SERVER_PORT);

            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

                    String request = in.readLine();
                    System.out.println("Received request: " + request);

                    if (request != null && request.startsWith("AUDIENCE")) {
                        String[] parts = request.split(":");
                        if (parts.length < 2) {
                            out.println("Invalid AUDIENCE request");
                            continue;
                        }
                        String[] options = parts[1].split(",");
                        int[] percentages = generatePercentages(options.length);

                        StringBuilder response = new StringBuilder();
                        for (int i = 0; i < options.length; i++) {
                            response.append(options[i]).append(" (%").append(percentages[i]).append(") ");
                        }
                        out.println(response.toString().trim());
                    } else if (request != null && request.startsWith("FIFTYFIFTY")) {
                        String[] parts = request.split(":");
                        if (parts.length < 3) {
                            out.println("Invalid FIFTYFIFTY request");
                            continue;
                        }
                        String correctAnswer = parts[1];
                        String[] allOptions = parts[2].split(",");

                        List<String> options = new ArrayList<>(Arrays.asList(allOptions));
                        options.remove(correctAnswer);
                        if (options.isEmpty()) {
                            out.println(correctAnswer);
                            continue;
                        }
                        Collections.shuffle(options);

                        String remainingWrong = options.get(0);
                        out.println(correctAnswer + "," + remainingWrong);
                    }
                } catch (IOException e) {
                    System.out.println("Exception with client connection: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Could not start server on port " + GameConstants.JOKER_SERVER_PORT + ": " + e.getMessage());
        }
    }

    private static int[] generatePercentages(int count) {
        int[] percentages = new int[count];
        Random random = new Random();
        int total = 100;

        for (int i = 0; i < count - 1; i++) {
            int max = total - (count - i - 1); // Her seçeneğe en az 1 puan kalsın
            percentages[i] = 1 + random.nextInt(max);
            total -= percentages[i];
        }
        percentages[count - 1] = total;

        // Karıştır
        for (int i = 0; i < percentages.length; i++) {
            int j = random.nextInt(percentages.length);
            int temp = percentages[i];
            percentages[i] = percentages[j];
            percentages[j] = temp;
        }

        return percentages;
    }
}