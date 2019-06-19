package edu.ithaca.dragon.par.io;

import edu.ithaca.dragon.par.domainModel.Question;
import edu.ithaca.dragon.par.domainModel.QuestionPool;
import edu.ithaca.dragon.util.JsonUtil;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ImageTaskTest {

    @Test
    public void toJsonAndBackTest() throws IOException {
        QuestionPool myQP = new QuestionPool(new JsonDatastore("src/test/resources/author/SampleQuestions.json"));
        List<Question> myQPList = myQP.getAllQuestions();
        ImageTask testImageTask = new ImageTask("../static/images/equine02.jpg\"", myQPList);

        //write to JSON
        JsonUtil.toJsonFile("src/test/resources/autoGenerated/ImageTaskTest-toJsonAndBackTest.json", testImageTask);

        //TODO load it back, test that it works (.equals()), make it delete the file when done
    }
}
