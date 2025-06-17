# ShadowFox Realtime Chat Application

A real-time multi-user chat application with chat rooms, built using Java Sockets and a modern Java Swing GUI.

## Features
- Multi-user chat with unique usernames
- Create, join, and list chat rooms
- Responsive, modern UI with login and chat pages
- Instructions pane with helpful tips
- Customizable images for branding and icons
- Graceful logout and reconnection support

## Prerequisites
- Java 11 or higher
- Maven

## Build Instructions
From the project root directory, run:

```sh
mvn clean package
```

If you encounter build errors, ensure all server/client windows are closed before rebuilding.

## Running the Server
Open a terminal and run:

```sh
cd ShadowFox/Advanced/RealtimeChatApplication
java -cp target/RealtimeChatApplication-1.0-SNAPSHOT.jar com.example.chat.ChatServer
```

## Running the Client (Multiple Instances)
Open one or more terminals and run for each client:

```sh
cd ShadowFox/Advanced/RealtimeChatApplication
java -cp target/RealtimeChatApplication-1.0-SNAPSHOT.jar com.example.chat.ChatClient
```

## UI Customization
- Images for the logo, instructions, and chat icons are located in `src/main/resources/images/`.
- You can replace `logo.png`, `instructions.png`, and `chat.png` with your own images for a personalized look.

## Usage
- Enter a unique username to log in.
- Use the instructions pane for chat commands:
  - `/create <room_name>` to create a room
  - `/join <room_name>` to join a room
  - `/rooms` to list available rooms
  - `/logout` or `/exit` to log out
- Messages are only visible to users in the same room.

## Troubleshooting
- If you get disconnected, simply log in again.
- If the build fails, ensure no Java processes are using the JAR file.

---
Enjoy chatting with ShadowFox! 