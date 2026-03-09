import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class ProductManager {

    public static void main(String[] args) {
        ArrayList<Product> products = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\n--- Product Management System ---");
            System.out.println("1. Add a product");
            System.out.println("2. Sort products by name");
            System.out.println("3. Display products");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");

            choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            switch (choice) {
                case 1:
                    System.out.print("Enter product name: ");
                    String name = scanner.nextLine();
                    System.out.print("Enter product price: ");
                    double price = scanner.nextDouble();
                    scanner.nextLine(); // Consume the newline character
                    products.add(new Product(name, price));
                    System.out.println("Product added successfully.");
                    break;
                case 2:
                    if (products.isEmpty()) {
                        System.out.println("No products to sort.");
                    } else {
                        Collections.sort(products);
                        System.out.println("Products have been sorted by name.");
                    }
                    break;
                case 3:
                    if (products.isEmpty()) {
                        System.out.println("No products to display.");
                    } else {
                        System.out.println("--- List of Products ---");
                        for (Product product : products) {
                            System.out.println(product);
                        }
                    }
                    break;
                case 4:
                    System.out.println("Exiting the program.");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 4);

        scanner.close();
    }
}
