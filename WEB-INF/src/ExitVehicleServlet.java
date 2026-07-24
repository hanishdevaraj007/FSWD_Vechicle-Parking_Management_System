import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import model.Vehicle;
import dao.ParkingDAO;
import jakarta.servlet.annotation.WebServlet;

@WebServlet("/exitVehicle")
public class ExitVehicleServlet extends HttpServlet {
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("text/html");
        PrintWriter pw = response.getWriter();

        String vehicleNumber = request.getParameter("vehicleNumber");

        ParkingDAO dao = new ParkingDAO();
        Vehicle vehicle = dao.exitVehicle(vehicleNumber);

        pw.println("<html><head><title>Exit Receipt</title></head><body>");

        if (vehicle != null) {
            pw.println("<h2 style='color:blue;'>Vehicle Checkout Successful!</h2>");
            pw.println("<p><b>Vehicle Number:</b> " + vehicle.getVehicleNumber() + "</p>");
            pw.println("<p><b>Vehicle Type:</b> " + vehicle.getVehicleType() + "</p>");
            pw.println("<p><b>Freed Slot:</b> Slot #" + vehicle.getSlotNumber() + "</p>");
            pw.println("<p><b>Entry Time:</b> " + vehicle.getEntryTime() + "</p>");
            pw.println("<h3><b>Total Amount Due:</b> ₹" + vehicle.getTotalFee() + "</h3>");
        } else {
            pw.println("<h2 style='color:red;'>Error: Active parked vehicle not found with number " + vehicleNumber + "</h2>");
        }

        pw.println("<br><a href='exit_vehicle.html'>Process Another Exit</a> | ");
        pw.println("<a href='park_vehicle.html'>Park Vehicle</a>");
        pw.println("</body></html>");
    }
}