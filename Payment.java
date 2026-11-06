import java.util.Scanner;

//common interface class
public interface Payment {
    
    //boolean method that is inherited by the other classes
    //processes the payment depending on the class 
    boolean processPayment(Scanner scanner, double amount);

    //return the name saved, will show in daily report
    String getMethodName();
}
