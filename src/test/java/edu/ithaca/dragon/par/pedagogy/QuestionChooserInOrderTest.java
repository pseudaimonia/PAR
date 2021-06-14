package edu.ithaca.dragon.par.pedagogy;

import edu.ithaca.dragon.par.domain.DomainDatasource;
import edu.ithaca.dragon.par.domain.DomainDatasourceSimple;
import edu.ithaca.dragon.par.domain.Question;
import edu.ithaca.dragon.util.JsonUtil;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class QuestionChooserInOrderTest {

    @Test
    public void checkRepeatQuestions() throws IOException {
        DomainDatasource domainDatasource = new DomainDatasourceSimple(JsonUtil.listFromJsonFile("src/test/resources/rewrite/SampleQuestions.json", Question.class));
        QuestionChooserInOrder questionChooser = new QuestionChooserInOrder();
//        assertEquals("generic0", questionChooser.chooseQuestion("poor", domainDatasource, ));
    }

}