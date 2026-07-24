import java.io.*;
import java.util.List;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.Vehicle;
import dao.ParkingDAO;

@WebServlet("/viewStatus")
public class ViewStatusServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("text/html");
        PrintWriter pw = response.getWriter();

        ParkingDAO dao = new ParkingDAO();
        List<Vehicle> activeList = dao.getActiveVehicles();
        int occupiedSlots = dao.getOccupiedSlotCount();
        int totalSlots = 10; // We seeded 10 slots in MySQL

        pw.println("<html><head><title>Parking Status</title>");
        pw.println("<link rel='stylesheet' href='style.css'>");
        pw.println("<div class='container'>");
        pw.println("</head><body>");
        pw.println("<h2>Live Parking Dashboard</h2>");
        pw.println("<h3>Slots Occupied: " + occupiedSlots + " / " + totalSlots + "</h3>");
        
        pw.println("<table border='1' cellpadding='8' cellspacing='0'>");
        pw.println("<tr><th>Slot #</th><th>Vehicle Number</th><th>Type</th><th>Entry Time</th></tr>");

        if (activeList.isEmpty()) {
            pw.println("<tr><td colspan='4' style='text-align:center;'>No vehicles currently parked.</td></tr>");
        } else {
            for (Vehicle v : activeList) {
                pw.println("<tr>");
                pw.println("<td>" + v.getSlotNumber() + "</td>");
                pw.println("<td>" + v.getVehicleNumber() + "</td>");
                pw.println("<td>" + v.getVehicleType() + "</td>");
                pw.println("<td>" + v.getEntryTime() + "</td>");
                pw.println("</tr>");
            }
        }

        pw.println("</table>");
        pw.println("<br>");
        pw.println("<a href='park_vehicle.html'>Park Vehicle</a> | ");
        pw.println("<a href='exit_vehicle.html'>Exit Vehicle</a>");
        pw.println("</body></html>");
    }
}