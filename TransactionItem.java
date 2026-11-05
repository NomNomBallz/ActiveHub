//this class allows each item inside a transaction to have multiple quantities, it also allows
// to calculate the subtotal of that item & its quantities

public class TransactionItem {
    private RentalItem item;
    private int quantity;

    public TransactionItem(RentalItem item, int quantity){
        this.item = item;
        this.quantity = quantity;
    }

    //getters and setters
    public RentalItem getItem(){return item;}
    public int getQuantity(){return quantity;}
    public void setQuantity(int qty){quantity = qty;}

    //calculate the item's subtotal
    public double calculateSubtotal(){
        return item.getPrice() * quantity;
    }

    @Override
    public String toString(){
        return String.format("%-20s | Qty: %-3d | Unit: RM%-6.2f | Subtotal: RM%-6.2f", item.getName(), quantity, item.getPrice(), calculateSubtotal());
    }
}
