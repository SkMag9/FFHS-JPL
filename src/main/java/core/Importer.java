package core;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Class to import an parse a question file
 */
public record Importer(String fileName) {

  /**
   * Get the corresponding regex pattern for an action
   * @param search
   * @return regex pattern
   */
  private String getSearchPattern(SearchPattern search) {
    return switch (search) {
      case QUESTION -> "^\\#\\sFrage\\s\\d+$";
      case CORRECT_ANSWER -> "^(A|B|C)\\*.+";
    };
  }

  /**
   * Read all questions for the given file
   * @return List of Questions
   * @throws FileNotFoundException if the file does not exist
   */
  public List<Question> getQuestions() throws FileNotFoundException {
    ArrayList<Question> questions = new ArrayList<>();

    // Text Array
    ArrayList<String> text = new ArrayList<>();

    // File Reader
    InputStream iStream = new FileInputStream(fileName);
    InputStreamReader isr = new InputStreamReader(Objects.requireNonNull(iStream),
        StandardCharsets.ISO_8859_1);
    BufferedReader fileLines = new BufferedReader(isr);

    // Add all lines to ArrayList
    fileLines.lines().forEach(text::add);
    for (int i = 0; i < text.size(); i++) {
      String question;
      String answerA;
      String answerB;
      String answerC;
      char correctAnswer = 0;

      if (text.get(i).matches(getSearchPattern(SearchPattern.QUESTION))) {
        // Set Game.Question
        question = text.get(i + 1);

        // Set A and ev. Correct Answer
        ParsedAnswer answer = getAnswer(text.get(i + 2));
        answerA = answer.getAnswer();
        if (answer.isCorrect()) {
          correctAnswer = 'A';
        }

        answer = getAnswer(text.get(i + 3));
        answerB = answer.getAnswer();
        if (answer.isCorrect()) {
          correctAnswer = 'B';
        }

        answer = getAnswer(text.get(i + 4));
        answerC = answer.getAnswer();
        if (answer.isCorrect()) {
          correctAnswer = 'C';
        }

        questions.add(new Question(question, answerA, answerB, answerC, correctAnswer));
      }
    }
    return questions;
  }

  /**
   * Parses an input as an answer
   * @param input
   * @return the Answer and whether it is correct
   */
  private ParsedAnswer getAnswer(String input) {
    if (input.matches(getSearchPattern(SearchPattern.CORRECT_ANSWER))) {
      return new ParsedAnswer(true, input.substring(3));
    }
    return new ParsedAnswer(false, input.substring(2));
  }

  enum SearchPattern {
    QUESTION,
    CORRECT_ANSWER
  }

  /**
   * Class holding a parsed answer variant and whether it is marked as correct
   */
  record ParsedAnswer(boolean correct, String answer) {

    public String getAnswer() {
      return answer;
    }

    public boolean isCorrect() {
      return correct;
    }
  }
}
