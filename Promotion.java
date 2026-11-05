import java.time.DayOfWeek;
import java.time.LocalTime;

abstract class Promotion {
    protected double deduction;

    abstract boolean isApplicable(TransactionRental transaction); // each promotion has its own rule
    abstract double calculateDeduction(TransactionRental transaction); // each promotion has its own maths
    abstract String getLabel();

    public double getDeduction() {
        return deduction;
    }

    public double applyDiscount(double subTotal) {
        return Math.max(0, subTotal - deduction);
    }

}

class PromotionA extends Promotion {
    private LocalTime bookingTime;
    private DayOfWeek bookingDay;

    PromotionA(LocalTime time, DayOfWeek day) {
        bookingTime = time;
        bookingDay = day;
    }

    @Override
    boolean isApplicable(TransactionRental transaction) {

        // Checking booking time if its Mon - Fri (9am - 4pm)
        boolean isWeekday = bookingDay != DayOfWeek.SATURDAY && bookingDay != DayOfWeek.SUNDAY;
        boolean isOffPeakHour = !bookingTime.isBefore(LocalTime.of(9,0)) && bookingTime.isBefore(LocalTime.of(16,0));

        if (isWeekday && isOffPeakHour && transaction.getFacilityTotal() > 0) {
            calculateDeduction(transaction);
            return true;

        }
        return false;

    }

    @Override
    double calculateDeduction(TransactionRental transaction) {
        deduction = transaction.getFacilityTotal() * 0.15;
        return deduction;
    }

    @Override
    String getLabel() {
        return "[A] Off-Peak Saver";
    }
}

class PromotionB extends Promotion {
    private int pax;
    
    PromotionB(int pax) {
        this.pax = pax;
    }

    @Override
    boolean isApplicable(TransactionRental transaction) {
        if (pax >= 8 && transaction.getFacilityTotal() > 0){
            calculateDeduction(transaction);
            return true;
        }
        return false;
    }

    @Override
    double calculateDeduction(TransactionRental transaction) {
        // ensure deduction is below rm60 and ensure facilityTotal is not below 0
        deduction = Math.min(Math.min(pax * 5, 60), transaction.getFacilityTotal());
        return deduction;

    }

    @Override
    String getLabel() {
        return "[B] Team Booking Reward";
    }
}

class PromotionC extends Promotion {

    @Override
    boolean isApplicable(TransactionRental transaction){

        // Check if there is 1 facility and 3 equipment in a single transaction

        int equipmentCount = 0;

        for (TransactionItem ti : transaction.getItems()){
            if (ti.getItem().getCategory().equalsIgnoreCase("EQUIPMENT")){
                equipmentCount += ti.getQuantity();
            }
        }

        boolean hasFacility = transaction.getFacilityTotal() > 0;

        if (hasFacility && equipmentCount >= 3) {
            calculateDeduction(transaction);
            return true;
        }
        return false;
    }

    @Override
    double calculateDeduction(TransactionRental transaction) {
        deduction = 20;
        return deduction;
    }

    @Override
    String getLabel() {
        return "[C] Equipment Bundle Discount";
    }
}