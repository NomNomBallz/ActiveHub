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
    private RentalItem MostFrequentFacility;
    private int MostFrequentFacilityQty;

    private RentalItem MostFrequentEquipment;
    private int MostFrequentEquipmentQty;
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
        this.MostFrequentEquipment = getMostFrequentEquipment();
        this.MostFrequentFacility = getMostFrequentFacility();
        this.PaymentMethodSummary = getPaymentMethodSummary();

    }

    // Setter
    public void setMostFrequentQty(int mostFrequentQty) {
        MostFrequentEquipmentQty = mostFrequentQty;
    }

    public double CalculateDailyRevenue() {

        double total = 0;

        for (TransactionRental t : paidTransactionList) {
            total += t.calcFinalAmount();
        }

        return total;
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

    public RentalItem getMostFrequentFacility() {

        HashMap<String, Integer> totals = new HashMap<>();
        HashMap<String, RentalItem> itemByName = new HashMap<>();

        for (TransactionRental t : paidTransactionList) {

            for (TransactionItem item : t.getItems()) {

                RentalItem rentalItem = item.getItem();

                if (rentalItem.getCategory().equalsIgnoreCase("Facility")) {

                    String name = rentalItem.getName();
                    int quantity = item.getQuantity();

                    totals.merge(name, quantity, Integer::sum);
                    itemByName.putIfAbsent(name, rentalItem);
                }
            }
        }

        String topName = null;
        int maxQuantity = 0;

        for (Map.Entry<String, Integer> entry : totals.entrySet()) {

            if (entry.getValue() > maxQuantity) {
                maxQuantity = entry.getValue();
                topName = entry.getKey();
            }
        }

        MostFrequentFacilityQty = maxQuantity;

        return itemByName.get(topName);
    }

    public RentalItem getMostFrequentEquipment() {

        HashMap<String, Integer> totals = new HashMap<>();
        HashMap<String, RentalItem> itemByName = new HashMap<>();

        for (TransactionRental t : paidTransactionList) {

            for (TransactionItem item : t.getItems()) {

                RentalItem rentalItem = item.getItem();

                if (rentalItem.getCategory().equalsIgnoreCase("Equipment")) {

                    String name = rentalItem.getName();
                    int quantity = item.getQuantity();

                    totals.merge(name, quantity, Integer::sum);
                    itemByName.putIfAbsent(name, rentalItem);
                }
            }
        }

        String topName = null;
        int maxQuantity = 0;

        for (Map.Entry<String, Integer> entry : totals.entrySet()) {

            if (entry.getValue() > maxQuantity) {
                maxQuantity = entry.getValue();
                topName = entry.getKey();
            }
        }

        MostFrequentEquipmentQty = maxQuantity;

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

                        "FACILITY\n" +
                        "Most Rented Facility          : %s\n" +
                        "Total Hours Rented            : %d hours\n\n" +

                        "EQUIPMENT\n" +
                        "Most Rented Equipment         : %s\n" +
                        "Total Units Rented             : %d units\n\n" +

                        "PAYMENT METHOD SUMMARY\n%s" +

                        "========================================",

                NoOfBooking,
                NoOfTransaction,
                TotalRevenue,
                MostFrequentFacility,
                MostFrequentFacilityQty,
                MostFrequentEquipment,
                MostFrequentEquipmentQty,
                PaymentMethodSummary);
    }

}
