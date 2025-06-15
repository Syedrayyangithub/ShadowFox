# Library Management System

A comprehensive library management system with local database storage and Google Books API integration.

## Features

### Core Features
- User authentication (login/registration)
- Book management (add, update, delete)
- Book borrowing and returning
- Search functionality with filters
- Category-based organization
- Book recommendations based on reading history

### Enhanced Features
- Google Books API integration
- Online book search
- Online book recommendations
- Detailed book information including:
  - Book descriptions
  - Cover images
  - ISBN information
  - Author details
  - Categories
- Ability to add online books to local library

## Prerequisites

- Java 21 or higher
- Maven
- Internet connection (for Google Books API features)

## Project Structure

```
LibraryManagementSystem/
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── example/
│                   └── LibraryManagementSystem/
│                       ├── Main.java                 # Main application class
│                       ├── api/
│                       │   └── GoogleBooksAPI.java   # Google Books API integration
│                       ├── database/
│                       │   └── DatabaseManager.java  # SQLite database management
│                       └── model/
│                           ├── Book.java            # Book model
│                           ├── User.java            # User model
│                           └── Borrowing.java       # Borrowing model
└── pom.xml                                         # Maven configuration
```

## Database Schema

### Users Table
- id (INTEGER PRIMARY KEY)
- username (TEXT UNIQUE)
- password (TEXT)
- email (TEXT UNIQUE)
- role (TEXT)

### Books Table
- id (INTEGER PRIMARY KEY)
- title (TEXT)
- author (TEXT)
- isbn (TEXT UNIQUE)
- category (TEXT)
- quantity (INTEGER)
- available (INTEGER)

### Borrowings Table
- id (INTEGER PRIMARY KEY)
- user_id (INTEGER)
- book_id (INTEGER)
- borrow_date (TEXT)
- due_date (TEXT)
- return_date (TEXT)

## Usage

### Building the Project
```bash
mvn clean package
```

### Running the Application
```bash
java --module-path target/dependency --add-modules javafx.controls,javafx.fxml -jar target/LibraryManagementSystem-1.0-SNAPSHOT.jar
```

### Default Login
- Username: admin
- Password: admin123

### Using the Application

#### Local Library Management
1. **Searching Books**
   - Use the search field to find books by title or author
   - Filter results by category using the dropdown
   - Results update in real-time as you type

2. **Borrowing Books**
   - Select a book from the list
   - Click "Borrow Selected"
   - The book's availability will be updated

3. **Returning Books**
   - Select a borrowed book
   - Click "Return Selected"
   - The book will be marked as available

4. **Getting Recommendations**
   - Click "Get Recommendations"
   - View personalized recommendations based on your borrowing history

#### Online Features

1. **Online Book Search**
   - Enter a search term
   - Click "Search Online"
   - Browse results from Google Books
   - Add interesting books to your library

2. **Online Recommendations**
   - Click "Get Online Recommendations"
   - View recommendations based on your favorite category
   - Add recommended books to your library

## Troubleshooting

### Common Issues

1. **Application won't start**
   - Ensure Java 21 is installed
   - Verify all dependencies are downloaded
   - Check if the database file is accessible

2. **Search not working**
   - Check internet connection for online features
   - Verify database connection
   - Ensure proper login

3. **Google Books API issues**
   - Verify internet connection
   - Check if the API is accessible
   - Ensure proper error handling

## Development

### Adding New Features
1. Create new model classes in the `model` package
2. Update database schema if needed
3. Add UI components in `Main.java`
4. Implement business logic

### API Integration
- Google Books API integration is in `GoogleBooksAPI.java`
- Uses Apache HttpClient for API requests
- Jackson for JSON processing

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- Google Books API for book data
- JavaFX for the user interface
- SQLite for local database storage 