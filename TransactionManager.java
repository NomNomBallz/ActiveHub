import java.util.*;
import java.io.*;
import java.time.temporal.ChronoUnit;

public class TransactionManager {
    ArrayList<TransactionRental> transactionList;
    private int nextID;

    public TransactionManager(BookingManager bookingManager, RentalCatalogue rentalCatalogue){
        this.transactionList = new ArrayList<TransactionRental>();
        this.nextID = 1; //default value for ID
        loadTransactions(bookingManager, rentalCatalogue);
    }


    //use constructor to create a TransactionRental object using booking class
    public TransactionRental createTransactionFromBooking(Booking booking, RentalCatalogue rentalCatalogue){
        if (booking == null) return null;

        String id = generateID();
        TransactionRental transaction = new TransactionRental(id, booking);
        if (booking.getFacilityType() != null){
            RentalItem facilityItem = rentalCatalogue.findItemByCode(booking.getFacilityType());
            if (facilityItem != null){
                int timeDiff =  (int) ChronoUnit.HOURS.between(booking.getStartTime(), booking.getEndTime());
                transaction.addItems(facilityItem, timeDiff);
            }
        }

        addTransaction(transaction);

        return transaction;
    }

    public TransactionRental createTransactionFromWalkIn(String name, String phoneNum){
        String id = generateID();

        TransactionRental transaction = new TransactionRental(id, name, phoneNum);
        addTransaction(transaction);

        return transaction;
    }

    //get transactions in memory
    public ArrayList<TransactionRental> getAllTransactions(){
        return transactionList;
    }

    //generate incrementing ID
    public String generateID(){
        return String.format("TRN%03d", nextID++);
    }

    //add a transaction to the arraylist
    public void addTransaction(TransactionRental trn){
        if (trn == null) return;
        transactionList.add(trn);
        saveTransactions();
    }

    //find a transaction by its ID
    public TransactionRental findTransactionByID(String id){
        if (id == null) return null;
        for (TransactionRental transaction : transactionList){
            if (transaction.getTransactionID().equalsIgnoreCase(id.trim())){
                return transaction;
            }
        }
        return null;

    }

    //find all transactions that a customer made and add them to a single array list
    public ArrayList<TransactionRental> findTransactionsByCustName(String name){
        ArrayList<TransactionRental> results = new ArrayList<TransactionRental>();

        if (name == null) return results;

        for (TransactionRental transaction : transactionList){
            if (transaction.getCustomerName().equalsIgnoreCase(name.trim())){
                results.add(transaction);
            }
        }

        return results;
    }

    public void saveTransactions(){
        try (PrintWriter writer = new PrintWriter(new FileWriter("ActiveHub/transactions.txt"))){


            for (TransactionRental transaction : transactionList){
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

                writer.printf("%s|%s|%s|%s|%.2f|%s|%s|%s%n",
                        transaction.getTransactionID(),
                        transaction.getCustomerName(),
                        transaction.getContactNumber(),
                        (transaction.getBookingID() != null ? transaction.getBookingID() : "NONE"),
                        transaction.getDiscountAmount(),
                        transaction.getPromotionName(),
                        transaction.getPaymentStatus(),
                        itemsStr.toString());
            }

        } catch (IOException e){
            System.out.println("An error occurred while trying to save data to transactions.txt: " + e.getMessage());
        }
    }

    public void loadTransactions(BookingManager bookingManager, RentalCatalogue rentalCatalogue){
        File file = new File("ActiveHub/transactions.txt");
        if (!file.exists()) return;

        transactionList.clear();
        int maxID = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))){
            String line;

            while ((line = reader.readLine()) != null){
                line = line.trim();
                if (line.isEmpty()) continue;

                // TXN_ID|CUSTOMER_NAME|CONTACT_NO|BOOKING_ID|DISCOUNT_AMOUNT|PROMO_NAME|PAYMENT_STATUS|ITEMS_DATA
                String[] parts = line.split("\\|"); 
                if (parts.length >= 7) {
                    String id = parts[0];
                    String name = parts[1];
                    String contact = parts[2];
                    String bookingId = parts[3].equalsIgnoreCase("NONE") ? null : parts[3];
                    double discount = Double.parseDouble(parts[4]);
                    String promoName = parts[5];
                    String status = parts[6];

                    TransactionRental transaction;

                    if (bookingId != null && bookingManager != null){
                        Booking booking = bookingManager.findBookingByID(bookingId);

                        if (booking != null) {
                            transaction = new TransactionRental(id, booking);
                        } else {
                            transaction = new TransactionRental(id, name, contact);
                        }
                    } else {
                        transaction = new TransactionRental(id, name, contact);
                    }

                    transaction.setDiscountAmount(discount);
                    transaction.setPromotionName(promoName);
                    transaction.setPaymentStatus(status);

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
                            if (num > maxID){
                                maxID = num;
                            }
                        } catch (NumberFormatException ignored) {}
                    }
                }

                
            }

            this.nextID = maxID + 1;

        } catch (IOException e){
            System.out.println("Error loading transactions from file: " + e.getMessage());
        }
    }



}


