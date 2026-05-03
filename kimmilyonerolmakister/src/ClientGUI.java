package client.gui;

import java.awt.*;
import javax.swing.*;

public class ClientGUI extends JFrame {
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private GamePanel gamePanel;
    private ResultPanel resultPanel;
    
    public ClientGUI() {
        setTitle("Kim Milyoner Olmak İster");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        
        gamePanel = new GamePanel(this);
        resultPanel = new ResultPanel(this);
        
        cardPanel.add(gamePanel, "GAME");
        cardPanel.add(resultPanel, "RESULT");
        
        add(cardPanel);
        
        showGamePanel();
    }
    
    public void showGamePanel() {
        cardLayout.show(cardPanel, "GAME");
        gamePanel.startGame();
    }
    
    public void showResultPanel(String result, String prize) {
        resultPanel.setResult(result, prize);
        cardLayout.show(cardPanel, "RESULT");
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ClientGUI gui = new ClientGUI();
            gui.setVisible(true);
        });
    }
}