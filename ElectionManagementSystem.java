import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.sql.ResultSet;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.HashMap;
import java.util.Map;





public class ElectionManagementSystem {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ElectionManagementSystem());
    }

    public ElectionManagementSystem() {
        JFrame frame = new JFrame("Election Management System");
        frame.setLayout(new BorderLayout());
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Admin Login", new AdminLogin());
        tabbedPane.addTab("Candidates Detail", new Candidates());
	
        tabbedPane.addTab("Voter SignIn", new Voters());
	tabbedPane.addTab("Voter Form", new VoterForm());

        tabbedPane.addTab("Voting process", new Voting());
   	tabbedPane.addTab("Result", new Result());
        frame.add(tabbedPane, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    public class DatabaseConnection {
        public static Connection getConnection() throws SQLException {
            String jdbcUrl = "jdbc:oracle:thin:@localhost:1521:xe";
            String dbUsername = "1DA21CS167";
            String dbPassword = "Tiger";

            try {
                Class.forName("oracle.jdbc.driver.OracleDriver");
                return DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword);
            } catch (ClassNotFoundException e) {
                throw new SQLException("Oracle JDBC driver not found", e);
            }catch (SQLException e) {
            throw new SQLException("Error connecting to the database", e);
            }
        }
    }

    private class AdminLogin extends JPanel {
        private JTextField usernameField;
        private JPasswordField passwordField;

        public AdminLogin() {
            setLayout(new BorderLayout());
            JPanel panel = new JPanel();
            JLabel usernameLabel = new JLabel("Username:");
            JLabel passwordLabel = new JLabel("Password:");
            usernameField = new JTextField(20);
            passwordField = new JPasswordField(20);
            JButton loginButton = new JButton("Login");

            loginButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String username = usernameField.getText();
                    String password = new String(passwordField.getPassword());

                    if (authenticate(username, password)) {
                        JOptionPane.showMessageDialog(null, "Login successful for admin");

                        try (Connection connection = DatabaseConnection.getConnection()) {
                            int electionId = Integer.parseInt(JOptionPane.showInputDialog("Enter Election ID:"));
                            String electionName = JOptionPane.showInputDialog("Enter Election Name:");
                            Date electionDate = parseDateInput(JOptionPane.showInputDialog("Enter Election Date (yyyy-MM-dd):"));

                            // Insert election details
                            if (electionDate != null) {
                                insertElectionDetails(connection, electionId, electionName, electionDate);
                            } else {
                                JOptionPane.showMessageDialog(null, "Invalid date format");
                            }
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(null, "Error connecting to the database");
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Invalid credentials");
                    }
                }
            });

            panel.add(usernameLabel);
            panel.add(usernameField);
            panel.add(passwordLabel);
            panel.add(passwordField);
            panel.add(loginButton);

            add(panel);
            setVisible(true);
        }

        private boolean authenticate(String username, String password) {
           
            String adminUsername = "admin";
            String adminPassword = "admin123";
            return username.equals(adminUsername) && password.equals(adminPassword);
        }

        private Date parseDateInput(String dateInput) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                java.util.Date parsedDate = dateFormat.parse(dateInput);
                return new Date(parsedDate.getTime());
            } catch (IllegalArgumentException | ParseException e) {
                e.printStackTrace();
                return null;
            }
        }

        private void insertElectionDetails(Connection connection, int electionId, String electionName, Date electionDate) throws SQLException {
            String sql = "INSERT INTO election (election_id, election_name, election_date) VALUES (?, ?, ?)";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, electionId);
                preparedStatement.setString(2, electionName);
                preparedStatement.setDate(3, electionDate);

              
                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(null, "Election details inserted successfully");
                } else {
                    JOptionPane.showMessageDialog(null, "Failed to insert election details");
                }
            }
        }
    }
    private class Candidates extends JPanel {
    private JTextField countryField;
    private JTextField ageField;

    public Candidates() {
        setLayout(new BorderLayout());
        JPanel panel = new JPanel();
        JLabel countryLabel = new JLabel("Your country:");
        JLabel ageLabel = new JLabel("Your present age:");
        countryField = new JTextField(20);
        ageField = new JTextField(20);
        JButton checkButton = new JButton("Check for Nomination");

        checkButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String country = countryField.getText();
                int age = Integer.parseInt(ageField.getText());

                if (authenticate(country, age)) {
                    JOptionPane.showMessageDialog(null, "Candidate can perform in nomination process");

                    try (Connection connection = DatabaseConnection.getConnection()) {
                        int candidate_id = Integer.parseInt(JOptionPane.showInputDialog("Enter Candidate id:"));
                        String candidate_name = JOptionPane.showInputDialog("Enter Candidate's Name:");
                        String candidate_party = JOptionPane.showInputDialog("Enter Candidate's party:");
                        int election_id = Integer.parseInt(JOptionPane.showInputDialog("Enter Election id:"));

                       
                        if (election_id != 0) {
                            insertCandidatesDetails(connection, candidate_id, candidate_name, candidate_party, election_id);
                        } else {
                            JOptionPane.showMessageDialog(null, "Invalid election_id ");
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Error connecting to the database");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "your are not eligible to contest in election");
                }
            }
        });

        panel.add(countryLabel);
        panel.add(countryField);
        panel.add(ageLabel);
        panel.add(ageField);
        panel.add(checkButton);

        add(panel);
        setVisible(true);
    }

    private boolean authenticate(String country, int age_no) {
       
        String hardcodedCountry = "India";
        int hardcodedAge = 25;


        return country.equals(hardcodedCountry) && age_no > hardcodedAge;
    }

    private void insertCandidatesDetails(Connection connection, int candidate_id, String candidate_name, String candidate_party, int election_id) throws SQLException {
        String sql = "INSERT INTO candidates (candidate_id, candidate_name, candidate_party, election_id) VALUES (?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, candidate_id);
            preparedStatement.setString(2, candidate_name);
            preparedStatement.setString(3, candidate_party);
            preparedStatement.setInt(4, election_id);

            
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Candidate details inserted successfully");
            } else {
                JOptionPane.showMessageDialog(null, "Failed to insert candidate details");
            }
        }
    }
 }


 private class Voters extends JPanel {
    private JTextField countryField;
    private JTextField ageField;

    public Voters() {
        setLayout(new BorderLayout());
        JPanel panel = new JPanel();
        JLabel countryLabel = new JLabel("Your country:");
        JLabel ageLabel = new JLabel("Your present age:");
        countryField = new JTextField(20);
        ageField = new JTextField(20);
        JButton checkButton = new JButton("Check for vote");

        checkButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String country = countryField.getText();
                int age = Integer.parseInt(ageField.getText());

                if (authenticate(country, age)) {
                    JOptionPane.showMessageDialog(null, "your are eligible for voting process");

                    try (Connection connection = DatabaseConnection.getConnection()) {
                        long voter_id = Long.parseLong(JOptionPane.showInputDialog("Enter voter_id (aadhar_no):"));
                        String voter_name = JOptionPane.showInputDialog("Enter voter's Name:");
                        String voter_address = JOptionPane.showInputDialog("Enter voter's address:");
                        String voter_gender = JOptionPane.showInputDialog("Enter voter's gender:");

                        
                        if (voter_id != 0) {
                            insertVotersDetails(connection, voter_id, voter_name, voter_address, voter_gender);
                        } else {
                            JOptionPane.showMessageDialog(null, "Invalid voter_id ");
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Error connecting to the database");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "your are not eligible for voting");
                }
            }
        });

        panel.add(countryLabel);
        panel.add(countryField);
        panel.add(ageLabel);
        panel.add(ageField);
        panel.add(checkButton);

        add(panel);
        setVisible(true);
    }

    private boolean authenticate(String country, int age_no) {
       
        String hardcodedCountry = "India";
        int hardcodedAge = 18;

        
        return country.equals(hardcodedCountry) && age_no > hardcodedAge;
    }

    private void insertVotersDetails(Connection connection, long voter_id, String voter_name, String voter_address, String voter_gender) throws SQLException {
        String sql = "INSERT INTO voters (voter_id, voter_name, voter_address, voter_gender) VALUES (?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, voter_id);
            preparedStatement.setString(2, voter_name);
            preparedStatement.setString(3, voter_address);
            preparedStatement.setString(4, voter_gender);

            
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "voter details inserted successfully!,  now you can vote");
            } else {
                JOptionPane.showMessageDialog(null, "Failed to insert voters details");
            }
        }
    }
 }




