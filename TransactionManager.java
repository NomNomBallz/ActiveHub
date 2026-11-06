import java.util.*;
import java.io.*;
import java.time.*;
import java.time.temporal.ChronoUnit;

public class TransactionManager {
    ArrayList<TransactionRental> transactionList;
    private int nextID;
    private BookingManager bookingManager; // kept so we can look up the booking a transaction came from

    public TransactionManager(BookingManager bookingManager, RentalCatalogue rentalCatalogue) {
        this.transactionList = new ArrayList<TransactionRental>();
        this.nextID = 1; // default value for ID
        this.bookingManager = bookingManager;
        loadTransactions(bookingManager, rentalCatalogue);

    }

    // use constructor to create a TransactionRental object using booking class
    public TransactionRental createTransactionFromBooking(Booking booking, RentalCatalogue rentalCatalogue) {
        if (booking == null)
            return null;

        String id = generateID();
        TransactionRental transaction = new TransactionRental(id, booking);
        if (booking.getFacilityType() != null) {
            RentalItem facilityItem = rentalCatalogue.findItemByCode(booking.getFacilityType());
            if (facilityItem != null) {
                long minutes = ChronoUnit.MINUTES.between(booking.getStartTime(), booking.getEndTime()); // get the time
                                                                                                         // difference
                                                                                                         // in minutes
                int timeDiff = (int) Math.ceil(minutes / 60.0); // round up the minutes to the highest hour
                transaction.addItems(facilityItem, timeDiff);
            }
        }

        addTransaction(transaction);

        return transaction;
    }

    public TransactionRental createTransactionFromWalkIn(String name, String phoneNum, int participants) {
        String id = generateID();

        TransactionRental transaction = new TransactionRental(id, name, phoneNum, participants);
        addTransaction(transaction);

        return transaction;
    }

    // get transactions in memory
    public ArrayList<TransactionRental> getAllTransactions() {
        return transactionList;
    }

    // generate incrementing ID
    public String generateID() {
        return String.format("TRN%03d", nextID++);
    }

    // add a transaction to the arraylist
    public void addTransaction(TransactionRental trn) {
        if (trn == null)
            return;
        transactionList.add(trn);
        saveTransactions();
    }

    // find a transaction by its ID
    public TransactionRental findTransactionByID(String id) {
        if (id == null)
            return null;
        for (TransactionRental transaction : transactionList) {
            if (transaction.getTransactionID().equalsIgnoreCase(id.trim())) {
                return transaction;
            }
        }
        return null;

    }

    //find a transaction by its booking ID
    public TransactionRental findTransactionByBookingID(String id){
        if (id == null) return null;
        for (TransactionRental transaction : transactionList){
            if (id.trim().equalsIgnoreCase(transaction.getBookingID())){
                return transaction;
            }
        }
        return null;
    }

    // find all transactions that a customer made and add them to a single array
    // list
    public ArrayList<TransactionRental> findTransactionsByCustName(String name) {
        ArrayList<TransactionRental> results = new ArrayList<TransactionRental>();

        if (name == null)
            return results;

        for (TransactionRental transaction : transactionList) {
            if (transaction.getCustomerName().equalsIgnoreCase(name.trim())) {
                results.add(transaction);
            }
        }

        return results;
    }

    // if a transaction was converted from a booking
    // this method takes that transaction and returns the booking it was converted
    // from
    public Booking getLinkedBooking(TransactionRental transaction) {
        if (transaction == null || transaction.getBookingID() == null || bookingManager == null)
            return null;
        return bookingManager.findBookingByID(transaction.getBookingID());
    }

    // re-check all promotions against the transaction's current items, apply the
    // best one, then save everything to the file
    public void recalculateAndSave(TransactionRental transaction) {
        if (transaction == null)
            return;
        PromotionManager.applyBestPromotion(transaction, getLinkedBooking(transaction));
        saveTransactions();
    }

