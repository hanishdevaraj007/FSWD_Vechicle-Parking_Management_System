import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import model.Vehicle;
import dao.ParkingDAO;
import jakarta.servlet.annotation.WebServlet;

@WebServlet("/parkVehicle")
public class ParkVehicleServlet extends HttpServlet {
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("text/html");
        PrintWriter pw = response.getWriter();

        // 1. Extract data from View (Form)
        String vehicleNumber = request.getParameter("vehicleNumber");
        String vehicleType = request.getParameter("vehicleType");

        // 2. Populate Model Object
        Vehicle vehicle = new Vehicle(vehicleNumber, vehicleType);

        // 3. Delegate business logic to Model DAO
        ParkingDAO dao = new ParkingDAO();
        boolean success = dao.parkVehicle(vehicle);

        // 4. Render output View
        pw.println("<html><head><title>Parking Ticket</title></head><body>");
        if (success) {
            pw.println("<h2 style='color:green;'>Vehicle Parked Successfully!</h2>");
            pw.println("<p><b>Vehicle No:</b> " + vehicle.getVehicleNumber() + "</p>");
            pw.println("<p><b>Assigned Slot:</b> Slot #" + vehicle.getSlotNumber() + "</p>");
        } else {
            pw.println("<h2 style='color:red;'>Parking Failed! No available slots for " + vehicleType + ".</h2>");
        }
        pw.println("<br><a href='park_vehicle.html'>Park Another Vehicle</a>");
        pw.println("</body></html>");
    }
}