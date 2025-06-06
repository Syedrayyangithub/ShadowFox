package EnhancedCalculator;

import java.util.Scanner;

public class EnhancedCalculator {

    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        boolean exit = false;
        while (!exit) {
            showMainMenu();
            int choice = readIntInput();

            switch (choice) {
                case 1 -> basicArithmeticMenu();
                case 2 -> scientificCalculationsMenu();
                case 3 -> unitConversionsMenu();
                case 4 -> {
                    System.out.println("Exiting calculator. Goodbye!");
                    exit = true;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
        scanner.close();
    }

    private static void showMainMenu() {
        System.out.println("\n--- Enhanced Calculator ---");
        System.out.println("1. Basic Arithmetic Operations");
        System.out.println("2. Scientific Calculations");
        System.out.println("3. Unit Conversions");
        System.out.println("4. Exit");
        System.out.print("Choose an option: ");
    }

    private static void basicArithmeticMenu() {
        System.out.println("\n-- Basic Arithmetic --");
        System.out.println("1. Addition (+)");
        System.out.println("2. Subtraction (-)");
        System.out.println("3. Multiplication (*)");
        System.out.println("4. Division (/)");
        System.out.print("Select operation: ");
        int op = readIntInput();

        System.out.print("Enter first number: ");
        double num1 = readDoubleInput();

        System.out.print("Enter second number: ");
        double num2 = readDoubleInput();

        switch (op) {
            case 1 -> System.out.println("Result: " + (num1 + num2));
            case 2 -> System.out.println("Result: " + (num1 - num2));
            case 3 -> System.out.println("Result: " + (num1 * num2));
            case 4 -> {
                if (num2 == 0) {
                    System.out.println("Error: Division by zero not allowed!");
                } else {
                    System.out.println("Result: " + (num1 / num2));
                }
            }
            default -> System.out.println("Invalid operation selected.");
        }
    }

    private static void scientificCalculationsMenu() {
        System.out.println("\n-- Scientific Calculations --");
        System.out.println("1. Square Root");
        System.out.println("2. Exponentiation (Power)");
        System.out.print("Select operation: ");
        int choice = readIntInput();

        switch (choice) {
            case 1 -> {
                System.out.print("Enter number (>= 0): ");
                double num = readDoubleInput();
                if (num < 0) {
                    System.out.println("Error: Cannot calculate square root of negative number.");
                } else {
                    System.out.println("Result: " + Math.sqrt(num));
                }
            }
            case 2 -> {
                System.out.print("Enter base: ");
                double base = readDoubleInput();
                System.out.print("Enter exponent: ");
                double exponent = readDoubleInput();
                System.out.println("Result: " + Math.pow(base, exponent));
            }
            default -> System.out.println("Invalid choice.");
        }
    }

    private static void unitConversionsMenu() {
        System.out.println("\n-- Unit Conversions --");
        System.out.println("1. Temperature Conversion");
        System.out.println("2. Currency Conversion");
        System.out.print("Select conversion type: ");
        int choice = readIntInput();

        switch (choice) {
            case 1 -> temperatureConversionMenu();
            case 2 -> currencyConversionMenu();
            default -> System.out.println("Invalid choice.");
        }
    }

    private static void temperatureConversionMenu() {
        System.out.println("\nTemperature Conversion:");
        System.out.println("1. Celsius to Fahrenheit");
        System.out.println("2. Fahrenheit to Celsius");
        System.out.print("Choose option: ");
        int choice = readIntInput();

        switch (choice) {
            case 1 -> {
                System.out.print("Enter temperature in Celsius: ");
                double celsius = readDoubleInput();
                double fahrenheit = (celsius * 9 / 5) + 32;
                System.out.println(celsius + " °C = " + fahrenheit + " °F");
            }
            case 2 -> {
                System.out.print("Enter temperature in Fahrenheit: ");
                double fahrenheit = readDoubleInput();
                double celsius = (fahrenheit - 32) * 5 / 9;
                System.out.println(fahrenheit + " °F = " + celsius + " °C");
            }
            default -> System.out.println("Invalid choice.");
        }
    }

    private static void currencyConversionMenu() {
        System.out.println("\nCurrency Conversion:");
        System.out.println("1. USD to INR");
        System.out.println("2. INR to USD");
        System.out.println("3. EUR to USD");
        System.out.println("4. USD to EUR");
        System.out.print("Choose option: ");
        int choice = readIntInput();

        // Sample fixed rates (these can be updated)
        final double usdToInrRate = 83.5;
        final double eurToUsdRate = 1.1;

        switch (choice) {
            case 1 -> {
                System.out.print("Enter amount in USD: ");
                double usd = readDoubleInput();
                double inr = usd * usdToInrRate;
                System.out.println("$" + usd + " = ₹" + String.format("%.2f", inr));
            }
            case 2 -> {
                System.out.print("Enter amount in INR: ");
                double inr = readDoubleInput();
                double usd = inr / usdToInrRate;
                System.out.println("₹" + inr + " = $" + String.format("%.2f", usd));
            }
            case 3 -> {
                System.out.print("Enter amount in EUR: ");
                double eur = readDoubleInput();
                double usd = eur * eurToUsdRate;
                System.out.println("€" + eur + " = $" + String.format("%.2f", usd));
            }
            case 4 -> {
                System.out.print("Enter amount in USD: ");
                double usd = readDoubleInput();
                double eur = usd / eurToUsdRate;
                System.out.println("$" + usd + " = €" + String.format("%.2f", eur));
            }
            default -> System.out.println("Invalid choice.");
        }
    }

    // Utility methods for input
    private static int readIntInput() {
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Enter a valid integer: ");
            }
        }
    }

    private static double readDoubleInput() {
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Enter a valid number: ");
            }
        }
    }
}
