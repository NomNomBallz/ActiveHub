import java.util.*;
import java.time.*;
import java.time.format.DateTimeParseException;

public class InputValidator {
    
    //validation for String
    public static String readString(Scanner scanner, String prompt){
        String input;
        while (true){
            System.out.print(prompt + ": ");
            input = scanner.nextLine().trim();
            
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("Error: Input cannot be empty, please try again!");
        }
    }

    //validation for int
    public static int readInt(Scanner scanner, String prompt){
        while(true){
            try{
                System.out.print(prompt + ": ");
                int value = Integer.parseInt(scanner.nextLine().trim());
                
                if (value > 0){
                    return value;
                }

                System.out.println("Error: Number must be greater than 0, please try again!");
            } catch (NumberFormatException e){
                System.out.println("Error: Please enter digits only!");
            }
            
        }
    }

    //validation for LocalDate
    public static LocalDate readDate(Scanner scanner, String prompt){
        while (true){
            try{
                System.out.print(prompt + ": ");
                String input = scanner.nextLine().trim();
                return LocalDate.parse(input);

            }catch(DateTimeParseException e){
                System.out.println("Error: Please enter a valid date format! (e.g. 2024-11-23)");
            }
        }
    }

    //validation for LocalTime
    public static LocalTime readTime(Scanner scanner, String prompt){
        while(true){
            try{
                System.out.print(prompt + ": ");
                String input = scanner.nextLine().trim();
                return LocalTime.parse(input);
            }catch(DateTimeParseException e){
                System.out.println("Error: Please enter a valid time format! (e.g. 19:30)");
            }
        }
    }


}
