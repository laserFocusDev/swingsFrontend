import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.text.SimpleDateFormat;

// Main app class
public class HelperAppIntegrated extends JFrame {

    // Data Classes
    static class Househelp {
        String name, hours, rate, services;
        public Househelp(String n, String h, String r, String s) {
            name = n; hours = h; rate = r; services = s;
        }
    }

    static class Complaint {
        String id, detail, status, priority, type;
        ArrayList<StatusUpdate> updates = new ArrayList<>();
        public Complaint(String id, String detail, String type, String priority, String status) {
            this.id = id;
            this.detail = detail;
            this.type = type;
            this.priority = priority;
            this.status = status;
            // Automatically add initial update to timeline
            updates.add(new StatusUpdate(status, "Complaint created"));
        }
        public String toString() {
            return id + " - " + detail;
        }
    }

    static class StatusUpdate {
        String status, note;
        Date timestamp;
        ArrayList<ImageIcon> images = new ArrayList<>();
        public StatusUpdate(String status, String note) {
            this.status = status;
            this.note = note;
            this.timestamp = new Date();
        }
    }

    static class Vendor {
        String name, contact, category;
        int stars;
        public Vendor(String n, String c, String cat, int s) {
            name=n; contact=c; category=cat; stars=s;
        }
    }

    // Dummy data
    static ArrayList<Househelp> househelps = new ArrayList<>();
    static ArrayList<Complaint> complaints = new ArrayList<>();
    static ArrayList<Vendor> vendors = new ArrayList<>();

    static ArrayList<String> notifications = new ArrayList<>();

    public HelperAppIntegrated() {
        setTitle("Helper App - Login");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField(15);
        JLabel roleLabel = new JLabel("Select Role:");
        String[] roles = {"Resident", "Vendor", "Admin"};
        JComboBox<String> roleCombo = new JComboBox<>(roles);
        JButton loginBtn = new JButton("Login");

        userLabel.setFont(new Font("Arial", Font.BOLD, 14));
        roleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        loginBtn.setBackground(new Color(59, 89, 182));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        loginBtn.setFont(new Font("Tahoma", Font.BOLD, 12));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);

        gbc.gridx=0; gbc.gridy=0; add(userLabel, gbc);
        gbc.gridx=1; add(userField, gbc);
        gbc.gridx=0; gbc.gridy=1; add(roleLabel, gbc);
        gbc.gridx=1; add(roleCombo, gbc);
        gbc.gridy=2; add(loginBtn, gbc);

        addDummyData();

        loginBtn.addActionListener(e -> {
            String username = userField.getText().trim();
            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter username!");
                return;
            }
            String role = (String) roleCombo.getSelectedItem();
            this.dispose();
            switch(role) {
                case "Resident": openResidentDashboard(username); break;
                case "Vendor": openVendorDashboard(username); break;
                case "Admin": openAdminDashboard(username); break;
            }
        });
    }

    private void addDummyData() {
        househelps.add(new Househelp("Anita", "9am-5pm", "₹15,000", "Cleaning, Cooking"));
        househelps.add(new Househelp("Priya", "10am-6pm", "₹14,000", "Cleaning"));
        househelps.add(new Househelp("Sita", "8am-4pm", "₹16,000", "Cooking, Babysitting"));

        vendors.add(new Vendor("Vendor A", "1234567890", "Plumbing", 4));
        vendors.add(new Vendor("Vendor B", "0987654321", "Cooking", 5));
        vendors.add(new Vendor("Vendor C", "1122334455", "Electrical", 3));

        Complaint c1 = new Complaint("#1001", "Repair needed in A101", "Plumbing", "High", "Pending");
        c1.updates.add(new StatusUpdate("Pending", "Complaint created"));
        complaints.add(c1);

        Complaint c2 = new Complaint("#1002", "Plumbing issue in B203", "Plumbing", "Medium", "Working");
        c2.updates.add(new StatusUpdate("Working", "Vendor started work"));
        complaints.add(c2);

        Complaint c3 = new Complaint("#1003", "Electrical fault in C305", "Electrical", "Low", "Resolved");
        c3.updates.add(new StatusUpdate("Resolved", "Issue fixed and verified"));
        complaints.add(c3);
    }

    // Renderer for complaint status colors in vendor module
    class ComplaintCellRenderer extends DefaultListCellRenderer {
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Complaint) {
                Complaint complaint = (Complaint) value;
                switch(complaint.status) {
                    case "Pending": c.setBackground(new Color(255, 102, 102)); break; // Light Red
                    case "Working": c.setBackground(new Color(255, 255, 153)); break; // Light Yellow
                    case "Resolved": c.setBackground(new Color(153, 255, 153)); break; // Light Green
                    default: c.setBackground(Color.WHITE);
                }
                if(isSelected) {
                    c.setBackground(c.getBackground().darker());
                }
            }
            return c;
        }
    }

    // Vendor Dashboard
