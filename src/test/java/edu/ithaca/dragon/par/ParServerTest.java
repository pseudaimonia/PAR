package edu.ithaca.dragon.par;


import edu.ithaca.dragon.par.domainModel.QuestionPool;
import edu.ithaca.dragon.par.io.Datastore;
import edu.ithaca.dragon.par.io.ImageTask;
import edu.ithaca.dragon.par.io.ImageTaskResponse;
import edu.ithaca.dragon.par.io.JsonDatastore;
import edu.ithaca.dragon.par.studentModel.StudentModel;
import edu.ithaca.dragon.util.DataUtil;
import edu.ithaca.dragon.util.JsonUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ParServerTest {

    @Test
    public void getOrCreateStudentModelTest() throws IOException {
        Datastore datastore = new JsonDatastore("src/test/resources/author/SampleQuestionPool.json");
        StudentModel sm1 = new StudentModel("s1", datastore.loadQuestions().subList(0, 1));
        StudentModel sm2 = new StudentModel("s2", datastore.loadQuestions().subList(0, 2));
        StudentModel sm3 = new StudentModel("s3", datastore.loadQuestions());

        Map<String, StudentModel> smMap = new HashMap<>();
        smMap.put("s1", sm1);
        smMap.put("s2", sm2);
        smMap.put("s3", sm3);

        StudentModel studentModel = ParServer.getOrCreateStudentModel(smMap, "s3", datastore);
        assertEquals("s3", studentModel.getUserId());
        assertEquals(15, studentModel.getUnseenQuestionCount());

        studentModel = ParServer.getOrCreateStudentModel(smMap, "s1", datastore);
        assertEquals("s1", studentModel.getUserId());
        assertEquals(1, studentModel.getUnseenQuestionCount());

        //check adding one
        studentModel = ParServer.getOrCreateStudentModel(smMap, "s4", datastore);
        assertEquals("s4", studentModel.getUserId());
        assertEquals(15, studentModel.getUnseenQuestionCount());
        assertEquals(4, smMap.size());

        //check an old one
        studentModel = ParServer.getOrCreateStudentModel(smMap, "s2", datastore);
        assertEquals("s2", studentModel.getUserId());
        assertEquals(2, studentModel.getUnseenQuestionCount());

        //check getting the added one
        studentModel = ParServer.getOrCreateStudentModel(smMap, "s4", datastore);
        assertEquals("s4", studentModel.getUserId());
        assertEquals(15, studentModel.getUnseenQuestionCount());
        assertEquals(4, smMap.size());

        //check adding another one
        studentModel = ParServer.getOrCreateStudentModel(smMap, "s5", datastore);
        assertEquals("s5", studentModel.getUserId());
        assertEquals(15, studentModel.getUnseenQuestionCount());
        assertEquals(5, smMap.size());
    }

    @Test
    public void nextImageTaskSingleTest() throws IOException{
        Datastore datastore = new JsonDatastore("src/test/resources/author/SampleQuestionPool.json");
        ParServer parServer = new ParServer(datastore);
        ImageTask nextTask = parServer.nextImageTaskSingle("s1");
        ImageTask intendedFirstTask = JsonUtil.fromJsonFile("src/test/resources/author/SampleImageTaskSingleQuestion.json", ImageTask.class);
        assertEquals(intendedFirstTask, nextTask);

        nextTask = parServer.nextImageTaskSingle("s2");
        assertEquals(intendedFirstTask, nextTask);

        nextTask = parServer.nextImageTaskSingle("s1");
        assertNotNull(nextTask);
        nextTask = parServer.nextImageTaskSingle("s1");
        ImageTask intendedLastTask = JsonUtil.fromJsonFile("src/test/resources/author/SampleImageTaskSingleQuestion3.json", ImageTask.class);
        assertEquals(intendedLastTask, nextTask);

        nextTask = parServer.nextImageTaskSingle("s2");
        assertNotNull(nextTask);

        nextTask = parServer.nextImageTaskSingle("s2");
        assertEquals(intendedLastTask, nextTask);
    }

    @Test
    public void nextImageTaskTest() throws IOException{
        Datastore datastore = new JsonDatastore("src/test/resources/author/SampleQuestionPool.json");
        ParServer parServer = new ParServer(datastore);
        ImageTask nextTask = parServer.nextImageTask("s1");
        ImageTask intendedFirstTask = JsonUtil.fromJsonFile("src/test/resources/author/nextImageTaskTest1.json", ImageTask.class);
        assertEquals(intendedFirstTask, nextTask);

        nextTask = parServer.nextImageTask("s2");
        assertEquals(intendedFirstTask, nextTask);

        nextTask = parServer.nextImageTask("s1");
        assertNotNull(nextTask);
        ImageTask intendedLastTask = JsonUtil.fromJsonFile("src/test/resources/author/nextImageTaskTest2.json", ImageTask.class);
        assertEquals(intendedLastTask, nextTask);

        nextTask = parServer.nextImageTask("s2");
        assertNotNull(nextTask);
        assertEquals(intendedLastTask, nextTask);

    }

    @Test
    public void imageTaskResponseSubmittedAndCalcScoreTest() throws IOException{

        Datastore datastore = new JsonDatastore("src/test/resources/author/SampleQuestionPool.json", "src/test/resources/autoGenerated/students");
        ParServer parServer = new ParServer(datastore);
        ImageTaskResponse responseSet2=new ImageTaskResponse("response1", Arrays.asList("PlaneQ1", "PlaneQ2", "PlaneQ3", "PlaneQ4", "PlaneQ5", "StructureQ1", "StructureQ2", "StructureQ3", "StructureQ4", "StructureQ5", "ZoneQ1", "ZoneQ2", "ZoneQ3", "ZoneQ4", "ZoneQ5"),Arrays.asList("Latera", "Transvers", "Latera", "Latera", "Transvers", "bone", "ligament", "tendon", "bone", "tumor", "3c", "1b", "3c", "2a", "2b"));
        ImageTaskResponse responseSet3=new ImageTaskResponse("response1", Arrays.asList("PlaneQ1", "PlaneQ2", "PlaneQ3", "PlaneQ4", "PlaneQ5", "StructureQ1", "StructureQ2", "StructureQ3", "StructureQ4", "StructureQ5", "ZoneQ1", "ZoneQ2", "ZoneQ3", "ZoneQ4", "ZoneQ5"),Arrays.asList("I'm","bad","student","I'm","bad","student","I'm","bad","student","I'm","bad","student","I'm","bad","student"));
        List<ImageTaskResponse> responsesFromFile = JsonUtil.listFromJsonFile("src/test/resources/author/SampleResponses.json", ImageTaskResponse.class);

        //star student
        parServer.imageTaskResponseSubmitted(responsesFromFile.get(0),"s1");//gives responses from response file
        assertEquals(100.0,parServer.calcScore("s1"), DataUtil.OK_DOUBLE_MARGIN);


        //great student
        parServer.imageTaskResponseSubmitted(responseSet2,"s2");//gives responses from responseSet2 that contains some right and wrong answers
        assertEquals(83.3333333,parServer.calcScore("s2"),DataUtil.OK_DOUBLE_MARGIN);
        parServer.imageTaskResponseSubmitted(responsesFromFile.get(0),"s2");//gives responses from file that give all the correct answer (for scores that are 0 gives the user 50)
        assertEquals(91.66666666666,parServer.calcScore("s2"), DataUtil.OK_DOUBLE_MARGIN);


        //terrible student
        parServer.imageTaskResponseSubmitted(responsesFromFile.get(0),"s3");//gives responses from file
        assertEquals(100.0,parServer.calcScore("s3"), DataUtil.OK_DOUBLE_MARGIN);
        parServer.imageTaskResponseSubmitted(responseSet3,"s3");//gives all wrong answers so it give the student half credit for each question
        assertEquals(50.0,parServer.calcScore("s3"), DataUtil.OK_DOUBLE_MARGIN);


    }

    @AfterEach
    public void cleanUp() throws IOException{
        //deletes files so test doesn't keep writing to the same file multiple times
        Path path1 = Paths.get("src/test/resources/autoGenerated/students/s1.json");
        Files.deleteIfExists(path1);
        Path path2 = Paths.get("src/test/resources/autoGenerated/students/s2.json");
        Files.deleteIfExists(path2);
        Path path3 = Paths.get("src/test/resources/autoGenerated/students/s3.json");
        Files.deleteIfExists(path3);
    }
}
