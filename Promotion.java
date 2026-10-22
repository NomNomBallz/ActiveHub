import java.time.DayOfWeek;
import java.time.LocalTime;

abstract class Promotion {
    protected double facilityTotal;
    protected int pax;
    protected double deduction;

    Promotion(double facilityTotal, int pax) {
        this.facilityTotal = facilityTotal;
        this.pax = pax;
    }

    abstract boolean isApplicable(); // each promotion has its own rule

    abstract double calculateDeduction(); // each promotion has its own maths

    abstract String getLabel();

    public double getDeduction() {
        return deduction;
    }

    public double applyDiscount(double subTotal) {
        return Math.max(0, subTotal - deduction);
    }

}

class PromotionA extends Promotion {
    PromotionA(double facilityTotal, int pax) {
        super(facilityTotal, pax);
    }

    @Override
    boolean isApplicable() {
        LocalTime bookingTime = LocalTime.of(14, 30);
        DayOfWeek bookingDay = DayOfWeek.MONDAY;

        // Checking booking time if its Mon - Fri (9am -4pm)

        if (bookingDay != DayOfWeek.SATURDAY &&
                bookingDay != DayOfWeek.SUNDAY &&
                !bookingTime.isBefore(LocalTime.of(9, 0)) &&
                bookingTime.isBefore(LocalTime.of(16, 0))) {

            calculateDeduction();
            return true;

        }
        return false;

    }

    @Override
    double calculateDeduction() {
        deduction = facilityTotal * 0.15; // direct access to the protected field
        return deduction;
    }

    @Override
    String getLabel() {
        return "[A] Off-Peak Saver";
    }
}

class PromotionB extends Promotion {
    PromotionB(double facilityTotal, int pax) {
        super(facilityTotal, pax);
    }

    @Override
    boolean isApplicable() {
        return pax >= 8;
    }

    @Override
    double calculateDeduction() {
        // ensure deduction is below rm60 and ensure facilityTotal is not below 0
        deduction = Math.min(Math.min(pax * 5, 60), facilityTotal);
        return deduction;

    }

    @Override
    String getLabel() {
        return "[B] Team Booking Reward";
    }
}

class PromotionC extends Promotion {
    PromotionC(double facilityTotal, int pax) {
        super(facilityTotal, pax);
    }

    @Override
    boolean isApplicable() {

        // Check if there is 1 facility and 3 equipement in a single transaction


        //TEMPORARY:
        //replace with getters once the relevant modules exst
        int noFacility = 2;
        int noEquipment = 3;



        if (noFacility > 0 && noEquipment >= 3) {
            calculateDeduction();
            return true;

        }
        return false;
    }

    @Override
    double calculateDeduction() {
        deduction = 20;
        return deduction;
    }

    @Override
    String getLabel() {
        return "[C] Equipment Bundle Discount";
    }
}