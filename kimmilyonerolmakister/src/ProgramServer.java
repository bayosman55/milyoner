package server;

import java.io.*;
import java.net.*;
import java.util.*;
import model.*;

public class ProgramServer {
    private static final List<Question> QUESTIONS = loadQuestions();
    
    private static List<Question> loadQuestions() {
        List<Question> questions = new ArrayList<>();
        // Örnek sorular - dosyadan da yüklenebilir
        questions.add(new Question("Python hangi yıl geliştirilmiştir?", "1991", 
            new String[]{"1991", "2000", "1989", "2010"}));
        questions.add(new Question("Java hangi şirket tarafından geliştirilmiştir?", "Sun Microsystems", 
            new String[]{"Microsoft", "Sun Microsystems", "Apple", "IBM"}));
        questions.add(new Question("Hangisi bir işletim sistemi değildir?", "Photoshop", 
            new String[]{"Windows", "Linux", "macOS", "Photoshop"}));
        questions.add(new Question("Hangisi bir veri yapısıdır?", "HashMap", 
            new String[]{"For döngüsü", "HashMap", "If-else", "Class"}));
        questions.add(new Question("TCP/IP modelinde kaç katman vardır?", "4", 
            new String[]{"3", "4", "5", "7"}));
        return questions;
    }
    
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(GameConstants.PROGRAM_SERVER_PORT)) {
            System.out.println("Program Server is running on port " + GameConstants.PROGRAM_SERVER_PORT);
            
            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                    
                    System.out.println("New contestant connected");
                    playGame(out, in);
                    
                } catch (IOException e) {
                    System.out.println("Exception with client connection: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Could not start server on port " + GameConstants.PROGRAM_SERVER_PORT + ": " + e.getMessage());
        }
    }
    
    private static void playGame(PrintWriter out, BufferedReader in) throws IOException {
        int correctAnswers = 0;
        int jokersLeft = 2;
        
        for (int i = 0; i < QUESTIONS.size(); i++) {
            Question currentQuestion = QUESTIONS.get(i);
            
            out.println("QUESTION:" + (i+1) + ":" + currentQuestion.getQuestion());
            
            StringBuilder options = new StringBuilder("OPTIONS:");
            for (String option : currentQuestion.getOptions()) {
                options.append(option).append(",");
            }
            out.println(options.toString());
            
            out.println("JOKERS:" + jokersLeft);
            out.println("PROMPT:Cevabınızı girin (" + getOptionLetters(currentQuestion.getOptions().length) + 
                      ") veya Joker kullanın (S, Y):");
            
            String answer = in.readLine().toUpperCase();
            
            if (answer.equals("S") || answer.equals("Y")) {
                if (jokersLeft > 0) {
                    jokersLeft--;
                    
                    try (Socket jokerSocket = new Socket(GameConstants.HOST, GameConstants.JOKER_SERVER_PORT);
                         PrintWriter jokerOut = new PrintWriter(jokerSocket.getOutputStream(), true);
                         BufferedReader jokerIn = new BufferedReader(new InputStreamReader(jokerSocket.getInputStream()))) {
                        
                        if (answer.equals("S")) {
                            jokerOut.println("AUDIENCE:" + String.join(",", currentQuestion.getOptions()));
                            String audienceResponse = jokerIn.readLine();
                            out.println("AUDIENCE:" + audienceResponse);
                            out.println("PROMPT:Cevabınızı girin (" + getOptionLetters(currentQuestion.getOptions().length) + "):");
                            answer = in.readLine().toUpperCase();
                        } else {
                            jokerOut.println("FIFTYFIFTY:" + currentQuestion.getCorrectAnswer() + ":" + 
                                           String.join(",", currentQuestion.getOptions()));
                            String fiftyFiftyResponse = jokerIn.readLine();
                            out.println("FIFTYFIFTY:" + fiftyFiftyResponse);
                            out.println("PROMPT:Cevabınızı girin (A, B):");
                            answer = in.readLine().toUpperCase();
                        }
                    } catch (IOException e) {
                        out.println("ERROR:Joker servisine bağlanırken hata oluştu: " + e.getMessage());
                        i--;
                        continue;
                    }
                } else {
                    out.println("ERROR:Kullanabileceğiniz joker hakkınız kalmadı!");
                    i--;
                    continue;
                }
            }
            
            int selectedOption = answer.charAt(0) - 'A';
            if (selectedOption >= 0 && selectedOption < currentQuestion.getOptions().length && 
                currentQuestion.getOptions()[selectedOption].equals(currentQuestion.getCorrectAnswer())) {
                correctAnswers++;
                out.println("RESULT:CORRECT:" + GameConstants.REWARDS[i+1]);
            } else {
                out.println("RESULT:WRONG:" + GameConstants.REWARDS[correctAnswers]);
                break;
            }
        }
        
        if (correctAnswers == QUESTIONS.size()) {
            out.println("RESULT:WIN:" + GameConstants.REWARDS[GameConstants.REWARDS.length-1]);
        }
    }
    
    private static String getOptionLetters(int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            if (i > 0) sb.append(", ");
            sb.append((char)('A' + i));
        }
        return sb.toString();
    }
}
