package edu.ithaca.dragon.par;

import edu.ithaca.dragon.par.domainModel.Question;
import edu.ithaca.dragon.par.domainModel.QuestionPool;
import edu.ithaca.dragon.par.domainModel.equineUltrasound.EquineQuestionTypes;
import edu.ithaca.dragon.par.io.*;
import edu.ithaca.dragon.par.studentModel.QuestionResponse;
import edu.ithaca.dragon.par.studentModel.ResponsesPerQuestion;
import edu.ithaca.dragon.par.studentModel.StudentModel;
import edu.ithaca.dragon.par.studentModel.UserResponseSet;
import edu.ithaca.dragon.util.JsonIoHelper;
import edu.ithaca.dragon.util.JsonIoHelperDefault;
import edu.ithaca.dragon.util.JsonIoUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.*;

class ParStudentAndAuthorServerTest {

    @Test
    void transferAuthoredQuestionsToStudentServerTest(@TempDir Path tempDir) throws IOException {
        Path currentQuestionPath = tempDir.resolve("currentAuthorQuestions.json");
        Path currentQuestionTemplatePath = tempDir.resolve("currentQuestionTemplates.json");
        JsonAuthorDatastore jsonAuthorDatastore = new JsonAuthorDatastore(
                currentQuestionPath.toString(), "src/test/resources/author/SampleQuestionPool.json",
                currentQuestionTemplatePath.toString(), "src/test/resources/author/DemoQuestionPoolTemplate.json",
                tempDir.resolve("currentAuthorModel.json").toString(), new JsonIoHelperDefault());
        assertEquals(15, jsonAuthorDatastore.getAllAuthoredQuestions().size());

        Path currentStudentModelDir = tempDir.resolve("students");
        assertTrue(new File(currentStudentModelDir.toString()).mkdir());
        JsonStudentModelDatastore jsonStudentDatastore = new JsonStudentModelDatastore(
                tempDir.resolve("currentQuestions.json").toString(),
                "src/test/resources/author/DemoQuestionPoolFollowup.json",
                new JsonIoHelperDefault(),
                currentStudentModelDir.toString());
        assertEquals(47, jsonStudentDatastore.getAllQuestions().size());

        JsonIoHelper jsonIoHelper = new JsonIoHelperDefault();
        JsonIoUtil jsonIoUtil = new JsonIoUtil(jsonIoHelper);
        List<CohortRecord> cohortRecords = jsonIoUtil.listFromFile("src/test/resources/author/CohortRecordsToFromJsonTest.json", CohortRecord.class);
        JSONCohortDatastore jsonCohortDatastore = CohortRecord.makeCohortDatastoreFromCohortRecords(cohortRecords);
        ParStudentAndAuthorServer parStudentAndAuthorServer = new ParStudentAndAuthorServer(jsonStudentDatastore, jsonAuthorDatastore, jsonCohortDatastore);
        parStudentAndAuthorServer.transferAuthoredQuestionsToStudentServer();

        assertEquals(0, jsonAuthorDatastore.getAllAuthoredQuestions().size());
        assertEquals(62, jsonStudentDatastore.getAllQuestions().size());

        //load again from file to ensure changes are kept
        jsonAuthorDatastore = new JsonAuthorDatastore(currentQuestionPath.toString(), currentQuestionTemplatePath.toString(), tempDir.resolve("currentAuthorModel.json").toString());
        jsonStudentDatastore = new JsonStudentModelDatastore(tempDir.resolve("currentQuestions.json").toString(), currentStudentModelDir.toString());

        assertEquals(0, jsonAuthorDatastore.getAllAuthoredQuestions().size());
        assertEquals(62, jsonStudentDatastore.getAllQuestions().size());
    }

