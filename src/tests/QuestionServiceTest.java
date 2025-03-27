package tests;

import databasePart1.DatabaseHelper; // Import your DatabaseHelper
import models.Question;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class QuestionServiceTest {

    private DatabaseHelper databaseHelper;

    public QuestionServiceTest() {
        databaseHelper = new DatabaseHelper();
        try {
            databaseHelper.connectToDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void testAddQuestion_ValidInput_Success() throws SQLException {
        Question question = new Question("Sample Title", "Sample Text", "Author");
        databaseHelper.addQuestion(question.getQuestionTitle(), question.getQuestionText(), question.getAuthor());

        // Retrieve the question ID (you'll need to adjust how you get it)
        List<Map<String, Object>> questions = databaseHelper.getAllQuestions();
        int questionId = -1; // Initialize
        for(Map<String, Object> q : questions){
            if(q.get("title").equals(question.getQuestionTitle()) && q.get("text").equals(question.getQuestionText()) && q.get("author").equals(question.getAuthor())){
                questionId = (int) q.get("question_id");
                break; // Exit the loop since we found the question
            }
        }
        question.setQuestionId(questionId);

        Map<String, Object> retrievedQuestion = databaseHelper.getQuestionById(question.getQuestionId());
        if (retrievedQuestion != null && retrievedQuestion.get("title").equals(question.getQuestionTitle()) && retrievedQuestion.get("text").equals(question.getQuestionText()) && retrievedQuestion.get("author").equals(question.getAuthor())) {
            System.out.println("testAddQuestion_ValidInput_Success passed");
        } else {
            System.out.println("testAddQuestion_ValidInput_Success failed: Database verification failed.");
        }
    }

    public void testAddQuestion_EmptyTitle() throws SQLException {
        Question question = new Question("", "Sample Text", "Author");
        try {
            databaseHelper.addQuestion(question.getQuestionTitle(), question.getQuestionText(), question.getAuthor());
            System.out.println("testAddQuestion_EmptyTitle failed: Question should not have been added.");
        } catch (SQLException e) {
            if (e.getMessage().contains("NULL")) {
                System.out.println("testAddQuestion_EmptyTitle passed");
            } else {
                System.out.println("testAddQuestion_EmptyTitle failed: Incorrect exception message: " + e.getMessage());
            }
        }
    }

    public void testAddQuestion_EmptyText() throws SQLException {
        Question question = new Question("Sample Title", "", "Author");
        try {
            databaseHelper.addQuestion(question.getQuestionTitle(), question.getQuestionText(), question.getAuthor());
            System.out.println("testAddQuestion_EmptyText failed: Question should not have been added.");
        } catch (SQLException e) {
            if (e.getMessage().contains("NULL")) {
                System.out.println("testAddQuestion_EmptyText passed");
            } else {
                System.out.println("testAddQuestion_EmptyText failed: Incorrect exception message: " + e.getMessage());
            }
        }
    }

    public void testAddQuestion_EmptyAuthor() throws SQLException {
        Question question = new Question("Sample Title", "Sample Text", "");
        try {
            databaseHelper.addQuestion(question.getQuestionTitle(), question.getQuestionText(), question.getAuthor());
            System.out.println("testAddQuestion_EmptyAuthor failed: Question should not have been added.");
        } catch (SQLException e) {
            if (e.getMessage().contains("NULL")) {
                System.out.println("testAddQuestion_EmptyAuthor passed");
            } else {
                System.out.println("testAddQuestion_EmptyAuthor failed: Incorrect exception message: " + e.getMessage());
            }
        }
    }

    public void testGetQuestionById_QuestionExists() throws SQLException {
        Question question = new Question("Sample Question Title", "Sample Question Text", "Author");
        databaseHelper.addQuestion(question.getQuestionTitle(), question.getQuestionText(), question.getAuthor());
        List<Map<String, Object>> questions = databaseHelper.getAllQuestions();
        int questionId = -1; // Initialize
        for(Map<String, Object> q : questions){
            if(q.get("title").equals(question.getQuestionTitle()) && q.get("text").equals(question.getQuestionText()) && q.get("author").equals(question.getAuthor())){
                questionId = (int) q.get("question_id");
                break; // Exit the loop since we found the question
            }
        }
        question.setQuestionId(questionId);

        Map<String, Object> retrievedQuestion = databaseHelper.getQuestionById(questionId);

        if (retrievedQuestion != null && retrievedQuestion.get("title").equals("Sample Question Title") && retrievedQuestion.get("text").equals("Sample Question Text") && retrievedQuestion.get("author").equals("Author")) {
            System.out.println("testGetQuestionById_QuestionExists passed");
        } else {
            System.out.println("testGetQuestionById_QuestionExists failed: Got " + retrievedQuestion);
        }
    }

    public void testGetQuestionById_QuestionNotFound() throws SQLException {
        Map<String, Object> question = databaseHelper.getQuestionById(999);

        if (question == null) {
            System.out.println("testGetQuestionById_QuestionNotFound passed");
        } else {
            System.out.println("testGetQuestionById_QuestionNotFound failed: Got " + question);
        }
    }

    public void testGetAllQuestions() throws SQLException {
        Question question1 = new Question("Question 1 Title", "Question 1 Text", "Author 1");
        Question question2 = new Question("Question 2 Title", "Question 2 Text", "Author 2");
        databaseHelper.addQuestion(question1.getQuestionTitle(), question1.getQuestionText(), question1.getAuthor());
        databaseHelper.addQuestion(question2.getQuestionTitle(), question2.getQuestionText(), question2.getAuthor());

        List<Map<String, Object>> questions = databaseHelper.getAllQuestions();

        boolean found1 = false;
        boolean found2 = false;
        for (Map<String, Object> q : questions) {
            if (q.get("title").equals("Question 1 Title") && q.get("text").equals("Question 1 Text") && q.get("author").equals("Author 1")) {
                found1 = true;
            }
            if (q.get("title").equals("Question 2 Title") && q.get("text").equals("Question 2 Text") && q.get("author").equals("Author 2")) {
                found2 = true;
            }
        }

        if (found1 && found2) {
            System.out.println("testGetAllQuestions passed");
        } else {
            System.out.println("testGetAllQuestions failed: Got " + questions);
        }
    }


    public void testUpdateQuestion_EmptyAuthor() throws SQLException {
        Question question = new Question("Original Title", "Original Text", "Author");
        databaseHelper.addQuestion(question.getQuestionTitle(), question.getQuestionText(), question.getAuthor());
                List<Map<String, Object>> questions = databaseHelper.getAllQuestions();
        int questionId = -1; // Initialize
        for(Map<String, Object> q : questions){
            if(q.get("title").equals(question.getQuestionTitle()) && q.get("text").equals(question.getQuestionText()) && q.get("author").equals(question.getAuthor())){
                questionId = (int) q.get("question_id");
                break; // Exit the loop since we found the question
            }
        }
        question.setQuestionId(questionId);

        Question updatedQuestion = new Question("Updated Title", "Updated Text", "");
        updatedQuestion.setQuestionId(questionId);
        try {
            databaseHelper.updateQuestion(updatedQuestion.getQuestionId(), updatedQuestion.getQuestionTitle(), updatedQuestion.getQuestionText());
            System.out.println("testUpdateQuestion_EmptyAuthor failed: Update should have thrown exception.");
        } catch (SQLException e) {
            if (e.getMessage().contains("NULL")) {
                System.out.println("testUpdateQuestion_EmptyAuthor passed");
            } else {
                System.out.println("testUpdateQuestion_EmptyAuthor failed: Incorrect exception message: " + e.getMessage());
            }
        }
    }

    public void testUpdateQuestion_EmptyTitle() throws SQLException {
        Question question = new Question("Original Title", "Original Text", "Author");
        databaseHelper.addQuestion(question.getQuestionTitle(), question.getQuestionText(), question.getAuthor());
        List<Map<String, Object>> questions = databaseHelper.getAllQuestions();
        int questionId = -1; // Initialize
        for(Map<String, Object> q : questions){
            if(q.get("title").equals(question.getQuestionTitle()) && q.get("text").equals(question.getQuestionText()) && q.get("author").equals(question.getAuthor())){
                questionId = (int) q.get("question_id");
                break; // Exit the loop since we found the question
            }
        }
        question.setQuestionId(questionId);

        Question updatedQuestion = new Question("", "Updated Text", "New Author");
        updatedQuestion.setQuestionId(questionId);
        try {
            databaseHelper.updateQuestion(updatedQuestion.getQuestionId(), updatedQuestion.getQuestionTitle(), updatedQuestion.getQuestionText());
            System.out.println("testUpdateQuestion_EmptyTitle failed: Update should have thrown exception.");
        } catch (SQLException e) {
            if (e.getMessage().contains("NULL")) {
                System.out.println("testUpdateQuestion_EmptyTitle passed");
            } else {
                System.out.println("testUpdateQuestion_EmptyTitle failed: Incorrect exception message: " + e.getMessage());
            }
        }
    }

    public void testUpdateQuestion_EmptyText() throws SQLException {
        Question question = new Question("Original Title", "Original Text", "Author");
        databaseHelper.addQuestion(question.getQuestionTitle(), question.getQuestionText(), question.getAuthor());
        List<Map<String, Object>> questions = databaseHelper.getAllQuestions();
        int questionId = -1; // Initialize
        for(Map<String, Object> q : questions){
            if(q.get("title").equals(question.getQuestionTitle()) && q.get("text").equals(question.getQuestionText()) && q.get("author").equals(question.getAuthor())){
                questionId = (int) q.get("question_id");
                break; // Exit the loop since we found the question
            }
        }
        question.setQuestionId(questionId);

        Question updatedQuestion = new Question("Updated Title", "", "New Author");
        updatedQuestion.setQuestionId(questionId);
        try {
            databaseHelper.updateQuestion(updatedQuestion.getQuestionId(), updatedQuestion.getQuestionTitle(), updatedQuestion.getQuestionText());
            System.out.println("testUpdateQuestion_EmptyText failed: Update should have thrown exception.");
        } catch (SQLException e) {
            if (e.getMessage().contains("NULL")) {
                System.out.println("testUpdateQuestion_EmptyText passed");
            } else {
                System.out.println("testUpdateQuestion_EmptyText failed: Incorrect exception message: " + e.getMessage());
            }
        }
    }

    public void testDeleteQuestion_ValidInput_Success() throws SQLException {
        Question question = new Question("Sample Title", "Sample Text", "Author");
        databaseHelper.addQuestion(question.getQuestionTitle(), question.getQuestionText(), question.getAuthor());
        List<Map<String, Object>> questions = databaseHelper.getAllQuestions();
        int questionId = -1; // Initialize
        for(Map<String, Object> q : questions){
            if(q.get("title").equals(question.getQuestionTitle()) && q.get("text").equals(question.getQuestionText()) && q.get("author").equals(question.getAuthor())){
                questionId = (int) q.get("question_id");
                break; // Exit the loop since we found the question
            }
        }
        question.setQuestionId(questionId);

        databaseHelper.deleteQuestion(questionId);

        Map<String, Object> retrievedQuestion = databaseHelper.getQuestionById(questionId);
        if (retrievedQuestion == null) {
            System.out.println("testDeleteQuestion_ValidInput_Success passed");
        } else {
            System.out.println("testDeleteQuestion_ValidInput_Success failed: Question should not exist.");
        }
    }

    public void testUpdateQuestion_ValidInput_Success() throws SQLException {
        Question question = new Question("Original Title", "Original Text", "Author");
        databaseHelper.addQuestion(question.getQuestionTitle(), question.getQuestionText(), question.getAuthor());
        List<Map<String, Object>> questions = databaseHelper.getAllQuestions();
        int questionId = -1; // Initialize
        for(Map<String, Object> q : questions){
            if(q.get("title").equals(question.getQuestionTitle()) && q.get("text").equals(question.getQuestionText()) && q.get("author").equals(question.getAuthor())){
                questionId = (int) q.get("question_id");
                break; // Exit the loop since we found the question
            }
        }
        question.setQuestionId(questionId);

        Question updatedQuestion = new Question("Updated Title", "Updated Text", "New Author");
        updatedQuestion.setQuestionId(questionId);
        databaseHelper.updateQuestion(updatedQuestion.getQuestionId(), updatedQuestion.getQuestionTitle(), updatedQuestion.getQuestionText());

        Map<String, Object> retrievedQuestion = databaseHelper.getQuestionById(questionId);
        if (retrievedQuestion != null && retrievedQuestion.get("title").equals(updatedQuestion.getQuestionTitle()) && retrievedQuestion.get("text").equals(updatedQuestion.getQuestionText()) && retrievedQuestion.get("author").equals(updatedQuestion.getAuthor())) {
            System.out.println("testUpdateQuestion_ValidInput_Success passed");
        } else {
            System.out.println("testUpdateQuestion_ValidInput_Success failed: Database verification failed.");
        }
    }

    public void testDeleteQuestion_InvalidId() throws SQLException {
        try {
            databaseHelper.deleteQuestion(999);
            System.out.println("testDeleteQuestion_InvalidId passed");
        } catch (SQLException e) {
            System.out.println("testDeleteQuestion_InvalidId failed: Exception thrown: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        QuestionServiceTest test = new QuestionServiceTest();
        try {
            test.testAddQuestion_ValidInput_Success();
            test.testAddQuestion_EmptyTitle();
            test.testAddQuestion_EmptyText();
            test.testAddQuestion_EmptyAuthor();
            test.testGetQuestionById_QuestionExists();
            test.testGetQuestionById_QuestionNotFound();
            test.testGetAllQuestions();
            test.testUpdateQuestion_ValidInput_Success();
            test.testUpdateQuestion_EmptyTitle();
            test.testUpdateQuestion_EmptyText();
            test.testUpdateQuestion_EmptyAuthor();
            test.testDeleteQuestion_ValidInput_Success();
            test.testDeleteQuestion_InvalidId();
        } catch (SQLException e) {
            System.err.println("SQLException during tests: " + e.getMessage());
        } finally {
            test.databaseHelper.closeConnection();
        }
    }
}