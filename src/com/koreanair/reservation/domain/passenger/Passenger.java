package com.koreanair.reservation.domain.passenger;

import java.time.LocalDate;

public class Passenger {

    private Long passengerId;
    private String name;
    private String contactInfo;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String nationality;
    private String passportNumber;
    private PassengerType passengerType;

    public static Passenger create(String fullName,
                                   String contactInfo,
                                   String passportNumber,
                                   LocalDate dateOfBirth,
                                   PassengerType passengerType) {
        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("승객 이름은 필수입니다.");
        }
        if (passportNumber == null || passportNumber.isBlank()) {
            throw new IllegalArgumentException("여권번호는 필수입니다.");
        }
        Passenger passenger = new Passenger();
        passenger.name = fullName.trim();
        passenger.contactInfo = contactInfo;
        passenger.passportNumber = passportNumber.trim();
        passenger.dateOfBirth = dateOfBirth;
        passenger.passengerType = passengerType != null ? passengerType : PassengerType.ADULT;
        String[] parts = passenger.name.split("\\s+", 2);
        passenger.firstName = parts[0];
        passenger.lastName = parts.length > 1 ? parts[1] : "";
        return passenger;
    }

    public Long getPassengerId() {
        return passengerId;
    }

    public String getName() {
        return name != null ? name : getFullName();
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public String getFullName() {
        if (firstName == null && lastName == null) {
            return name;
        }
        return firstName + " " + lastName;
    }

    public PassengerType getPassengerType() {
        return passengerType;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public void updatePassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }
}
