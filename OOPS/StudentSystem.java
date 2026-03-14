import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class StudentSystem {

    public static void main(String[] args) {
        Map<Integer, String> studentMap = new HashMap<>();
        Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\n--- Student Information System ---");
            System.out.println("1. Add a new student");
            System.out.println("2. Retrieve a student's name by ID");
            System.out.println("3. Display all students");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");

            choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            switch (choice) {
                case 1:
                    System.out.print("Enter student ID: ");
                    int id = scanner.nextInt();
                    scanner.nextLine(); // Consume the newline character
                    if (studentMap.containsKey(id)) {
                        System.out.println("Error: A student with this ID already exists.");
                    } else {
                        System.out.print("Enter student name: ");
                        String name = scanner.nextLine();
                        studentMap.put(id, name);
                        System.out.println("Student added successfully.");
                    }
                    break;
                case 2:
                    System.out.print("Enter student ID to retrieve name: ");
                    int retrieveId = scanner.nextInt();
                    scanner.nextLine(); // Consume the newline character
                    if (studentMap.containsKey(retrieveId)) {
                        String studentName = studentMap.get(retrieveId);
                        System.out.println("Student with ID " + retrieveId + " is: " + studentName);
                    } else {
                        System.out.println("Student with ID " + retrieveId + " not found.");
                    }
                    break;
                case 3:
                    if (studentMap.isEmpty()) {
                        System.out.println("No student information stored.");
                    } else {
                        System.out.println("--- List of All Students ---");
                        for (Map.Entry<Integer, String> entry : studentMap.entrySet()) {
                            System.out.println("ID: " + entry.getKey() + ", Name: " + entry.getValue());
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
