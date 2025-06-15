package com.example.LibraryManagementSystem;

import com.example.LibraryManagementSystem.api.GoogleBooksAPI;
import com.example.LibraryManagementSystem.database.DatabaseManager;
import com.example.LibraryManagementSystem.model.Book;
import com.example.LibraryManagementSystem.model.User;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class Main extends Application {
    private DatabaseManager dbManager;
    private User currentUser;
    private ListView<Book> bookListView;
    private TextField searchField;
    private ComboBox<String> categoryFilter;

    @Override
    public void start(Stage primaryStage) {
        dbManager = DatabaseManager.getInstance();
        showLoginScreen(primaryStage);
        Scene scene = primaryStage.getScene();
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
    }

    private void showLoginScreen(Stage stage) {
        VBox loginBox = new VBox(20);
        loginBox.setPadding(new Insets(30));
        loginBox.setAlignment(Pos.CENTER);
        loginBox.setMaxWidth(400);

        Label titleLabel = new Label("Library Management System");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        titleLabel.getStyleClass().add("header-label");
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setMaxWidth(Double.MAX_VALUE);

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setMaxWidth(300);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(300);

        Button loginButton = new Button("Login");
        loginButton.setMaxWidth(300);
        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            System.out.println("Login attempt: " + username);
            if (username.isEmpty() || password.isEmpty()) {
                showAlert("Error", "Please enter both username and password.");
                return;
            }
            try {
                User user = dbManager.authenticateUser(username, password);
                if (user != null) {
                    System.out.println("Login successful for: " + username);
                    currentUser = user;
                    showMainScreen(stage);
                } else {
                    System.out.println("Login failed for: " + username);
                    showAlert("Error", "Invalid username or password.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                showAlert("Error", "Database error: " + ex.getMessage());
            }
        });
        passwordField.setOnAction(e -> loginButton.fire());

        Button registerButton = new Button("Register");
        registerButton.setMaxWidth(300);
        registerButton.setOnAction(e -> showRegistrationScreen(stage));

        loginBox.getChildren().addAll(titleLabel, usernameField, passwordField, loginButton, registerButton);
        Scene scene = new Scene(loginBox, 500, 400);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Login - Library Management System");
        stage.show();
    }

    private void showMainScreen(Stage stage) {
        VBox mainLayout = new VBox(20);
        mainLayout.setPadding(new Insets(30));

        Label headerLabel = new Label("Library Management System");
        headerLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        headerLabel.getStyleClass().add("header-label");
        headerLabel.setAlignment(Pos.CENTER);
        headerLabel.setMaxWidth(Double.MAX_VALUE);

        // Top section with search and filters
        HBox topBox = new HBox(10);
        topBox.setAlignment(Pos.CENTER_LEFT);
        topBox.setPadding(new Insets(10, 0, 20, 0));
        searchField = new TextField();
        searchField.setPromptText("Search books by title or author...");
        searchField.setPrefWidth(300);

        categoryFilter = new ComboBox<>();
        categoryFilter.getItems().addAll("All", "Fiction", "Science Fiction", "Fantasy", "Romance", "Mystery");
        categoryFilter.setValue("All");

        Button searchButton = new Button("Search");
        java.net.URL searchIconUrl = getClass().getResource("/search.png");
        if (searchIconUrl != null) {
            ImageView searchIcon = new ImageView(new Image(searchIconUrl.toExternalForm()));
            searchIcon.setFitHeight(16);
            searchIcon.setFitWidth(16);
            searchButton.setGraphic(searchIcon);
        }
        Button clearButton = new Button("Clear");
        Button onlineSearchButton = new Button("Search Online");

        topBox.getChildren().addAll(searchField, categoryFilter, searchButton, clearButton, onlineSearchButton);

        // Main content (book list, etc.)
        VBox centerBox = new VBox(10);
        centerBox.setAlignment(Pos.CENTER);
        bookListView = new ListView<>();
        bookListView.setPrefHeight(400);

        // Add a label to show search results
        Label resultsLabel = new Label("All Books");
        resultsLabel.setPadding(new Insets(5));

        centerBox.getChildren().addAll(resultsLabel, bookListView);

        // Bottom section (actions)
        HBox bottomBox = new HBox(10);
        bottomBox.setAlignment(Pos.CENTER_RIGHT);
        bottomBox.setPadding(new Insets(20, 0, 0, 0));
        Button borrowButton = new Button("Borrow Selected");
        Button returnButton = new Button("Return Selected");
        Button recommendButton = new Button("Get Recommendations");
        Button onlineRecommendButton = new Button("Get Online Recommendations");
        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> {
            currentUser = null;
            showLoginScreen(stage);
        });
        bottomBox.getChildren().addAll(borrowButton, returnButton, recommendButton, onlineRecommendButton,
                logoutButton);

        mainLayout.getChildren().addAll(headerLabel, topBox, centerBox, bottomBox);

        // Add event handlers
        searchButton.setOnAction(e -> searchBooks());
        clearButton.setOnAction(e -> {
            searchField.clear();
            categoryFilter.setValue("All");
            refreshBookList();
            resultsLabel.setText("All Books");
        });

        onlineSearchButton.setOnAction(e -> showOnlineSearch());

        // Add real-time search as you type
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.length() >= 2) {
                searchBooks();
            } else if (newVal.isEmpty()) {
                refreshBookList();
                resultsLabel.setText("All Books");
            }
        });

        // Add category filter change listener
        categoryFilter.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (!searchField.getText().isEmpty()) {
                searchBooks();
            }
        });

        borrowButton.setOnAction(e -> borrowSelectedBook());
        returnButton.setOnAction(e -> returnSelectedBook());
        recommendButton.setOnAction(e -> showRecommendations());
        onlineRecommendButton.setOnAction(e -> showOnlineRecommendations());

        Scene scene = new Scene(mainLayout, 900, 600);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Library Management System");
        stage.setMaximized(true);
        stage.show();
    }

    private void showRegistrationScreen(Stage stage) {
        VBox registerBox = new VBox(20);
        registerBox.setPadding(new Insets(30));
        registerBox.setAlignment(Pos.CENTER);
        registerBox.setMaxWidth(400);

        Label titleLabel = new Label("Register New User");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        titleLabel.getStyleClass().add("header-label");
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setMaxWidth(Double.MAX_VALUE);

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setMaxWidth(300);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(300);

        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.setMaxWidth(300);

        Button registerButton = new Button("Register");
        registerButton.setMaxWidth(300);
        registerButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            String email = emailField.getText();
            if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
                showAlert("Error", "Please fill in all fields.");
                return;
            }
            if (registerUser(username, password, email)) {
                showAlert("Registration Successful", "You can now login with your credentials.");
                showLoginScreen(stage);
            } else {
                showAlert("Registration Failed", "Username or email already exists.");
            }
        });

        Button backButton = new Button("Back to Login");
        backButton.setMaxWidth(300);
        backButton.setOnAction(e -> showLoginScreen(stage));

        registerBox.getChildren().addAll(titleLabel, usernameField, passwordField, emailField, registerButton,
                backButton);
        Scene scene = new Scene(registerBox, 500, 400);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Registration - Library Management System");
        stage.setMaximized(true);
        stage.show();
    }

    private boolean authenticateUser(String username, String password) {
        try (Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(
                        "SELECT * FROM users WHERE username = ? AND password = ?")) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                currentUser = new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getString("role"));
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean registerUser(String username, String password, String email) {
        try (Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(
                        "INSERT INTO users (username, password, email, role) VALUES (?, ?, ?, ?)")) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, email);
            stmt.setString(4, "USER");
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void refreshBookList() {
        bookListView.getItems().clear();
        try (Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement("SELECT * FROM books")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Book book = new Book(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("isbn"),
                        rs.getString("category"),
                        rs.getInt("quantity"),
                        rs.getInt("available"));
                bookListView.getItems().add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void searchBooks() {
        String searchTerm = searchField.getText().toLowerCase();
        String category = categoryFilter.getValue();
        bookListView.getItems().clear();

        try (Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(
                        "SELECT * FROM books WHERE (LOWER(title) LIKE ? OR LOWER(author) LIKE ?) " +
                                "AND (? = 'All' OR category = ?)")) {
            stmt.setString(1, "%" + searchTerm + "%");
            stmt.setString(2, "%" + searchTerm + "%");
            stmt.setString(3, category);
            stmt.setString(4, category);
            ResultSet rs = stmt.executeQuery();

            int resultCount = 0;
            while (rs.next()) {
                Book book = new Book(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("isbn"),
                        rs.getString("category"),
                        rs.getInt("quantity"),
                        rs.getInt("available"));
                bookListView.getItems().add(book);
                resultCount++;
            }

            // Update the results label
            Label resultsLabel = (Label) ((VBox) bookListView.getParent()).getChildren().get(0);
            if (searchTerm.isEmpty() && category.equals("All")) {
                resultsLabel.setText("All Books");
            } else {
                resultsLabel.setText(String.format("Found %d book(s) matching '%s' in category '%s'",
                        resultCount, searchTerm, category));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Search Error", "An error occurred while searching for books.");
        }
    }

    private void borrowSelectedBook() {
        Book selectedBook = bookListView.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showAlert("Error", "Please select a book to borrow.");
            return;
        }
        if (selectedBook.getAvailable() <= 0) {
            showAlert("Error", "This book is not available for borrowing.");
            return;
        }

        try (Connection conn = dbManager.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Update book availability
                PreparedStatement updateBook = conn.prepareStatement(
                        "UPDATE books SET available = available - 1 WHERE id = ?");
                updateBook.setInt(1, selectedBook.getId());
                updateBook.executeUpdate();

                // Create borrowing record
                PreparedStatement createBorrowing = conn.prepareStatement(
                        "INSERT INTO borrowings (user_id, book_id, borrow_date, due_date) VALUES (?, ?, ?, ?)");
                createBorrowing.setInt(1, currentUser.getId());
                createBorrowing.setInt(2, selectedBook.getId());
                createBorrowing.setString(3, LocalDate.now().toString());
                createBorrowing.setString(4, LocalDate.now().plusDays(14).toString());
                createBorrowing.executeUpdate();

                conn.commit();
                showAlert("Success", "Book borrowed successfully.");
                refreshBookList();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to borrow book.");
        }
    }

    private void returnSelectedBook() {
        Book selectedBook = bookListView.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showAlert("Error", "Please select a book to return.");
            return;
        }

        // Check if the user has borrowed this book
        try (Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(
                        "SELECT * FROM borrowings WHERE user_id = ? AND book_id = ? AND return_date IS NULL")) {

            stmt.setInt(1, currentUser.getId());
            stmt.setInt(2, selectedBook.getId());
            ResultSet rs = stmt.executeQuery();

            if (!rs.next()) {
                showAlert("Error", "You haven't borrowed this book.");
                return;
            }

            // Update the borrowing record
            try (PreparedStatement updateStmt = conn.prepareStatement(
                    "UPDATE borrowings SET return_date = ? WHERE user_id = ? AND book_id = ? AND return_date IS NULL")) {
                updateStmt.setString(1, LocalDate.now().toString());
                updateStmt.setInt(2, currentUser.getId());
                updateStmt.setInt(3, selectedBook.getId());
                updateStmt.executeUpdate();
            }

            // Update book availability
            try (PreparedStatement bookStmt = conn.prepareStatement(
                    "UPDATE books SET available = available + 1 WHERE id = ?")) {
                bookStmt.setInt(1, selectedBook.getId());
                bookStmt.executeUpdate();
            }

            showAlert("Success", "Book returned successfully!");
            refreshBookList();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to return the book.");
        }
    }

    private void showRecommendations() {
        if (currentUser == null) {
            showAlert("Error", "Please log in to get recommendations.");
            return;
        }

        try (Connection conn = dbManager.getConnection()) {
            // Get user's most borrowed category
            String favoriteCategory = getFavoriteCategory(conn);

            // Get recommended books based on favorite category
            List<Book> recommendations = getRecommendedBooks(conn, favoriteCategory);

            // Show recommendations in a new window
            Stage recommendationsStage = new Stage();
            VBox recommendationsBox = new VBox(10);
            recommendationsBox.setPadding(new Insets(20));

            Label titleLabel = new Label("Recommended Books for You");
            titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

            ListView<Book> recommendationsList = new ListView<>();
            recommendationsList.getItems().addAll(recommendations);

            Label explanationLabel = new Label(String.format(
                    "Based on your reading history, you seem to enjoy %s books. Here are some recommendations:",
                    favoriteCategory));

            recommendationsBox.getChildren().addAll(titleLabel, explanationLabel, recommendationsList);

            Scene scene = new Scene(recommendationsBox, 400, 500);
            recommendationsStage.setTitle("Book Recommendations");
            recommendationsStage.setScene(scene);
            recommendationsStage.show();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to generate recommendations.");
        }
    }

    private String getFavoriteCategory(Connection conn) throws SQLException {
        String query = """
                    SELECT b.category, COUNT(*) as count
                    FROM borrowings br
                    JOIN books b ON br.book_id = b.id
                    WHERE br.user_id = ?
                    GROUP BY b.category
                    ORDER BY count DESC
                    LIMIT 1
                """;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, currentUser.getId());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("category");
            }
        }

        // If no borrowing history, return a default category
        return "Fiction";
    }

    private List<Book> getRecommendedBooks(Connection conn, String favoriteCategory) throws SQLException {
        List<Book> recommendations = new ArrayList<>();

        // Get books from favorite category that haven't been borrowed by the user
        String query = """
                    SELECT b.*
                    FROM books b
                    LEFT JOIN borrowings br ON b.id = br.book_id AND br.user_id = ?
                    WHERE b.category = ? AND br.id IS NULL
                    ORDER BY b.available DESC
                    LIMIT 5
                """;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, currentUser.getId());
            stmt.setString(2, favoriteCategory);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Book book = new Book(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("isbn"),
                        rs.getString("category"),
                        rs.getInt("quantity"),
                        rs.getInt("available"));
                recommendations.add(book);
            }
        }

        // If not enough recommendations, add some popular books from other categories
        if (recommendations.size() < 5) {
            String additionalQuery = """
                        SELECT b.*
                        FROM books b
                        LEFT JOIN borrowings br ON b.id = br.book_id AND br.user_id = ?
                        WHERE b.category != ? AND br.id IS NULL
                        ORDER BY b.available DESC
                        LIMIT ?
                    """;

            try (PreparedStatement stmt = conn.prepareStatement(additionalQuery)) {
                stmt.setInt(1, currentUser.getId());
                stmt.setString(2, favoriteCategory);
                stmt.setInt(3, 5 - recommendations.size());
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    Book book = new Book(
                            rs.getInt("id"),
                            rs.getString("title"),
                            rs.getString("author"),
                            rs.getString("isbn"),
                            rs.getString("category"),
                            rs.getInt("quantity"),
                            rs.getInt("available"));
                    recommendations.add(book);
                }
            }
        }

        return recommendations;
    }

    private void showOnlineSearch() {
        String query = searchField.getText();
        if (query.isEmpty()) {
            showAlert("Error", "Please enter a search term.");
            return;
        }

        List<GoogleBooksAPI.BookInfo> results = null;
        try {
            results = GoogleBooksAPI.searchBooks(query);
        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert("API Error",
                    "There was a problem connecting to the Google Books API. Please check your internet connection and try again.");
            return;
        }

        if (results == null || results.isEmpty()) {
            showAlert("Search Results", "No books found online for: '" + query + "'. Try a different search term.");
            return;
        }

        Stage searchStage = new Stage();
        VBox searchBox = new VBox(10);
        searchBox.setPadding(new Insets(20));

        Label titleLabel = new Label("Online Search Results");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        ListView<GoogleBooksAPI.BookInfo> resultsList = new ListView<>();
        resultsList.setCellFactory(lv -> new ListCell<GoogleBooksAPI.BookInfo>() {
            @Override
            protected void updateItem(GoogleBooksAPI.BookInfo item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    VBox box = new VBox(5);
                    Label titleLabel = new Label(item.getTitle());
                    titleLabel.setStyle("-fx-font-weight: bold;");
                    Label authorLabel = new Label("By: " + item.getAuthor());
                    Label categoryLabel = new Label("Category: " + item.getCategory());
                    Label isbnLabel = new Label("ISBN: " + item.getIsbn());

                    TextArea descriptionArea = new TextArea(item.getDescription());
                    descriptionArea.setWrapText(true);
                    descriptionArea.setEditable(false);
                    descriptionArea.setPrefRowCount(3);

                    box.getChildren().addAll(titleLabel, authorLabel, categoryLabel, isbnLabel, descriptionArea);
                    setGraphic(box);
                }
            }
        });
        resultsList.getItems().addAll(results);

        Button addToLibraryButton = new Button("Add Selected to Library");
        addToLibraryButton.setOnAction(e -> {
            GoogleBooksAPI.BookInfo selected = resultsList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                addBookToLibrary(selected);
                searchStage.close();
            }
        });

        searchBox.getChildren().addAll(titleLabel, resultsList, addToLibraryButton);
        Scene scene = new Scene(searchBox, 600, 500);
        searchStage.setTitle("Online Book Search");
        searchStage.setScene(scene);
        searchStage.show();
    }

    private void addBookToLibrary(GoogleBooksAPI.BookInfo bookInfo) {
        try (Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(
                        "INSERT INTO books (title, author, isbn, category, quantity, available) VALUES (?, ?, ?, ?, ?, ?)")) {

            stmt.setString(1, bookInfo.getTitle());
            stmt.setString(2, bookInfo.getAuthor());
            stmt.setString(3, bookInfo.getIsbn());
            stmt.setString(4, bookInfo.getCategory());
            stmt.setInt(5, 1); // Initial quantity
            stmt.setInt(6, 1); // Initially available

            stmt.executeUpdate();
            refreshBookList();
            showAlert("Success", "Book added to library successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to add book to library.");
        }
    }

    private void showOnlineRecommendations() {
        if (currentUser == null) {
            showAlert("Error", "Please log in to get recommendations.");
            return;
        }

        try (Connection conn = dbManager.getConnection()) {
            String favoriteCategory = getFavoriteCategory(conn);
            List<GoogleBooksAPI.BookInfo> recommendations = GoogleBooksAPI.getRecommendations(favoriteCategory);

            if (recommendations.isEmpty()) {
                showAlert("Recommendations", "No online recommendations available.");
                return;
            }

            Stage recommendationsStage = new Stage();
            VBox recommendationsBox = new VBox(10);
            recommendationsBox.setPadding(new Insets(20));

            Label titleLabel = new Label("Online Book Recommendations");
            titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

            ListView<GoogleBooksAPI.BookInfo> recommendationsList = new ListView<>();
            recommendationsList.setCellFactory(lv -> new ListCell<GoogleBooksAPI.BookInfo>() {
                @Override
                protected void updateItem(GoogleBooksAPI.BookInfo item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        VBox box = new VBox(5);
                        Label titleLabel = new Label(item.getTitle());
                        titleLabel.setStyle("-fx-font-weight: bold;");
                        Label authorLabel = new Label("By: " + item.getAuthor());
                        Label categoryLabel = new Label("Category: " + item.getCategory());
                        Label isbnLabel = new Label("ISBN: " + item.getIsbn());

                        TextArea descriptionArea = new TextArea(item.getDescription());
                        descriptionArea.setWrapText(true);
                        descriptionArea.setEditable(false);
                        descriptionArea.setPrefRowCount(3);

                        box.getChildren().addAll(titleLabel, authorLabel, categoryLabel, isbnLabel, descriptionArea);
                        setGraphic(box);
                    }
                }
            });
            recommendationsList.getItems().addAll(recommendations);

            Button addToLibraryButton = new Button("Add Selected to Library");
            addToLibraryButton.setOnAction(e -> {
                GoogleBooksAPI.BookInfo selected = recommendationsList.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    addBookToLibrary(selected);
                    recommendationsStage.close();
                }
            });

            recommendationsBox.getChildren().addAll(titleLabel, recommendationsList, addToLibraryButton);
            Scene scene = new Scene(recommendationsBox, 600, 500);
            recommendationsStage.setTitle("Online Book Recommendations");
            recommendationsStage.setScene(scene);
            recommendationsStage.show();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to generate recommendations.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}