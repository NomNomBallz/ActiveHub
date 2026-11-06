import java.util.*;
import java.time.*;

public class TransactionRental {
    private String transactionID, bookingID, customerName, paymentStatus, appliedPromotionName, contactNumber;
    private ArrayList<TransactionItem> itemList;
    private double discountAmount;
    private int participants;
    private LocalDate arrivalDate;
    private LocalTime arrivalTime;
    private String paymentMethod;
    private LocalDateTime paymentDateTime;

    // overloading constructors, 1 for walk-in and 1 for booking

    // constructor for booking
    public TransactionRental(String id, Booking booking) {
        this.transactionID = id;
        this.customerName = booking.getName();
        this.bookingID = booking.getBookingID();
        this.itemList = new ArrayList<>();
        this.discountAmount = 0.0;
        this.appliedPromotionName = "None";
        this.paymentStatus = "PENDING";
        this.paymentMethod = "NONE";
        this.paymentDateTime = null;
        this.contactNumber = booking.getContactNo();
        this.participants = booking.getNumParticipants();
        this.arrivalDate = booking.getBookingDate();
        this.arrivalTime = booking.getStartTime();
    }

    // constructor for walk-ins
    public TransactionRental(String id, String name, String phoneNum, int participants) {
        this.transactionID = id;
        this.customerName = name;
        this.bookingID = null;
        this.itemList = new ArrayList<>();
        this.discountAmount = 0.0;
        this.appliedPromotionName = "None";
        this.paymentStatus = "PENDING";
        this.paymentMethod = "NONE";
        this.paymentDateTime = null;
        this.contactNumber = phoneNum;
        this.participants = participants;
        this.arrivalDate = LocalDate.now();
        this.arrivalTime = LocalTime.now().withSecond(0).withNano(0); // keeps the hours and minutes only
    }

    // basic getters and setters for attributes
    public String getTransactionID() {
        return transactionID;
    }

