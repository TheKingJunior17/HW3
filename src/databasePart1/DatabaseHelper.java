package databasePart1;

import application.User;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import models.Answer;

public class DatabaseHelper {

    static final String JDBC_DRIVER = "org.h2.Driver";
    static final String DB_URL = "jdbc:h2:~/FoundationDatabase";
    static final String USER = "sa";
    static final String PASS = "";

    private Connection connection;
    private Statement statement;

    public DatabaseHelper() {
    }

    public void connectToDatabase() throws SQLException {
        try {
            Class.forName(JDBC_DRIVER);
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            statement = connection.createStatement();
            // You can use this command to clear the database and restart from fresh.
            //statement.execute("DROP ALL OBJECTS");
            createTables();
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver not found: " + e.getMessage());
        }
    }

    private void createTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            // User-related tables
            String userTable = "CREATE TABLE IF NOT EXISTS cse360users ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "userName VARCHAR(255) UNIQUE, "
                    + "password VARCHAR(255) NOT NULL, "
                    + "name VARCHAR(255), "
                    + "email VARCHAR(255));";
            stmt.execute(userTable);

            String userRolesTable = "CREATE TABLE IF NOT EXISTS UserRoles ("
                    + "userId INT, "
                    + "role VARCHAR(20), "
                    + "FOREIGN KEY (userId) REFERENCES cse360users(id), "
                    + "PRIMARY KEY (userId, role))";
            stmt.execute(userRolesTable);

            String invitationCodesTable = "CREATE TABLE IF NOT EXISTS InvitationCodes ("
                    + "code VARCHAR(10) PRIMARY KEY, "
                    + "isUsed BOOLEAN DEFAULT FALSE, "
                    + "expires_at TIMESTAMP)";
            stmt.execute(invitationCodesTable);

            // Question/answer tables
            String createQuestionsTable = "CREATE TABLE IF NOT EXISTS questions ( "
                    + "question_id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "question_title VARCHAR(255), "
                    + "question_text VARCHAR(255) NOT NULL, "
                    + "author VARCHAR(255) NOT NULL, "
                    + "creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
            stmt.execute(createQuestionsTable);

