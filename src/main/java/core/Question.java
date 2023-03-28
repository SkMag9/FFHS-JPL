package core;

/**
 * Object holding a question with 3 answer possibilities
 */
public class Question {

  private final String question;
  private final String answerA;
  private final String answerB;
  private final String answerC;
  public char correctAnswer;

  public Question(String question, String answerA, String answerB, String answerC,
      char correctAnswer) {
    this.question = question;
    this.answerA = answerA;
    this.answerB = answerB;
    this.answerC = answerC;
    this.correctAnswer = correctAnswer;
  }

  /**
   * @return the text of the correct answer, if any exists
   */
  public String getCorrectAnswerText() {
    switch (correctAnswer) {
      case 'A':
        return answerA;
      case 'B':
        return answerB;
      case 'C':
        return answerC;
    }
    return "No correct answer found";
  }

  public String getQuestion() {
    return question;
  }

  public String getAnswerA() {
    return answerA;
  }

  public String getAnswerB() {
    return answerB;
  }

  public String getAnswerC() {
    return answerC;
  }

  public char getCorrectAnswer() {
    return correctAnswer;
  }
}
