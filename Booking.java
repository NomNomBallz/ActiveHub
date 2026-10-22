import java.time.*;

public class Booking {
    private String custName;
    private String contactNo;
    private LocalDate bookingDate;
    private LocalTime bookingTime;
    private int bookingID;

    public Booking(String cname, String cNo, int id){
        this.custName = cname;
        this.contactNo = cNo;
        this.bookingDate = LocalDate.now();
        this.bookingTime = LocalTime.now();
        this.bookingID = id;
    }

    


}
