package models;
import java.util.ArrayList;
import java.util.List;

public class Answers {
    private List<Answer> answerList;

    public Answers() {
        answerList = new ArrayList<>();
    }

    public void addAnswer(Answer answer) {
        answerList.add(answer);
    }

    public Answer getAnswerById(int answerId) {
        for (Answer answer : answerList) {
            if (answer.getAnswerId() == answerId) {
                return answer;
            }
        }
        return null;
    }

    public List<Answer> getAllAnswers() {
        return new ArrayList<>(answerList);
    }

    public void updateAnswer(Answer answer) {
        for (int i = 0; i < answerList.size(); i++) {
            if (answerList.get(i).getAnswerId() == answer.getAnswerId()) {
                answerList.set(i, answer);
                return;
            }
        }
    }

    public void deleteAnswer(int answerId) {
        answerList.removeIf(answer -> answer.getAnswerId() == answerId);
    }

    public List<Answer> getAnswersByQuestionId(int questionId) {
        List<Answer> answersForQuestion = new ArrayList<>();
        for (Answer answer : answerList) {
            if (answer.getQuestionId() == questionId) {
                answersForQuestion.add(answer);
            }
        }
        return answersForQuestion;
    }

}