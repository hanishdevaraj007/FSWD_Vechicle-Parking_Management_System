package model;

import java.sql.Timestamp;

public class Vehicle {
    private int id;
    private String vehicleNumber;
    private String vehicleType;
    private int slotNumber;
    private Timestamp entryTime;
    private Timestamp exitTime;
    private double totalFee;
    private String status;

    // Constructors
    public Vehicle() {}

    public Vehicle(String vehicleNumber, String vehicleType) {
        this.vehicleNumber = vehicleNumber;
        this.vehicleType = vehicleType;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public int getSlotNumber() { return slotNumber; }
    public void setSlotNumber(int slotNumber) { this.slotNumber = slotNumber; }

    public Timestamp getEntryTime() { return entryTime; }
    public void setEntryTime(Timestamp entryTime) { this.entryTime = entryTime; }

    public Timestamp getExitTime() { return exitTime; }
    public void setExitTime(Timestamp exitTime) { this.exitTime = exitTime; }

    public double getTotalFee() { return totalFee; }
    public void setTotalFee(double totalFee) { this.totalFee = totalFee; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}