            // Answers table with the correct_answer column (Original modification)
            String createAnswersTable = "CREATE TABLE IF NOT EXISTS answers ( "
                    + "answer_id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "question_id INT NOT NULL, "
                    + "answer_text TEXT NOT NULL, "
                    + "author VARCHAR(255) NOT NULL, "
                    + "correct_answer BOOLEAN DEFAULT FALSE, " 
                    + "creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                    + "FOREIGN KEY (question_id) REFERENCES questions(question_id))";
            stmt.execute(createAnswersTable);

            // Hema's additions: Clarification tables
            String createQuestionClarificationsTable = "CREATE TABLE IF NOT EXISTS QuestionClarifications ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "question_id INT NOT NULL, "
                    + "clarification_text TEXT NOT NULL, "
                    + "response_text TEXT, "
                    + "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                    + "FOREIGN KEY (question_id) REFERENCES questions(question_id))";
            stmt.execute(createQuestionClarificationsTable);

            String createAnswerClarificationsTable = "CREATE TABLE IF NOT EXISTS AnswerClarifications ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "answer_id INT NOT NULL, "
                    + "clarification_text TEXT NOT NULL, "
                    + "response_text TEXT, "
                    + "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                    + "FOREIGN KEY (answer_id) REFERENCES answers(answer_id))";
            stmt.execute(createAnswerClarificationsTable);
        }
    }

    private Set<String> getUserRoles(int userId) throws SQLException {
        Set<String> roles = new HashSet<>();
        String sql = "SELECT role FROM userroles WHERE userId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    roles.add(rs.getString("role"));
                }
            }
        }
        return roles;
    }
    
    public String getUserRole(String userName) {
        String query = "SELECT role FROM UserRoles WHERE userId = (SELECT id FROM cse360users WHERE userName = ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, userName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("role");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean deleteUser(String username) {
        String query = "DELETE FROM cse360users WHERE userName = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public User getUser(String username) {
        String query = "SELECT * FROM cse360users WHERE userName = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int userId = rs.getInt("id");
                String userName = rs.getString("userName");
                String password = rs.getString("password");
                String name = rs.getString("name");
                String email = rs.getString("email");
                Set<String> roles = getUserRoles(userId);
                return new User(userName, password, roles, name, email);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateUser(User user) {
        String query = "UPDATE cse360users SET password = ? WHERE userName = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, user.getPassword());
            pstmt.setString(2, user.getUserName());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isDatabaseEmpty() throws SQLException {
        String query = "SELECT COUNT(*) AS count FROM cse360users";
        ResultSet resultSet = statement.executeQuery(query);
        if (resultSet.next()) {
            return resultSet.getInt("count") == 0;
        }
        return true;
    }

    public boolean login(User user) throws SQLException {
        String query = "SELECT id, password, name, email FROM cse360users WHERE userName = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, user.getUserName());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int userId = rs.getInt("id");
                    String storedPassword = rs.getString("password");
                    if (user.getPassword().equals(storedPassword)) {
                        user.setRoles(getUserRoles(userId));
                        user.setName(rs.getString("name"));
                        user.setEmail(rs.getString("email"));
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void insertUserRole(int userId, String role) throws SQLException {
        String insertRole = "INSERT INTO UserRoles (userId, role) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertRole)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, role);
            pstmt.executeUpdate();
        }
    }

    public boolean doesUserExist(String userName) {
        String query = "SELECT COUNT(*) FROM cse360users WHERE userName = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, userName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void register(User user) throws SQLException {
        String insertUser = "INSERT INTO cse360users (userName, password, name, email) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, user.getUserName());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getName());
            pstmt.setString(4, user.getEmail());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int userId = generatedKeys.getInt(1);
                    for (String role : user.getRoles()) {
                        insertUserRole(userId, role);
                    }
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        }
    }

    public void addQuestion(String title, String text, String author) throws SQLException {
        String insertQuestion = "INSERT INTO questions (question_title, question_text, author) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertQuestion, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, title);
            pstmt.setString(2, text);
            pstmt.setString(3, author);
            pstmt.executeUpdate();
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int questionId = generatedKeys.getInt(1);
                    // Optionally use questionId if needed.
                }
            }
        }
    }

    public void addAnswer(int questionId, String answerText, String author) throws SQLException {
        String insertAnswer = "INSERT INTO answers (question_id, answer_text, author) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertAnswer)) {
            pstmt.setInt(1, questionId);
            pstmt.setString(2, answerText);
            pstmt.setString(3, author);
            pstmt.executeUpdate();
        }
    }

    public List<String> getAnswersForQuestion(int questionId) throws SQLException {
        List<String> answers = new ArrayList<>();
        String query = "SELECT answer_text, author FROM answers WHERE question_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, questionId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String formattedAnswer = rs.getString("author") + ": " + rs.getString("answer_text");
                    answers.add(formattedAnswer);
                }
            }
        }
        return answers;
    }

    public List<Answer> getAnswerVerifiedForQuestion(int questionId) throws SQLException {
        List<Answer> answers = new ArrayList<>();
        String query = "SELECT answer_id, answer_text, author, correct_answer FROM answers WHERE question_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, questionId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Answer answer = new Answer(questionId, rs.getString("answer_text"), rs.getString("author"));
                    answer.setAnswerId(rs.getInt("answer_id"));
                    answer.setCorrect(rs.getBoolean("correct_answer")); // Ensure Answer class has setCorrect(boolean)
                    answers.add(answer);
                }
            }
        }
        return answers;
    }

    public void updateCorrectAnswer(int answerId, boolean isCorrect) throws SQLException {
        String updateQuery = "UPDATE answers SET correct_answer = ? WHERE answer_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
            pstmt.setBoolean(1, isCorrect);
            pstmt.setInt(2, answerId);
            pstmt.executeUpdate();
        }
    }
    
    public Answer getAnswerById(int answerId) throws SQLException {
        String query = "SELECT question_id, answer_text, author, correct_answer, creation_date FROM answers WHERE answer_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, answerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int questionId = rs.getInt("question_id");
                    String answerText = rs.getString("answer_text");
                    String author = rs.getString("author");
                    boolean correct = rs.getBoolean("correct_answer");
                    Answer answer = new Answer(questionId, answerText, author);
                    answer.setAnswerId(answerId);
                    answer.setCorrect(correct);
                    return answer;
                }
            }
        }
        return null;
    }
    public Map<String, Object> getQuestionById(int questionId) throws SQLException {
        Map<String, Object> question = new HashMap<>();
        String query = "SELECT question_title, question_text, author, creation_date FROM questions WHERE question_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, questionId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    question.put("title", rs.getString("question_title"));
                    question.put("text", rs.getString("question_text"));
                    question.put("author", rs.getString("author"));
                    Timestamp creationDateTimestamp = rs.getTimestamp("creation_date");
                    if (creationDateTimestamp != null) {
                        question.put("creationDate", creationDateTimestamp.toLocalDateTime());
                    } else {
                        question.put("creationDate", null); // Handling null case, if creationDate is null in DB
                    }
                } else {
                    // Optionally handle case when no result is found
                    System.out.println("No question found with ID: " + questionId);
                }
            }
        }
        return question; // Return the question map (may be empty if no results)
    }

    public void deleteQuestion(int questionId) throws SQLException {
        String deleteQuestion = "DELETE FROM questions WHERE question_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteQuestion)) {
            pstmt.setInt(1, questionId);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("No question found with ID " + questionId);
            }
        }
    }

    public void updateQuestion(int questionId, String title, String text) throws SQLException {
        String updateQuestion = "UPDATE questions SET question_title = ?, question_text = ? WHERE question_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(updateQuestion)) {
            pstmt.setString(1, title);
            pstmt.setString(2, text);
            pstmt.setInt(3, questionId);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("No question found with ID " + questionId);
            }
        }
    }

    public void deleteAnswer(int answerId) throws SQLException {
        String deleteAnswer = "DELETE FROM answers WHERE answer_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteAnswer)) {
            pstmt.setInt(1, answerId);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("No answer found with ID " + answerId);
            }
        }
    }

    public void updateAnswer(int answerId, String answerText, String author) throws SQLException {
        String updateAnswer = "UPDATE answers SET answer_text = ?, author = ? WHERE answer_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(updateAnswer)) {
            pstmt.setString(1, answerText);
            pstmt.setString(2, author);
            pstmt.setInt(3, answerId);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("No answer found with ID " + answerId);
            }
        }
    }
    
    public List<Map<String, Object>> getAllQuestions() throws SQLException {
        List<Map<String, Object>> questions = new ArrayList<>();
        String sql = "SELECT question_id, question_title, question_text, author, creation_date FROM questions";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Map<String, Object> question = new HashMap<>();
                question.put("question_id", rs.getInt("question_id"));
                question.put("title", rs.getString("question_title"));
                question.put("text", rs.getString("question_text"));
                question.put("author", rs.getString("author"));
                question.put("creation_date", rs.getTimestamp("creation_date").toLocalDateTime());
                questions.add(question);
            }
        }
        return questions;
    }

    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String query = "SELECT id, userName, password, name, email FROM cse360users"; 
        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                int userId = rs.getInt("id");
                String userName = rs.getString("userName");
                String password = rs.getString("password");
                String name = rs.getString("name");
                String email = rs.getString("email");

                // Fetch roles from the UserRoles table
                Set<String> roles = getUserRoles(userId);

                // Create the User object
                User user = new User(userName, password, roles, name, email);
                users.add(user);
            }
        }
        return users;
    }
    
    public int getUserId(String username) throws SQLException {
        String query = "SELECT id FROM cse360users WHERE userName = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }
        return -1;
    }

    public boolean addRoleToUser(String username, String role) throws SQLException {
        int userId = getUserId(username);
        if (userId == -1) {
            return false;
        }
        Set<String> roles = getUserRoles(userId);
        if (roles.contains(role)) {
            return false;
        }
        insertUserRole(userId, role);
        return true;
    }

    public boolean doesColumnExist(String tableName, String columnName) throws SQLException {
        String sql = "SELECT * FROM " + tableName + " LIMIT 1";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            ResultSetMetaData metaData = rs.getMetaData();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                if (metaData.getColumnName(i).equalsIgnoreCase(columnName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void addQuestionTitleColumn() throws SQLException {
        String alterTableSQL = "ALTER TABLE questions ADD COLUMN question_title VARCHAR(255);";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(alterTableSQL);
        }
    }


    public void addQuestionClarification(int questionId, String clarificationText) throws SQLException {
        String sql = "INSERT INTO QuestionClarifications (question_id, clarification_text) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, questionId);
            pstmt.setString(2, clarificationText);
            pstmt.executeUpdate();
        }
    }

    public void addAnswerClarification(int answerId, String clarificationText) throws SQLException {
        String sql = "INSERT INTO AnswerClarifications (answer_id, clarification_text) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, answerId);
            pstmt.setString(2, clarificationText);
            pstmt.executeUpdate();
        }
    }

    public List<Map<String, Object>> getAllQuestionClarifications() throws SQLException {
        List<Map<String, Object>> clarifications = new ArrayList<>();
        String sql = "SELECT id AS clarification_id, question_id, clarification_text, response_text, timestamp FROM QuestionClarifications";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Map<String, Object> clar = new HashMap<>();
                clar.put("clarification_id", rs.getInt("clarification_id"));
                clar.put("question_id", rs.getInt("question_id"));
                clar.put("clarification_text", rs.getString("clarification_text"));
                clar.put("response_text", rs.getString("response_text"));
                clar.put("timestamp", rs.getTimestamp("timestamp"));
                clarifications.add(clar);
            }
        }
        return clarifications;
    }

    public List<Map<String, Object>> getAllAnswerClarifications() throws SQLException {
        List<Map<String, Object>> clarifications = new ArrayList<>();
        String sql = "SELECT id AS clarification_id, answer_id, clarification_text, response_text, timestamp FROM AnswerClarifications";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Map<String, Object> clar = new HashMap<>();
                clar.put("clarification_id", rs.getInt("clarification_id"));
                clar.put("answer_id", rs.getInt("answer_id"));
                clar.put("clarification_text", rs.getString("clarification_text"));
                clar.put("response_text", rs.getString("response_text"));
                clar.put("timestamp", rs.getTimestamp("timestamp"));
                clarifications.add(clar);
            }
        }
        return clarifications;
    }

    public void respondToQuestionClarification(int clarificationId, String responseText) throws SQLException {
        String sql = "UPDATE QuestionClarifications SET response_text = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, responseText);
            pstmt.setInt(2, clarificationId);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("No question clarification found with ID " + clarificationId);
            }
        }
    }

    public void respondToAnswerClarification(int clarificationId, String responseText) throws SQLException {
        String sql = "UPDATE AnswerClarifications SET response_text = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, responseText);
            pstmt.setInt(2, clarificationId);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("No answer clarification found with ID " + clarificationId);
            }
        }
    }

    public boolean doesQuestionExist(int questionId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM questions WHERE question_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, questionId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    public boolean doesAnswerExist(int answerId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM answers WHERE answer_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, answerId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    public void closeConnection() {
        try {
            if (statement != null) statement.close();
        } catch (SQLException se2) {
            se2.printStackTrace();
        }
        try {
            if (connection != null) connection.close();
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }
    public boolean validateInvitationCode(String code) {
    	String query = "SELECT expires_at FROM InvitationCodes WHERE code = ? AND isUsed = FALSE";
    	try (PreparedStatement pstmt = connection.prepareStatement(query)) {
    		pstmt.setString(1, code);
    		ResultSet rs = pstmt.executeQuery();
    		if (rs.next()) {
    			Timestamp expiresAt = rs.getTimestamp("expires_at");
    			Timestamp currentTime = new Timestamp(System.currentTimeMillis());
    			if (currentTime.after(expiresAt)) {
    				return false;
    			}
	         markInvitationCodeAsUsed(code);
	         return true;
	     }
	 } catch (SQLException e) {
	     e.printStackTrace();
	 }
	 return false;
	}
	
	private void markInvitationCodeAsUsed(String code) {
	 String query = "UPDATE InvitationCodes SET isUsed = TRUE WHERE code = ?";
	 try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	     pstmt.setString(1, code);
	     pstmt.executeUpdate();
	 } catch (SQLException e) {
	     e.printStackTrace();
	 	}
	  }

	public String generateInvitationCode() {
	 // Generate a random code (first 4 characters of a UUID)
	 String code = java.util.UUID.randomUUID().toString().substring(0, 4);
	 
	 // Set the expiration time to 10 minutes from now
	 java.time.LocalDateTime expirationTime = java.time.LocalDateTime.now().plusMinutes(10);
	 java.sql.Timestamp sqlExpirationTime = java.sql.Timestamp.valueOf(expirationTime);
	 
	 // SQL query to insert the invitation code into the InvitationCodes table
	 String query = "INSERT INTO InvitationCodes (code, expires_at) VALUES (?, ?)";
	 
	 try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	     pstmt.setString(1, code);
	     pstmt.setTimestamp(2, sqlExpirationTime);
	     pstmt.executeUpdate();
	 } catch (SQLException e) {
	     e.printStackTrace();
	 }
	 
	 return code;
		}

	public int countAdminUsers() throws SQLException {
	 String query = "SELECT COUNT(DISTINCT u.userName) AS adminCount " +
	                "FROM cse360users u " +
	                "JOIN UserRoles r ON u.id = r.userId " +
	                "WHERE LOWER(r.role) = 'admin'";
	 try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	     try (ResultSet rs = pstmt.executeQuery()) {
	         if (rs.next()) {
	             return rs.getInt("adminCount");
	         }
	     }
	 }
	 return 0;
	}
	
	//Removes a role from a user. Returns true if the role is successfully removed.
	public boolean removeRoleFromUser(String username, String role) throws SQLException {
	 int userId = getUserId(username);
	 if (userId == -1) {
	     return false; // User not found
	 }
	 String query = "DELETE FROM UserRoles WHERE userId = ? AND role = ?";
	 try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	     pstmt.setInt(1, userId);
	     pstmt.setString(2, role);
	     int affected = pstmt.executeUpdate();
	     return affected > 0;
	 }
	}
}