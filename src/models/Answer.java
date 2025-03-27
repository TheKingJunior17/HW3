package models;
import java.time.LocalDateTime;

public class Answer {
    private int answerId;
    private int questionId; 
    private String answerText;
    private String author;
    private LocalDateTime creationDate;
    private boolean correct;
    // method to set for boolean
   

    public boolean isCorrect() {
        return correct;
    }
    
    // Constructor
    public Answer(int questionId, String answerText, String author) { 
        this.questionId = questionId;

        if (answerText == null || answerText.isEmpty()) {
            throw new IllegalArgumentException("Answer text cannot be empty.");
        }
        if (author == null || author.isEmpty()) {
            throw new IllegalArgumentException("Author cannot be empty.");
        }

        this.answerText = answerText;
        this.author = author;
        this.creationDate = LocalDateTime.now();
    }

    // Getters
    public int getAnswerId() { 
    	return answerId; 
    }
    public int getQuestionId() { 
    	return questionId; 
    } 
    public String getAnswerText() { 
    	return answerText; 
    }
    public String getAuthor() { 
    	return author; 
    }
    public LocalDateTime getCreationDate() { 
    	return creationDate; 
    }
    
    public Boolean getCorrect() { //Implementation boolean advised by Luis but made by Juan
        return correct; 
        }

    // Setters
    public void setAnswerId(int answerId) { 
    	this.answerId = answerId; 
    }
    public void setQuestionId(int questionId) { 
    	this.questionId = questionId; 
    }

    public void setAnswerText(String answerText) {
        if (answerText == null || answerText.isEmpty()) {
            throw new IllegalArgumentException("Answer text cannot be empty.");
        }
        this.answerText = answerText;
    }

    public void setAuthor(String author) {
        if (author == null || author.isEmpty()) {
            throw new IllegalArgumentException("Author cannot be empty.");
        }
        this.author = author;
    }
    
    public void setCorrect(boolean correct) { 
        this.correct = correct; //Similarly here
        }


    public void setCreationDate(LocalDateTime creationDate) { this.creationDate = creationDate; }


    @Override
    public String toString() {
        return "Answer ID: " + answerId + "\n" +
               "Question ID: " + questionId + "\n" + 
               "Answer Text: " + answerText + "\n" +
               "Author: " + author + "\n" +
               "Creation Date: " + creationDate + "\n";
    }
}