    public String getBookingID() {
        return bookingID;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public String getPromotionName() {
        return appliedPromotionName;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public int getParticipants() {
        return participants;
    }

    public LocalDate getArrivalDate() {
        return arrivalDate;
    }

    public LocalTime getArrivalTime() {
        return arrivalTime;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public LocalDateTime getPaymentDateTime() {
        return paymentDateTime;
    }

    public ArrayList<TransactionItem> getItems() {
        return itemList;
    }

    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }

    public void setBookingID(String bookingID) {
        this.bookingID = bookingID;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public void setPromotionName(String appliedPromotionName) {
        this.appliedPromotionName = appliedPromotionName;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public void setContactNumber(String phoneNum) {
        this.contactNumber = phoneNum;
    }

    public void setParticipants(int participants) {
        this.participants = participants;
    }

    public void setArrivalDate(LocalDate arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public void setArrivalTime(LocalTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setPaymentDateTime(LocalDateTime paymentDateTime) {
        this.paymentDateTime = paymentDateTime;
    }

    // method to add items into the arraylist
    public void addItems(RentalItem itemCode, int qty) {
        if (qty <= 0) { // if quantity is below 0, show error message and abort operation
            System.out.println("ERROR: Quantity must be greater than 0!");
            return;
        }

        // loop through every transactionItem in the arraylist and check if they have
        // the same code as input
        // if there is a match, change the quantity to add it up
        for (TransactionItem ti : itemList) {
            if (ti.getItem().getCode().equalsIgnoreCase(itemCode.getCode())) {
                ti.setQuantity(ti.getQuantity() + qty);
                return;
            }
        }

        // if there is no match, just add the new item into the arraylist
        itemList.add(new TransactionItem(itemCode, qty));
    }

    // method to remove an item from the arraylist
    public boolean removeItem(String itemCode) {
        for (int i = 0; i < itemList.size(); i++) {
            if (itemCode.equalsIgnoreCase(itemList.get(i).getItem().getCode())) {
                itemList.remove(i);
                return true; // return true if the item was successfully removed
            }
        }

        return false; // return false if item not removed/not found
    }

    // find an item that is already in this transaction by its code, null if it is
    // not there
    public TransactionItem findItem(String itemCode) {
        if (itemCode == null)
            return null;
        for (TransactionItem ti : itemList) {
            if (ti.getItem().getCode().equalsIgnoreCase(itemCode.trim())) {
                return ti;
            }
        }
        return null;
    }

    public boolean updateItemQuantity(String itemCode, int qty) {
        TransactionItem target = findItem(itemCode);
        if (target == null || qty <= 0) {
            return false;
        }
        target.setQuantity(qty);
        return true;
    }

    // walk-ins can correct their group size, a booking-based transaction takes it
    // from the booking so it cannot be changed here
    public boolean updateParticipants(int participants) {
        if (bookingID != null || participants <= 0) {
            return false;
        }
        this.participants = participants;
        return true;
    }

    // check to see if a transaction has already been paid or not
    // a paid transaction is final and should no longer be edited
    public boolean isPaid() {
        return "PAID".equalsIgnoreCase(paymentStatus);
    }

    // check to see if a transaction is paid today or not
    public boolean isPaidToday() {
        LocalDate today = LocalDate.now();

        return isPaid()
                && paymentDateTime != null
                && paymentDateTime.toLocalDate().equals(today);
    }

    // check to see if a transaction is eligible to be paid
    public boolean canBePaid() {
        return !isPaid() && !itemList.isEmpty();
    }

    // record a full payment, returns false if the transaction cannot be paid
    // (already paid or empty cart)
    public boolean markAsPaid(String method, LocalDateTime paidAt) {
        if (!canBePaid() || method == null || paidAt == null) {
            return false;
        }
        this.paymentMethod = method.toUpperCase();
        this.paymentDateTime = paidAt.withSecond(0).withNano(0); // hours and minutes only, same as the arrival time
        this.paymentStatus = "PAID";
        return true;
    }

    public double getEquipmentTotal() {
        double total = 0.0;
        for (TransactionItem ti : itemList) {
            if (ti.getItem().getCategory().equalsIgnoreCase("EQUIPMENT")) {
                total += ti.calculateSubtotal();
            }
        }
        return total;
    }

    public double getFacilityTotal() {
        double total = 0.0;
        for (TransactionItem ti : itemList) {
            if (ti.getItem().getCategory().equalsIgnoreCase("FACILITY")) {
                total += ti.calculateSubtotal();
            }
        }
        return total;
    }

    public double getOtherTotal() {
        double total = 0.0;
        for (TransactionItem ti : itemList) {
            String category = ti.getItem().getCategory();
            if (!category.equalsIgnoreCase("EQUIPMENT") && !category.equalsIgnoreCase("FACILITY")) {
                total += ti.calculateSubtotal();
            }
        }
        return total;
    }

    public double calcSubtotal() {
        return getEquipmentTotal() + getFacilityTotal() + getOtherTotal();
    }

    public double calcNetSubtotal() {
        return Math.max(0.0, calcSubtotal() - discountAmount);
    }

    public double calcServiceCharge() {
        return calcNetSubtotal() * 0.10;
    }

    public double calcFinalAmount() {
        return calcNetSubtotal() + calcServiceCharge();
    }

    public void printReceipt() {
        System.out.println("====================================================================");
        System.out.println("                       ACTIVEHUB SPORTS CENTRE                      ");
        System.out.println("                      RENTAL TRANSACTION RECEIPT                    ");
        System.out.println("====================================================================");
        System.out.printf("Transaction ID : %-20s Payment Status : %s%n", transactionID, paymentStatus);
        System.out.printf("Customer Name  : %-20s Customer Type  : %s%n", customerName,
                (bookingID != null ? "Pre-Booked" : "Walk-In"));
        System.out.printf("Contact No.    : %-20s Booking ID     : %s%n", contactNumber,
                (bookingID != null ? bookingID : "N/A"));
        System.out.printf("Participants   : %-20d Visit Date/Time: %s %s%n", participants, arrivalDate, arrivalTime);
        if (isPaid()) {
            String paidAt = (paymentDateTime != null)
                    ? paymentDateTime.toLocalDate() + " " + paymentDateTime.toLocalTime()
                    : "N/A";
            System.out.printf("Payment Method : %-20s Paid On        : %s%n", paymentMethod, paidAt);
        }
        System.out.println("--------------------------------------------------------------------");
        System.out.printf("%-10s %-40s %-5s %-8s %-8s%n", "Code", "Description", "Qty", "Price", "Total");
        System.out.println("--------------------------------------------------------------------");

        if (itemList.isEmpty()) {
            System.out.println("                          [ CART IS EMPTY ]                         ");
        } else {
            for (TransactionItem ti : itemList) {
                System.out.printf("%-10s %-40s %-5d %8.2f %8.2f%n",
                        ti.getItem().getCode(),
                        ti.getItem().getName() + ((ti.getItem().getCategory().equalsIgnoreCase("FACILITY"))
                                ? (" (" + ti.getQuantity() + "hrs)")
                                : ""),
                        ti.getQuantity(),
                        ti.getItem().getPrice(),
                        ti.calculateSubtotal());
            }
        }

        System.out.println("--------------------------------------------------------------------");
        System.out.printf("%-49s RM %8.2f%n", "Gross Subtotal:", calcSubtotal());
        if (discountAmount > 0) {
            System.out.printf("%-49s-RM %8.2f%n", "Discount (" + appliedPromotionName + "):", discountAmount);
            System.out.printf("%-49s RM %8.2f%n", "Net Subtotal:", calcNetSubtotal());
        }
        System.out.printf("%-49s RM %8.2f%n", "Service Charge (10%):", calcServiceCharge());
        System.out.println("--------------------------------------------------------------------");
        System.out.printf("%-49s RM %8.2f%n", "FINAL PAYABLE AMOUNT:", calcFinalAmount());
        System.out.println("====================================================================\n");
    }

}
