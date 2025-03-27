package database;

import databasePart1.DatabaseHelper; // Import your DatabaseHelper
import models.Answer;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AnswersDatabase {

    private DatabaseHelper dbHelper; // Use DatabaseHelper

    public AnswersDatabase(DatabaseHelper dbHelper) { // Inject DatabaseHelper
        this.dbHelper = dbHelper;
    }

    public void addAnswer(Answer answer) throws SQLException { // Add throws SQLException
        try {
            dbHelper.addAnswer(answer.getQuestionId(), answer.getAnswerText(), answer.getAuthor());
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public List<Answer> getAllAnswers(int questionId) throws SQLException { // Add throws SQLException
        List<Answer> answers = new ArrayList<>();
        try {
            // Retrieve answer texts from the helper
            List<String> answerTexts = dbHelper.getAnswersForQuestion(questionId);
            for (String answerText : answerTexts) {
                // Note: If you need additional details like author or id, you may need to update this logic.
                Answer answer = new Answer(questionId, answerText, "");
                answers.add(answer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return answers;
    }

    public Answer getAnswerById(int answerId) throws SQLException { // Revised method
        try {
            // Directly delegate to DatabaseHelper's getAnswerById method.
            return dbHelper.getAnswerById(answerId);
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public List<Answer> getAnswersByQuestionId(int questionId) throws SQLException { // Add throws SQLException
        return getAllAnswers(questionId); // Reuse getAllAnswers
    }

    public void updateAnswer(Answer answer) throws SQLException { // Add throws SQLException
        try {
            dbHelper.updateAnswer(answer.getAnswerId(), answer.getAnswerText(), answer.getAuthor());
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void deleteAnswer(int answerId) throws SQLException { // Add throws SQLException
        try {
            dbHelper.deleteAnswer(answerId);
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }
}
