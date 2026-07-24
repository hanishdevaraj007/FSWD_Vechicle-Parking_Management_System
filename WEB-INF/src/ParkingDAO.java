package dao;
import java.util.List;      
import java.util.ArrayList;
import java.sql.*;
import model.Vehicle;

public class ParkingDAO {
    private static final String URL = "jdbc:mysql://localhost:3306/parking_db";
    private static final String USER = "root";
    private static final String PASS = "You_are_not_allowed_to_use_this_password";

    // Helper method to establish database connection
    private Connection getConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(URL, USER, PASS);
    }

    // Assigns an available slot and parks the vehicle
    public boolean parkVehicle(Vehicle vehicle) {
        String findSlotQuery = "SELECT slot_number FROM parking_slots WHERE slot_type = ? AND is_occupied = FALSE LIMIT 1";
        String updateSlotQuery = "UPDATE parking_slots SET is_occupied = TRUE WHERE slot_number = ?";
        String insertVehicleQuery = "INSERT INTO vehicle_parking (vehicle_number, vehicle_type, slot_number, status) VALUES (?, ?, ?, 'PARKED')";

        try (Connection con = getConnection()) {
            // 1. Find available slot
            PreparedStatement pstFind = con.prepareStatement(findSlotQuery);
            pstFind.setString(1, vehicle.getVehicleType());
            ResultSet rs = pstFind.executeQuery();

            if (rs.next()) {
                int assignedSlot = rs.getInt("slot_number");
                vehicle.setSlotNumber(assignedSlot);

                // 2. Mark slot as occupied
                PreparedStatement pstUpdate = con.prepareStatement(updateSlotQuery);
                pstUpdate.setInt(1, assignedSlot);
                pstUpdate.executeUpdate();

                // 3. Insert vehicle record
                PreparedStatement pstInsert = con.prepareStatement(insertVehicleQuery);
                pstInsert.setString(1, vehicle.getVehicleNumber());
                pstInsert.setString(2, vehicle.getVehicleType());
                pstInsert.setInt(3, assignedSlot);
                pstInsert.executeUpdate();

                return true; // Parking successful
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false; // Parking full or error occurred
    }

    // Method to process vehicle exit and calculate billing
    public Vehicle exitVehicle(String vehicleNumber) {
        String findVehicleQuery = "SELECT id, vehicle_type, slot_number, entry_time " +
                                  "FROM vehicle_parking WHERE vehicle_number = ? AND status = 'PARKED'";
        
        String updateVehicleQuery = "UPDATE vehicle_parking " +
                                    "SET exit_time = CURRENT_TIMESTAMP, total_fee = ?, status = 'EXITED' " +
                                    "WHERE id = ?";
        
        String freeSlotQuery = "UPDATE parking_slots SET is_occupied = FALSE WHERE slot_number = ?";

        Vehicle v = null;

        try (Connection con = getConnection()) {
            // 1. Find parked vehicle
            PreparedStatement pstFind = con.prepareStatement(findVehicleQuery);
            pstFind.setString(1, vehicleNumber);
            ResultSet rs = pstFind.executeQuery();

            if (rs.next()) {
                v = new Vehicle();
                v.setId(rs.getInt("id"));
                v.setVehicleNumber(vehicleNumber);
                v.setVehicleType(rs.getString("vehicle_type"));
                v.setSlotNumber(rs.getInt("slot_number"));
                v.setEntryTime(rs.getTimestamp("entry_time"));

                // 2. Calculate parking duration and fee
                long currentTimeMillis = System.currentTimeMillis();
                long entryTimeMillis = v.getEntryTime().getTime();
                long durationMillis = currentTimeMillis - entryTimeMillis;

                // Calculate total hours (Round up, minimum 1 hour)
                double hours = Math.ceil((double) durationMillis / (1000 * 60 * 60));
                if (hours < 1) hours = 1;

                // Rates: 20 per hour for 2-Wheeler, 50 per hour for 4-Wheeler
                double hourlyRate = "2-Wheeler".equalsIgnoreCase(v.getVehicleType()) ? 20.0 : 50.0;
                double totalFee = hours * hourlyRate;
                v.setTotalFee(totalFee);

                // 3. Update vehicle record (status = EXITED, total_fee)
                PreparedStatement pstUpdate = con.prepareStatement(updateVehicleQuery);
                pstUpdate.setDouble(1, totalFee);
                pstUpdate.setInt(2, v.getId());
                pstUpdate.executeUpdate();

                // 4. Free up slot
                PreparedStatement pstFree = con.prepareStatement(freeSlotQuery);
                pstFree.setInt(1, v.getSlotNumber());
                pstFree.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return v; // Returns populated Vehicle object if checkout succeeded, null if vehicle not found
    }

    // 1. Get list of currently parked vehicles
    public List<Vehicle> getActiveVehicles() {
        List<Vehicle> list = new ArrayList<>();
        String query = "SELECT vehicle_number, vehicle_type, slot_number, entry_time FROM vehicle_parking WHERE status = 'PARKED'";
        
        try (Connection con = getConnection();
             PreparedStatement pst = con.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {
            
            while (rs.next()) {
                Vehicle v = new Vehicle();
                v.setVehicleNumber(rs.getString("vehicle_number"));
                v.setVehicleType(rs.getString("vehicle_type"));
                v.setSlotNumber(rs.getInt("slot_number"));
                v.setEntryTime(rs.getTimestamp("entry_time"));
                list.add(v);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 2. Get counts for total free / occupied slots
    public int getOccupiedSlotCount() {
        String query = "SELECT COUNT(*) FROM parking_slots WHERE is_occupied = TRUE";
        try (Connection con = getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    
}