    @Test
    void windowSizeTests(@TempDir Path tempDir) throws IOException {
        Path currentQuestionPath = tempDir.resolve("currentAuthorQuestions.json");
        Path currentQuestionTemplatePath = tempDir.resolve("currentQuestionTemplates.json");
        JsonAuthorDatastore jsonAuthorDatastore = new JsonAuthorDatastore(
                currentQuestionPath.toString(), "src/test/resources/author/SampleQuestionPool.json",
                currentQuestionTemplatePath.toString(), "src/test/resources/author/DemoQuestionPoolTemplate.json",
                tempDir.resolve("currentAuthorModel.json").toString(), new JsonIoHelperDefault());

        Path currentStudentModelDir = tempDir.resolve("students");
        assertTrue(new File(currentStudentModelDir.toString()).mkdir());
        JsonStudentModelDatastore jsonStudentDatastore = new JsonStudentModelDatastore(
                tempDir.resolve("currentQuestions.json").toString(),
                "src/test/resources/author/SampleQuestionsEmpty.json",
                new JsonIoHelperDefault(),
                currentStudentModelDir.toString());

        JsonIoHelper jsonIoHelper = new JsonIoHelperDefault();
        JsonIoUtil jsonIoUtil = new JsonIoUtil(jsonIoHelper);
        List<CohortRecord> cohortRecords = jsonIoUtil.listFromFile("src/test/resources/author/CohortRecordsToFromJsonTest.json", CohortRecord.class);
        JSONCohortDatastore jsonCohortDatastore = CohortRecord.makeCohortDatastoreFromCohortRecords(cohortRecords);
        ParStudentAndAuthorServer parStudentAndAuthorServer = new ParStudentAndAuthorServer(jsonStudentDatastore, jsonAuthorDatastore, jsonCohortDatastore);
        Map<EquineQuestionTypes, String> estStrings = parStudentAndAuthorServer.calcKnowledgeEstimateStringsByType("no one");
        assertEquals(4, estStrings.size());
        assertEquals("", estStrings.get(EquineQuestionTypes.plane));
        assertEquals("", estStrings.get(EquineQuestionTypes.attachment));

        parStudentAndAuthorServer.transferAuthoredQuestionsToStudentServer();
        estStrings = parStudentAndAuthorServer.calcKnowledgeEstimateStringsByType("no one");

        assertEquals("____", estStrings.get(EquineQuestionTypes.plane));
        assertEquals("____", estStrings.get(EquineQuestionTypes.attachment));

    }

    @Test
    public void nextImageTaskTest(@TempDir Path tempDir) throws IOException{
        Path currentStudentModelDir = tempDir.resolve("students");
        assertTrue(new File(currentStudentModelDir.toString()).mkdir());
        JsonStudentModelDatastore jsonStudentDatastore = new JsonStudentModelDatastore(
                tempDir.resolve("currentQuestions.json").toString(),
                "src/test/resources/author/SampleQuestionPool3.json",
                new JsonIoHelperDefault(),
                currentStudentModelDir.toString());

        JsonIoHelper jsonIoHelper = new JsonIoHelperDefault();
        JsonIoUtil jsonIoUtil = new JsonIoUtil(jsonIoHelper);
        List<CohortRecord> cohortRecords = jsonIoUtil.listFromFile("src/test/resources/author/CohortRecordsToFromJsonTest.json", CohortRecord.class);
        JSONCohortDatastore jsonCohortDatastore = CohortRecord.makeCohortDatastoreFromCohortRecords(cohortRecords);
        ParStudentAndAuthorServer parStudentAndAuthorServer = new ParStudentAndAuthorServer(jsonStudentDatastore, null, jsonCohortDatastore);

        ImageTask nextTask = parStudentAndAuthorServer.nextImageTask("s1");
        ImageTask intendedFirstTask = new JsonIoUtil(new JsonIoHelperDefault()).fromFile("src/test/resources/author/nextImageTaskTest1.json", ImageTask.class);
        assertEquals(intendedFirstTask, nextTask);

        nextTask = parStudentAndAuthorServer.nextImageTask("s2");
        assertEquals(intendedFirstTask, nextTask);

        nextTask = parStudentAndAuthorServer.nextImageTask("s1");

        assertNotNull(nextTask);
        ImageTask intendedLastTask = new JsonIoUtil(new JsonIoHelperDefault()).fromFile("src/test/resources/author/nextImageTaskTest2.json", ImageTask.class);
        assertEquals(intendedLastTask, nextTask);

        nextTask = parStudentAndAuthorServer.nextImageTask("s2");
        assertNotNull(nextTask);
        assertEquals(intendedLastTask, nextTask);

    }

