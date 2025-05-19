package chat;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatClientGUI extends JFrame {

    private JTextPane messagePane;
    private JTextField textField;
    private JButton sendButton;
    private ChatClient client;
    private String userName = ChatUI.userName;

    public ChatClientGUI() {
        super("Chat Application");
        setSize(480, 640);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        Color backgroundColor = new Color(240, 240, 240);
        Color buttonColor = new Color(75, 75, 75);
        Color textColor = new Color(50, 50, 50);
        Font textFont = new Font("Arial", Font.PLAIN, 14);
        Font buttonFont = new Font("Arial", Font.BOLD, 12);

        messagePane = new JTextPane();
        messagePane.setEditable(false);
        messagePane.setBackground(backgroundColor);
        messagePane.setForeground(textColor);
        messagePane.setFont(textFont);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(buttonFont);
        logoutButton.setBackground(buttonColor);
        logoutButton.setForeground(Color.WHITE);
        logoutButton.addActionListener(e -> {
            String departureMessage = userName + " has left the chat.";
            client.sendMessage(departureMessage);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
            setVisible(false);
            new ChatUI().setVisible(true);
        });

        JPanel logoutPanel = new JPanel(new BorderLayout());
        logoutPanel.setBackground(Color.CYAN);
        logoutPanel.add(logoutButton, BorderLayout.EAST);
        add(logoutPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(messagePane);
        add(scrollPane, BorderLayout.CENTER);

        this.setTitle("Chat Application - " + userName);

        textField = new JTextField();
        textField.setFont(textFont);
        textField.setForeground(textColor);
        textField.setBackground(backgroundColor);
        textField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String message = "[" + new SimpleDateFormat("HH:mm").format(new Date()) + "] " + userName + ": " + textField.getText();
                client.sendMessage(message);
                appendMessage(message, true);
                textField.setText("");
            }
        });

        JLabel nameLabel = new JLabel(userName);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setOpaque(true); // Make the label opaque
        nameLabel.setBackground(Color.DARK_GRAY);
        nameLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        logoutPanel.add(nameLabel, BorderLayout.WEST);

        sendButton = new JButton("Send");
        sendButton.setFont(buttonFont);
        sendButton.setBackground(buttonColor);
        sendButton.setForeground(Color.WHITE);
        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String message = "[" + new SimpleDateFormat("HH:mm").format(new Date()) + "] " + userName + ": " + textField.getText();
                client.sendMessage(message);
                appendMessage(message, true);
                textField.setText("");
            }
        });

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(backgroundColor);
        bottomPanel.add(textField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        try {
            this.client = new ChatClient("127.0.0.1", 6000, this::onMessageReceived);
            client.startClient();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error connecting to the server", "Connection error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private void onMessageReceived(String message) {
        // Check if the received message was sent by the current user
        if (!message.contains(userName + ":")) {
            appendMessage(message, false);
        }
    }

    private void appendMessage(String message, boolean isUserMessage) {
        StyledDocument doc = messagePane.getStyledDocument();
        SimpleAttributeSet left = new SimpleAttributeSet();
        SimpleAttributeSet right = new SimpleAttributeSet();

        StyleConstants.setAlignment(left, StyleConstants.ALIGN_LEFT);
        StyleConstants.setAlignment(right, StyleConstants.ALIGN_RIGHT);

        try {
            if (isUserMessage) {
                doc.insertString(doc.getLength(), message + "\n", right);
                doc.setParagraphAttributes(doc.getLength() - message.length() - 1, message.length(), right, false);
            } else {
                doc.insertString(doc.getLength(), message + "\n", left);
                doc.setParagraphAttributes(doc.getLength() - message.length() - 1, message.length(), left, false);
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ChatClientGUI().setVisible(true);
        });
    }
}
