package database;

import databasePart1.DatabaseHelper; // Import your DatabaseHelper
import models.Question;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QuestionsDatabase {

    private DatabaseHelper dbHelper; // Use DatabaseHelper

    public QuestionsDatabase(DatabaseHelper dbHelper) { // Inject DatabaseHelper
        this.dbHelper = dbHelper;
    }

    public void addQuestion(Question question) throws SQLException { // Add throws SQLException
        try {
            dbHelper.addQuestion(question.getQuestionTitle(), question.getQuestionText(), question.getAuthor());
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public Question getQuestionById(int questionId) throws SQLException {
        try {
            Map<String, Object> questionData = dbHelper.getQuestionById(questionId);
            if (questionData != null && !questionData.isEmpty()) {
                // Create Question object using data
                Question question = new Question(
                    (String) questionData.get("title"), 
                    (String) questionData.get("text"), 
                    (String) questionData.get("author")
                );
                question.setQuestionId(questionId);

                Timestamp timestamp = (Timestamp) questionData.get("creation_date");
                LocalDateTime creationDate = timestamp.toLocalDateTime();
                question.setCreationDate(creationDate); // Set the converted LocalDateTime

                return question;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return null;
    }
    public List<Question> getAllQuestions() throws SQLException {
        List<Question> questions = new ArrayList<>();
        try {
            List<Map<String, Object>> questionList = dbHelper.getAllQuestions(); // Use corrected method name
            if (questionList != null) {
                for (Map<String, Object> questionData : questionList) {
                    // Create Question object using data
                    Question question = new Question(
                        (String) questionData.get("title"), 
                        (String) questionData.get("text"), 
                        (String) questionData.get("author")
                    );
                    question.setQuestionId((Integer) questionData.get("question_id"));

                    // Fetch the Timestamp and convert to LocalDateTime
                    Timestamp timestamp = (Timestamp) questionData.get("creation_date");
                    LocalDateTime creationDate = timestamp.toLocalDateTime();  // Convert to LocalDateTime
                    question.setCreationDate(creationDate); // Set the converted LocalDateTime

                    questions.add(question);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return questions;
    }



    public void updateQuestion(Question question) throws SQLException { // Add throws SQLException
        try {
            dbHelper.updateQuestion(question.getQuestionId(), question.getQuestionTitle(), question.getQuestionText());
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void deleteQuestion(int questionId) throws SQLException { // Add throws SQLException
        try {
            dbHelper.deleteQuestion(questionId);
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void addQuestionTitleColumn() throws SQLException { // Add throws SQLException
        try {
            dbHelper.addQuestionTitleColumn();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }
    
}