    @Test
    public void imageTaskResponseSubmittedAndCalcScoreTest(@TempDir Path tempDir) throws IOException{

        Path currentStudentModelDir = tempDir.resolve("students");
        assertTrue(new File(currentStudentModelDir.toString()).mkdir());
        JsonStudentModelDatastore jsonStudentDatastore = new JsonStudentModelDatastore(
                tempDir.resolve("currentQuestions.json").toString(),
                "src/test/resources/author/SampleQuestionPool3.json",
                new JsonIoHelperDefault(),
                currentStudentModelDir.toString());

        JsonIoHelper jsonIoHelper = new JsonIoHelperDefault();
        JsonIoUtil jsonIoUtil = new JsonIoUtil(jsonIoHelper);
        List<CohortRecord> cohortRecords = jsonIoUtil.listFromFile("src/test/resources/author/CohortRecordsToFromJsonTest.json", CohortRecord.class);
        JSONCohortDatastore jsonCohortDatastore = CohortRecord.makeCohortDatastoreFromCohortRecords(cohortRecords);
        ParStudentAndAuthorServer parStudentAndAuthorServer = new ParStudentAndAuthorServer(jsonStudentDatastore, null, jsonCohortDatastore);

        ImageTaskResponseOOP responseSet2=new ImageTaskResponseOOP("response1", Arrays.asList("PlaneQ1", "PlaneQ2", "PlaneQ3", "PlaneQ4", "PlaneQ5", "StructureQ1", "StructureQ2", "StructureQ3", "StructureQ4", "StructureQ5", "ZoneQ1", "ZoneQ2", "ZoneQ3", "ZoneQ4", "ZoneQ5"),Arrays.asList("Latera", "Transvers", "Latera", "Latera", "Transvers", "bone", "ligament", "tendon", "bone", "tumor", "3c", "1b", "3c", "2a", "2b"));
        ImageTaskResponseOOP responseSet3=new ImageTaskResponseOOP("response1", Arrays.asList("PlaneQ1", "PlaneQ2", "PlaneQ3", "PlaneQ4", "PlaneQ5", "StructureQ1", "StructureQ2", "StructureQ3", "StructureQ4", "StructureQ5", "ZoneQ1", "ZoneQ2", "ZoneQ3", "ZoneQ4", "ZoneQ5"),Arrays.asList("I'm","bad","student","I'm","bad","student","I'm","bad","student","I'm","bad","student","I'm","bad","student"));
        List<ImageTaskResponseOOP> responsesFromFile = new JsonIoUtil(new JsonIoHelperDefault()).listFromFile("src/test/resources/author/SampleResponses.json", ImageTaskResponseOOP.class);

        //star student
        ImageTaskResponseOOP imageTaskResponseImp1 = responsesFromFile.get(0);
        imageTaskResponseImp1.setUserId("s1");
        parStudentAndAuthorServer.submitImageTaskResponse(imageTaskResponseImp1);//gives responses from response file
        assertEquals("100.000",parStudentAndAuthorServer.calcOverallKnowledgeEstimate("s1"));


        responseSet2.setUserId("s2");
        parStudentAndAuthorServer.submitImageTaskResponse(responseSet2);//gives responses from responseSet2 that contains some right and wrong answers
        assertEquals("83.333",parStudentAndAuthorServer.calcOverallKnowledgeEstimate("s2"));
        //score should stay the same even when the correct answers are entered afterwards since they were all answered in a
        //time window that didnt exceed 30 seconds
        imageTaskResponseImp1.setUserId("s2");
        parStudentAndAuthorServer.submitImageTaskResponse(imageTaskResponseImp1);
        assertEquals("83.333",parStudentAndAuthorServer.calcOverallKnowledgeEstimate("s2"));
        parStudentAndAuthorServer.submitImageTaskResponse(imageTaskResponseImp1);
        assertEquals("83.333",parStudentAndAuthorServer.calcOverallKnowledgeEstimate("s2"));


        //score should go way down since they had it, but then got it wrong within 30 seconds
        imageTaskResponseImp1.setUserId("s3");
        parStudentAndAuthorServer.submitImageTaskResponse(responsesFromFile.get(0));//gives responses from file
        assertEquals("100.000",parStudentAndAuthorServer.calcOverallKnowledgeEstimate("s3"));
        responseSet3.setUserId("s3");
        parStudentAndAuthorServer.submitImageTaskResponse(responseSet3);
        assertEquals("0.000",parStudentAndAuthorServer.calcOverallKnowledgeEstimate("s3"));

    }