public class VoterForm extends JPanel {
    private JTextField voterIdField;
    private JTextField detailedAddressField;
    private JTextField constituencyField;
    private JTextField mobileNumberField;
    private JTextField emailAddressField;
    private JTextField reasonField;

    public VoterForm() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(5, 5, 5, 5);  // Padding

        JLabel voterIdLabel = new JLabel("Aadhar Number (voter_id):");
        JLabel detailedAddressLabel = new JLabel("Detailed Address:");
        JLabel constituencyLabel = new JLabel("Constituency:");
        JLabel mobileNumberLabel = new JLabel("Mobile Number:");
        JLabel emailAddressLabel = new JLabel("Email Address:");
        JLabel reasonLabel = new JLabel("Reason for Online Voting:");

        voterIdField = new JTextField(12);
        detailedAddressField = new JTextField(20);
        constituencyField = new JTextField(20);
        mobileNumberField = new JTextField(15);
        emailAddressField = new JTextField(20);
        reasonField = new JTextField(20);

        JButton submitButton = new JButton("Submit");

        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveVoterDetailsToDatabase();
                // No need to dispose() or close the window here
            }
        });

        
        addComponent(voterIdLabel, gbc, 0, 0);
        addComponent(voterIdField, gbc, 1, 0);
        addComponent(detailedAddressLabel, gbc, 0, 1);
        addComponent(detailedAddressField, gbc, 1, 1);
        addComponent(constituencyLabel, gbc, 0, 2);
        addComponent(constituencyField, gbc, 1, 2);
        addComponent(mobileNumberLabel, gbc, 0, 3);
        addComponent(mobileNumberField, gbc, 1, 3);
        addComponent(emailAddressLabel, gbc, 0, 4);
        addComponent(emailAddressField, gbc, 1, 4);
        addComponent(reasonLabel, gbc, 0, 5);
        addComponent(reasonField, gbc, 1, 5);
        addComponent(submitButton, gbc, 0, 6, 2, 1);


    }

    private void addComponent(Component component, GridBagConstraints gbc, int gridx, int gridy) {
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        add(component, gbc);
    }

    private void addComponent(Component component, GridBagConstraints gbc, int gridx, int gridy, int gridwidth, int gridheight) {
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        gbc.gridwidth = gridwidth;
        gbc.gridheight = gridheight;
        add(component, gbc);
    }

    private void saveVoterDetailsToDatabase() {
        try (Connection connection = DatabaseConnection.getConnection()) {
            if (connection != null) {
                String insertSql = "INSERT INTO voters_address (voter_id, detailed_address, constituency, mobile_number, email_address, reason_for_online_voting) " +
                        "VALUES (?,?,?,?,?,?)";

                try (PreparedStatement preparedStatement = connection.prepareStatement(insertSql)) {
                    long voterId = Long.parseLong(voterIdField.getText());

                    preparedStatement.setLong(1, voterId);
                    preparedStatement.setString(2, detailedAddressField.getText());
                    preparedStatement.setString(3, constituencyField.getText());
                    preparedStatement.setString(4, mobileNumberField.getText());
                    preparedStatement.setString(5, emailAddressField.getText());
                    preparedStatement.setString(6, reasonField.getText());

                    int rowsAffected = preparedStatement.executeUpdate();
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(null, "Your detailed form submitted successfully");
                    } else {
                        System.out.println("Failed to save voter details.");
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error saving voter details to the database: " + ex.getMessage());
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Invalid Aadhar number format.");
        }
    }
}


public class Voting extends JPanel {
    private JTextField aadharField;
    private long voterId;
    private int vote_id=1;
    private int electionId=1;
    private static final String RECORD_VOTE_QUERY = "INSERT INTO voting (voter_id, candidate_id, election_id) VALUES (?, ?, ?)";
    private static final Logger LOGGER = Logger.getLogger(Voting.class.getName());

    public Voting() {
        setLayout(new BorderLayout());
        JPanel panel = new JPanel();

       
        JLabel aadharLabel = new JLabel("Enter Aadhar Number (voter_id):");
        aadharField = new JTextField(20);
        JButton authenticateButton = new JButton("Authenticate and Vote");

        authenticateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                voterId = Long.parseLong(aadharField.getText());

                
                if (isVoterEligible(voterId)) {
                   
                    if (!hasVoterVoted(voterId, 1)) { // Assuming election_id=1 for MLA election
                      
                        displayCandidates(1); // Assuming election_id=1 for MLA election
                    } else {
                        JOptionPane.showMessageDialog(null, "You have already voted in the MLA election");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid Aadhar Number or you are not eligible to vote");
                }
            }
        });

        panel.add(aadharLabel);
        panel.add(aadharField);
        panel.add(authenticateButton);

        add(panel);
        setVisible(true);
    }

    private boolean isVoterEligible(long voterId) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String sql = "SELECT COUNT(*) FROM voters WHERE voter_id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setLong(1, voterId);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        int count = resultSet.getInt(1);
                        return count > 0;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error checking voter eligibility from the database");
        }

        return false;
    }

    private boolean hasVoterVoted(long voterId, int electionId) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String sql = "SELECT COUNT(*) FROM voting WHERE voter_id = ? AND election_id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setLong(1, voterId);
                preparedStatement.setInt(2, electionId);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        int count = resultSet.getInt(1);
                        return count > 0;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error checking voter vote status from the database");
        }

        return false;
    }

   private void displayCandidates(int electionId) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String sql = "SELECT candidate_id, candidate_name, candidate_party FROM candidates WHERE election_id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, electionId);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    List<Candidate> candidates = new ArrayList<>();

                    while (resultSet.next()) {
                        int candidateId = resultSet.getInt("candidate_id");
                        String candidateName = resultSet.getString("candidate_name");
                        String partyName = resultSet.getString("candidate_party");
                        candidates.add(new Candidate(candidateId, candidateName, partyName));
                    }

                    displayCandidatesInUI(candidates);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error retrieving candidates from the database");
        }
    }

          private void displayCandidatesInUI(List<Candidate> candidates) {
        JComboBox<Candidate> candidateComboBox = new JComboBox<>(candidates.toArray(new Candidate[0]));

        int option = JOptionPane.showConfirmDialog(
                null,
                candidateComboBox,
                "Select Candidate",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

       
        if (option == JOptionPane.OK_OPTION) {
            Candidate selectedCandidate = (Candidate) candidateComboBox.getSelectedItem();
            recordVote(selectedCandidate, voterId, 1); // Assuming election_id=1 for MLA election
        }
    }


private void recordVote(Candidate selectedCandidate, long voterId, int electionId) {
    try (Connection connection = DatabaseConnection.getConnection()) {
       
        connection.setAutoCommit(false);

        try (PreparedStatement preparedStatement = connection.prepareStatement(RECORD_VOTE_QUERY, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setLong(1, voterId);
            preparedStatement.setInt(2, selectedCandidate.getCandidateId());
            preparedStatement.setInt(3, electionId);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
               
                connection.commit();

                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        // Ignore the retrieved value since it's not used
                    }
                } catch (SQLException e) {
                    // Log the exception for future analysis
                    LOGGER.log(Level.WARNING, "Exception while retrieving generated key", e);
                }

                JOptionPane.showMessageDialog(null, "Vote recorded for " + selectedCandidate.getCandidateName());
            } else {
               
                connection.rollback();

                JOptionPane.showMessageDialog(null, "Failed to record the vote");
            }
        }
    } catch (SQLException e) {
        LOGGER.log(Level.SEVERE, "Error recording the vote in the database", e);
        JOptionPane.showMessageDialog(null, "Error recording the vote in the database");
    }
}




   private static class Candidate {
        private int candidateId;
        private String candidateName;
        private String partyName;

        public Candidate(int candidateId, String candidateName, String partyName) {
            this.candidateId = candidateId;
            this.candidateName = candidateName;
            this.partyName = partyName;
        }

        public int getCandidateId() {
            return candidateId;
        }

        public String getCandidateName() {
            return candidateName;
        }

        public String getPartyName() {
            return partyName;
        }

        @Override
        public String toString() {
            return candidateName + " (" + partyName + ")";
        }
    }

 }


