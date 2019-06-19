package edu.ithaca.dragon.par.studentModel;


import edu.ithaca.dragon.par.domainModel.Question;
import edu.ithaca.dragon.util.JsonUtil;

import java.io.IOException;
import java.util.List;


import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class UserQuestionSetTest {

    @Test
    public void getLenSeenTest() throws IOException {
        List<Question> questionsFromFile = JsonUtil.listFromJsonFile("src/test/resources/author/SampleQuestions.json", Question.class);
        UserQuestionSet que = new UserQuestionSet("99", questionsFromFile);

        //checks all questions are unseen
        int len = que.getLenOfSeenQuestions();
        assertEquals(0, len);

        //checks 1 question has been seen
        que.givenQuestion("PlaneQ1");
        len = que.getLenOfSeenQuestions();
        assertEquals(1,len);

        //checks 2 questions have been seen
        que.givenQuestion("StructureQ1");
        len = que.getLenOfSeenQuestions();
        assertEquals(2,len);

        //checks getLen does not change the length of seen list
        len = que.getLenOfSeenQuestions();
        assertEquals(2,len);

        //checks 3 questions have been seen
        que.givenQuestion("ZoneQ1");
        len = que.getLenOfSeenQuestions();
        assertEquals(3,len);

        //checks length of seen questions list remains the same for a repeat question
        que.givenQuestion("ZoneQ1");
        len = que.getLenOfSeenQuestions();
        assertEquals(3,len);

        //checks length of seen list does not change when invalid question ID is used
        que.givenQuestion("4");
        len = que.getLenOfSeenQuestions();
        assertEquals(3,len);

        //checks length of seen list does not change when invalid question ID is used
        que.givenQuestion("1");
        len = que.getLenOfSeenQuestions();
        assertEquals(3,len);

    }

    @Test
    public void getLenUnseenTest() throws IOException {
        List<Question> questionsFromFile = JsonUtil.listFromJsonFile("src/test/resources/author/SampleQuestions.json", Question.class);
        UserQuestionSet que = new UserQuestionSet("99", questionsFromFile);

        //checks all questions are seen
        int len = que.getLenOfUnseenQuestions();
        assertEquals(3, len);

        //checks 2 questions are unseen
        que.givenQuestion("PlaneQ1");
        len = que.getLenOfUnseenQuestions();
        assertEquals(2,len);

        //checks 1 question is unseen
        que.givenQuestion("StructureQ1");
        len = que.getLenOfUnseenQuestions();
        assertEquals(1,len);

        //checks getLen does not change the length of seen list
        len = que.getLenOfUnseenQuestions();
        assertEquals(1,len);

        //checks 0 questions are unseen
        que.givenQuestion("ZoneQ1");
        len = que.getLenOfUnseenQuestions();
        assertEquals(0,len);

        //checks length of unseen questions list remains the same for a repeat question
        que.givenQuestion("ZoneQ1");
        len = que.getLenOfUnseenQuestions();
        assertEquals(0,len);

        //checks length of unseen list does not change when invalid question ID is used
        que.givenQuestion("4");
        len = que.getLenOfUnseenQuestions();
        assertEquals(0,len);

        //checks length of unseen list does not change when invalid question ID is used
        que.givenQuestion("1");
        len = que.getLenOfUnseenQuestions();
        assertEquals(0,len);

    }

    @Test
    public void getQTest() throws IOException{
        List<Question> questionsFromFile = JsonUtil.listFromJsonFile("src/test/resources/author/SampleQuestions.json", Question.class);
        UserQuestionSet que = new UserQuestionSet("100", questionsFromFile);

        //checks correct question is returned
        Question q = que.getQ("PlaneQ1");
        assertEquals("On which plane is the ultrasound taken?", q.getQuestionText());

        //checks correct question is returned
        q = que.getQ("StructureQ1");
        assertEquals("What structure is in the near field?", q.getQuestionText());

        //checks correct question is returned
        q = que.getQ("ZoneQ1");
        assertEquals("In what zone is this ultrasound taken?", q.getQuestionText());

        //checks correct question is returned when repeating question ID
        q = que.getQ("ZoneQ1");
        assertEquals("In what zone is this ultrasound taken?", q.getQuestionText());

        //checks no question is returned
        q = que.getQ("5");
        assertNull(q);

        //checks no question is returned
        q = que.getQ("4");
        assertNull(q);


    }

    @Test
    public void getTimesSeenTest() throws IOException{
        List<Question> questionsFromFile = JsonUtil.listFromJsonFile("src/test/resources/author/SampleQuestions.json", Question.class);
        UserQuestionSet que = new UserQuestionSet("101", questionsFromFile);

        //checks question has not been seen
        int seen1 = que.getTimesSeen("PlaneQ1");
        assertEquals(0, seen1);

        //checks question has been seen once
        que.givenQuestion("PlaneQ1");
        int seen2 = que.getTimesSeen("PlaneQ1");
        assertEquals(1, seen2);

        //checks question has been seen twice
        que.givenQuestion("StructureQ1");
        que.increaseTimesSeen("StructureQ1");
        int seen3 = que.getTimesSeen("StructureQ1");
        assertEquals(2, seen3);

        //checks seeing question multiple times in a row
        que.givenQuestion("ZoneQ1");
        que.increaseTimesSeen("ZoneQ1");
        que.increaseTimesSeen("ZoneQ1");
        que.increaseTimesSeen("ZoneQ1");
        int seen4 = que.getTimesSeen("ZoneQ1");
        assertEquals(4, seen4);

        //checks with invalid question ID
        int seen5 = que.getTimesSeen("5");
        assertEquals(0, seen5);

        //checks with invalid question ID
        int seen6 = que.getTimesSeen("6");
        assertEquals(0, seen6);

        //checks with invalid question ID
        int seen42 = que.getTimesSeen("42");
        assertEquals(0, seen42);

    }


    @Test
    public void getUserIdTest() throws IOException{
        List<Question> questionsFromFile = JsonUtil.listFromJsonFile("src/test/resources/author/SampleQuestions.json", Question.class);
        UserQuestionSet que1 = new UserQuestionSet("1", questionsFromFile);

        //checks correct user ID is retrieved
        String userId1 = que1.getUserId();
        assertEquals("1", userId1);

        //checks correct user ID is retrieved
        UserQuestionSet que2 = new UserQuestionSet("2", questionsFromFile);
        String userId2 = que2.getUserId();
        assertEquals("2", userId2);

        //checks correct user ID is retrieved
        UserQuestionSet que3 = new UserQuestionSet("3", questionsFromFile);
        String userId3 = que3.getUserId();
        assertEquals("3", userId3);

        //checks correct user ID is retrieved
        UserQuestionSet que4 = new UserQuestionSet("557", questionsFromFile);
        String userId4 = que4.getUserId();
        assertEquals("557", userId4);

        //checks most previously accessed userQuestionSets to make sure user IDs have not been changed
        userId2 = que2.getUserId();
        assertEquals("2", userId2);
        userId1 = que1.getUserId();
        assertEquals("1", userId1);
        userId3 = que3.getUserId();
        assertEquals("3", userId3);

    }

    @Test
    public void increaseTimesSeenTest() throws IOException{
        List<Question> questionsFromFile = JsonUtil.listFromJsonFile("src/test/resources/author/SampleQuestions.json", Question.class);
        UserQuestionSet que = new UserQuestionSet("1", questionsFromFile);

        //checks times seen increases consecutively
        que.givenQuestion("PlaneQ1");
        int seen = que.getTimesSeen("PlaneQ1");
        assertEquals(1, seen);
        que.increaseTimesSeen("PlaneQ1");
        seen = que.getTimesSeen("PlaneQ1");
        assertEquals(2, seen);

        //checks times seen increases multiple times consecutively
        que.givenQuestion("StructureQ1");
        seen = que.getTimesSeen("StructureQ1");
        assertEquals(1, seen);
        que.increaseTimesSeen("StructureQ1");
        que.increaseTimesSeen("StructureQ1");
        seen = que.getTimesSeen("StructureQ1");
        assertEquals(3, seen);

        //checks unseen question doesn't have an increase in times seen(has not been seen)
        //checks other question does not have an increase in times seen
        seen = que.getTimesSeen("ZoneQ1");
        assertEquals(0, seen);
        que.increaseTimesSeen("ZoneQ1");
        seen = que.getTimesSeen("ZoneQ1");
        assertEquals(0, seen);
        seen = que.getTimesSeen("StructureQ1");
        assertEquals(3, seen);

        //invalid question ID
        seen = que.getTimesSeen("2");
        assertEquals(0, seen);

        //invalid question ID
        seen = que.getTimesSeen("7");
        assertEquals(0, seen);

        //invalid question ID
        seen = que.getTimesSeen("4");
        assertEquals(0, seen);

        //checks question has no increase in times seen
        seen = que.getTimesSeen("StructureQ1");
        assertEquals(3, seen);

    }

    @Test
    public void getUnseenQuestionsTest() throws IOException{
        List<Question> questionsFromFile = JsonUtil.listFromJsonFile("src/test/resources/author/SampleQuestions.json", Question.class);
        UserQuestionSet que = new UserQuestionSet("1", questionsFromFile);

        //checks all questions are unseen
        List<Question> unseenList = que.getUnseenQuestions();
        int len = unseenList.size();
        assertEquals(3, len);

        //decrease in number unseen
        que.givenQuestion("StructureQ1");
        unseenList = que.getUnseenQuestions();
        len = unseenList.size();
        assertEquals(2, len);

        //no decrease in number unseen
        que.givenQuestion("StructureQ1");
        unseenList = que.getUnseenQuestions();
        len = unseenList.size();
        assertEquals(2, len);

        //decrease in number unseen
        que.givenQuestion("ZoneQ1");
        unseenList = que.getUnseenQuestions();
        len = unseenList.size();
        assertEquals(1, len);

        //invalid question ID
        que.givenQuestion("6");
        unseenList = que.getUnseenQuestions();
        len = unseenList.size();
        assertEquals(1, len);

        //invalid question ID
        que.givenQuestion("5");
        unseenList = que.getUnseenQuestions();
        len = unseenList.size();
        assertEquals(1, len);

        //decrease in number unseen
        que.givenQuestion("PlaneQ1");
        unseenList = que.getUnseenQuestions();
        len = unseenList.size();
        assertEquals(0, len);

        //no decrease in number unseen
        que.givenQuestion("ZoneQ1");
        unseenList = que.getUnseenQuestions();
        len = unseenList.size();
        assertEquals(0, len);

        //no decrease in number unseen
        que.givenQuestion("PlaneQ1");
        unseenList = que.getUnseenQuestions();
        len = unseenList.size();
        assertEquals(0, len);
    }

    @Test
    public void getSeenQuestionsTest() throws IOException{
        List<Question> questionsFromFile = JsonUtil.listFromJsonFile("src/test/resources/author/SampleQuestions.json", Question.class);
        UserQuestionSet que = new UserQuestionSet("1", questionsFromFile);

        //checks all questions are seen
        List<Question> seenList = que.getSeenQuestions();
        int len = seenList.size();
        assertEquals(0, len);

        //increase in number seen
        que.givenQuestion("StructureQ1");
        seenList = que.getSeenQuestions();
        len = seenList.size();
        assertEquals(1, len);

        //no increase in number seen
        que.givenQuestion("StructureQ1");
        seenList = que.getSeenQuestions();
        len = seenList.size();
        assertEquals(1, len);

        //increase in number seen
        que.givenQuestion("ZoneQ1");
        seenList = que.getSeenQuestions();
        len = seenList.size();
        assertEquals(2, len);

        //invalid question ID
        que.givenQuestion("6");
        seenList = que.getSeenQuestions();
        len = seenList.size();
        assertEquals(2, len);

        //invalid question ID
        que.givenQuestion("5");
        seenList = que.getSeenQuestions();
        len = seenList.size();
        assertEquals(2, len);

        //increase in number seen
        que.givenQuestion("PlaneQ1");
        seenList = que.getSeenQuestions();
        len = seenList.size();
        assertEquals(3, len);

        //no increase in number seen
        que.givenQuestion("ZoneQ1");
        seenList = que.getSeenQuestions();
        len = seenList.size();
        assertEquals(3, len);

        //no increase in number seen
        que.givenQuestion("PlaneQ1");
        seenList = que.getSeenQuestions();
        len = seenList.size();
        assertEquals(3, len);
    }

    @Test
    public void givenQuestionTest() throws IOException{
        List<Question> questionsFromFile = JsonUtil.listFromJsonFile("src/test/resources/author/SampleQuestions.json", Question.class);
        UserQuestionSet que = new UserQuestionSet("14", questionsFromFile);

        //checks all questions are unseen before tests start
        List<Question> seen = que.getSeenQuestions();
        List<Question> unseen = que.getUnseenQuestions();
        int unseenLen = unseen.size();
        int seenLen = seen.size();
        assertEquals(3, unseenLen);
        assertEquals(0, seenLen);
        for (int i = 0; i <unseen.size(); i++){
            int numSeen = que.getTimesSeen(unseen.get(i).getId());
            assertEquals(0, numSeen);
        }


        //Time seen increase for PlaneQ1, length of seen and unseen list change
        que.givenQuestion("PlaneQ1");
        seen = que.getSeenQuestions();
        unseen = que.getUnseenQuestions();
        unseenLen = unseen.size();
        seenLen = seen.size();
        assertEquals(2, unseenLen);
        assertEquals(1, seenLen);
        int ts = que.getTimesSeen("PlaneQ1");
        assertEquals(1, ts);

        //Time seen increase for ZoneQ1, length of seen and unseen list change
        que.givenQuestion("ZoneQ1");
        seen = que.getSeenQuestions();
        unseen = que.getUnseenQuestions();
        unseenLen = unseen.size();
        seenLen = seen.size();
        assertEquals(1, unseenLen);
        assertEquals(2, seenLen);
        ts = que.getTimesSeen("ZoneQ1");
        assertEquals(1, ts);

        //Invalid Question ID, no values change
        que.givenQuestion("3");
        seen = que.getSeenQuestions();
        unseen = que.getUnseenQuestions();
        unseenLen = unseen.size();
        seenLen = seen.size();
        assertEquals(1, unseenLen);
        assertEquals(2, seenLen);
        ts = que.getTimesSeen("3");
        assertEquals(0, ts);

        //already seen question, seen and unseen list lengths do not change, times seen increases
        que.givenQuestion("PlaneQ1");
        seen = que.getSeenQuestions();
        unseen = que.getUnseenQuestions();
        unseenLen = unseen.size();
        seenLen = seen.size();
        assertEquals(1, unseenLen);
        assertEquals(2, seenLen);
        ts = que.getTimesSeen("PlaneQ1");
        assertEquals(2, ts);

        //new question, seen and unseen question lists change in length, times seen is 1
        que.givenQuestion("StructureQ1");
        seen = que.getSeenQuestions();
        unseen = que.getUnseenQuestions();
        unseenLen = unseen.size();
        seenLen = seen.size();
        assertEquals(0, unseenLen);
        assertEquals(3, seenLen);
        ts = que.getTimesSeen("StructureQ1");
        assertEquals(1, ts);

        //invalid question ID, no values change
        que.givenQuestion("1");
        seen = que.getSeenQuestions();
        unseen = que.getUnseenQuestions();
        unseenLen = unseen.size();
        seenLen = seen.size();
        assertEquals(0, unseenLen);
        assertEquals(3, seenLen);
        ts = que.getTimesSeen("1");
        assertEquals(0, ts);

        //already seen question, times seen increases, seen and unseen lists remain unchanged
        que.givenQuestion("ZoneQ1");
        seen = que.getSeenQuestions();
        unseen = que.getUnseenQuestions();
        unseenLen = unseen.size();
        seenLen = seen.size();
        assertEquals(0, unseenLen);
        assertEquals(3, seenLen);
        ts = que.getTimesSeen("ZoneQ1");
        assertEquals(2, ts);

        //increase in times seen
        que.givenQuestion("ZoneQ1");
        ts = que.getTimesSeen("ZoneQ1");
        assertEquals(3, ts);

        //increase in times seen
        que.givenQuestion("ZoneQ1");
        ts = que.getTimesSeen("ZoneQ1");
        assertEquals(4, ts);

        //increase in times seen
        que.givenQuestion("ZoneQ1");
        ts = que.getTimesSeen("ZoneQ1");
        assertEquals(5, ts);

        //increase in times seen
        que.givenQuestion("ZoneQ1");
        ts = que.getTimesSeen("ZoneQ1");
        assertEquals(6, ts);

        //no change in times seen
        ts = que.getTimesSeen("PlaneQ1");
        assertEquals(2, ts);

        //no change in times seen
        ts = que.getTimesSeen("StructureQ1");
        assertEquals(1, ts);

        //no change in times seen
        ts = que.getTimesSeen("ZoneQ1");
        assertEquals(6, ts);

        //increase in times seen
        que.givenQuestion("PlaneQ1");
        ts = que.getTimesSeen("PlaneQ1");
        assertEquals(3, ts);


        //new question set
        List<Question> qFromFile = JsonUtil.listFromJsonFile("src/test/resources/author/SampleQuestions.json", Question.class);
        UserQuestionSet qSet = new UserQuestionSet("14", qFromFile);

        //checks all questions have been seen 0 times
        List<Question> unseenQ = qSet.getUnseenQuestions();
        for (int i = 0; i <unseenQ.size(); i++){
            int numSeen = qSet.getTimesSeen(unseenQ.get(i).getId());
            assertEquals(0, numSeen);
        }

        //increase in times seen
        qSet.givenQuestion("PlaneQ1");
        qSet.givenQuestion("PlaneQ1");
        ts = qSet.getTimesSeen("PlaneQ1");
        assertEquals(2, ts);

        //other question seen values have not been changed
        ts = qSet.getTimesSeen("ZoneQ1");
        assertEquals(0, ts);
        ts = qSet.getTimesSeen("StructureQ1");
        assertEquals(0, ts);

        //increase in times seen
        qSet.givenQuestion("ZoneQ1");
        qSet.givenQuestion("ZoneQ1");
        qSet.givenQuestion("ZoneQ1");
        ts = qSet.getTimesSeen("ZoneQ1");
        assertEquals(3, ts);

        //other question seen values have not been changed
        ts = qSet.getTimesSeen("PlaneQ1");
        assertEquals(2, ts);
        ts = qSet.getTimesSeen("StructureQ1");
        assertEquals(0, ts);

    }

}
