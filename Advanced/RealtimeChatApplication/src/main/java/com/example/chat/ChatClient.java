package com.example.chat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public class ChatClient extends JFrame {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 5000;

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private Thread listenerThread;

    private JTextArea chatArea;
    private JTextField messageField;
    private JTextField usernameField;
    private JTextField newRoomField;
    private JButton sendButton;
    private JButton logoutButton;
    private JButton createRoomButton;
    private JList<String> roomList;
    private DefaultListModel<String> roomListModel;
    private String username;
    private String currentRoom = "General";
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private JPanel loginPanel;
    private JPanel chatPanel;

    public ChatClient() {
        setTitle("Chat Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // --- Login Panel ---
        loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        // Logo at the top
        try {
            ImageIcon logoIcon = new ImageIcon(ImageIO.read(getClass().getResource("/images/logo.png")));
            JLabel logoLabel = new JLabel(logoIcon);
            loginPanel.add(logoLabel, gbc);
            gbc.gridy++;
        } catch (Exception e) {
            // If image not found, skip
        }
        JLabel loginTitle = new JLabel("Welcome to ShadowFox Chat");
        loginTitle.setFont(new Font("SansSerif", Font.BOLD, 24));
        loginPanel.add(loginTitle, gbc);
        gbc.gridy++;
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        loginPanel.add(userLabel, gbc);
        gbc.gridy++;
        usernameField = new JTextField(18);
        usernameField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        loginPanel.add(usernameField, gbc);
        gbc.gridy++;
        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        loginButton.addActionListener(e -> connectAndLogin());
        loginPanel.add(loginButton, gbc);

        // --- Chat Panel ---
        chatPanel = new JPanel(new BorderLayout(10, 10));
        chatPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Instructions panel
        JPanel instructionsPanel = new JPanel(new BorderLayout());
        JPanel instructionsHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        try {
            ImageIcon instrIcon = new ImageIcon(ImageIO.read(getClass().getResource("/images/instructions.png")));
            JLabel instrLabel = new JLabel(instrIcon);
            instructionsHeader.add(instrLabel);
        } catch (Exception e) {
        }
        JLabel instrTitle = new JLabel("Instructions");
        instrTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        instructionsHeader.add(instrTitle);
        instructionsPanel.add(instructionsHeader, BorderLayout.NORTH);

        JTextArea instructionsArea = new JTextArea();
        instructionsArea.setEditable(false);
        instructionsArea.setBackground(new Color(245, 245, 245));
        instructionsArea.setFont(new Font("SansSerif", Font.PLAIN, 15));
        instructionsArea.setText(
                "\u2022 To create a room: /create <room_name>\n" +
                        "\u2022 To join a room: /join <room_name>\n" +
                        "\u2022 To see available rooms: /rooms\n" +
                        "\n" +
                        "\u2022 Usernames must be unique.\n" +
                        "\u2022 If you get disconnected, simply log in again.\n" +
                        "\u2022 Messages are only visible to users in the same room.\n" +
                        "\u2022 You can open multiple clients for testing.\n" +
                        "\n" +
                        "\u2022 For best experience, use short room names and avoid special characters.\n");
        instructionsArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        instructionsArea.setLineWrap(true);
        instructionsArea.setWrapStyleWord(true);
        JScrollPane instructionsScroll = new JScrollPane(instructionsArea);
        instructionsScroll.setPreferredSize(new Dimension(320, 0));
        instructionsPanel.add(instructionsScroll, BorderLayout.CENTER);
        instructionsPanel.setPreferredSize(new Dimension(340, 0));

        // Chat area
        JPanel chatHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        try {
            ImageIcon chatIcon = new ImageIcon(ImageIO.read(getClass().getResource("/images/chat.png")));
            JLabel chatLabel = new JLabel(chatIcon);
            chatHeader.add(chatLabel);
        } catch (Exception e) {
        }
        JLabel chatTitle = new JLabel("Chat Room");
        chatTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        chatHeader.add(chatTitle);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setFont(new Font("Monospaced", Font.PLAIN, 15));
        JScrollPane chatScrollPane = new JScrollPane(chatArea);
        chatScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JPanel chatAreaPanel = new JPanel(new BorderLayout());
        chatAreaPanel.add(chatHeader, BorderLayout.NORTH);
        chatAreaPanel.add(chatScrollPane, BorderLayout.CENTER);

        // Message panel
        JPanel messagePanel = new JPanel(new BorderLayout(5, 5));
        messageField = new JTextField();
        messageField.setFont(new Font("SansSerif", Font.PLAIN, 15));
        sendButton = new JButton("Send");
        sendButton.setFont(new Font("SansSerif", Font.BOLD, 15));
        messagePanel.add(messageField, BorderLayout.CENTER);
        messagePanel.add(sendButton, BorderLayout.EAST);

        // Logout button
        logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(messagePanel, BorderLayout.CENTER);
        bottomPanel.add(logoutButton, BorderLayout.EAST);

        // Split pane for instructions and chat
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, instructionsPanel, chatAreaPanel);
        splitPane.setDividerLocation(360);
        chatPanel.add(splitPane, BorderLayout.CENTER);
        chatPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Add panels to mainPanel
        mainPanel.add(loginPanel, "login");
        mainPanel.add(chatPanel, "chat");
        setContentPane(mainPanel);
        cardLayout.show(mainPanel, "login");

        // Add action listeners
        sendButton.addActionListener(e -> sendMessage());
        logoutButton.addActionListener(e -> logout());
        messageField.addActionListener(e -> sendMessage());
    }

    private void setChatComponentsEnabled(boolean enabled) {
        messageField.setEnabled(enabled);
        sendButton.setEnabled(enabled);
        logoutButton.setEnabled(enabled);
        usernameField.setEnabled(!enabled);
    }

    private void connectAndLogin() {
        username = usernameField.getText().trim();
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a username.");
            return;
        }

        try {
            socket = new Socket(SERVER_HOST, SERVER_PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Read server prompt and send username
            String response = in.readLine();
            if (response.startsWith("ERROR:")) {
                JOptionPane.showMessageDialog(this, response);
                cleanup();
                return;
            }

            out.println(username);
            setChatComponentsEnabled(true);
            cardLayout.show(mainPanel, "chat");
            startListener();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Could not connect to server: " + e.getMessage());
        }
    }

    private void startListener() {
        listenerThread = new Thread(() -> {
            try {
                String line;
                while ((line = in.readLine()) != null) {
                    final String message = line;
                    SwingUtilities.invokeLater(() -> {
                        if (message.startsWith("Available rooms:")) {
                            updateRoomList(message);
                        } else {
                            chatArea.append(message + "\n");
                            chatArea.setCaretPosition(chatArea.getDocument().getLength());
                        }
                    });
                }
            } catch (IOException e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "Lost connection to server.");
                    logout();
                });
            }
        });
        listenerThread.setDaemon(true);
        listenerThread.start();
    }

    private void updateRoomList(String message) {
        String[] lines = message.split("\n");
        roomListModel.clear();
        for (int i = 1; i < lines.length; i++) {
            String room = lines[i].replaceFirst("- ", "").trim();
            if (!room.isEmpty()) {
                roomListModel.addElement(room);
            }
        }
        if (roomListModel.contains(currentRoom)) {
            roomList.setSelectedValue(currentRoom, true);
        }
    }

    private void joinRoom(String room) {
        if (out != null) {
            out.println("/join " + room);
            currentRoom = room;
            chatArea.setText("");
        }
    }

    private void createRoom() {
        String room = newRoomField.getText().trim();
        if (!room.isEmpty() && out != null) {
            out.println("/create " + room);
            newRoomField.setText("");
        }
    }

    private void sendMessage() {
        String message = messageField.getText().trim();
        if (!message.isEmpty() && out != null) {
            out.println(message);
            messageField.setText("");
        }
    }

    private void logout() {
        try {
            if (out != null) {
                out.println("/logout");
            }
            if (listenerThread != null) {
                listenerThread.interrupt();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException ignored) {
        }
        setChatComponentsEnabled(false);
        chatArea.setText("");
        currentRoom = "General";
        SwingUtilities.invokeLater(() -> cardLayout.show(mainPanel, "login"));
    }

    private void cleanup() {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException ignored) {
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ChatClient().setVisible(true);
        });
    }
}