import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Report {

    // Managers
    private BookingManager BookingManager;
    private TransactionManager TransactionManager;

    // attribute
    private int NoOfBooking;
    private int NoOfTransaction;
    private double TotalRevenue;
    private RentalItem MostFrequent;
    private int MostFrequentQty;
    private String PaymentMethodSummary;
    private ArrayList<TransactionRental> paidTransactionList;

    // Constructor
    public Report(BookingManager bm, TransactionManager tm) {
        this.BookingManager = bm;
        this.TransactionManager = tm;
        this.paidTransactionList = getPaidTransactionList();
        // Number of bookings today
        this.NoOfBooking = bm.bookings.size();
        // Number of paid transactions
        this.NoOfTransaction = paidTransactionList.size();
        this.TotalRevenue = CalculateDailyRevenue();
        this.MostFrequent = getMostFrequentRentalItem();
        this.PaymentMethodSummary = getPaymentMethodSummary();

    }

    // Setter
    public void setMostFrequentQty(int mostFrequentQty) {
        MostFrequentQty = mostFrequentQty;
    }

    public double CalculateDailyRevenue() {
        // Get current's datetime
        LocalDateTime current = LocalDateTime.now();
        double Total = 0;

        // TEMPORARY:
        // Still missing logic where we get all transactions that are PAID TODAY
        // For now this is just add transcations that is recorded today.

        return Total;
    }

    public ArrayList<TransactionRental> getPaidTransactionList() {

        ArrayList<TransactionRental> transactionList = TransactionManager.transactionList;
        ArrayList<TransactionRental> paidTransactions = new ArrayList<>();

        if (transactionList.isEmpty()) {
            return paidTransactions;
        }

        for (TransactionRental t : transactionList) {

            if (t.isPaidToday()) {
                paidTransactions.add(t);
            }

        }

        return paidTransactions;
    }

    public int getNoOfTransaction() {
        return paidTransactionList.size();

    }

    public RentalItem getMostFrequentRentalItem() {
        ArrayList<TransactionRental> transactionList = paidTransactionList;
        if (transactionList.isEmpty()) {
            return null;
        }

        HashMap<String, Integer> totals = new HashMap<>();
        HashMap<String, RentalItem> itemByName = new HashMap<>();

        // Step 1: total quantity per item name
        for (TransactionRental t : transactionList) {

            ArrayList<TransactionItem> items = t.getItems();
            for (TransactionItem item : items) {
                int quantity = item.getQuantity();
                RentalItem rentalItem = item.getItem();
                String name = rentalItem.getName();

                totals.merge(name, quantity, Integer::sum);
                itemByName.putIfAbsent(name, rentalItem);
            }

        }

        // Step 2: find the name with the highest total
        String topName = null;
        int maxQuantity = 0;
        for (Map.Entry<String, Integer> entry : totals.entrySet()) {
            if (entry.getValue() > maxQuantity) {
                maxQuantity = entry.getValue();
                topName = entry.getKey();
            }
        }

        setMostFrequentQty(maxQuantity);
        return itemByName.get(topName);
    }

    public String getPaymentMethodSummary() {

        HashMap<String, Integer> paymentSummary = new HashMap<>();

        LocalDate today = LocalDate.now();

        for (TransactionRental t : paidTransactionList) {

            LocalDateTime paymentDate = t.getPaymentDateTime();

            String method = t.getPaymentMethod();

            // Count payment method usage
            paymentSummary.merge(method, 1, Integer::sum);
        }

        String paymentInfo = "";

        for (Map.Entry<String, Integer> entry : paymentSummary.entrySet()) {

            paymentInfo += String.format(
                    "%-20s : %d transactions%n",
                    entry.getKey(),
                    entry.getValue());
        }

        return paymentInfo;
    }

    @Override
    public String toString() {

        return String.format(
                "========================================\n" +
                        "           ACTIVEHUB DAILY REPORT\n" +
                        "========================================\n\n" +

                        "FACILITY BOOKINGS\n" +
                        "Total Facility Bookings       : %d\n\n" +

                        "RENTAL TRANSACTIONS\n" +
                        "Completed Rental Transactions : %d\n\n" +

                        "REVENUE\n" +
                        "Total Daily Revenue           : RM %.2f\n\n" +

                        "MOST FREQUENTLY RENTED\n" +
                        "Item / Facility               : %s\n" +
                        "Total Quantity Rented         : %d\n\n" +

                        "PAYMENT METHOD SUMMARY\n%s" +

                        "========================================",

                NoOfBooking,
                NoOfTransaction,
                TotalRevenue,
                MostFrequent,
                MostFrequentQty,
                PaymentMethodSummary);
    }

}