    @Test
    public void increaseTimesSeenTest(@TempDir Path tempDir) throws IOException{

        //set up
        Path currentStudentModelDir = tempDir.resolve("students");
        assertTrue(new File(currentStudentModelDir.toString()).mkdir());
        JsonStudentModelDatastore jsonStudentDatastore = new JsonStudentModelDatastore(
                tempDir.resolve("currentQuestions.json").toString(),
                "src/test/resources/author/SampleQuestionPool3.json",
                new JsonIoHelperDefault(),
                currentStudentModelDir.toString());
        JsonIoHelper jsonIoHelper = new JsonIoHelperDefault();
        JsonIoUtil jsonIoUtil = new JsonIoUtil(jsonIoHelper);
        List<CohortRecord> cohortRecords = jsonIoUtil.listFromFile("src/test/resources/author/CohortRecordsToFromJsonTest.json", CohortRecord.class);
        JSONCohortDatastore jsonCohortDatastore = CohortRecord.makeCohortDatastoreFromCohortRecords(cohortRecords);
        ParStudentAndAuthorServer parStudentAndAuthorServer = new ParStudentAndAuthorServer(jsonStudentDatastore, null, jsonCohortDatastore);

        //user has seen no questions
        assertEquals(0,jsonStudentDatastore.getOrCreateStudentModel("testUser111").getSeenQuestionCount());

        //calling nextImageTask should increase timesSeen
        parStudentAndAuthorServer.nextImageTask("testUser111");
        assertEquals(1,jsonStudentDatastore.getOrCreateStudentModel("testUser111").getSeenQuestionCount());

        //even after logging out, the increase of timesSeen should have been saved
        parStudentAndAuthorServer.logout("testUser111");
        assertEquals(1,jsonStudentDatastore.getOrCreateStudentModel("testUser111").getSeenQuestionCount());

        //submitting answers should not increase timesSeen
        parStudentAndAuthorServer.submitImageTaskResponse(new ImageTaskResponseOOP("testUser111", Arrays.asList("PlaneQ1"), Arrays.asList("Longitudinal")));
        assertEquals(1,jsonStudentDatastore.getOrCreateStudentModel("testUser111").getSeenQuestionCount());
    }

