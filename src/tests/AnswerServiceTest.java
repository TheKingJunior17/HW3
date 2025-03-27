package tests;

import databasePart1.DatabaseHelper; // Import your DatabaseHelper
import models.Answer;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class AnswerServiceTest {

    private DatabaseHelper databaseHelper; // Use DatabaseHelper directly

    public AnswerServiceTest() {
        databaseHelper = new DatabaseHelper();
        try {
            databaseHelper.connectToDatabase(); // Connect to the database
        } catch (SQLException e) {
            e.printStackTrace(); // Handle connection errors appropriately
        }
    }

    public void testAddAnswer_ValidInput_Success() throws SQLException {
        Answer answer = new Answer(1, "Great answer!", "Jane Doe");
        databaseHelper.addAnswer(answer.getQuestionId(), answer.getAnswerText(), answer.getAuthor());

        // Use getAnswerById to retrieve the answer (not getQuestionById)
        Answer retrievedAnswer = databaseHelper.getAnswerById(answer.getAnswerId());
        if (retrievedAnswer != null && 
            retrievedAnswer.getAnswerText().equals(answer.getAnswerText()) && 
            retrievedAnswer.getAuthor().equals(answer.getAuthor())) {
            System.out.println("testAddAnswer_ValidInput_Success passed");
        } else {
            System.out.println("testAddAnswer_ValidInput_Success failed: Database verification failed.");
        }
    }

    public void testAddAnswer_MissingText_Warning() throws SQLException {
        try {
            Answer answer = new Answer(1, "", "Jane Doe");
            databaseHelper.addAnswer(answer.getQuestionId(), answer.getAnswerText(), answer.getAuthor());
            System.out.println("testAddAnswer_MissingText_Warning failed: Answer should not have been added.");
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("Answer text cannot be empty")) { // Check for a specific message
                System.out.println("testAddAnswer_MissingText_Warning passed");
            } else {
                System.out.println("testAddAnswer_MissingText_Warning failed: Incorrect exception message: " + e.getMessage());
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("NULL")) { // Check for a NOT NULL constraint violation
                System.out.println("testAddAnswer_MissingText_Warning passed");
            } else {
                System.out.println("testAddAnswer_MissingText_Warning failed: Incorrect exception message: " + e.getMessage());
            }
        }
    }

    public void testAddAnswer_MissingAuthor_Warning() throws SQLException {
        try {
            Answer answer = new Answer(1, "Great answer!", "");
            databaseHelper.addAnswer(answer.getQuestionId(), answer.getAnswerText(), answer.getAuthor());
            System.out.println("testAddAnswer_MissingAuthor_Warning failed: Answer should not have been added.");
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("Author") || e.getMessage().contains("empty")) {
                System.out.println("testAddAnswer_MissingAuthor_Warning passed");
            } else {
                System.out.println("testAddAnswer_MissingAuthor_Warning failed: Incorrect exception message: " + e.getMessage());
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("NULL")) { // Check for a NOT NULL constraint violation
                System.out.println("testAddAnswer_MissingAuthor_Warning passed");
            } else {
                System.out.println("testAddAnswer_MissingAuthor_Warning failed: Incorrect exception message: " + e.getMessage());
            }
        }
    }

    public void testAddAnswer_InvalidQuestionId_Warning() throws SQLException {
        Answer answer = new Answer(-1, "Great answer!", "Jane Doe");
        try {
            databaseHelper.addAnswer(answer.getQuestionId(), answer.getAnswerText(), answer.getAuthor());
            System.out.println("testAddAnswer_InvalidQuestionId_Warning failed: Answer should not have been added.");
        } catch (SQLException e) {
            if (e.getMessage().contains("FOREIGN KEY")) { // Check for foreign key constraint violation
                System.out.println("testAddAnswer_InvalidQuestionId_Warning passed");
            } else {
                System.out.println("testAddAnswer_InvalidQuestionId_Warning failed: Incorrect exception message: " + e.getMessage());
            }
        }
    }

    public void testGetAnswerById_AnswerExists() throws SQLException {
        Answer answer = new Answer(1, "Sample Answer", "Author");
        databaseHelper.addAnswer(answer.getQuestionId(), answer.getAnswerText(), answer.getAuthor()); // Add using DatabaseHelper
        int answerId = databaseHelper.getAnswerById(1).getAnswerId();
        Answer retrievedAnswer = databaseHelper.getAnswerById(answerId);

        if (retrievedAnswer != null && 
            retrievedAnswer.getAnswerText().equals("Sample Answer") && 
            retrievedAnswer.getAuthor().equals("Author") && 
            retrievedAnswer.getQuestionId() == 1) {
            System.out.println("testGetAnswerById_AnswerExists passed");
        } else {
            System.out.println("testGetAnswerById_AnswerExists failed: Got " + retrievedAnswer);
        }
    }

    public void testGetAnswerById_AnswerNotFound() throws SQLException {
        Answer answer = databaseHelper.getAnswerById(999);

        if (answer == null) {
            System.out.println("testGetAnswerById_AnswerNotFound passed");
        } else {
            System.out.println("testGetAnswerById_AnswerNotFound failed: Got " + answer);
        }
    }

    public void testGetAllAnswers_AnswersExist() throws SQLException {
        Answer answer1 = new Answer(1, "Answer 1", "Author 1");
        Answer answer2 = new Answer(1, "Answer 2", "Author 2");
        databaseHelper.addAnswer(answer1.getQuestionId(), answer1.getAnswerText(), answer1.getAuthor());
        databaseHelper.addAnswer(answer2.getQuestionId(), answer2.getAnswerText(), answer2.getAuthor());

        List<String> answers = databaseHelper.getAnswersForQuestion(1);

        if (answers.contains("Answer 1") && answers.contains("Answer 2")) {
            System.out.println("testGetAllAnswers_AnswersExist passed");
        } else {
            System.out.println("testGetAllAnswers_AnswersExist failed: Got " + answers);
        }
    }

    public void testGetAllAnswers_NoAnswers() throws SQLException {
        List<String> answers = databaseHelper.getAnswersForQuestion(1);

        if (answers.isEmpty()) {
            System.out.println("testGetAllAnswers_NoAnswers passed");
        } else {
            System.out.println("testGetAllAnswers_NoAnswers failed: Got " + answers);
        }
    }

    public void testUpdateAnswer_ValidInput_Success() throws SQLException {
        Answer answer = new Answer(1, "Original Text", "Author");
        databaseHelper.addAnswer(answer.getQuestionId(), answer.getAnswerText(), answer.getAuthor());
        int answerId = databaseHelper.getAnswerById(1).getAnswerId();

        Answer updatedAnswer = new Answer(1, "Updated Text", "New Author");
        updatedAnswer.setAnswerId(answerId);
        databaseHelper.updateAnswer(updatedAnswer.getAnswerId(), updatedAnswer.getAnswerText(), updatedAnswer.getAuthor());

        Answer retrievedAnswer = databaseHelper.getAnswerById(answerId);
        if (retrievedAnswer != null && 
            retrievedAnswer.getAnswerText().equals(updatedAnswer.getAnswerText()) && 
            retrievedAnswer.getAuthor().equals(updatedAnswer.getAuthor())) {
            System.out.println("testUpdateAnswer_ValidInput_Success passed");
        } else {
            System.out.println("testUpdateAnswer_ValidInput_Success failed: Database verification failed.");
        }
    }

    public void testUpdateAnswer_EmptyText() throws SQLException {
        Answer answer = new Answer(1, "Original Text", "Author");
        databaseHelper.addAnswer(answer.getQuestionId(), answer.getAnswerText(), answer.getAuthor());
        int answerId = databaseHelper.getAnswerById(1).getAnswerId();

        Answer updatedAnswer = new Answer(1, "", "New Author");
        updatedAnswer.setAnswerId(answerId);
        try {
            databaseHelper.updateAnswer(updatedAnswer.getAnswerId(), updatedAnswer.getAnswerText(), updatedAnswer.getAuthor());
            System.out.println("testUpdateAnswer_EmptyText failed: Update should have thrown exception.");
        } catch (SQLException e) {
            if (e.getMessage().contains("NULL")) {
                System.out.println("testUpdateAnswer_EmptyText passed");
            } else {
                System.out.println("testUpdateAnswer_EmptyText failed: Incorrect exception message: " + e.getMessage());
            }
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("Answer text cannot be empty")) {
                System.out.println("testUpdateAnswer_EmptyText passed");
            } else {
                System.out.println("testUpdateAnswer_EmptyText failed: Incorrect exception message: " + e.getMessage());
            }
        }
    }

    public void testUpdateAnswer_EmptyAuthor() throws SQLException {
        Answer answer = new Answer(1, "Original Text", "Author");
        databaseHelper.addAnswer(answer.getQuestionId(), answer.getAnswerText(), answer.getAuthor());
        int answerId = databaseHelper.getAnswerById(1).getAnswerId();

        Answer updatedAnswer = new Answer(1, "Updated Text", "");
        updatedAnswer.setAnswerId(answerId);
        try {
            databaseHelper.updateAnswer(updatedAnswer.getAnswerId(), updatedAnswer.getAnswerText(), updatedAnswer.getAuthor());
            System.out.println("testUpdateAnswer_EmptyAuthor failed: Update should have thrown exception.");
        } catch (SQLException e) {
            if (e.getMessage().contains("NULL")) {
                System.out.println("testUpdateAnswer_EmptyAuthor passed");
            } else {
                System.out.println("testUpdateAnswer_EmptyAuthor failed: Incorrect exception message: " + e.getMessage());
            }
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("Author") || e.getMessage().contains("empty")) {
                System.out.println("testUpdateAnswer_EmptyAuthor passed");
            } else {
                System.out.println("testUpdateAnswer_EmptyAuthor failed: Incorrect exception message: " + e.getMessage());
            }
        }
    }

    public void testDeleteAnswer_ValidInput_Success() throws SQLException {
        Answer answer = new Answer(1, "Sample Text", "Author");
        databaseHelper.addAnswer(answer.getQuestionId(), answer.getAnswerText(), answer.getAuthor());
        int answerId = databaseHelper.getAnswerById(1).getAnswerId();

        databaseHelper.deleteAnswer(answerId);

        Answer retrievedAnswer = databaseHelper.getAnswerById(answerId);
        if (retrievedAnswer == null) {
            System.out.println("testDeleteAnswer_ValidInput_Success passed");
        } else {
            System.out.println("testDeleteAnswer_ValidInput_Success failed: Answer should not exist.");
        }
    }

    public void testDeleteAnswer_InvalidId() throws SQLException {
        try {
            databaseHelper.deleteAnswer(999);
            System.out.println("testDeleteAnswer_InvalidId passed"); // Should not throw an exception
        } catch (SQLException e) {
            System.out.println("testDeleteAnswer_InvalidId failed: Exception thrown: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        AnswerServiceTest test = new AnswerServiceTest();
        try {
            test.testAddAnswer_ValidInput_Success();
            test.testAddAnswer_MissingText_Warning();
            test.testAddAnswer_MissingAuthor_Warning();
            test.testAddAnswer_InvalidQuestionId_Warning();
            test.testGetAnswerById_AnswerExists();
            test.testGetAnswerById_AnswerNotFound();
            test.testGetAllAnswers_AnswersExist();
            test.testGetAllAnswers_NoAnswers();
            test.testUpdateAnswer_ValidInput_Success();
            test.testUpdateAnswer_EmptyText();
            test.testUpdateAnswer_EmptyAuthor();
            test.testDeleteAnswer_ValidInput_Success();
            test.testDeleteAnswer_InvalidId();
        } catch (SQLException e) {
            System.err.println("SQLException during tests: " + e.getMessage());
        } finally {
            test.databaseHelper.closeConnection(); // Close the connection after tests
        }
    }
}