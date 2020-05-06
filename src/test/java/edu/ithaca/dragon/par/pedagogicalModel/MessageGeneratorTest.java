package edu.ithaca.dragon.par.pedagogicalModel;

import edu.ithaca.dragon.par.domainModel.Question;
import edu.ithaca.dragon.par.domainModel.QuestionPool;
import edu.ithaca.dragon.par.domainModel.equineUltrasound.EquineQuestionTypes;
import edu.ithaca.dragon.par.io.ImageTask;
import edu.ithaca.dragon.par.io.JsonQuestionPoolDatastore;
import edu.ithaca.dragon.par.io.StudentModelRecord;
import edu.ithaca.dragon.par.studentModel.StudentModel;
import edu.ithaca.dragon.util.JsonUtil;
import org.junit.jupiter.api.Test;
import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



public class MessageGeneratorTest {
    @Test
    public void generateMessageTest() throws IOException {
        //task generator

        TaskGenerator taskGenerator = new LevelTaskGenerator(EquineQuestionTypes.makeLevelToTypesMap());

        QuestionPool myQP = new QuestionPool(new JsonQuestionPoolDatastore("src/test/resources/author/testQP.json").getAllQuestions());
        StudentModelRecord smr = JsonUtil.fromJsonFile("src/test/resources/author/students/masteredStudent.json", StudentModelRecord.class);
        StudentModel masteredStudentModel = smr.buildStudentModel(myQP);
        ImageTask it = taskGenerator.makeTask(masteredStudentModel, 4);

        //mastered
        MessageGenerator.generateMessage(masteredStudentModel, it, 4);
        assertEquals("You have mastered the material, feel free to keep practicing", it.getMessage());

        //not level 7, no message
        StudentModelRecord  smr2 = JsonUtil.fromJsonFile("src/test/resources/author/students/buckmank.json", StudentModelRecord.class);
        StudentModel level4Student = smr2.buildStudentModel(myQP);
        ImageTask it2 = taskGenerator.makeTask(level4Student, 4);
        MessageGenerator.generateMessage(level4Student, it2, 4);
        assertEquals(null, it2.getMessage());

//        //add another student (3 total)
//        List<Question> noQuestions = new ArrayList<Question>();
//        StudentModel student = new StudentModel("student", noQuestions);

    }
}