public class Result extends JPanel {
    private JTextArea resultTextArea;  // Use JTextArea instead of JLabel
    private JScrollPane scrollPane;
    private JButton updateButton;

    public Result() {
        setLayout(new BorderLayout());

        resultTextArea = new JTextArea();
        resultTextArea.setEditable(false);  // Make it non-editable
        scrollPane = new JScrollPane(resultTextArea);
        add(scrollPane, BorderLayout.CENTER);

       
        updateButton = new JButton("Update Results");
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateElectionResults();
            }
        });
        add(updateButton, BorderLayout.SOUTH);

      
        displayElectionResults();

        setVisible(true);
    }

    private void displayElectionResults() {
        try (Connection connection = DatabaseConnection.getConnection()) {
            int electionId = 1; // Replace with your actual logic for getting the election ID

            String sql = "SELECT c.candidate_name AS candidate_name, COUNT(*) AS vote_count " +
                    "FROM voting v " +
                    "JOIN candidates c ON v.candidate_id = c.candidate_id " +
                    "WHERE v.election_id = ? " +
                    "GROUP BY c.candidate_name";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, electionId);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    Map<String, Integer> results = new HashMap<>();

                    while (resultSet.next()) {
                        String candidateName = resultSet.getString("candidate_name");
                        int voteCount = resultSet.getInt("vote_count");
                        results.put(candidateName, voteCount);
                    }

                    displayResultsInUI(results);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error retrieving election results from the database: " + e.getMessage());
        }
    }

    private void displayResultsInUI(Map<String, Integer> results) {
        SwingUtilities.invokeLater(() -> {
            StringBuilder textContent = new StringBuilder("Election Results:\n\n");

            for (Map.Entry<String, Integer> entry : results.entrySet()) {
                textContent.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }

            resultTextArea.setText(textContent.toString());
        });
    }

    private void updateElectionResults() {
       
        displayElectionResults();
    }
}

}
