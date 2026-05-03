package model;

public class Question {
    private String question;
    private String correctAnswer;
    private String[] options;
    
    public Question(String question, String correctAnswer, String[] options) {
        this.question = question;
        this.correctAnswer = correctAnswer;
        this.options = options;
    }
    
    // Getter methods
    public String getQuestion() { return question; }
    public String getCorrectAnswer() { return correctAnswer; }
    public String[] getOptions() { return options; }
}