    @Test
    public void structureQuestionBug(@TempDir Path tempDir) throws IOException {

        Path currentStudentModelDir = tempDir.resolve("students");
        assertTrue(new File(currentStudentModelDir.toString()).mkdir());
        JsonStudentModelDatastore jsonStudentDatastore = new JsonStudentModelDatastore(
                tempDir.resolve("currentQuestions.json").toString(),
                "src/test/resources/author/SampleQuestionPool4.json",
                new JsonIoHelperDefault(),
                currentStudentModelDir.toString());

        JsonIoHelper jsonIoHelper = new JsonIoHelperDefault();
        JsonIoUtil jsonIoUtil = new JsonIoUtil(jsonIoHelper);
        List<CohortRecord> cohortRecords = jsonIoUtil.listFromFile("src/test/resources/author/CohortRecordsToFromJsonTest.json", CohortRecord.class);
        JSONCohortDatastore jsonCohortDatastore = CohortRecord.makeCohortDatastoreFromCohortRecords(cohortRecords);
        ParStudentAndAuthorServer parStudentAndAuthorServer = new ParStudentAndAuthorServer(jsonStudentDatastore, null, jsonCohortDatastore);

        //load existing student into the jsonStudentDatastore
        Files.copy(Paths.get("src/test/resources/author/students/PSaASTestUser.json"), tempDir.resolve("students/PSaASTestUser.json"), StandardCopyOption.REPLACE_EXISTING);
        assertEquals(10,jsonStudentDatastore.getOrCreateStudentModel("PSaASTestUser").getResponseCount());

        //first time answering these structure questions
        //TODO: Note! The response being lowercase might be a factor
        UserResponseSet urs = jsonStudentDatastore.getOrCreateStudentModel("PSaASTestUser").getUserResponseSet();
        ResponsesPerQuestion responsesPerQuestion1 = new ResponsesPerQuestion("PSAaSTestUser", jsonStudentDatastore.findTopLevelQuestionTemplateById("341-structure0-./images/metacarpal42.jpg"), "deep digital flexor tendon");
        ResponsesPerQuestion responsesPerQuestion2 = new ResponsesPerQuestion("PSAaSTestUser", jsonStudentDatastore.findTopLevelQuestionTemplateById("344-structure1-./images/metacarpal42.jpg"), "suspensory ligament (branches)");
        ResponsesPerQuestion responsesPerQuestion3 = new ResponsesPerQuestion("PSAaSTestUser", jsonStudentDatastore.findTopLevelQuestionTemplateById("347-structure2-./images/metacarpal42.jpg"), "superficial digital flexor tendon");
        ResponsesPerQuestion responsesPerQuestion4 = new ResponsesPerQuestion("PSAaSTestUser", jsonStudentDatastore.findTopLevelQuestionTemplateById("350-structure3-./images/metacarpal42.jpg"), "metacarpus bone 3 (third metacarpal bone)");
        urs.addResponse(responsesPerQuestion1);
        urs.addResponse(responsesPerQuestion2);  //these calls could use .addAllResponses()
        urs.addResponse(responsesPerQuestion3);
        urs.addResponse(responsesPerQuestion4);

        //submitting the same structure questions and their attachment questions
        //TODO: check if structure questions get submitted when their attachment questions are submitted
        ResponsesPerQuestion responsesPerQuestion5 = new ResponsesPerQuestion("PSAaSTestUser", jsonStudentDatastore.findTopLevelQuestionTemplateById("341-structure0-./images/metacarpal42.jpg"), "deep digital flexor tendon"); //structure
        ResponsesPerQuestion responsesPerQuestion6 = new ResponsesPerQuestion("PSAaSTestUser", jsonStudentDatastore.findTopLevelQuestionTemplateById("341-structure0-./images/metacarpal42.jpg").getFollowupQuestions().get(0), "lateral humeral epicondyle"); //attachment
        ResponsesPerQuestion responsesPerQuestion7 = new ResponsesPerQuestion("PSAaSTestUser", jsonStudentDatastore.findTopLevelQuestionTemplateById("341-structure0-./images/metacarpal42.jpg").getFollowupQuestions().get(1), "distal phalanx (p3)");  //attachment
        ResponsesPerQuestion responsesPerQuestion8 = new ResponsesPerQuestion("PSAaSTestUser", jsonStudentDatastore.findTopLevelQuestionTemplateById("344-structure1-./images/metacarpal42.jpg"), "suspensory ligament (branches)"); //structure
        ResponsesPerQuestion responsesPerQuestion9 = new ResponsesPerQuestion("PSAaSTestUser", jsonStudentDatastore.findTopLevelQuestionTemplateById("344-structure1-./images/metacarpal42.jpg").getFollowupQuestions().get(0), "proximal metacarpus 3"); //attachment
        ResponsesPerQuestion responsesPerQuestion10 = new ResponsesPerQuestion("PSAaSTestUser", jsonStudentDatastore.findTopLevelQuestionTemplateById("344-structure1-./images/metacarpal42.jpg").getFollowupQuestions().get(1), "Proximal sesamoid bones"); //attachment
        ResponsesPerQuestion responsesPerQuestion11 = new ResponsesPerQuestion("PSAaSTestUser", jsonStudentDatastore.findTopLevelQuestionTemplateById("347-structure2-./images/metacarpal42.jpg"), "superficial digital flexor tendon"); //structure
        ResponsesPerQuestion responsesPerQuestion12 = new ResponsesPerQuestion("PSAaSTestUser", jsonStudentDatastore.findTopLevelQuestionTemplateById("347-structure2-./images/metacarpal42.jpg").getFollowupQuestions().get(0), "medial humeral epicondyle"); //attachment
        ResponsesPerQuestion responsesPerQuestion13 = new ResponsesPerQuestion("PSAaSTestUser", jsonStudentDatastore.findTopLevelQuestionTemplateById("347-structure2-./images/metacarpal42.jpg").getFollowupQuestions().get(1), "both proximal and middle phalanxes"); //attachment


    }

