public class rentalItem {

    // attributes
    private String code;
    private String name;
    private String category;
    private double price;
    private String rentalMode;

    // contructor

    public rentalItem(String code, String name, String category, double price, String rentalMode) {

        this.code = code;
        this.name = name;
        this.category = category;
        this.price = price;
        this.rentalMode = rentalMode;

    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setRentalMode(String rentalMode) {
        this.rentalMode = rentalMode;
    }

    public String getRentalMode() {
        return rentalMode;
    }

    @Override
    public String toString() {

        return String.format("%s|%s|%s|%.2f|%s", code, name, category, price, rentalMode );
    }

}
