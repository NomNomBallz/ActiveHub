import java.time.*;

public class Booking {
    private String bookingID, custName, contactNo, facilityType, status;
    private int numberOfParticipants;
    private LocalDate bookingDate;
    private LocalTime startTime, endTime;

    // constructor
    public Booking(String id, String name, String contact, String facility, int participantqty, LocalDate date,
            LocalTime start, LocalTime end, String status) {

        setBookingID(id); // B001, B002, etc
        setName(name); // Timothy Lim, Muhammad Ali
        setContactNo(contact); // 0125649283, 0112234359
        setBookingDate(date); // 2026-05-04
        setEndTime(end); // HH:mm, 12:00, 16:30
        setStartTime(start); // HH:mm, 14:00, 18:30
        setFacilityType(facility); // FC001, FC002
        setNumParticipants(participantqty); // 2, 4, 6
        setStatus(status); // BOOKED, CANCELLED
    }

    // getters
    public String getName() {
        return custName;
    }

    public String getContactNo() {
        return contactNo;
    }

    public String getFacilityType() {
        return facilityType;
    }

    public String getBookingID() {
        return bookingID;
    }

    public int getNumParticipants() {
        return numberOfParticipants;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public String getStatus() {
        return status;
    }

    // setters
    public void setName(String custName) {
        this.custName = custName;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public void setFacilityType(String facilityType) {
        this.facilityType = facilityType;
    }

    public void setBookingID(String bookingID) {
        this.bookingID = bookingID;
    }

    public void setNumParticipants(int numberOfParticipants) {
        this.numberOfParticipants = numberOfParticipants;
    }

    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    //additional helper classes

    //convert booking object to a format that can be stored in the bookings.txt file
    public String convertToFileString() {
        return bookingID + "|" + custName + "|" + contactNo + "|" + facilityType + "|" + numberOfParticipants
        + "|" + bookingDate + "|" + startTime + "|" + endTime + "|" + status;
    }



}
