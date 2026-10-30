import java.time.*;
import java.util.*;

public class main {
    static Scanner scanner = new Scanner(System.in);
    static rentalCatalogue rentalCatalogue = new rentalCatalogue();
    static bookingManager bookingManager = new bookingManager();


    public static void main(String[] args) {
        
        boolean loop = true;

        do{

            System.out.print("\n===== TASTEHUB SYSTEM =====\r\n" + //
                    "1. Facility Booking Module\r\n" + //
                    "2. View Rental Catalogue\r\n" + //
                    "3. Create Rental Transaction\r\n" + //
                    "4. Apply Promotion\r\n" + //
                    "5. Make Payment\r\n" + //
                    "6. Daily Report\r\n" + //
                    "7. Exit\r\n");
                
            int option = inputValidator.readInt(scanner, "Enter a choice! (1-7)");

            switch (option) {
                case 1 -> bookingModule();
                case 2 -> System.out.print(rentalCatalogue);
                case 3 -> System.out.println("chose 3.");
                case 4 -> applyPromotion();
                case 5 -> System.out.println("chose 5.");
                case 6 -> System.out.println("chose 6.");
                case 7 -> {
                    System.out.println("Exiting program!");
                    loop = false;
                }
                default -> System.out.println("Invalid option!");

            }
        }while(loop);

    }

    public static void bookingModule(){
        boolean bookingLoop = true;
        while(bookingLoop){
            System.out.println("\n===== FACILITY BOOKING MODULE =====");
            System.out.println("1. Create New Booking");
            System.out.println("2. View All Bookings");
            System.out.println("3. Cancel Booking");
            System.out.println("4. Exit");
            
            int bookingChoice = inputValidator.readInt(scanner,"Select an option (1-4)");

            if (bookingChoice == 1){
                System.out.println("\n===== CREATE NEW BOOKING =====");
                String name = inputValidator.readString(scanner, "Enter customer name");
                String contact = inputValidator.readString(scanner, "Enter contact number");
                String facility = inputValidator.readString(scanner, "Enter choice of facility (e.g F002)");
                int participantqty = inputValidator.readInt(scanner, "Enter number of participants");
                LocalDate date = inputValidator.readDate(scanner, "Enter booking date (YYYY-MM-DD)");
                LocalTime start = inputValidator.readTime(scanner, "Enter start time (HH:mm)");
                LocalTime end = inputValidator.readTime(scanner, "Enter end time (HH:mm)");

                while(!end.isAfter(start)){
                    System.out.println("Error: End time must be after start time!");
                    end = inputValidator.readTime(scanner, "Enter end time (HH:mm)");
                }
                
                boolean bookingCreate = bookingManager.createBooking(name, contact, facility, participantqty, date, start, end);

                if (bookingCreate){
                    System.out.println("SUCCESS: Booking successfully created!");
                } else {
                    System.out.println("FAIL: " + facility + " is already taken at that time!");
                }

            }else if (bookingChoice == 2){
                System.out.println("\n===== VIEW ALL BOOKINGS =====");
                bookingManager.displayAllBookings();

            } else if (bookingChoice == 3){
                System.out.println("\n===== CANCEL BOOKING =====");

                String cancelID = inputValidator.readString(scanner, "Enter the ID of the booking you'd like to cancel (e.g. B002)");

                boolean bookingCancelled = bookingManager.cancelBooking(cancelID);

                if (bookingCancelled){
                    System.out.println("SUCCESS: Booking " + cancelID.toUpperCase() + "has been cancelled!");
                } else {
                    System.out.println("FAIL: Booking " + cancelID.toUpperCase() + "has not been found or is already cancelled.");
                }

                
            } else if (bookingChoice == 4) {
                System.out.println("Returning to main menu!");
                bookingLoop = false;
            } else {
                System.out.println("Invalid! Returning to main menu.");
                bookingLoop = false;
            }
        }
        

    }

    static void applyPromotion() {
        Promotion[] promotions = {
                // TEMPORARY
                // hard coded values for now
                new PromotionA(40, 9),
                new PromotionB(40, 9),
                new PromotionC(40, 9)
        };

        Promotion best = PromotionManager.chooseBest(promotions);
        PromotionManager.displayPromotionInfo(promotions, best);

    }

    

}
