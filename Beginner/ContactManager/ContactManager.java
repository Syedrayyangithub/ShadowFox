package ContactManager;

import java.util.ArrayList;
import java.util.Scanner;

public class ContactManager {

    private ArrayList<Contact> contacts;
    private Scanner scanner;

    public ContactManager() {
        contacts = new ArrayList<>();
        scanner = new Scanner(System.in);
    }

    private void showMenu() {
        System.out.println("\n--- Contact Manager ---");
        System.out.println("1. Add Contact");
        System.out.println("2. View All Contacts");
        System.out.println("3. Search Contact by Name");
        System.out.println("4. Edit Contact");
        System.out.println("5. Delete Contact");
        System.out.println("6. Exit");
        System.out.print("Choose an option: ");
    }

    private void addContact() {
        System.out.print("Enter Name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Enter Phone: ");
        String phone = scanner.nextLine().trim();

        System.out.print("Enter Email: ");
        String email = scanner.nextLine().trim();

        Contact contact = new Contact(name, phone, email);
        contacts.add(contact);

        System.out.println("Contact added successfully!");
    }

    private void viewContacts() {
        if (contacts.isEmpty()) {
            System.out.println("No contacts to display.");
            return;
        }
        System.out.println("\nAll Contacts:");
        for (int i = 0; i < contacts.size(); i++) {
            System.out.println((i + 1) + ". " + contacts.get(i));
        }
    }

    private void searchContact() {
        System.out.print("Enter name to search: ");
        String searchName = scanner.nextLine().trim().toLowerCase();

        boolean found = false;
        for (Contact contact : contacts) {
            if (contact.getName().toLowerCase().contains(searchName)) {
                System.out.println(contact);
                found = true;
            }
        }

        if (!found) {
            System.out.println("No contacts found with that name.");
        }
    }

    private void editContact() {
        if (contacts.isEmpty()) {
            System.out.println("No contacts available to edit.");
            return;
        }

        viewContacts();
        System.out.print("Enter the number of the contact to edit: ");
        int index = readIntInput();

        if (index < 1 || index > contacts.size()) {
            System.out.println("Invalid contact number.");
            return;
        }

        Contact contact = contacts.get(index - 1);

        System.out.print("Enter new Name (leave blank to keep \"" + contact.getName() + "\"): ");
        String name = scanner.nextLine().trim();
        if (!name.isEmpty())
            contact.setName(name);

        System.out.print("Enter new Phone (leave blank to keep \"" + contact.getPhone() + "\"): ");
        String phone = scanner.nextLine().trim();
        if (!phone.isEmpty())
            contact.setPhone(phone);

        System.out.print("Enter new Email (leave blank to keep \"" + contact.getEmail() + "\"): ");
        String email = scanner.nextLine().trim();
        if (!email.isEmpty())
            contact.setEmail(email);

        System.out.println("Contact updated successfully!");
    }

    private void deleteContact() {
        if (contacts.isEmpty()) {
            System.out.println("No contacts available to delete.");
            return;
        }

        viewContacts();
        System.out.print("Enter the number of the contact to delete: ");
        int index = readIntInput();

        if (index < 1 || index > contacts.size()) {
            System.out.println("Invalid contact number.");
            return;
        }

        contacts.remove(index - 1);
        System.out.println("Contact deleted successfully!");
    }

    private int readIntInput() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid number: ");
            }
        }
    }

    public void run() {
        boolean exit = false;
        while (!exit) {
            showMenu();
            int choice = readIntInput();

            switch (choice) {
                case 1 -> addContact();
                case 2 -> viewContacts();
                case 3 -> searchContact();
                case 4 -> editContact();
                case 5 -> deleteContact();
                case 6 -> {
                    exit = true;
                    System.out.println("Exiting Contact Manager. Goodbye!");
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
        scanner.close();
    }

    public static void main(String[] args) {
        ContactManager app = new ContactManager();
        app.run();
    }
}
