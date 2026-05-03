package client.gui;

import java.awt.*;
import javax.swing.*;

public class ResultPanel extends JPanel {
    private ClientGUI parent;
    private JLabel resultLabel;
    private JLabel prizeLabel;
    private JButton restartButton;
    
    public ResultPanel(ClientGUI parent) {
        this.parent = parent;
        setLayout(new BorderLayout());
        
        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        resultLabel = new JLabel("", SwingConstants.CENTER);
        resultLabel.setFont(new Font("Arial", Font.BOLD, 24));
        
        prizeLabel = new JLabel("", SwingConstants.CENTER);
        prizeLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        
        centerPanel.add(resultLabel);
        centerPanel.add(prizeLabel);
        add(centerPanel, BorderLayout.CENTER);
        
        restartButton = new JButton("Yeniden Başla");
        restartButton.addActionListener(e -> parent.showGamePanel());
        add(restartButton, BorderLayout.SOUTH);
        
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }
    
    public void setResult(String result, String prize) {
        resultLabel.setText(result);
        prizeLabel.setText("Kazandığınız ödül: " + prize);
    }
}