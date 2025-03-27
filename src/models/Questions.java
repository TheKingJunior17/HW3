package models;
import java.util.ArrayList;
import java.util.List;

public class Questions {
    private List<Question> questionList;

    public Questions() {
        questionList = new ArrayList<>();
    }

    public void addQuestion(Question question) {
        questionList.add(question);
    }

    public Question getQuestionById(int questionId) {
        for (Question question : questionList) {
            if (question.getQuestionId() == questionId) {
                return question;
            }
        }
        return null; 
    }

    public List<Question> getAllQuestions() {
        return new ArrayList<>(questionList); 
    }

    public void updateQuestion(Question question) {
        for (int i = 0; i < questionList.size(); i++) {
            if (questionList.get(i).getQuestionId() == question.getQuestionId()) {
                questionList.set(i, question);
                return;
            }
        }
    }

    public void deleteQuestion(int questionId) {
        questionList.removeIf(question -> question.getQuestionId() == questionId);
    }

}