import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class NameManager {

    public static void main(String[] args) {
        ArrayList<String> names = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\n--- Menu ---");
            System.out.println("1. Add a name");
            System.out.println("2. Remove a name");
            System.out.println("3. Search for a name");
            System.out.println("4. Sort names");
            System.out.println("5. Display names");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");

            choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            switch (choice) {
                case 1:
                    System.out.print("Enter a name to add: ");
                    String nameToAdd = scanner.nextLine();
                    names.add(nameToAdd);
                    System.out.println("'" + nameToAdd + "' has been added.");
                    break;
                case 2:
                    System.out.print("Enter a name to remove: ");
                    String nameToRemove = scanner.nextLine();
                    if (names.remove(nameToRemove)) {
                        System.out.println("'" + nameToRemove + "' has been removed.");
                    } else {
                        System.out.println("'" + nameToRemove + "' not found in the list.");
                    }
                    break;
                case 3:
                    System.out.print("Enter a name to search for: ");
                    String nameToSearch = scanner.nextLine();
                    if (names.contains(nameToSearch)) {
                        System.out.println("'" + nameToSearch + "' found in the list.");
                    } else {
                        System.out.println("'" + nameToSearch + "' not found in the list.");
                    }
                    break;
                case 4:
                    Collections.sort(names);
                    System.out.println("Names have been sorted alphabetically.");
                    break;
                case 5:
                    if (names.isEmpty()) {
                        System.out.println("The list is empty.");
                    } else {
                        System.out.println("Names in the list:");
                        for (String name : names) {
                            System.out.println("- " + name);
                        }
                    }
                    break;
                case 6:
                    System.out.println("Exiting the program.");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 6);

        scanner.close();
    }
}