    @Test
    public void getCorrectMessageTest(@TempDir Path tempDir) throws IOException{
        Path currentStudentModelDir = tempDir.resolve("students");
        assertTrue(new File(currentStudentModelDir.toString()).mkdir());
        JsonStudentModelDatastore jsonStudentDatastore = new JsonStudentModelDatastore(
                tempDir.resolve("currentQP-10-5-2020.json").toString(),
                "src/test/resources/author/currentQP-10-5-2020.json",
                new JsonIoHelperDefault(),
                currentStudentModelDir.toString());

        JsonIoHelper jsonIoHelper = new JsonIoHelperDefault();
        JsonIoUtil jsonIoUtil = new JsonIoUtil(jsonIoHelper);
        List<CohortRecord> cohortRecords = jsonIoUtil.listFromFile("src/test/resources/author/CohortRecordsToFromJsonTest.json", CohortRecord.class);
        JSONCohortDatastore jsonCohortDatastore = CohortRecord.makeCohortDatastoreFromCohortRecords(cohortRecords);
        ParStudentAndAuthorServer server = new ParStudentAndAuthorServer(jsonStudentDatastore, null, jsonCohortDatastore);

        StudentModel student = jsonStudentDatastore.getOrCreateStudentModel("masteredStudent");

        QuestionPool myQP = new QuestionPool(new JsonQuestionPoolDatastore("src/test/resources/author/currentQP-10-5-2020.json").getAllQuestions());




        ImageTask it = server.nextImageTask(student.getUserId());

        //mastered
        student.setCurrentLevel(7);
        student.setPreviousLevel(6);
        String message = server.getMessage(student.getUserId(), it);
        assertEquals("You have mastered the material, feel free to keep practicing", message);

        //not level 7, no message to display
        StudentModel student2 = jsonStudentDatastore.getOrCreateStudentModel("buckmank");
        ImageTask it2 = server.nextImageTask(student2.getUserId());
        student2.setPreviousLevel(4);
        student2.setCurrentLevel(4);
        message = server.getMessage(student2.getUserId(), it2);
        assertNull(message);


        //goes down level, structure
        List<QuestionResponseOOP> resp= new ArrayList<>();
        resp.add(new QuestionResponseOOP("491-zone-./images/metacarpal37.jpg", "In which zone is the ultrasound taken?", "1"));
        resp.add(new QuestionResponseOOP("463-zone-./images/metacarpal25.jpg", "In which zone is the ultrasound taken?", "1"));
        resp.add(new QuestionResponseOOP("379-zone-./images/metacarpal41.jpg", "In which zone is the ultrasound taken?", "1"));
        resp.add(new QuestionResponseOOP("351-zone-./images/metacarpal42.jpg", "In which zone is the ultrasound taken?", "1"));

        ImageTaskResponseOOP itr = new ImageTaskResponseOOP();
        itr.setUserId(student2.getUserId());
        itr.setQuestionResponses(resp);
        student2.imageTaskResponseSubmitted(itr, myQP, 4);

        student2.setCurrentLevel(6);
        student2.setPreviousLevel(7);
        message = server.getMessage(student2.getUserId(), it2);
        assertEquals("Looks like you're having trouble with zone questions, go look at resources and come back if you need to", message);

        //goes up level
        student2.setPreviousLevel(5);
        message = server.getMessage(student2.getUserId(), it2);
        assertEquals("You're doing great!", message);

        //stay on level 7, repeated question
        student.setPreviousLevel(7);
        student.setCurrentLevel(7);


        Date date = new Date();
        for (ResponsesPerQuestion response:student.getUserResponseSet().getResponsesPerQuestionList()){
            List<QuestionResponse> r = response.getAllResponses();
            QuestionResponse last = r.get(response.getAllResponses().size()-1);
            last.setMillSeconds(date.getTime()-1799500);
            response.setAllResponses(r);
        }
        for (Question question : it.getTaskQuestions()){
            if (student.getUserQuestionSet().getTimesSeen(question.getId())==0){
                student.increaseTimesSeen(question.getId());
            }
        }

        Question q = student.getUserQuestionSet().getAllQuestions().get(0);
        List<Question> questionList = new ArrayList<>();
        questionList.add(q);
        it.setTaskQuestions(questionList);


        ResponsesPerQuestion rpq = new ResponsesPerQuestion(student.getUserId(), q, "huh");
        student.getUserResponseSet().addResponse(rpq);



        message = server.getMessage(student.getUserId(), it);
        assertEquals("You've mastered the material and started repeating questions", message);

    }
} 