void openVendorDashboard(String username) {
    JFrame frame = new JFrame("Vendor Dashboard - " + username);
    frame.setSize(900, 600);
    frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
    frame.setLayout(new BorderLayout());
    frame.getContentPane().setBackground(Color.WHITE);

    JLabel label = new JLabel("Vendor Dashboard", SwingConstants.CENTER);
    label.setFont(new Font("Arial", Font.BOLD, 24));
    label.setBorder(new EmptyBorder(20, 0, 20, 0));
    frame.add(label, BorderLayout.NORTH);

    DefaultListModel<Complaint> complaintListModel = new DefaultListModel<>();
    for (Complaint c : complaints) complaintListModel.addElement(c);

    JList<Complaint> complaintList = new JList<>(complaintListModel);
    complaintList.setCellRenderer(new ComplaintCellRenderer());
    complaintList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    JScrollPane scroll = new JScrollPane(complaintList);
    scroll.setPreferredSize(new Dimension(400, 500));

    JPanel detailPanel = new JPanel();
    detailPanel.setLayout(new BoxLayout(detailPanel, BoxLayout.Y_AXIS));
    detailPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

    JLabel compDetail = new JLabel("Select a complaint to see details");
    compDetail.setFont(new Font("Tahoma", Font.PLAIN, 16));
    detailPanel.add(compDetail);
    detailPanel.add(Box.createVerticalStrut(10));

    JLabel statusLabel = new JLabel();
    JLabel priorityLabel = new JLabel();
    JLabel typeLabel = new JLabel();
    detailPanel.add(new JLabel("Status:"));
    detailPanel.add(statusLabel);
    detailPanel.add(Box.createVerticalStrut(8));
    detailPanel.add(new JLabel("Priority:"));
    detailPanel.add(priorityLabel);
    detailPanel.add(Box.createVerticalStrut(8));
    detailPanel.add(new JLabel("Type:"));
    detailPanel.add(typeLabel);
    detailPanel.add(Box.createVerticalStrut(12));

    DefaultListModel<String> updateListModel = new DefaultListModel<>();
    JList<String> updateList = new JList<>(updateListModel);
    JScrollPane updateScroll = new JScrollPane(updateList);
    updateScroll.setPreferredSize(new Dimension(400, 200));
    detailPanel.add(updateScroll);

    JButton updateStatusBtn = new JButton("Update Complaint Status & Add Photos");
    updateStatusBtn.setFont(new Font("Tahoma", Font.BOLD, 14));
    updateStatusBtn.setBackground(new Color(59, 89, 182));
    updateStatusBtn.setForeground(Color.WHITE);
    updateStatusBtn.setFocusPainted(false);
    JPanel btnPanel = new JPanel();
    btnPanel.setBackground(Color.WHITE);
    btnPanel.add(updateStatusBtn);

    complaintList.addListSelectionListener(e -> {
        if (!e.getValueIsAdjusting()) {
            Complaint selected = complaintList.getSelectedValue();
            if (selected != null) {
                compDetail.setText("<html><b>Details: </b>" + selected.detail + "<br><b>Complaint ID:</b> " + selected.id + "</html>");
                statusLabel.setText(selected.status);
                priorityLabel.setText(selected.priority);
                typeLabel.setText(selected.type);

                updateListModel.clear();
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");
                for (StatusUpdate update : selected.updates) {
                    updateListModel.addElement(sdf.format(update.timestamp) + " - " + update.status + ": " + update.note);
                }
            } else {
                compDetail.setText("Select a complaint to see details");
                statusLabel.setText("");
                priorityLabel.setText("");
                typeLabel.setText("");
                updateListModel.clear();
            }
        }
    });

    updateStatusBtn.addActionListener(e -> {
        Complaint selected = complaintList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(frame, "Select a complaint first");
            return;
        }

        String[] statuses = {"Pending", "Working", "Resolved"};
        JPanel panel = new JPanel(new GridLayout(0, 1));
        JComboBox<String> statusCombo = new JComboBox<>(statuses);
        statusCombo.setSelectedItem(selected.status);

        JTextField noteField = new JTextField();

        java.util.ArrayList<ImageIcon> chosenPhotos = new java.util.ArrayList<>();
        JButton addPhotosBtn = new JButton("Add Progress Photos");
        addPhotosBtn.addActionListener(ev -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setMultiSelectionEnabled(true);
            chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Image files", "jpg", "png", "jpeg", "gif"));
            int res = chooser.showOpenDialog(frame);
            if (res == JFileChooser.APPROVE_OPTION) {
                java.io.File[] files = chooser.getSelectedFiles();
                for (java.io.File f : files) {
                    chosenPhotos.add(new ImageIcon(f.getAbsolutePath()));
                }
                JOptionPane.showMessageDialog(frame, chosenPhotos.size() + " photo(s) selected");
            }
        });

        panel.add(new JLabel("Select Status:"));
        panel.add(statusCombo);
        panel.add(new JLabel("Progress Note:"));
        panel.add(noteField);
        panel.add(addPhotosBtn);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Update Complaint Status", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String newStatus = (String) statusCombo.getSelectedItem();
            String note = noteField.getText().trim();
            if (newStatus != null && !newStatus.isEmpty()) {
                selected.status = newStatus;
                StatusUpdate update = new StatusUpdate(newStatus, note.isEmpty() ? "No additional notes" : note);
                update.images.addAll(chosenPhotos);
                selected.updates.add(update);

                notifications.add("Vendor " + username + " updated complaint " + selected.id + " to " + newStatus);

                updateListModel.clear();
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");
                for (StatusUpdate su : selected.updates) {
                    updateListModel.addElement(sdf.format(su.timestamp) + " - " + su.status + ": " + su.note);
                }
                complaintList.repaint();

                JOptionPane.showMessageDialog(frame, "Status updated successfully.");
            }
        }
    });

    JPanel mainPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    mainPanel.add(scroll);
    JPanel rightPanel = new JPanel(new BorderLayout());
    rightPanel.add(detailPanel, BorderLayout.CENTER);
    rightPanel.add(btnPanel, BorderLayout.SOUTH);
    rightPanel.setPreferredSize(new Dimension(450, 600));
    mainPanel.add(rightPanel);

    frame.add(mainPanel);
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
}

    // Resident Dashboard
    void openResidentDashboard(String username) {
        JFrame frame = new JFrame("Resident Dashboard - " + username);
        frame.setSize(900, 600);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane();

        // Dashboard Tab
        JPanel dashboardPanel = new JPanel(new BorderLayout());
        dashboardPanel.add(new JLabel("Welcome, " + username + "! Resident Dashboard", SwingConstants.CENTER), BorderLayout.CENTER);
        tabbedPane.addTab("Dashboard", dashboardPanel);

        // Create Complaint Tab with dropdowns and priority
        JPanel complaintPanel = new JPanel(null);

        JLabel lblDetail = new JLabel("Enter your complaint details:");
        lblDetail.setBounds(20, 20, 200, 25);
        JTextArea complaintArea = new JTextArea();
        complaintArea.setBounds(20, 50, 700, 100);
        complaintArea.setLineWrap(true);
        complaintArea.setWrapStyleWord(true);

        JLabel lblType = new JLabel("Select Complaint Type:");
        lblType.setBounds(20, 160, 200, 25);
        String[] complaintTypes = {"Plumbing", "Electrical", "Cleaning", "Cooking", "Miscellaneous"};
        JComboBox<String> typeCombo = new JComboBox<>(complaintTypes);
        typeCombo.setBounds(200, 160, 150, 25);

        JLabel lblPriority = new JLabel("Select Priority Level:");
        lblPriority.setBounds(400, 160, 150, 25);
        String[] priorities = {"Low", "Medium", "High"};
        JComboBox<String> priorityCombo = new JComboBox<>(priorities);
        priorityCombo.setBounds(550, 160, 150, 25);

        JButton submitComplaintBtn = new JButton("Submit Complaint");
        submitComplaintBtn.setBounds(350, 210, 180, 30);

        complaintPanel.add(lblDetail);
        complaintPanel.add(complaintArea);
        complaintPanel.add(lblType);
        complaintPanel.add(typeCombo);
        complaintPanel.add(lblPriority);
        complaintPanel.add(priorityCombo);
        complaintPanel.add(submitComplaintBtn);

        submitComplaintBtn.addActionListener(ev -> {
            String detail = complaintArea.getText().trim();
            if (detail.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Complaint details cannot be empty.");
                return;
            }
            String type = (String) typeCombo.getSelectedItem();
            String priority = (String) priorityCombo.getSelectedItem();
            String id = "#"+(1000+complaints.size()+1);
            Complaint newComplaint = new Complaint(id, detail, type, priority, "Pending");
            complaints.add(newComplaint);
            notifications.add("New complaint submitted by "+username+", ID: "+id);
            JOptionPane.showMessageDialog(frame, "Complaint submitted with ID: " + id);
            complaintArea.setText("");
        });

        tabbedPane.addTab("Create Complaint", complaintPanel);

        // Track Complaint Tab with timeline & updates
        JPanel trackPanel = new JPanel(new BorderLayout());
        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Enter Complaint ID:"));
        JTextField complaintIdField = new JTextField(10);
        JButton searchBtn = new JButton("Search");
        inputPanel.add(complaintIdField);
        inputPanel.add(searchBtn);

        JTextArea complaintDetailsArea = new JTextArea();
        complaintDetailsArea.setEditable(false);
        JScrollPane detailsScroll = new JScrollPane(complaintDetailsArea);

        searchBtn.addActionListener(e -> {
            String searchId = complaintIdField.getText().trim();
            if (searchId.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Enter Complaint ID.");
                return;
            }
            Complaint found = null;
            for (Complaint c : complaints) {
                if (c.id.equalsIgnoreCase(searchId)) {
                    found = c;
                    break;
                }
            }
            if (found == null) {
                complaintDetailsArea.setText("Complaint ID " + searchId + " not found.");
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("Details: ").append(found.detail).append("\n");
                sb.append("Type: ").append(found.type).append("\n");
                sb.append("Priority: ").append(found.priority).append("\n");
                sb.append("Status: ").append(found.status).append("\n");
                sb.append("Timeline and Progress Updates:\n");
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                for (StatusUpdate su : found.updates) {
                    sb.append(sdf.format(su.timestamp)).append(" - ").append(su.status)
                      .append(": ").append(su.note).append("\n");
                    for (ImageIcon icon : su.images) {
                        sb.append("[Photo attached]\n");
                    }
                    sb.append("\n");
                }
                complaintDetailsArea.setText(sb.toString());
            }
        });

        trackPanel.add(inputPanel, BorderLayout.NORTH);
        trackPanel.add(detailsScroll, BorderLayout.CENTER);
        tabbedPane.addTab("Track Complaint", trackPanel);

        // Maintenance Payments Tab
        JPanel paymentPanel = new JPanel(null);
        JLabel label = new JLabel("Enter Maintenance Amount (₹):");
        label.setBounds(20, 30, 200, 25);
        JTextField amountField = new JTextField();
        amountField.setBounds(220, 30, 150, 25);
        JButton payBtn = new JButton("Pay Now");
        payBtn.setBounds(180, 70, 100, 30);
        paymentPanel.add(label);
        paymentPanel.add(amountField);
        paymentPanel.add(payBtn);
        payBtn.addActionListener(ev -> {
            String amt = amountField.getText().trim();
            if (!amt.isEmpty()) {
                notifications.add("Maintenance payment from " + username + ": ₹" + amt);
                JOptionPane.showMessageDialog(frame, "Payment of ₹" + amt + " successful!");
                amountField.setText("");
            } else {
                JOptionPane.showMessageDialog(frame, "Enter an amount.");
            }
        });
        tabbedPane.addTab("Maintenance Payments", paymentPanel);

        frame.add(tabbedPane);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // Admin Dashboard
    void openAdminDashboard(String username) {
        JFrame frame = new JFrame("Admin Dashboard - " + username);
        frame.setSize(900, 600);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        statsPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Open complaints count
        long openComplaints = complaints.stream().filter(c -> !c.status.equalsIgnoreCase("Resolved")).count();
        JLabel openComplaintsLbl = createStatLabel("Open Complaints", String.valueOf(openComplaints), Color.RED);

        // Active Vendors count
        int activeVendors = vendors.size();
        JLabel activeVendorsLbl = createStatLabel("Active Vendors", String.valueOf(activeVendors), Color.BLUE);

        // Resolved this week (simulate by counting resolved complaints)
        long resolvedThisWeek = complaints.stream().filter(c -> c.status.equalsIgnoreCase("Resolved")).count();
        JLabel resolvedLbl = createStatLabel("Resolved This Week", String.valueOf(resolvedThisWeek), Color.GREEN);

        // Househelps count
        int househelpCount = househelps.size();
        JLabel househelpsLbl = createStatLabel("House Helps", String.valueOf(househelpCount), Color.ORANGE);

        statsPanel.add(openComplaintsLbl);
        statsPanel.add(activeVendorsLbl);
        statsPanel.add(resolvedLbl);
        statsPanel.add(househelpsLbl);

        // Tabbed pane for vendor management and content moderation
        JTabbedPane tabbedPane = new JTabbedPane();

        // Vendor Management Panel showing vendors with details
        String[] colNames = {"Name", "Stars", "Contact", "Category"};
        Object[][] vendorData = new Object[vendors.size()][4];
        for (int i = 0; i < vendors.size(); i++) {
            Vendor v = vendors.get(i);
            vendorData[i][0] = v.name;
            vendorData[i][1] = v.stars;
            vendorData[i][2] = v.contact;
            vendorData[i][3] = v.category;
        }
        JTable vendorTable = new JTable(vendorData, colNames);
        JScrollPane vendorScroll = new JScrollPane(vendorTable);
        JPanel vendorMgmtPanel = new JPanel(new BorderLayout());
        vendorMgmtPanel.add(vendorScroll, BorderLayout.CENTER);
        tabbedPane.addTab("Vendor Management", vendorMgmtPanel);

        // Content Moderation (simple text area)
        JTextArea contentArea = new JTextArea("Content items for moderation...");
        JScrollPane contentScroll = new JScrollPane(contentArea);
        tabbedPane.addTab("Content Moderation", contentScroll);

        mainPanel.add(statsPanel, BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        frame.add(mainPanel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private JLabel createStatLabel(String title, String value, Color color) {
        JLabel label = new JLabel("<html><center><h2>" + value + "</h2><br>" + title + "</center></html>", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 22));
        label.setOpaque(true);
        label.setBackground(color);
        label.setForeground(Color.WHITE);
        label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        label.setPreferredSize(new Dimension(200, 100));
        return label;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new HelperAppIntegrated().setVisible(true);
        });
    }
}