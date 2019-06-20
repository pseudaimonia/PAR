package edu.ithaca.dragon.par.spring;

import edu.ithaca.dragon.par.ParServer;
import edu.ithaca.dragon.par.domainModel.Question;
import edu.ithaca.dragon.par.io.ImageTask;
import edu.ithaca.dragon.par.io.ImageTaskResponse;
import edu.ithaca.dragon.util.JsonUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ParRestController {

    ParRestController(){
        super();
    }

    @GetMapping("/")
    public String index() {
        return "Greetings from PAR API!";
    }

    @GetMapping("/nextImageTask")
    public ImageTask nextImageTask() {
        Logger logger = LogManager.getLogger(this.getClass());
        try {
            //current imageTask is a test and needs to be rerouted for final
            ImageTask imageTaskFromFile = JsonUtil.fromJsonFile("src/test/resources/author/SampleImageTask.json", ImageTask.class);
            return imageTaskFromFile;
        }
        catch (IOException e){
            logger.error("ImageTask not built", e);
            return null;
        }
    }

    @PostMapping("/recordResponse")
    public ResponseEntity<String> recordResponse(@RequestBody ImageTaskResponse response) {
        try {
            String updated = ParServer.sendNewImageTaskResponse(response);
            return ResponseEntity.ok().body(updated);
        } catch (Exception e){
            return ResponseEntity.notFound().build();
        }
    }

}
