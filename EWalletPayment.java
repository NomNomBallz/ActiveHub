import java.util.Scanner;

public class EWalletPayment implements Payment {

    @Override
    public boolean processPayment(Scanner scanner, double amount){
        System.out.println("\n===== E-WALLET PAYMENT =====");
        System.out.printf("Amount due: RM %.2f%n", amount);

        //choose the wallet provider
        String provider = null;
        while (provider == null){
            System.out.println("1. GrabPay");
            System.out.println("2. Touch 'n Go eWallet");
            System.out.println("3. ShopeePay");
            System.out.println("4. Cancel");

            int choice = InputValidator.readInt(scanner, "Select e-wallet provider (1-4)");

            switch (choice){
                case 1 -> provider = "GrabPay";
                case 2 -> provider = "Touch 'n Go eWallet";
                case 3 -> provider = "ShopeePay";
                case 4 -> {
                    System.out.println("E-wallet payment cancelled.");
                    return false;
                }
                default -> System.out.println("Invalid option! Please try again.");
            }
        }

        //validates phone number for immersion purposes
        String phone = null;
        while (phone == null){
            String input = InputValidator.readString(scanner, "Enter registered phone number (Type 'CANCEL' to abort)");

            if (input.equalsIgnoreCase("CANCEL")){
                System.out.println("E-wallet payment cancelled.");
                return false;
            }

            String digits = input.replaceAll("[\\s-]", "");
            if (digits.startsWith("+")){
                digits = digits.substring(1);
            }

            if (digits.matches("\\d{8,12}")){
                phone = digits;
            } else {
                System.out.println("Error: Please enter a valid phone number! (8 to 12 digits)");
            }
        }

        //simulated approval, only the last 4 digits are ever shown
        System.out.printf("Payment request of RM %.2f sent to %s account ending %s...%n", amount, provider, phone.substring(phone.length() - 4));
        System.out.println("Payment approved.");
        return true;
    }

    @Override
    public String getMethodName(){
        return "EWALLET";
    }
}