import java.util.Scanner;

public class CashPayment implements Payment{

    @Override
    public boolean processPayment(Scanner scanner, double amount){
        System.out.println("\n===== CASH PAYMENT =====");
        System.out.printf("Amount due: RM %.2f%n", amount);
 
        //work in cents so decimal rounding can never give a wrong result
        long dueCents = Math.round(amount * 100);
 
        while (true){
            String input = InputValidator.readString(scanner, "Enter amount received in RM (Type 'CANCEL' to abort)");
 
            if (input.equalsIgnoreCase("CANCEL")){
                System.out.println("Cash payment cancelled.");
                return false;
            }
 
            //digits only, with up to 2 decimal places (e.g. 50 or 50.50)
            if (!input.matches("\\d+(\\.\\d{1,2})?")){
                System.out.println("Error: Please enter a valid amount! (e.g. 50 or 50.50)");
                continue;
            }
 
            long receivedCents = Math.round(Double.parseDouble(input) * 100);
 
            if (receivedCents < dueCents){
                System.out.printf("Error: RM %.2f is not enough, still short by RM %.2f!%n", receivedCents / 100.0, (dueCents - receivedCents) / 100.0);
                continue;
            }
 
            System.out.printf("Payment received: RM %.2f%n", receivedCents / 100.0);
            System.out.printf("Change to return: RM %.2f%n", (receivedCents - dueCents) / 100.0);
            return true;
        }

    }

    @Override 
    public String getMethodName(){
        return "CASH";
    }
    
}