    // print a summary table of every transaction so staff can find the ID they need
    public void displayAllTransactions() {
        if (transactionList.isEmpty()) {
            System.out.println("No transactions recorded yet.");
            return;
        }

        System.out.println("\n----------------------------------------------------------------------");
        System.out.printf("%-8s %-20s %-10s %-9s %6s %14s%n", "ID", "Customer", "Type", "Status", "Items",
                "Payable (RM)");
        System.out.println("----------------------------------------------------------------------");
        for (TransactionRental t : transactionList) {
            System.out.printf("%-8s %-20s %-10s %-9s %6d %14.2f%n",
                    t.getTransactionID(),
                    t.getCustomerName(),
                    (t.getBookingID() != null ? "Booking" : "Walk-In"),
                    t.getPaymentStatus(),
                    t.getItems().size(),
                    t.calcFinalAmount());
        }
        System.out.println("----------------------------------------------------------------------\n");
    }

    //get every transaction that is still waiting for payment (not paid yet and has at least one item)
    public ArrayList<TransactionRental> getPayableTransactions(){
        ArrayList<TransactionRental> results = new ArrayList<TransactionRental>();
        for (TransactionRental transaction : transactionList){
            if (transaction.canBePaid()){
                results.add(transaction);
            }
        }
        return results;
    }

    //print a summary table of transactions that are pending payment, then return how many there are
    public int displayPayableTransaction(){
        ArrayList<TransactionRental> payable = getPayableTransactions();

        if (payable.isEmpty()){
            System.out.println("No transactions are waiting for payment.");
            return 0;
        }
 
        System.out.println("\n----------------------------------------------------------------------");
        System.out.printf("%-8s %-20s %-10s %-9s %6s %14s%n", "ID", "Customer", "Type", "Status", "Items", "Payable (RM)");
        System.out.println("----------------------------------------------------------------------");
        for (TransactionRental t : payable){
            System.out.printf("%-8s %-20s %-10s %-9s %6d %14.2f%n",
                    t.getTransactionID(),
                    t.getCustomerName(),
                    (t.getBookingID() != null ? "Booking" : "Walk-In"),
                    t.getPaymentStatus(),
                    t.getItems().size(),
                    t.calcFinalAmount());
        }
        System.out.println("----------------------------------------------------------------------\n");
 
        return payable.size();
    }

    //record a full payment on a transaction and save it to the file, returns false if the transaction cannot be paid
    public boolean completePayment(TransactionRental transaction, String method){
        if (transaction == null) return false;
 
        boolean paid = transaction.markAsPaid(method, LocalDateTime.now());
        if (paid){
            saveTransactions();
        }
        return paid;
    }

