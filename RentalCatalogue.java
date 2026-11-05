import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;


public class RentalCatalogue {

    // create arraylist
    private ArrayList<RentalItem> rentalItems = new ArrayList<RentalItem>();

    public RentalCatalogue() {

        // Read txt file
        Path filePath = Path.of("ActiveHub/rentalItems.txt");

        try {
            String content = Files.readString(filePath);

            // Split the string by new line
            String[] lines = content.split("\\R");

            // Split the items by commas
            for (String line : lines) {

                // ignore comments in txt file with "#"
                if (line.trim().isEmpty() || line.trim().startsWith("#")) {
                    continue;
                }

                String[] itemAttributes = line.split(", ");

                // Create the rentalItems object using the itemAttributes array
                RentalItem itemObj = new RentalItem(itemAttributes[0], itemAttributes[1], itemAttributes[2],
                        Double.parseDouble(itemAttributes[3]), itemAttributes[4]);

                rentalItems.add(itemObj);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //
    public boolean checkFacilityCode(String code){
        RentalItem item = findItemByCode(code);
        return item != null && item.getCategory().equalsIgnoreCase("FACILITY");

    }

    //find a specific Item in the arraylist and returns it by code
    public RentalItem findItemByCode(String code){
        if (code == null) return null;

        for (RentalItem item : rentalItems){
            if (item.getCode().equalsIgnoreCase(code.trim())){
                return item;
            }
        }
        return null;
    }

    @Override
    public String toString() {

        String code;
        String name;
        String category;
        double price;
        String rentalMode;
        RentalItem rentalItem;

        String stringCatalogue = String.format("\n| %-9s | %-24s | %-15s | %-20s |%n", "Item Code", "Item Name",
                "Category", "Rental Price") +
                "| --------- | ------------------------ | --------------- | -------------------- |\n";

        for (int i = 0; i < rentalItems.size(); i++) {

            rentalItem = rentalItems.get(i);
            code = rentalItem.getCode();
            name = rentalItem.getName();
            category = rentalItem.getCategory();
            price = rentalItem.getPrice();
            rentalMode = rentalItem.getRentalMode().replace(" Per", "/").toLowerCase();

            String priceStr = String.format("RM %.2f %s", price, rentalMode);

            stringCatalogue += String.format("| %-9s | %-24s | %-15s | %-20s |%n", code, name, category, priceStr);
        }

        return stringCatalogue;
    }

}
