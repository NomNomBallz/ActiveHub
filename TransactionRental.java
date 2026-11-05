import java.util.*;
import java.time.*;


public class TransactionRental {
    private String transactionID, bookingID, customerName, paymentStatus, appliedPromotionName, contactNumber; 
    private ArrayList<TransactionItem> itemList;
    private double discountAmount;
    private int participants;
    private LocalDate arrivalDate;
    private LocalTime arrivalTime;

    //overloading constructors, 1 for walk-in and 1 for booking

    //constructor for booking
    public TransactionRental(String id, Booking booking){
        this.transactionID = id; 
        this.customerName = booking.getName();
        this.bookingID = booking.getBookingID();
        this.itemList = new ArrayList<>();
        this.discountAmount = 0.0; 
        this.appliedPromotionName = "None";
        this.paymentStatus = "PENDING";
        this.contactNumber = booking.getContactNo();
        this.participants = booking.getNumParticipants();
        this.arrivalDate = booking.getBookingDate();
        this.arrivalTime = booking.getStartTime();
    }

    //constructor for walk-ins
    public TransactionRental(String id, String name, String phoneNum, int participants){
        this.transactionID = id; 
        this.customerName = name;
        this.bookingID = null;
        this.itemList = new ArrayList<>();
        this.discountAmount = 0.0; 
        this.appliedPromotionName = "None";
        this.paymentStatus = "PENDING";
        this.contactNumber = phoneNum;
        this.participants = participants;
        this.arrivalDate = LocalDate.now();
        this.arrivalTime = LocalTime.now();
    }

    //basic getters and setters for attributes 
    public String getTransactionID(){return transactionID;}
    public String getBookingID(){return bookingID;}
    public String getCustomerName(){return customerName;}
    public String getPaymentStatus(){return paymentStatus;}
    public String getPromotionName(){return appliedPromotionName;}
    public double getDiscountAmount(){return discountAmount;}
    public String getContactNumber(){return contactNumber;}
    public int getParticipants(){return participants;}
    public LocalDate getArrivalDate(){return arrivalDate;}
    public LocalTime getArrivalTime(){return arrivalTime;}
    public ArrayList<TransactionItem> getItems(){return itemList;}

    public void setTransactionID(String transactionID){this.transactionID = transactionID;}
    public void setBookingID(String bookingID){this.bookingID = bookingID;}
    public void setCustomerName(String customerName){this.customerName = customerName;}
    public void setPaymentStatus(String paymentStatus){this.paymentStatus = paymentStatus;}
    public void setPromotionName(String appliedPromotionName){this.appliedPromotionName = appliedPromotionName;}
    public void setDiscountAmount(double discountAmount){this.discountAmount = discountAmount;} 
    public void setContactNumber(String phoneNum){this.contactNumber = phoneNum;}
    public void setParticipants(int participants){this.participants = participants;}
    public void setArrivalDate(LocalDate arrivalDate){this.arrivalDate = arrivalDate;}
    public void setArrivalTime(LocalTime arrivalTime){this.arrivalTime = arrivalTime;}
    
    //method to add items into the arraylist
    public void addItems(RentalItem itemCode, int qty){ 
        if (qty <= 0){ //if quantity is below 0, show error message and abort operation
            System.out.println("ERROR: Quantity must be greater than 0!");
            return;
        }

        //loop through every transactionItem in the arraylist and check if they have the same code as input
        //if there is a match, change the quantity to add it up
        for (TransactionItem ti : itemList){
            if (ti.getItem().getCode().equalsIgnoreCase(itemCode.getCode())){
                ti.setQuantity(ti.getQuantity() + qty);
                return;
            }
        }
        
        //if there is no match, just add the new item into the arraylist
        itemList.add(new TransactionItem(itemCode, qty));
    }

    //method to remove an item from the arraylist
    public boolean removeItem(String itemCode){
        for (int i=0; i < itemList.size(); i++){
            if (itemCode.equalsIgnoreCase(itemList.get(i).getItem().getCode())){
                itemList.remove(i);
                return true; //return true if the item was successfully removed 
            }
        }

        return false; //return false if item not removed/not found
    }

    public double getEquipmentTotal(){
        double total = 0.0;
        for (TransactionItem ti : itemList){
            if (ti.getItem().getCategory().equalsIgnoreCase("EQUIPMENT")){
                total += ti.calculateSubtotal();
            }
        }
        return total;
    }

    public double getFacilityTotal(){
        double total = 0.0;
        for (TransactionItem ti : itemList){
            if (ti.getItem().getCategory().equalsIgnoreCase("FACILITY")){
                total += ti.calculateSubtotal();
            }
        }
        return total;
    }

    public double getOtherTotal(){
        double total = 0.0;
        for (TransactionItem ti : itemList){
            String category = ti.getItem().getCategory();
            if (!category.equalsIgnoreCase("EQUIPMENT") && !category.equalsIgnoreCase("FACILITY")){
                total += ti.calculateSubtotal();
            }
        }
        return total;
    }

    public double calcSubtotal(){
        return getEquipmentTotal() + getFacilityTotal() + getOtherTotal();
    }

    public double calcNetSubtotal(){
        return Math.max(0.0, calcSubtotal() - discountAmount);
    }

    public double calcServiceCharge(){
        return calcNetSubtotal() * 0.10;
    }

    public double calcFinalAmount(){
        return calcNetSubtotal() + calcServiceCharge();
    }

    public void printReceipt() {
        System.out.println("====================================================================");
        System.out.println("                       ACTIVEHUB SPORTS CENTRE                      ");
        System.out.println("                      RENTAL TRANSACTION RECEIPT                    ");
        System.out.println("====================================================================");
        System.out.printf("Transaction ID : %-20s Payment Status : %s%n", transactionID, paymentStatus);
        System.out.printf("Customer Name  : %-20s Customer Type  : %s%n", customerName, (bookingID != null ? "Pre-Booked" : "Walk-In"));
        System.out.printf("Contact No.    : %-20s Booking ID     : %s%n", contactNumber, (bookingID != null ? bookingID : "N/A"));
        System.out.printf("Participants   : %-20d Visit Date/Time: %s %s%n", participants, arrivalDate, arrivalTime);
        System.out.println("--------------------------------------------------------------------");
        System.out.printf("%-10s %-40s %-5s %-8s %-8s%n", "Code", "Description", "Qty", "Price", "Total");
        System.out.println("--------------------------------------------------------------------");
 
        if (itemList.isEmpty()) {
            System.out.println("                          [ CART IS EMPTY ]                         ");
        } else {
            for (TransactionItem ti : itemList) {
                System.out.printf("%-10s %-40s %-5d %8.2f %8.2f%n",
                        ti.getItem().getCode(),
                        ti.getItem().getName() + ((ti.getItem().getCategory().equalsIgnoreCase("FACILITY")) ? (" (" + ti.getQuantity() + "hrs)") : ""),
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
        System.out.printf("%-49s RM %8.2f%n", "Service Charge (10%%):", calcServiceCharge());
        System.out.println("--------------------------------------------------------------------");
        System.out.printf("%-49s RM %8.2f%n", "FINAL PAYABLE AMOUNT:", calcFinalAmount());
        System.out.println("====================================================================\n");
    }
 
}



