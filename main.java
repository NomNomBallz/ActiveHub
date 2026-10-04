import java.util.Scanner;

public class main {

    public static void main(String[] args) {

        rentalCatalogue rentalCatalogue = new rentalCatalogue();

        Scanner scanner = new Scanner(System.in);

        System.out.print("===== TASTEHUB SYSTEM =====\r\n" + //
                "1. Facility Booking Module\r\n" + //
                "2. View Rental Catalogue\r\n" + //
                "3. Create Rental Transaction\r\n" + //
                "4. Apply Promotion\r\n" + //
                "5. Make Payment\r\n" + //
                "6. Daily Report\r\n" + //
                "7. Exit\r\n");


        try {
            int option = scanner.nextInt();

            switch (option) {
                case 1 -> System.out.println("chose 1.");
                case 2 -> System.out.print(rentalCatalogue);
                case 3 -> System.out.println("chose 3.");
                case 4 -> applyPromotion();
                case 5 -> System.out.println("chose 5.");
                case 6 -> System.out.println("chose 6.");
                case 7 -> System.out.println("chose 7.");
                default -> System.out.println("Invalid option");

            }

        } catch (Exception e) {
        }

    }

    static void applyPromotion() {
        Promotion[] promotions = {
                // TEMPORARY
                // hard coded values for now
                new PromotionA(40, 9),
                new PromotionB(40, 9),
                new PromotionC(40, 9)
        };

        Promotion best = PromotionManager.chooseBest(promotions);
        PromotionManager.displayPromotionInfo(promotions, best);

    }

}
