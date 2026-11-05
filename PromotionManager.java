import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

class PromotionManager {

    public static void applyBestPromotion(TransactionRental transaction, Booking booking){
        LocalTime bookingTime = (booking != null) ? booking.getStartTime() : LocalTime.now();
        DayOfWeek bookingDay = (booking != null) ? booking.getBookingDate().getDayOfWeek() : LocalDate.now().getDayOfWeek();
        int pax = (booking != null) ? booking.getNumParticipants() : 1;

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

        LocalTime bookingTime = (booking != null) ? booking.getStartTime() : LocalTime.now();
        LocalDate bookingDate = (booking != null) ? booking.getBookingDate() : LocalDate.now();
        DayOfWeek bookingDay = bookingDate.getDayOfWeek();
        int pax = (booking != null) ? booking.getNumParticipants() : 1;

        Promotion[] promotions = new Promotion[] {
            new PromotionA(bookingTime, bookingDay),
            new PromotionB(pax),
            new PromotionC()
        };

        Promotion best = chooseBest(promotions, transaction);

        System.out.println("====================================================");
        System.out.println("               PROMOTION EVALUATION                 ");
        System.out.println("====================================================");
        System.out.printf("Booking Date  : %s (%s)%n", bookingDate, bookingDay);
        System.out.printf("Booking Time  : %s%n", bookingTime);
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