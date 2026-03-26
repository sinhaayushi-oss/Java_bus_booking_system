import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;

/*
  Simple bus booking program
  Written in a student-like style — small comments and simple names.
  Save as AdvancedBusBookingSystem.java
*/

public class AdvancedBusBookingSystem extends JFrame {

    private JTabbedPane tabs;
    private JTextField userFld, nameFld, seatsFld, dateFld;
    private JPasswordField passFld;
    private JComboBox<String> busBox, payBox;
    private JTextArea viewArea;
    private String loggedIn = "";
    private final int MAX_SEATS = 40;
    private final int FARE = 200;
    private HashMap<String, Integer> seatMap = new HashMap<String, Integer>();

    public AdvancedBusBookingSystem() {
        setTitle("Advanced Bus Booking System");
        setSize(700, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        tabs = new JTabbedPane();

        tabs.add("Login", makeLoginPanel());
        tabs.add("Book Ticket", makeBookingPanel());
        tabs.add("View Tickets", makeViewPanel());
        tabs.add("Cancel Ticket", makeCancelPanel());

        // disable tabs except login
        tabs.setEnabledAt(1, false);
        tabs.setEnabledAt(2, false);
        tabs.setEnabledAt(3, false);

        add(tabs);
        setVisible(true);
    }

    private JPanel makeLoginPanel() {
        JPanel p = new JPanel(null);

        JLabel t = new JLabel("Login to Book", JLabel.CENTER);
        t.setBounds(200, 30, 300, 30);
        t.setFont(new Font("Arial", Font.BOLD, 20));
        p.add(t);

        JLabel u = new JLabel("Username:");
        u.setBounds(200, 100, 100, 25);
        p.add(u);

        userFld = new JTextField();
        userFld.setBounds(300, 100, 150, 25);
        p.add(userFld);

        JLabel pw = new JLabel("Password:");
        pw.setBounds(200, 140, 100, 25);
        p.add(pw);

        passFld = new JPasswordField();
        passFld.setBounds(300, 140, 150, 25);
        p.add(passFld);

        JButton loginBtn = new JButton("Login");
        loginBtn.setBounds(300, 180, 100, 30);
        loginBtn.addActionListener(e -> {
            String usr = userFld.getText();
            String pwtext = new String(passFld.getPassword());
            // simple check - student project
            if ("admin".equals(usr) && "1234".equals(pwtext)) {
                loggedIn = usr;
                tabs.setEnabledAt(1, true);
                tabs.setEnabledAt(2, true);
                tabs.setEnabledAt(3, true);
                tabs.setSelectedIndex(1);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials!");
            }
        });
        p.add(loginBtn);

        return p;
    }

    private JPanel makeBookingPanel() {
        JPanel p = new JPanel();
        p.setLayout(null);

        JLabel lblName = new JLabel("Name:");
        lblName.setBounds(100, 40, 100, 25);
        p.add(lblName);

        nameFld = new JTextField();
        nameFld.setBounds(200, 40, 200, 25);
        p.add(nameFld);

        JLabel lblBus = new JLabel("Bus No:");
        lblBus.setBounds(100, 80, 100, 25);
        p.add(lblBus);

        busBox = new JComboBox<String>(new String[] {"BUS101", "BUS202", "BUS303"});
        busBox.setBounds(200, 80, 200, 25);
        p.add(busBox);

        JLabel lblSeats = new JLabel("Seats:");
        lblSeats.setBounds(100, 120, 100, 25);
        p.add(lblSeats);

        seatsFld = new JTextField();
        seatsFld.setBounds(200, 120, 200, 25);
        p.add(seatsFld);

        JLabel lblDate = new JLabel("Travel Date (dd-mm-yyyy):");
        lblDate.setBounds(100, 160, 200, 25);
        p.add(lblDate);

        dateFld = new JTextField();
        dateFld.setBounds(300, 160, 100, 25);
        p.add(dateFld);

        JLabel lblPay = new JLabel("Payment Method:");
        lblPay.setBounds(100, 200, 150, 25);
        p.add(lblPay);

        payBox = new JComboBox<String>(new String[] {"Cash", "UPI", "Card"});
        payBox.setBounds(250, 200, 150, 25);
        p.add(payBox);

        JButton bookBtn = new JButton("Book Ticket");
        bookBtn.setBounds(200, 250, 150, 30);
        bookBtn.addActionListener(e -> doBooking());
        p.add(bookBtn);

        return p;
    }

    private JPanel makeViewPanel() {
        JPanel p = new JPanel(new BorderLayout());
        viewArea = new JTextArea();
        viewArea.setEditable(false);
        p.add(new JScrollPane(viewArea), BorderLayout.CENTER);

        JButton r = new JButton("Refresh");
        r.addActionListener(e -> loadTickets());
        p.add(r, BorderLayout.SOUTH);

        return p;
    }

    private JPanel makeCancelPanel() {
        JPanel p = new JPanel();
        p.setLayout(null);

        JLabel lab = new JLabel("Enter Ticket ID to cancel:");
        lab.setBounds(150, 100, 200, 25);
        p.add(lab);

        JTextField cancelField = new JTextField();
        cancelField.setBounds(150, 130, 200, 25);
        p.add(cancelField);

        JButton cancelBtn = new JButton("Cancel Ticket");
        cancelBtn.setBounds(180, 170, 150, 30);
        cancelBtn.addActionListener(e -> {
            String id = cancelField.getText().trim();
            if (id.length() == 0) {
                JOptionPane.showMessageDialog(this, "Please enter an ID");
                return;
            }
            File f = new File("ticket_" + id + ".txt");
            if (f.exists()) {
                boolean ok = f.delete();
                if (ok) {
                    JOptionPane.showMessageDialog(this, "Ticket " + id + " cancelled.");
                } else {
                    JOptionPane.showMessageDialog(this, "Could not delete file.");
                }
                loadTickets();
            } else {
                JOptionPane.showMessageDialog(this, "Ticket ID not found.");
            }
        });
        p.add(cancelBtn);

        return p;
    }

    private void doBooking() {
        String name = nameFld.getText().trim();
        String bus = (String) busBox.getSelectedItem();
        String seatsText = seatsFld.getText().trim();
        String date = dateFld.getText().trim();
        String pay = (String) payBox.getSelectedItem();

        if (name.length() == 0 || seatsText.length() == 0 || date.length() == 0) {
            JOptionPane.showMessageDialog(this, "All fields are required!");
            return;
        }

        int seats = 0;
        try {
            seats = Integer.parseInt(seatsText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid seat number.");
            return;
        }

        Integer already = seatMap.get(bus);
        if (already == null) already = 0;

        if (already + seats > MAX_SEATS) {
            JOptionPane.showMessageDialog(this, "Not enough seats on " + bus);
            return;
        }

        int fare = seats * FARE;
        String ticketId = "TICK" + (new Random().nextInt(9000) + 1000); // like 1000-9999

        String ticket =
                "Ticket ID: " + ticketId + "\n" +
                "Name: " + name + "\n" +
                "Bus: " + bus + "\n" +
                "Seats: " + seats + "\n" +
                "Date: " + date + "\n" +
                "Payment: " + pay + "\n" +
                "Fare: ₹" + fare + "\n";

        // show preview
        JOptionPane.showMessageDialog(this, ticket, "Ticket Preview", JOptionPane.INFORMATION_MESSAGE);

        // save to file
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new FileWriter("ticket_" + ticketId + ".txt"));
            out.write(ticket);
            out.close();
        } catch (IOException ex) {
            try { if (out != null) out.close(); } catch (Exception ignored) {}
            JOptionPane.showMessageDialog(this, "Error saving ticket!");
            return;
        }

        seatMap.put(bus, already + seats);
        clearInputs();
        loadTickets();
    }

    private void clearInputs() {
        nameFld.setText("");
        seatsFld.setText("");
        dateFld.setText("");
    }

    private void loadTickets() {
        viewArea.setText("");
        File dir = new File(".");
        // beginner-style filename filter using anonymous class
        File[] files = dir.listFiles(new FilenameFilter() {
            public boolean accept(File d, String name) {
                return name.startsWith("ticket_") && name.endsWith(".txt");
            }
        });

        if (files == null) {
            viewArea.append("No tickets found.\n");
            return;
        }

        for (File f : files) {
            BufferedReader r = null;
            try {
                r = new BufferedReader(new FileReader(f));
                String line;
                while ((line = r.readLine()) != null) {
                    viewArea.append(line + "\n");
                }
                viewArea.append("\n----------------------------\n");
                r.close();
            } catch (IOException ex) {
                try { if (r != null) r.close(); } catch (Exception ignored) {}
                viewArea.append("Error reading " + f.getName() + "\n");
            }
        }
    }

    public static void main(String[] args) {
        // simple start
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new AdvancedBusBookingSystem();
            }
        });
    }
}