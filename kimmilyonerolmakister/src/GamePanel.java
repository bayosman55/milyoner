package client.gui;

import java.awt.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class GamePanel extends JPanel {
    private ClientGUI parent;
    private JLabel questionLabel;
    private JButton[] optionButtons;
    private JButton audienceButton;
    private JButton fiftyFiftyButton;
    private JLabel jokerLabel;
    private PrintWriter out;
    private BufferedReader in;
    
    public GamePanel(ClientGUI parent) {
        this.parent = parent;
        setLayout(new BorderLayout());
        
        // Soru alanı
        questionLabel = new JLabel("", SwingConstants.CENTER);
        questionLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(questionLabel, BorderLayout.NORTH);
        
        // Seçenekler paneli
        JPanel optionsPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        optionButtons = new JButton[4];
        for (int i = 0; i < optionButtons.length; i++) {
            optionButtons[i] = new JButton();
            optionButtons[i].setFont(new Font("Arial", Font.PLAIN, 16));
            final char optionChar = (char) ('A' + i);
            optionButtons[i].addActionListener(e -> sendAnswer(optionChar));
            optionsPanel.add(optionButtons[i]);
        }
        add(optionsPanel, BorderLayout.CENTER);
        
        // Joker paneli
        JPanel jokerPanel = new JPanel(new FlowLayout());
        audienceButton = new JButton("Seyirciye Sor (S)");
        fiftyFiftyButton = new JButton("Yarı Yarıya (Y)");
        jokerLabel = new JLabel("Joker Hakkı: 2");
        
        audienceButton.addActionListener(e -> useJoker('S'));
        fiftyFiftyButton.addActionListener(e -> useJoker('Y'));
        
        jokerPanel.add(audienceButton);
        jokerPanel.add(fiftyFiftyButton);
        jokerPanel.add(jokerLabel);
        add(jokerPanel, BorderLayout.SOUTH);
        
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }
    
    public void startGame() {
        new Thread(() -> {
            try {
                Socket socket = new Socket("localhost", 4337);
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                
                String serverResponse;
                while ((serverResponse = in.readLine()) != null) {
                    processServerMessage(serverResponse);
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Sunucuya bağlanırken hata: " + e.getMessage(), 
                    "Hata", JOptionPane.ERROR_MESSAGE);
            }
        }).start();
    }
    
    private void processServerMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            if (message.startsWith("QUESTION:")) {
                String[] parts = message.split(":");
                String questionNumber = parts[1];
                String questionText = parts[2];
                questionLabel.setText(questionNumber + ". Soru: " + questionText);
            } 
            else if (message.startsWith("OPTIONS:")) {
                String[] options = message.substring(8).split(",");
                for (int i = 0; i < optionButtons.length && i < options.length; i++) {
                    optionButtons[i].setText(((char)('A' + i)) + ") " + options[i]);
                    optionButtons[i].setEnabled(true);
                    optionButtons[i].setVisible(true);
                }
            }
            else if (message.startsWith("JOKERS:")) {
                int jokersLeft = Integer.parseInt(message.substring(7));
                jokerLabel.setText("Joker Hakkı: " + jokersLeft);
                audienceButton.setEnabled(jokersLeft > 0);
                fiftyFiftyButton.setEnabled(jokersLeft > 0);
            }
            else if (message.startsWith("PROMPT:")) {
                // Prompt mesajını görüntüleyebilirsiniz
            }
            else if (message.startsWith("AUDIENCE:")) {
                String[] percentages = message.substring(9).split(" ");
                for (int i = 0; i < optionButtons.length && i < percentages.length; i++) {
                    optionButtons[i].setText(((char)('A' + i)) + ") " + percentages[i]);
                }
            }
            else if (message.startsWith("FIFTYFIFTY:")) {
                String[] remaining = message.substring(11).split(",");
                for (int i = 0; i < optionButtons.length; i++) {
                    optionButtons[i].setVisible(false);
                }
                optionButtons[0].setText("A) " + remaining[0]);
                optionButtons[1].setText("B) " + remaining[1]);
                optionButtons[0].setVisible(true);
                optionButtons[1].setVisible(true);
            }
            else if (message.startsWith("RESULT:")) {
                String[] parts = message.split(":");
                String result = parts[1];
                String prize = parts[2];
                
                if (result.equals("CORRECT")) {
                    JOptionPane.showMessageDialog(this, "Doğru cevap! Ödül: " + prize, 
                        "Tebrikler", JOptionPane.INFORMATION_MESSAGE);
                } 
                else if (result.equals("WRONG")) {
                    parent.showResultPanel("Yanlış cevap!", prize);
                } 
                else if (result.equals("WIN")) {
                    parent.showResultPanel("Tebrikler! Tüm soruları doğru cevapladınız!", prize);
                }
            }
        });
    }
    
    private void sendAnswer(char answer) {
        out.println(answer);
    }
    
    private void useJoker(char jokerType) {
        out.println(jokerType);
    }
}