package edu.ithaca.dragon.par.io;

import edu.ithaca.dragon.par.domainModel.QuestionPool;
import edu.ithaca.dragon.par.studentModel.UserQuestionSet;
import edu.ithaca.dragon.util.JsonUtil;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserQuestionSetRecordTest {
    @Test
    public void toJsonAndBackTest() throws IOException {
        QuestionPool qp = new QuestionPool(new JsonQuestionPoolDatastore("src/test/resources/author/questionPools/SampleQuestionPool.json").getAllQuestions());
        UserQuestionSet que = UserQuestionSet.buildNewUserQuestionSetFromQuestions("99", qp.getAllQuestions());

        UserQuestionSetRecord myUQSR = new UserQuestionSetRecord(que);
        //write to Json
        JsonUtil.toJsonFile("src/test/resources/autoGenerated/UserQuestionSetRecordTest-toJsonAndBackTest.json", myUQSR);
        //read from Json
        UserQuestionSetRecord UQSRFromJson = JsonUtil.fromJsonFile("src/test/resources/autoGenerated/UserQuestionSetRecordTest-toJsonAndBackTest.json",UserQuestionSetRecord.class);
        //make a UserQuestionSetOld from a UserQuestionSetRecord
        UserQuestionSet fromRecord = UQSRFromJson.buildUserQuestionSet(qp);
        //compare the two UserQuestionSets
        assertEquals(que, fromRecord);

        Path path = Paths.get("src/test/resources/autoGenerated/UserQuestionSetRecordTest-toJsonAndBackTest.json");
        assertTrue(Files.deleteIfExists(path));
    }
}
