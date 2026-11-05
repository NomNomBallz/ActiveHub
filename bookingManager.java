import java.time.*;
import java.util.*;
import java.io.*;

public class BookingManager {
    private ArrayList<Booking> bookings;

    public BookingManager(){
        bookings = new ArrayList<>();
        loadBookings();
    }   

    //returns booking object
    public Booking findBookingByID(String id){
        if (id == null) return null;

        for (Booking booking : bookings){
            if (booking.getBookingID().equalsIgnoreCase(id.trim())){
                return booking;
            }
        }
        return null;
    }
    
    //pretty self explanatory
    public boolean createBooking(String name, String contact, String facility, int participantqty, LocalDate date,
            LocalTime start, LocalTime end){
        
        //if this check fails, it means there is a conflicting booking, returns false and the booking is not created
        if (isItBooked(facility, date, start, end)){
            System.out.println("Error: Slot already taken!");
            return false;
        }

        String tempID = generateBookingID();

        Booking newBooking = new Booking(tempID, name, contact, facility, participantqty,
        date, start, end, "RESERVED");
        
        bookings.add(newBooking);
        saveBookings();

        return true;
        
    }

    //cancel a booking by entering its ID
    public boolean cancelBooking(String bookingID){
        for (Booking bookingEntry : bookings ){
            if (bookingEntry.getBookingID().equalsIgnoreCase(bookingID.trim())){

                if ("CANCELLED".equalsIgnoreCase(bookingEntry.getStatus())){
                    System.out.println("Booking " + bookingID + " is already cancelled!");
                    return false;
                }

                bookingEntry.setStatus("CANCELLED");
                saveBookings();
                return true;
            }
        }
        return false;
    }

    //view all existing bookings
    public void displayAllBookings(){
        if (bookings.isEmpty()){
            System.out.println("\nNo bookings found in the system.");
        }

        System.out.println("\n========================================================================================================================");
        System.out.printf("%-6s | %-18s | %-12s | %-10s | %-13s | %-12s | %-18s | %-10s%n", "ID", "Customer Name", "Contact", "Date", "Time Slot", "Participants", "Facility", "Status");
        System.out.println("========================================================================================================================");

        for (Booking bookingEntry : bookings){
            String timeSlot = bookingEntry.getStartTime() + " - " + bookingEntry.getEndTime();
            
            System.out.printf("%-6s | %-18s | %-12s | %-10s | %-13s | %-12d | %-18s | %-10s%n",
                                bookingEntry.getBookingID(),
                                truncate(bookingEntry.getName(), 18),
                                truncate(bookingEntry.getContactNo(), 12),
                                bookingEntry.getBookingDate(),
                                timeSlot,
                                bookingEntry.getNumParticipants(),
                                truncate(bookingEntry.getFacilityType(), 18),
                                bookingEntry.getStatus());
            System.out.println("========================================================================================================================");
        }


    }

    //helper method for view all bookings, shortens names/strings that are too long
    private String truncate(String text, int width){
        if (text != null && text.length() > width){
            return text.substring(0, width - 3) + "...";
        }
        return text;
    }

    //check to see if any bookings conflict or not. if conflicting, return true
    private boolean isItBooked(String facility, LocalDate date, LocalTime start, LocalTime end){

        for (Booking bookingEntry : bookings){

            if ("CANCELLED".equalsIgnoreCase(bookingEntry.getStatus())){
                continue;
            }

            boolean sameFacility = bookingEntry.getFacilityType().equalsIgnoreCase(facility);
            boolean sameDate = bookingEntry.getBookingDate().equals(date);

            if (sameFacility && sameDate){
                boolean isOverlapping = start.isBefore(bookingEntry.getEndTime()) && end.isAfter(bookingEntry.getStartTime());
                
                if (isOverlapping){
                    return true;
                }
            }

        }

        return false;
    }

    //create a unique ID for each booking entry
    private String generateBookingID(){
        int count = bookings.size();
        return String.format("B%03d", count + 1);
    }

    //saves the data in current memory to the txt file
    private void saveBookings(){
        try(BufferedWriter writer = new BufferedWriter(new FileWriter("ActiveHub/bookings.txt"))){
            //for every object (bookingEntry) in the arraylist (bookings), get all of the data and put in bookings.txt 
            for (Booking bookingEntry : bookings){
                writer.write(bookingEntry.convertToFileString());
                writer.newLine();
            }

        } catch (IOException e){
            System.out.println("An error occurred while trying to save data to bookings.txt: " + e.getMessage());
        }
    }

    //gets the data from the txt file and loads it into current memory
    private void loadBookings(){
        File file = new File("ActiveHub/bookings.txt");

        //initialise the classes that read lines
        try(BufferedReader reader = new BufferedReader(new FileReader(file))){
            
            String line;

            //if the line is not null (which means there is content), then continue
            while( (line = reader.readLine()) != null){
                
                if (line.trim().isEmpty()) continue;
                
                String[] bookingInfo = line.split("\\|");

                //convert the data taken from the txt file back into java types to insert into arraylist
                String id = bookingInfo[0];
                String name = bookingInfo[1];
                String contact = bookingInfo[2];
                String facility = bookingInfo[3];
                int participantqty = Integer.parseInt(bookingInfo[4]);
                LocalDate date = LocalDate.parse(bookingInfo[5]);
                LocalTime startTime = LocalTime.parse(bookingInfo[6]);
                LocalTime endTime = LocalTime.parse(bookingInfo[7]);
                String status = bookingInfo[8];

                //put it into the running arraylist
                Booking booking = new Booking(id, name, contact, facility, participantqty, date, startTime, endTime, status);
                bookings.add(booking);

            }
            
        } catch (IOException e){
            System.out.println("An error occurred while trying to read bookings.txt: " + e.getMessage());
        }

        
    }
}
