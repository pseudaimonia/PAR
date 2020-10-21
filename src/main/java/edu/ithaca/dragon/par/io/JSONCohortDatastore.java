package edu.ithaca.dragon.par.io;

import edu.ithaca.dragon.par.cohortModel.Cohort;
import edu.ithaca.dragon.par.domainModel.QuestionOrderedInfo;
import edu.ithaca.dragon.par.domainModel.QuestionPool;
import edu.ithaca.dragon.par.domainModel.equineUltrasound.EquineQuestionTypes;
import edu.ithaca.dragon.par.pedagogicalModel.*;
import edu.ithaca.dragon.util.JsonIoHelperDefault;
import edu.ithaca.dragon.util.JsonIoUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JSONCohortDatastore implements CohortDatastore {
    private final Map<String, Cohort> cohortMap;
    private final List<Cohort> masterCohortList;

    public JSONCohortDatastore(){
        this.cohortMap = new HashMap<>();
        this.masterCohortList = new ArrayList<>();
    }

    @Override
    public int getNumberCohorts() {
        return masterCohortList.size();
    }

    @Override
    public int getTotalNumberStudents(){ return cohortMap.size();}

    @Override
    public List<Cohort> getMasterCohortList(){
        return masterCohortList;
    }

    public void addCohort(TaskGenerator taskGenerator, List<String> studentIDs){
        Cohort toAdd = new Cohort(taskGenerator, studentIDs);
        masterCohortList.add(toAdd);

        for (String studentID : studentIDs) {
            cohortMap.put(studentID, masterCohortList.get(masterCohortList.size() - 1));
        }
    }

    @Override
    public TaskGenerator getTaskGeneratorFromStudentID(String studentIDIn){
        if (!cohortMap.containsKey(studentIDIn)){
            return null;
        } else {
            Cohort cohortOfStudent = cohortMap.get(studentIDIn);
            return cohortOfStudent.getTaskGenerator();
        }
    }

    @Override
    public MessageGenerator getMessageGeneratorFromStudentID(String studentIDIn) {
        return null;
    }
}
