import java.util.Scanner;

public class EnhancedCalculator {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int choice;
        double a, b;

        do {
            System.out.println("\n===== Enhanced Calculator =====");
            System.out.println("1. Addition");
            System.out.println("2. Subtraction");
            System.out.println("3. Multiplication");
            System.out.println("4. Division");
            System.out.println("5. Power (a^b)");
            System.out.println("6. Square Root");
            System.out.println("7. Exit");
            System.out.print("Choose an option: ");
            choice = sc.nextInt();

            switch (choice) {
                case 1:
                    System.out.print("Enter two numbers: ");
                    a = sc.nextDouble();
                    b = sc.nextDouble();
                    System.out.println("Result: " + (a + b));
                    break;
                case 2:
                    System.out.print("Enter two numbers: ");
                    a = sc.nextDouble();
                    b = sc.nextDouble();
                    System.out.println("Result: " + (a - b));
                    break;
                case 3:
                    System.out.print("Enter two numbers: ");
                    a = sc.nextDouble();
                    b = sc.nextDouble();
                    System.out.println("Result: " + (a * b));
                    break;
                case 4:
                    System.out.print("Enter two numbers: ");
                    a = sc.nextDouble();
                    b = sc.nextDouble();
                    if (b != 0) {
                        System.out.println("Result: " + (a / b));
                    } else {
                        System.out.println("Error: Cannot divide by zero.");
                    }
                    break;
                case 5:
                    System.out.print("Enter base and exponent: ");
                    a = sc.nextDouble();
                    b = sc.nextDouble();
                    System.out.println("Result: " + Math.pow(a, b));
                    break;
                case 6:
                    System.out.print("Enter number: ");
                    a = sc.nextDouble();
                    if (a >= 0) {
                        System.out.println("Result: " + Math.sqrt(a));
                    } else {
                        System.out.println("Error: Cannot take square root of negative number.");
                    }
                    break;
                case 7:
                    System.out.println("Exiting Calculator. Thank you!");
                    break;
                default:
                    System.out.println("Invalid option. Try again.");
            }

        } while (choice != 7);

        sc.close();
    }
}
