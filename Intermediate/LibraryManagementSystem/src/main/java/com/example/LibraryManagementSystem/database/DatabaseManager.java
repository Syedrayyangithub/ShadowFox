package com.example.LibraryManagementSystem.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import com.example.LibraryManagementSystem.model.User;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:library.db";
    private static DatabaseManager instance;
    private Connection connection;

    private DatabaseManager() {
        initializeDatabase();
        addSampleBooks();
        addDefaultUser();
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    private void initializeDatabase() {
        try {
            connection = DriverManager.getConnection(DB_URL);
            createTables();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTables() {
        try (Statement statement = connection.createStatement()) {
            // Create Users table
            statement.execute("""
                        CREATE TABLE IF NOT EXISTS users (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            username TEXT UNIQUE NOT NULL,
                            password TEXT NOT NULL,
                            email TEXT UNIQUE NOT NULL,
                            role TEXT NOT NULL
                        )
                    """);

            // Create Books table
            statement.execute("""
                        CREATE TABLE IF NOT EXISTS books (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            title TEXT NOT NULL,
                            author TEXT NOT NULL,
                            isbn TEXT UNIQUE NOT NULL,
                            category TEXT NOT NULL,
                            quantity INTEGER NOT NULL,
                            available INTEGER NOT NULL
                        )
                    """);

            // Create Borrowings table
            statement.execute("""
                        CREATE TABLE IF NOT EXISTS borrowings (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            user_id INTEGER NOT NULL,
                            book_id INTEGER NOT NULL,
                            borrow_date TEXT NOT NULL,
                            due_date TEXT NOT NULL,
                            return_date TEXT,
                            FOREIGN KEY (user_id) REFERENCES users (id),
                            FOREIGN KEY (book_id) REFERENCES books (id)
                        )
                    """);

            // Create Recommendations table
            statement.execute("""
                        CREATE TABLE IF NOT EXISTS recommendations (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            user_id INTEGER NOT NULL,
                            book_id INTEGER NOT NULL,
                            rating INTEGER,
                            FOREIGN KEY (user_id) REFERENCES users (id),
                            FOREIGN KEY (book_id) REFERENCES books (id)
                        )
                    """);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addSampleBooks() {
        try (Statement statement = connection.createStatement()) {
            // Clear existing books
            statement.execute("DELETE FROM books");

            // Add sample books
            statement.execute("""
                        INSERT INTO books (title, author, isbn, category, quantity, available)
                        VALUES
                        ('The Great Gatsby', 'F. Scott Fitzgerald', '9780743273565', 'Fiction', 5, 5),
                        ('To Kill a Mockingbird', 'Harper Lee', '9780446310789', 'Fiction', 3, 3),
                        ('1984', 'George Orwell', '9780451524935', 'Science Fiction', 4, 4),
                        ('The Hobbit', 'J.R.R. Tolkien', '9780547928227', 'Fantasy', 6, 6),
                        ('Pride and Prejudice', 'Jane Austen', '9780141439518', 'Romance', 4, 4),
                        ('The Catcher in the Rye', 'J.D. Salinger', '9780316769488', 'Fiction', 3, 3),
                        ('The Alchemist', 'Paulo Coelho', '9780062315007', 'Fiction', 5, 5),
                        ('The Da Vinci Code', 'Dan Brown', '9780307474278', 'Mystery', 4, 4),
                        ('The Little Prince', 'Antoine de Saint-Exupéry', '9780156013987', 'Fiction', 3, 3),
                        ('The Kite Runner', 'Khaled Hosseini', '9781594631931', 'Fiction', 4, 4)
                    """);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addDefaultUser() {
        try (Statement statement = connection.createStatement()) {
            // Clear existing users
            statement.execute("DELETE FROM users");

            // Add default user
            statement.execute("""
                        INSERT INTO users (username, password, email, role)
                        VALUES ('admin', 'admin123', 'admin@library.com', 'ADMIN')
                    """);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public User authenticateUser(String username, String password) throws SQLException {
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(
                        "SELECT * FROM users WHERE username = ? AND password = ?")) {

            stmt.setString(1, username);
            stmt.setString(2, password); // In a real app, use password hashing!

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getString("role"));
            }
            return null;
        }
    }
}