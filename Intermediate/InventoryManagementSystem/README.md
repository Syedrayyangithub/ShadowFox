# Inventory Management System

A simple Inventory Management System built with JavaFX that allows users to manage their inventory items through a graphical user interface.

## Features

- Add new inventory items
- Update existing items
- Delete items
- View all items in a list
- Simple and intuitive GUI

## Prerequisites

- Java Development Kit (JDK) 21 or later
- Maven 3.6.0 or later

## Project Structure

```
InventoryManagementSystem/
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── example/
│                   └── InventoryManagementSystem/
│                       ├── Main.java
│                       └── Item.java
├── pom.xml
└── README.md
```

## How to Run

1. **Clone the repository** or download the project files

2. **Navigate to the project directory**
   ```bash
   cd ShadowFox/Intermediate/InventoryManagementSystem
   ```

3. **Build the project using Maven**
   ```bash
   mvn clean package
   ```

4. **Run the application**
   ```bash
   java --module-path "target/dependency" --add-modules javafx.controls,javafx.fxml -jar target/InventoryManagementSystem-1.0-SNAPSHOT-jar-with-dependencies.jar
   ```

## Using the Application

1. **Adding an Item**
   - Enter the item name in the "Item Name" field
   - Enter the quantity in the "Quantity" field
   - Enter the price in the "Price" field
   - Click the "Add Item" button

2. **Updating an Item**
   - Select an item from the list
   - Modify the details in the input fields
   - Click the "Update Item" button

3. **Deleting an Item**
   - Select an item from the list
   - Click the "Delete Item" button

## Troubleshooting

If you encounter any issues:

1. **JavaFX Runtime Components Missing**
   - Make sure you're running the application from the correct directory
   - Verify that the build process completed successfully
   - Check that all dependencies were copied to the target/dependency directory

2. **Build Errors**
   - Ensure you have the correct JDK version installed
   - Verify that Maven is properly installed and configured
   - Check that you're in the correct directory when running Maven commands

## Development

To modify or extend the application:

1. The main application logic is in `Main.java`
2. The `Item` class in `Item.java` defines the structure of inventory items
3. The GUI is built using JavaFX in the `Main` class

## License

This project is part of the ShadowFox learning series and is available for educational purposes..