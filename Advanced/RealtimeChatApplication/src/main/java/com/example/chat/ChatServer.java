package com.example.chat;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class ChatServer {
    private static final int PORT = 5000;
    private final Set<ClientHandler> clients = ConcurrentHashMap.newKeySet();
    private final Map<String, Set<ClientHandler>> chatRooms = new ConcurrentHashMap<>();
    private final Set<String> loggedInUsers = ConcurrentHashMap.newKeySet();
    private ServerSocket serverSocket;

    public void start() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Chat Server started on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(clientSocket);
                clients.add(handler);
                new Thread(handler).start();
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        } finally {
            stop();
        }
    }

    private void stop() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing server: " + e.getMessage());
        }
    }

    private class ClientHandler implements Runnable {
        private final Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String username;
        private String currentRoom = "General"; // Default room

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                // Get username
                out.println("Enter your username:");
                String proposedUsername = in.readLine();

                // Check if username is already logged in
                if (loggedInUsers.contains(proposedUsername)) {
                    out.println("ERROR: Username already in use. Please choose another.");
                    cleanup();
                    return;
                }

                username = proposedUsername;
                loggedInUsers.add(username);

                // Join default room
                joinRoom(currentRoom);
                broadcastMessage(currentRoom, "[System] " + username + " has joined the chat.");

                String message;
                while ((message = in.readLine()) != null) {
                    if (message.startsWith("/join ")) {
                        String room = message.substring(6).trim();
                        joinRoom(room);
                    } else if (message.startsWith("/create ")) {
                        String room = message.substring(8).trim();
                        createRoom(room);
                    } else if (message.startsWith("/rooms")) {
                        listRooms();
                    } else if (message.equalsIgnoreCase("/logout") || message.equalsIgnoreCase("/exit")) {
                        break;
                    } else {
                        broadcastMessage(currentRoom, username + ": " + message);
                    }
                }
            } catch (IOException e) {
                System.err.println("Client error: " + e.getMessage());
            } finally {
                cleanup();
            }
        }

        private void joinRoom(String room) {
            if (currentRoom != null) {
                leaveRoom(currentRoom);
            }
            chatRooms.computeIfAbsent(room, k -> ConcurrentHashMap.newKeySet()).add(this);
            currentRoom = room;
            out.println("[System] You joined room: " + room);
            broadcastMessage(room, "[System] " + username + " has joined room: " + room);
            broadcastRoomList();
        }

        private void createRoom(String room) {
            if (chatRooms.containsKey(room)) {
                out.println("[System] Room '" + room + "' already exists.");
                return;
            }
            // Create the room and add it to the map
            chatRooms.put(room, ConcurrentHashMap.newKeySet());
            out.println("[System] Room '" + room + "' created successfully.");
            joinRoom(room);
        }

        private void leaveRoom(String room) {
            Set<ClientHandler> members = chatRooms.get(room);
            if (members != null) {
                members.remove(this);
                broadcastMessage(room, "[System] " + username + " has left room: " + room);
                if (members.isEmpty()) {
                    chatRooms.remove(room);
                }
            }
        }

        private void listRooms() {
            StringBuilder sb = new StringBuilder("Available rooms:\n");
            for (String room : chatRooms.keySet()) {
                sb.append("- ").append(room).append("\n");
            }
            out.println(sb.toString());
        }

        private void broadcastMessage(String room, String message) {
            Set<ClientHandler> members = chatRooms.get(room);
            if (members != null) {
                for (ClientHandler client : members) {
                    client.out.println(message);
                }
            }
        }

        private void broadcastRoomList() {
            StringBuilder sb = new StringBuilder("Available rooms:\n");
            for (String room : chatRooms.keySet()) {
                sb.append("- ").append(room).append("\n");
            }
            for (ClientHandler client : clients) {
                client.out.println(sb.toString());
            }
        }

        private void cleanup() {
            if (currentRoom != null) {
                leaveRoom(currentRoom);
            }
            if (username != null) {
                loggedInUsers.remove(username);
            }
            clients.remove(this);
            try {
                if (out != null)
                    out.close();
                if (in != null)
                    in.close();
                if (socket != null)
                    socket.close();
            } catch (IOException e) {
                System.err.println("Cleanup error: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        new ChatServer().start();
    }
}