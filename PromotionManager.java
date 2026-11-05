import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

class PromotionManager {

    public static void applyBestPromotion(TransactionRental transaction, Booking booking){
        LocalTime bookingTime = (booking != null) ? booking.getStartTime() : transaction.getArrivalTime();
        DayOfWeek bookingDay = (booking != null) ? booking.getBookingDate().getDayOfWeek() : transaction.getArrivalDate().getDayOfWeek();
        int pax = (booking != null) ? booking.getNumParticipants() : transaction.getParticipants();

        Promotion[] promotions = new Promotion[] {
            new PromotionA(bookingTime, bookingDay),
            new PromotionB(pax),
            new PromotionC()
        };

        Promotion best = chooseBest(promotions, transaction);

        if (best != null){
            transaction.setDiscountAmount(best.getDeduction());
            transaction.setPromotionName(best.getLabel());
        } else {
            transaction.setDiscountAmount(0.0);
            transaction.setPromotionName("None");
        }
    }

    public static Promotion chooseBest(Promotion[] promotions, TransactionRental transaction) {
        Promotion best = null;
        for (Promotion p : promotions) {
            if (p.isApplicable(transaction)) {
                p.calculateDeduction(transaction);
                if (best == null || p.getDeduction() > best.getDeduction()) {
                    best = p; // strict > keeps A over B over C on ties
                }
            }
        }
        return best; // null if nothing applies
    }

    public static void displayPromotionInfo(TransactionRental transaction, Booking booking) {

        LocalTime bookingTime = (booking != null) ? booking.getStartTime() : transaction.getArrivalTime();
        LocalDate bookingDate = (booking != null) ? booking.getBookingDate() : transaction.getArrivalDate();
        DayOfWeek bookingDay = bookingDate.getDayOfWeek();
        int pax = (booking != null) ? booking.getNumParticipants() : transaction.getParticipants();

        Promotion[] promotions = new Promotion[] {
            new PromotionA(bookingTime, bookingDay),
            new PromotionB(pax),
            new PromotionC()
        };

        Promotion best = chooseBest(promotions, transaction);

        System.out.println("====================================================");
        System.out.println("               PROMOTION EVALUATION                 ");
        System.out.println("====================================================");
        String kind = (booking != null) ? "Booking" : "Arrival"; //walk-ins are judged on when they arrived
        System.out.printf("%s Date  : %s (%s)%n", kind, bookingDate, bookingDay);
        System.out.printf("%s Time  : %s%n", kind, bookingTime);
        System.out.printf("Participants  : %d%n", pax);
        System.out.printf("Gross Subtotal: RM %.2f%n", transaction.calcSubtotal());
        System.out.println("----------------------------------------------------");
        System.out.println("Eligible Promotions Breakdown:");

        boolean anyApplicable = false;
        for (Promotion p : promotions) {
            if (p.isApplicable(transaction)) {
                anyApplicable = true;
                double saving = p.calculateDeduction(transaction);
                boolean isWinner = (best != null && p.getLabel().equals(best.getLabel()));
                
                System.out.printf("  * %-32s -> Saves RM %6.2f %s%n", 
                        p.getLabel(), 
                        saving, 
                        (isWinner ? "[SELECTED]" : ""));
            }
        }

        if (!anyApplicable) {
            System.out.println("  [No promotional discounts apply for this transaction]");
        } else {
            System.out.println("----------------------------------------------------");
            System.out.printf("Applied Promotion : %s%n", best.getLabel());
            System.out.printf("Total Discount    : RM %.2f%n", best.getDeduction());
        }
        System.out.println("====================================================\n");
    }
}