import java.util.Scanner;
import java.time.*;

public class main {
        public static void main(String[] args) {

        rentalCatalogue rentalCatalogue = new rentalCatalogue();
        int bookID;
        String cusName, contactNum;
    
        Scanner scanner = new Scanner(System.in);
        
        outer:
        do{
            System.out.print("===== TASTEHUB SYSTEM =====\r\n" + //
                            "1. Facility Booking Module\r\n" + //
                            "2. View Rental Catalogue\r\n" + //
                            "3. Create Rental Transaction\r\n" + //
                            "4. Apply Promotion\r\n" + //
                            "5. Make Payment\r\n" + //
                            "6. Daily Report\r\n" + //
                            "7. Exit\r\n\n");
            System.out.print("Please enter your option (1-7): ");
            String option = scanner.nextLine();
                
            switch(option){
                case "1" -> {
                    System.out.println("\n----- Facility Booking Module -----");
                    System.out.println("1. Create New Booking");
                    System.out.println("2. View All Bookings");
                    System.out.println("3. Go Back\n");
                    System.out.print("Please enter your option (1-3): ");
                    int optionFCB = scanner.nextInt();

                    switch(optionFCB){
                        case 1 -> {}
                        case 2 -> {}
                        case 3 -> {}
                        default -> System.out.println("\nInvalid option! Try again!\n");
                    }

                }
                case "2" -> System.out.print(rentalCatalogue);
                case "3" -> System.out.println("chose 3.");
                case "4" -> System.out.println("chose 4.");
                case "5" -> System.out.println("chose 5.");
                case "6" -> System.out.println("chose 6.");
                case "7" -> {
                    System.out.println("Exiting system!");
                    break outer;
                }

                default -> System.out.println("\nInvalid option! Try again!\n");

            }
            
        }while (true);
    }
}
