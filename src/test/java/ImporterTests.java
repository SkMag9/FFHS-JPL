import core.Importer;
import core.Question;
import java.io.FileNotFoundException;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ImporterTests {


  @Test
  public void TestImport() {
    Importer importer = new Importer("src/test/resources/testquestions.txt");
    try {
      List<Question> questions = importer.getQuestions();
      Assertions.assertEquals(3, questions.size());
      Question first = questions.get(0);
      Assertions.assertEquals("Im Gegensatz zum «Derby» wird der «Oxford» in der Regel ...?",
          first.getQuestion());
      Assertions.assertEquals("parallel- und nicht kreuzgeschnürt", first.getAnswerA());
      Assertions.assertEquals("nur alle drei Tage gestutzt", first.getAnswerB());
      Assertions.assertEquals("nur mit Eingriff von links angeboten", first.getAnswerC());
      Assertions.assertEquals('A', first.getCorrectAnswer());
    } catch (FileNotFoundException e) {
      Assertions.fail(e.getMessage());
    }
  }

  @Test
  public void TestImportWithNonExistingFile() {
    Importer importer = new Importer("src/test/resources/doesnotexist");
    Assertions.assertThrows(FileNotFoundException.class, () -> importer.getQuestions());
  }
}
