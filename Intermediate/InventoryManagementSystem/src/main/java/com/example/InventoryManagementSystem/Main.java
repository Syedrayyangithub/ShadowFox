package com.example.InventoryManagementSystem;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class Main extends Application {

    private List<Item> inventory = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) {
        TextField nameField = new TextField();
        nameField.setPromptText("Item Name");
        TextField quantityField = new TextField();
        quantityField.setPromptText("Quantity");
        TextField priceField = new TextField();
        priceField.setPromptText("Price");

        Button addButton = new Button("Add Item");
        Button updateButton = new Button("Update Item");
        Button deleteButton = new Button("Delete Item");

        ListView<Item> itemListView = new ListView<>();
        itemListView.setPrefHeight(200);

        addButton.setOnAction(e -> {
            try {
                String name = nameField.getText();
                int quantity = Integer.parseInt(quantityField.getText());
                double price = Double.parseDouble(priceField.getText());
                Item item = new Item(name, quantity, price);
                inventory.add(item);
                itemListView.getItems().add(item);
                clearFields(nameField, quantityField, priceField);
            } catch (NumberFormatException ex) {
                showAlert("Invalid input", "Please enter valid numbers for quantity and price.");
            }
        });

        updateButton.setOnAction(e -> {
            Item selectedItem = itemListView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                try {
                    String name = nameField.getText();
                    int quantity = Integer.parseInt(quantityField.getText());
                    double price = Double.parseDouble(priceField.getText());
                    selectedItem.setName(name);
                    selectedItem.setQuantity(quantity);
                    selectedItem.setPrice(price);
                    itemListView.refresh();
                    clearFields(nameField, quantityField, priceField);
                } catch (NumberFormatException ex) {
                    showAlert("Invalid input", "Please enter valid numbers for quantity and price.");
                }
            } else {
                showAlert("No item selected", "Please select an item to update.");
            }
        });

        deleteButton.setOnAction(e -> {
            Item selectedItem = itemListView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                inventory.remove(selectedItem);
                itemListView.getItems().remove(selectedItem);
                clearFields(nameField, quantityField, priceField);
            } else {
                showAlert("No item selected", "Please select an item to delete.");
            }
        });

        VBox root = new VBox(10, nameField, quantityField, priceField, addButton, updateButton, deleteButton,
                itemListView);
        Scene scene = new Scene(root, 300, 400);

        primaryStage.setTitle("Inventory Management System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void clearFields(TextField... fields) {
        for (TextField field : fields) {
            field.clear();
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}