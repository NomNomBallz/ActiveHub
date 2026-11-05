import java.time.*;
import java.util.*;

public class main {
    static Scanner scanner = new Scanner(System.in);
    static RentalCatalogue rentalCatalogue = new RentalCatalogue();
    static BookingManager bookingManager = new BookingManager();
    static TransactionManager transactionManager = new TransactionManager(bookingManager, rentalCatalogue);

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
                
            int option = InputValidator.readInt(scanner, "Enter a choice! (1-7)");

            switch (option) {
                case 1 -> bookingModule();
                case 2 -> System.out.print(rentalCatalogue);
                case 3 -> createTransaction();
                case 4 -> {}
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

    //1. booking module
    private static void bookingModule(){
        boolean bookingLoop = true;
        while(bookingLoop){
            System.out.println("\n===== FACILITY BOOKING MODULE =====");
            System.out.println("1. Create New Booking");
            System.out.println("2. View All Bookings");
            System.out.println("3. Cancel Booking");
            System.out.println("4. Main Menu");
            
            int bookingChoice = InputValidator.readInt(scanner,"Select an option (1-4)");

            if (bookingChoice == 1){
                System.out.println("\n===== CREATE NEW BOOKING =====");
                String name = InputValidator.readString(scanner, "Enter customer name");
                String contact = InputValidator.readString(scanner, "Enter contact number");
                String facility = InputValidator.readString(scanner, "Enter choice of facility (e.g F002)");
                int participantqty = InputValidator.readInt(scanner, "Enter number of participants");
                LocalDate date = InputValidator.readDate(scanner, "Enter booking date (YYYY-MM-DD)");
                LocalTime start = InputValidator.readTime(scanner, "Enter start time (HH:mm)");
                LocalTime end = InputValidator.readTime(scanner, "Enter end time (HH:mm)");

                while(!end.isAfter(start)){
                    System.out.println("Error: End time must be after start time!");
                    end = InputValidator.readTime(scanner, "Enter end time (HH:mm)");
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

                String cancelID = InputValidator.readString(scanner, "Enter the ID of the booking you'd like to cancel (e.g. B002)");

                boolean bookingCancelled = bookingManager.cancelBooking(cancelID);

                if (bookingCancelled){
                    System.out.println("SUCCESS: Booking " + cancelID.toUpperCase() + " has been cancelled!");
                } else {
                    System.out.println("FAIL: Booking " + cancelID.toUpperCase() + " has not been found or is already cancelled.");
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

    //3. transaction rental module
    private static void createTransaction(){
        boolean rtLoop = true;
        while (rtLoop){
            System.out.println("\n===== RENTAL TRANSACTION MODULE =====");
            System.out.println("1. Create Transaction (From Existing Booking)");
            System.out.println("2. Create Transaction (Walk-in)");
            System.out.println("3. Edit Existing Transaction");
            System.out.println("4. Main Menu");

            int rtChoice = InputValidator.readInt(scanner, "Select an option (1-4)");
            
            switch(rtChoice){
                case 1 -> {
                    System.out.println("\n===== RENTAL TRANSACTION MODULE =====");

                    String bookingID = InputValidator.readString(scanner, "Please enter the booking ID (Type 'CANCEL' to abort)");

                    if (bookingID.equalsIgnoreCase("CANCEL")){
                        System.out.println("Transaction aborted!");
                        return;
                    }  

                    Booking linkedBooking = bookingManager.findBookingByID(bookingID);
                    if (linkedBooking == null){
                        System.out.println("Error: Booking ID '" + bookingID + "'not found!");
                        return;
                    }

                    TransactionRental transaction = transactionManager.createTransactionFromBooking(linkedBooking, rentalCatalogue);

                    System.out.println("Successfully linked to Booking: " + linkedBooking.getBookingID());
                    addItemsToCart(transaction);
                    printTransaction(transaction, linkedBooking, transactionManager);
                }
                case 2 -> {}
                case 3 -> {}
                case 4 -> {
                    System.out.println("Exiting to main menu!");
                    rtLoop = false;
                }
                default -> {
                    System.out.println("Invalid! Returning to main menu.");
                    rtLoop = false;
                }
            }
        }
    }

    //some helper methods for 3. rental transaction module
    private static void addItemsToCart(TransactionRental transaction){
        boolean addingItem = true;
        while(addingItem){
            System.out.print(rentalCatalogue);
            String itemCode = InputValidator.readString(scanner, "Enter item code to add/update (type 'DONE' to finish)");

            if (itemCode.equalsIgnoreCase("DONE")){
                addingItem = false;
            } else {
                RentalItem newItem = rentalCatalogue.findItemByCode(itemCode);
                if (newItem == null){
                    System.out.println("Invalid item code! Please try again.");
                } else {
                    String qtyPrompt = newItem.getCategory().equalsIgnoreCase("FACILITY") ? "Enter duration in hours" : "Enter item quantity";
                    int qty = InputValidator.readInt(scanner, qtyPrompt);
                    transaction.addItems(newItem, qty);
                    System.out.println("Updated: " + newItem.getName() + "(Qty/Hrs: " + qty + ") ");
                }
            }
        }
    }  

    //another helper method for rental transaction module
    private static void printTransaction(TransactionRental transaction, Booking booking, TransactionManager transactionManager){
        PromotionManager.applyBestPromotion(transaction, booking);

        System.out.println("\nTransaction saved successfully!");

        transaction.printReceipt();
    }



    

}
