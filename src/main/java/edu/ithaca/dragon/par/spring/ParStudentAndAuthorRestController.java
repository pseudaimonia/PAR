package edu.ithaca.dragon.par.spring;

import edu.ithaca.dragon.par.ParStudentAndAuthorServer;
import edu.ithaca.dragon.par.domainModel.equineUltrasound.EquineQuestionTypes;
import edu.ithaca.dragon.par.io.*;
import edu.ithaca.dragon.par.pedagogicalModel.ImageTaskSettings;
import edu.ithaca.dragon.par.pedagogicalModel.PageSettings;
import edu.ithaca.dragon.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ParStudentAndAuthorRestController {
    private final Logger logger = LogManager.getLogger(this.getClass());

    private ParStudentAndAuthorServer parServer;
    private JsonIoHelper jsonIoHelper;
    private JsonIoUtil jsonIoUtil;

    ParStudentAndAuthorRestController(){
        super();
        jsonIoHelper = new JsonIoHelperSpring();
        jsonIoUtil = new JsonIoUtil(jsonIoHelper);
        try {
            JsonAuthorDatastore jsonAuthorDatastore = new JsonAuthorDatastore(
                    "localData/currentAuthoredQuestions.json",
                    "author/AuthorQuestionsDefault.json",
                    "localData/currentAuthorQuestionTemplates.json",
                    "author/AuthorQuestionTemplatesDefault.json",
                    "localData/currentAuthorModel.json",
                    jsonIoHelper);
            JsonStudentModelDatastore jsonStudentDatastore = new JsonStudentModelDatastore(
                    "localData/currentQuestionPool.json",
                    "author/DemoQuestionPool.json",
                    jsonIoHelper,
                    "localData/students");
            parServer = new ParStudentAndAuthorServer(jsonStudentDatastore, jsonAuthorDatastore);
        }
        catch(IOException e){
            throw new RuntimeException("Server can't start without all necessary files loaded: ", e);
        }
    }

    @GetMapping("/")
    public String index() {
        return "Greetings from PAR API!";
    }

    @GetMapping("/getPageSettings")
    public PageSettings getPageSettings(@RequestParam String userId) throws IOException  {
        if(userId.equals("author")){
            return jsonIoUtil.fromReadOnlyFile("author/AuthorPageSettingsExample.json", PageSettings.class);
        }
        return jsonIoUtil.fromReadOnlyFile("author/PageSettingsExample.json", PageSettings.class);
    }

    @GetMapping("/getImageTaskSettings")
    public ImageTaskSettings getImageTaskSettings(@RequestParam String userId) throws IOException  {
        if(userId.equals("author")){
            return jsonIoUtil.fromReadOnlyFile("author/AuthorSettingsExample.json", ImageTaskSettings.class);
        }
        return jsonIoUtil.fromReadOnlyFile("author/SettingsExample.json", ImageTaskSettings.class);
    }

    @GetMapping("/nextImageTask")
    public ImageTask nextImageTask(@RequestParam String userId) throws IOException {
        return parServer.nextImageTask(userId);
    }

    @PostMapping("/recordResponse")
    public ResponseEntity<String> recordResponse(@RequestBody ImageTaskResponse response) {
        try {
            parServer.submitImageTaskResponse(response);
            return ResponseEntity.ok().body("ok");
        } catch (Exception e){
            logger.warn(e);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/logout")
    public void logout(@RequestParam String userId) throws IOException{
        parServer.logout(userId);
    }

    @GetMapping("/calcScore")
    public String calcScore(@RequestParam String userId) throws IOException {
        return parServer.calcOverallKnowledgeEstimate(userId);
    }

    @GetMapping("/calcScoreByType")
    public Map<String, Double> calcScoreByType(@RequestParam String userId) throws IOException{
        return parServer.calcKnowledgeEstimateByType(userId);
    }
    @GetMapping("/knowledgeBase")
    public Map<EquineQuestionTypes,String> knowledgeBaseEstimate(@RequestParam String userId)throws IOException {
        return parServer.calcKnowledgeEstimateStringsByType(userId);
    }
    @GetMapping("/nextAuthorImageTask")
    public ImageTask nextAuthorImageTask() throws IOException {
        return parServer.nextAuthorImageTask();
    }

    @PostMapping("/submitAuthorImageTaskResponse")
    public ResponseEntity<String> submitAuthorImageTaskResponse(@RequestBody ImageTaskResponse response) {
        try {
            parServer.submitAuthorImageTaskResponse(response);
            return ResponseEntity.ok().body("ok");
        } catch (Exception e){
            logger.warn(e);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/transferAuthoredQuestionsToStudents")
    public ResponseEntity<String> transferAuthoredQuestionsToStudents() {
        try {
            parServer.transferAuthoredQuestionsToStudentServer();
            return ResponseEntity.ok().body("ok");
        } catch (Exception e){
            logger.warn(e);
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/authoredQuestions")
    public List<ImageTask> authoredQuestions(){
        return parServer.authoredQuestions();
    }

    @GetMapping("/teacherReport")
    public TeacherReport getTeacherReport()throws IOException{
        return parServer.buildTeacherReport();
    }

}
