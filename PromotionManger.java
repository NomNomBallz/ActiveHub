import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

class PromotionManager {

    static Promotion chooseBest(Promotion[] promotions) {
        Promotion best = null;
        for (Promotion p : promotions) {
            if (p.isApplicable()) {
                p.calculateDeduction();
                if (best == null || p.getDeduction() > best.getDeduction()) {
                    best = p; // strict > keeps A over B over C on ties
                }
            }
        }
        return best; // null if nothing applies
    }

    static void displayPromotionInfo(Promotion[] promotions, Promotion best) {

        // TEMPORARY: replace these with getters once Booking / RentalTransaction exist
        LocalDate bookingDate = LocalDate.of(2026, 10, 5); // a Monday
        DayOfWeek bookingDay = bookingDate.getDayOfWeek();
        LocalTime bookingTime = LocalTime.of(14, 30);
        int pax = 9;
        double facilityTotal = 40.00;
        double equipmentTotal = 20.00;
        double accessoryTotal = 0.00;

        double subtotal = facilityTotal + equipmentTotal + accessoryTotal;

        StringBuilder sb = new StringBuilder();
        sb.append("===== PROMOTION MODULE =====\n\n");
        sb.append(String.format("Booking Date       : %s, %s%n", bookingDay,
                bookingDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))));
        sb.append(String.format("Booking Time       : %s%n",
                bookingTime.format(DateTimeFormatter.ofPattern("h:mm a"))));
        sb.append(String.format("Participants       : %d%n%n", pax));

        sb.append(String.format("Facility/Court Charge     : RM%.2f%n", facilityTotal));
        sb.append(String.format("Equipment Charge          : RM%.2f%n", equipmentTotal));
        sb.append(String.format("Accessory/Service Charge  : RM%.2f%n", accessoryTotal));
        sb.append(String.format("Subtotal                  : RM%.2f%n%n", subtotal));

        sb.append("Eligible Promotions:\n");
        for (Promotion p : promotions) {
            if (p.isApplicable()) {
                sb.append(p.getLabel()).append("\n");
                sb.append(String.format("    Saving: RM%.2f%n%n", p.calculateDeduction()));
            }
        }

        if (best == null) {
            sb.append("No promotion applicable.\n");
            System.out.print(sb);
            return;
        }

        double afterDiscount = subtotal - best.getDeduction();
        double serviceCharge = afterDiscount * 0.10;

        sb.append("Selected Promotion:\n").append(best.getLabel()).append("\n\n");
        sb.append(String.format("Discount Amount: RM%.2f%n%n", best.getDeduction()));
        sb.append(String.format("Subtotal after Discount: RM%.2f%n", afterDiscount));
        sb.append(String.format("Service Charge (10%%): RM%.2f%n%n", serviceCharge));
        sb.append(String.format("Final Payable Amount: RM%.2f%n", afterDiscount + serviceCharge));

        System.out.print(sb);
    }
}