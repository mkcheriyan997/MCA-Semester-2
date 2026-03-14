import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class AttendeeManager {

    public static void main(String[] args) {
        Map<Integer, Attendee> attendees = new HashMap<>();
        Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\n--- Attendee Management System ---");
            System.out.println("1. Add a new attendee");
            System.out.println("2. Display all attendees");
            System.out.println("3. Search attendee by Registration ID");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");

            choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            switch (choice) {
                case 1:
                    System.out.print("Enter Registration ID: ");
                    int regId = scanner.nextInt();
                    scanner.nextLine(); // Consume the newline character

                    if (attendees.containsKey(regId)) {
                        System.out.println("Error: An attendee with Registration ID " + regId + " already exists.");
                    } else {
                        System.out.print("Enter Attendee Name: ");
                        String name = scanner.nextLine();
                        System.out.print("Enter Attendee Email: ");
                        String email = scanner.nextLine();

                        Attendee newAttendee = new Attendee(name, email, regId);
                        attendees.put(regId, newAttendee);
                        System.out.println("Attendee added successfully.");
                    }
                    break;
                case 2:
                    if (attendees.isEmpty()) {
                        System.out.println("No attendees to display.");
                    } else {
                        System.out.println("--- List of All Attendees ---");
                        for (Attendee attendee : attendees.values()) {
                            System.out.println(attendee);
                        }
                    }
                    break;
                case 3:
                    System.out.print("Enter Registration ID to search: ");
                    int searchId = scanner.nextInt();
                    scanner.nextLine(); // Consume the newline character

                    Attendee foundAttendee = attendees.get(searchId);
                    if (foundAttendee != null) {
                        System.out.println("Attendee found: " + foundAttendee);
                    } else {
                        System.out.println("Attendee with Registration ID " + searchId + " not found.");
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