    // write transactions in memory to transactions.txt
    public void saveTransactions() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("transactions.txt"))) {

            for (TransactionRental transaction : transactionList) {
                StringBuilder itemsStr = new StringBuilder();
                ArrayList<TransactionItem> items = transaction.getItems();

                for (int i = 0; i < items.size(); i++) {
                    TransactionItem ti = items.get(i);
                    itemsStr.append(ti.getItem().getCode()).append(":").append(ti.getQuantity());
                    if (i < items.size() - 1) {
                        itemsStr.append(";");
                    }
                }

                if (itemsStr.length() == 0) {
                    itemsStr.append("NONE");
                }
 
                writer.printf("%s|%s|%s|%s|%.2f|%s|%s|%s|%d|%s|%s|%s|%s%n",
                        transaction.getTransactionID(),
                        transaction.getCustomerName(),
                        transaction.getContactNumber(),
                        (transaction.getBookingID() != null ? transaction.getBookingID() : "NONE"),
                        transaction.getDiscountAmount(),
                        transaction.getPromotionName(),
                        transaction.getPaymentStatus(),
                        itemsStr.toString(),
                        transaction.getParticipants(),
                        transaction.getArrivalDate(),
                        transaction.getArrivalTime(),
                        transaction.getPaymentMethod(),
                        (transaction.getPaymentDateTime() != null ? transaction.getPaymentDateTime() : "NONE"));
            }

        } catch (IOException e) {
            System.out.println("An error occurred while trying to save data to transactions.txt: " + e.getMessage());
        }
    }

    public void loadTransactions(BookingManager bookingManager, RentalCatalogue rentalCatalogue){
        File file = new File("transactions.txt");
        if (!file.exists()) return;
 
        transactionList.clear();
        int maxID = 0;
 
        try (BufferedReader reader = new BufferedReader(new FileReader(file))){
            String line;
 
            while ((line = reader.readLine()) != null){
                line = line.trim();
                if (line.isEmpty()) continue;
 
                // TXN_ID|CUSTOMER_NAME|CONTACT_NO|BOOKING_ID|DISCOUNT_AMOUNT|PROMO_NAME|PAYMENT_STATUS|ITEMS_DATA|PARTICIPANTS|ARRIVAL_DATE|ARRIVAL_TIME|PAYMENT_METHOD|PAYMENT_DATETIME
                String[] parts = line.split("\\|"); 
                if (parts.length >= 7) {
                    String id = parts[0];
                    String name = parts[1];
                    String contact = parts[2];
                    String bookingId = parts[3].equalsIgnoreCase("NONE") ? null : parts[3];
                    double discount = Double.parseDouble(parts[4]);
                    String promoName = parts[5];
                    String status = parts[6];
                    //the columns after ITEMS_DATA were added later, so older lines without them still load with defaults
                    int participants = 1;
                    if (parts.length > 8){
                        try {
                            participants = Integer.parseInt(parts[8]);
                        } catch (NumberFormatException e){
                            participants = 1;
                        }
                    }
                    
                    TransactionRental transaction;

                    if (bookingId != null && bookingManager != null) {
                        Booking booking = bookingManager.findBookingByID(bookingId);

                        if (booking != null) {
                            transaction = new TransactionRental(id, booking);
                        } else {
                            transaction = new TransactionRental(id, name, contact, participants);
                        }
                    } else {
                        transaction = new TransactionRental(id, name, contact, participants);
                    }
 
                    transaction.setDiscountAmount(discount);
                    transaction.setPromotionName(promoName);
                    transaction.setPaymentStatus(status);
 
                    if (parts.length >= 11){
                        try {
                            transaction.setArrivalDate(LocalDate.parse(parts[9]));
                            transaction.setArrivalTime(LocalTime.parse(parts[10]));
                        } catch (java.time.format.DateTimeParseException e){
                            //damaged values, keep the defaults from the constructor
                        }
                    }
 
                    if (parts.length >= 13){
                        transaction.setPaymentMethod(parts[11]);
                        if (!parts[12].equalsIgnoreCase("NONE")){
                            try {
                                transaction.setPaymentDateTime(LocalDateTime.parse(parts[12]));
                            } catch (java.time.format.DateTimeParseException e){
                                //damaged value, the payment time stays empty
                            }
                        }
                    }
 
                    if (parts.length >= 8 && !parts[7].equalsIgnoreCase("NONE") && rentalCatalogue != null) {
                        String[] itemTokens = parts[7].split(";");
                        for (String token : itemTokens) {
                            String[] pair = token.split(":");
                            if (pair.length == 2) {
                                String code = pair[0];
                                int qty = Integer.parseInt(pair[1]);
                                RentalItem item = rentalCatalogue.findItemByCode(code);
                                if (item != null) {
                                    transaction.addItems(item, qty);
                                }
                            }
                        }
                    }
 
                    transactionList.add(transaction);
 
                    if (id.toUpperCase().startsWith("TRN")){
                        try {
                            int num = Integer.parseInt(id.substring(3));
                            if (num > maxID) {
                                maxID = num;
                            }
                        } catch (NumberFormatException ignored) {
                        }
                    }
                }
 
                
            }
 
            this.nextID = maxID + 1;

        } catch (IOException e){
            System.out.println("Error loading transactions from file: " + e.getMessage());
        }
    }   

}
