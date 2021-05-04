package com.softwarelab.dataextractor.viewmodels;

import com.google.common.io.CharStreams;
import com.softwarelab.dataextractor.core.services.usecases.ProjectUseCase;
import com.softwarelab.dataextractor.core.exception.CMDProcessException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Wilson
 * on Mon, 19/04/2021.
 */
@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CMDProcessor {
    ProjectUseCase projectUseCase;

    public   List<String> processCMD(String command, String projectPath) throws IOException, CMDProcessException, InterruptedException {
        if (!projectUseCase.existsByLocalPath(projectPath))
            throw new CMDProcessException("Invalid Project Location");

        if (!isValidDir(projectPath))
            throw new CMDProcessException("Invalid git directory. Directory must contain the .git folder");

       return execute(command, projectPath);
    }
    public  List<String> execute(String command, String directory) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder()
                .command("cmd","/c",command)
                .directory(new File(directory));

        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
       process.waitFor(6, TimeUnit.SECONDS);

       List<String> result;
       try(Reader reader = new InputStreamReader(process.getInputStream())){
           result = CharStreams.readLines(reader);
       }
       return result;
    }
    public  boolean isValidDir(String directory){
        try{
            String normalDir = String.join("", execute(CMD.INSIDE_WORK_DIR_CMD.getCommand(), directory));
            if(!"true".equals(normalDir))
                return false;

            //immediate parent dir shouldn't be a working dir
            String parentDir = String.join("",execute("cd .. && "+CMD.INSIDE_WORK_DIR_CMD.getCommand(),directory));

            return !"true".equals(parentDir);
        }catch (Exception e){
            return false;
        }
    }
}
