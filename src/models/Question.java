package models;

import java.time.LocalDateTime;
import java.sql.Timestamp;

public class Question {
    private int questionId;
    private String questionText;
    private String author;
    private String questionTitle;
    private LocalDateTime creationDate;

    public Question(String questionText, String author) {
        if (questionText == null || questionText.isEmpty()) {
            throw new IllegalArgumentException("Question text cannot be empty.");
        }
        if (author == null || author.isEmpty()) {
            throw new IllegalArgumentException("Author cannot be empty.");
        }

        this.questionText = questionText;
        this.author = author;
        this.creationDate = LocalDateTime.now(); 
    }

    public Question(String questionTitle, String questionText, String author) {
        this.questionTitle = questionTitle;
        this.questionText = questionText;
        this.author = author;
        this.creationDate = LocalDateTime.now(); 
    }

    public Question(int questionId, String questionTitle, String questionText, String author, Timestamp creationDate) {
        this.questionId = questionId;
        this.questionTitle = questionTitle;
        this.questionText = questionText;
        this.author = author;
        this.creationDate = creationDate.toLocalDateTime();
    }

    // Getters
    public int getQuestionId() {
        return questionId;
    }

    public String getQuestionText() {
        return questionText;
    }

    public String getAuthor() {
        return author;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }
    
    public String getQuestionTitle() { 
        return questionTitle;
    }

    // Setters
    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public void setQuestionText(String questionText) {
        if (questionText == null || questionText.isEmpty()) {
            throw new IllegalArgumentException("Question text cannot be empty.");
        }
        this.questionText = questionText;
    }

    public void setAuthor(String author) {
        if (author == null || author.isEmpty()) {
            throw new IllegalArgumentException("Author cannot be empty.");
        }
        this.author = author;
    }

    public void setCreationDate(LocalDateTime creationDate) { 
        this.creationDate = creationDate;
    }
    
    public void setQuestionTitle(String questionTitle) { 
        this.questionTitle = questionTitle;
    }

    @Override
    public String toString() {
        return "Question ID: " + questionId + "\n" +
               "Question Title: " + questionTitle + "\n" +
               "Question Text: " + questionText + "\n" +
               "Author: " + author + "\n" +
               "Creation Date: " + creationDate + "\n"; 
    }
}
