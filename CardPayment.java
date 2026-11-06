import java.util.Scanner;

public class CardPayment implements Payment{
    @Override
    public boolean processPayment(Scanner scanner, double amount){
        System.out.println("\n===== CREDIT/DEBIT CARD PAYMENT =====");
        System.out.printf("Amount due: RM %.2f%n", amount);

        //choose debit or credit
        String cardType = null;
        while (cardType == null){
            System.out.println("1. Credit Card");
            System.out.println("2. Debit Card");
            System.out.println("3. Cancel");

            int choice = InputValidator.readInt(scanner, "Select card type (1-3)");

            switch (choice){
                case 1 -> cardType = "Credit";
                case 2 -> cardType = "Debit";
                case 3 -> {
                    System.out.println("Card payment cancelled.");
                    return false;
                }
                default -> System.out.println("Invalid! Please try again!");
            }
        }

        //validation to check card number, card number has to be 16 digits
        String cardNumber = null;
        while (cardNumber == null){
            String input = InputValidator.readString(scanner, "Enter 16-digit card number (Type 'CANCEL' to abort)");
 
            if (input.equalsIgnoreCase("CANCEL")){
                System.out.println("Card payment cancelled.");
                return false;
            }
 
            String digits = input.replaceAll("[\\s-]", "");
 
            if (digits.matches("\\d{16}")){
                cardNumber = digits;
            } else {
                System.out.println("Error: A card number must be exactly 16 digits!");
            }
        }

        //fake approval, only the last 4 digits are ever shown
        System.out.println("Contacting bank...");
        System.out.printf("%s card ending %s approved. RM %.2f charged.%n", cardType, cardNumber.substring(12), amount);
        return true;
    }

    @Override
    public String getMethodName(){
        return "CARD";
    }
}
