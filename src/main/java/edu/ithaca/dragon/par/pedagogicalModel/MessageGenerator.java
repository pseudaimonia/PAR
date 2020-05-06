package edu.ithaca.dragon.par.pedagogicalModel;

import edu.ithaca.dragon.par.io.ImageTask;
import edu.ithaca.dragon.par.studentModel.StudentModel;

public class MessageGenerator {
    public static void generateMessage(StudentModel studentModel, ImageTask imageTask, int questionCountPerTypeForAnalysis, int previousLevel){
        int level = studentModel.getLevel();

        //mastered student
        if (level == 7){
            imageTask.setMessage("You have mastered the material, feel free to keep practicing");
        }
        //down level
        else if (previousLevel-level > 0 && previousLevel != -1){

            imageTask.setMessage("Looks like you're having trouble, go look at resources and come back if you need to");
        }

        //up level
        else if (previousLevel-level < 0 && previousLevel != -1){
            imageTask.setMessage("You're doing great!");
        }

        //stayed on same level
    }
}
