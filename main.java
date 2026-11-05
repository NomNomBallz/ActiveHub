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

            System.out.print("\n===== ACTIVEHUB SYSTEM =====\r\n" + //
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
                        //2. rental catalogue
                case 2 -> System.out.print(rentalCatalogue);
                case 3 -> createTransaction();
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
                
                //facility must be a real and valid code, otherwise later transaction is unable to find the proper facility
                String facility = InputValidator.readString(scanner, "Enter choice of facility (e.g FC002)");
                while (!rentalCatalogue.checkFacilityCode(facility)){
                    System.out.println("Error: '" + facility + "' is not a valid facility code! Please check the catalogue.");
                    facility = InputValidator.readString(scanner, "Enter facility code (e.g. FC001 or FC002)");
                }

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
                    System.out.println("\n===== NEW TRANSACTION (BOOKING) =====");

                    String bookingID = InputValidator.readString(scanner, "Please enter the booking ID (Type 'CANCEL' to abort)");

                    if (bookingID.equalsIgnoreCase("CANCEL")){ //if user inputs CANCEL, abort the transaction
                        System.out.println("Transaction aborted!");
                        return;
                    }  

                    Booking linkedBooking = bookingManager.findBookingByID(bookingID);
                    if (linkedBooking == null){ //if a booking does not exist, abort the transaction
                        System.out.println("Error: Booking ID '" + bookingID + "'not found!");
                        return;
                    }

                    if (linkedBooking.getStatus().equalsIgnoreCase("CANCELLED")){ //if a booking has already been cancelled, abort transaction
                        System.out.println("Error: Booking " + linkedBooking.getBookingID() + " is a cancelled booking!");
                        return;
                    }

                    //if the booking has already been converted into a transaction, return an error
                    TransactionRental existing = transactionManager.findTransactionByID(linkedBooking.getBookingID());
                    if (existing != null){
                        System.out.println("Error: Booking " + linkedBooking.getBookingID() + " already has transaction " + existing.getTransactionID() + ".");
                        System.out.println("Use 'Edit Existing Transaction' to add more items to it.");
                        return;
                    }

                    //create the booking if pass all checks
                    TransactionRental transaction = transactionManager.createTransactionFromBooking(linkedBooking, rentalCatalogue);

                    System.out.println("Successfully linked to Booking: " + linkedBooking.getBookingID());
                    addItemsToCart(transaction);
                    finaliseTransaction(transaction);
                }
                case 2 -> {
                    System.out.println("\n===== NEW TRANSACTION (WALK-IN) =====");

                    //enter customer name, can enter CANCEL to abort if this was an accidental prompt
                    String name = InputValidator.readString(scanner, "Enter customer name (Type 'CANCEL' to abort)");
                    if (name.equalsIgnoreCase("CANCEL")){
                        System.out.println("Transaction aborted!");
                        return;
                    }

                    String contact = InputValidator.readString(scanner, "Enter contact number");
                    int participants = InputValidator.readInt(scanner, "Enter number of participants");
                    
                    TransactionRental newTransaction = transactionManager.createTransactionFromWalkIn(name, contact, participants);

                    System.out.println("Successfully created walk-in transaction: " + newTransaction.getTransactionID());
                    addItemsToCart(newTransaction);
                    finaliseTransaction(newTransaction);

                }
                case 3 -> editTransaction();
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

    //a helper method for rental transaction module (belongs to 3.)
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

    //another helper method for rental transaction module (belongs to 3.)
    //applies the best promotion, saves, shows promotion breakdown then the bill.
    private static void finaliseTransaction(TransactionRental transaction){
        transactionManager.recalculateAndSave(transaction);

        PromotionManager.displayPromotionInfo(transaction, transactionManager.getLinkedBooking(transaction));
        transaction.printReceipt();

        System.out.println("Transaction " + transaction.getTransactionID() + " saved successfully!");
    }

    //edit an existing transaction (belongs to 3.)
    private static void editTransaction(){
        System.out.println("\n===== EDIT EXISTING TRANSACTION =====");

        //initial prompt, user can choose to CANCEL or LIST all transactions before proceeding
        TransactionRental transaction = null;
        while (transaction == null){
           String id = InputValidator.readString(scanner, "Enter transaction ID (e.g. TRN001) - type 'LIST' to view all, 'CANCEL' to abort");
 
            if (id.equalsIgnoreCase("CANCEL")){
                System.out.println("Edit aborted!");
                return;
            }
            if (id.equalsIgnoreCase("LIST")){
                transactionManager.displayAllTransactions();
                continue;
            }
 
            transaction = transactionManager.findTransactionByID(id);
            if (transaction == null){
                System.out.println("Error: Transaction ID '" + id + "' not found!");
            }
        }

        //if the transaction has already been paid, kenot edit anymore
        if (transaction.isPaid()){
            System.out.println("Error: Transaction " + transaction.getTransactionID() + " has already been PAID and can't be edited anymore!");
            return;
        }

        //edit the transaction
        boolean editing = true;
        while (editing){
            transaction.printReceipt();
            System.out.println("1. Add more items (adds on top of existing quantity)");
            System.out.println("2. Change an item's quantity");
            System.out.println("3. Remove an item");
            System.out.println("4. Change number of participants (walk-in only)");
            System.out.println("5. Finish editing");

            int editChoice = InputValidator.readInt(scanner, "Select an option (1-5)");

            //after every change the promotions are checked again and the file is updated
            switch (editChoice){
                case 1 -> {
                    addItemsToCart(transaction);
                    transactionManager.recalculateAndSave(transaction);
                }

                case 2 -> {
                    String code = InputValidator.readString(scanner, "Enter the item code to change (type 'CANCEL' to abort)");
                    
                    if (code.equalsIgnoreCase("CANCEL")){
                        return;
                    }
            
                    TransactionItem target = transaction.findItem(code);
                    if (target == null){
                        System.out.println("Error: '" + code + "' is not in this transaction!");
                        return;
                    }
            
                    String qtyPrompt = target.getItem().getCategory().equalsIgnoreCase("FACILITY") ? "Enter new duration in hours" : "Enter new quantity";
                    int qty = InputValidator.readInt(scanner, qtyPrompt);
            
                    if (transaction.updateItemQuantity(code, qty)){
                        System.out.println("Updated: " + target.getItem().getName() + " is now " + qty + ".");
                    } else {
                        System.out.println("Error: Quantity must be greater than 0! Use 'Remove an item' to delete it.");
                    }
                    transactionManager.recalculateAndSave(transaction);
                }

                case 3 -> {
                    String code = InputValidator.readString(scanner, "Enter the item code to remove (type 'CANCEL' to abort)");
                    
                    if (code.equalsIgnoreCase("CANCEL")){
                        return;
                    }
            
                    if (transaction.removeItem(code)){
                        System.out.println("Removed item " + code.toUpperCase() + " from the transaction.");
                    } else {
                        System.out.println("Error: '" + code + "' is not in this transaction!");
                    }

                    transactionManager.recalculateAndSave(transaction);
                }
                case 4 -> {
                    //no point asking if it is a booking-based transaction, the number comes from the booking
                    if (transaction.getBookingID() != null){
                        System.out.println("Error: The number of participants comes from booking " + transaction.getBookingID() + " and cannot be changed here.");
                        return;
                    }
            
                    int participants = InputValidator.readInt(scanner, "Enter new number of participants (currently " + transaction.getParticipants() + ")");
            
                    if (transaction.updateParticipants(participants)){
                        System.out.println("Updated: participants is now " + participants + ".");
                    } else {
                        System.out.println("Error: Unable to change the number of participants!");
                    }
                    transactionManager.recalculateAndSave(transaction);
                }

                case 5 -> editing = false;
                default -> System.out.println("Invalid option! Please try again.");
            }

        }

        finaliseTransaction(transaction);
    }

    private static void applyPromotion(){
        System.out.println("\n===== PROMOTION CHECKER =====");
        boolean promotionLoop = true;
        while(promotionLoop){
            String id = InputValidator.readString(scanner, "Enter the transaction ID for the transaction you want to check (type 'CANCEL' to abort)");
            
            if (id.equalsIgnoreCase("CANCEL")){
                return;
            }
            
            TransactionRental transaction = transactionManager.findTransactionByID(id);

            if (transaction != null){
                PromotionManager.displayPromotionInfo(transaction, transactionManager.getLinkedBooking(transaction));
            } else {
                System.out.println("\nInvalid transaction ID!");
            }
        }
        

    }

}
