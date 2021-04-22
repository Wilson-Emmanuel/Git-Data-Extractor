package com.softwarelab.dataextractor.core.processors;

import com.softwarelab.dataextractor.core.domain.services.ProjectService;
import com.softwarelab.dataextractor.core.exception.CMDProcessException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Wilson
 * on Mon, 19/04/2021.
 */
@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CMDProcessor {
    ProjectService projectService;
    public  BufferedReader processCMD(String command, String projectPath) throws IOException, CMDProcessException {
        if (!projectService.existsByLocalPath(projectPath))
            throw new CMDProcessException("Invalid Project Location");

        if (!isValidDir(projectPath))
            throw new CMDProcessException("Invalid git directory. Directory must contain the .git folder");

        return execute(command, projectPath);
    }
    private  BufferedReader execute(String command, String directory) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder()
                .command("cmd","/c",command)
                .directory(new File(directory));

        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        return new BufferedReader(new InputStreamReader(process.getInputStream()));
    }
    private  boolean isValidDir(String directory){
        try{
            String normalDir = execute(CMD.INSIDE_WORK_DIR_CMD.getCommand(),directory).readLine();
            if(!"true".equals(normalDir))
                return false;
            //immediate parent dir shouldn't be a working dir
            String parentDir = execute("cd .. && "+CMD.INSIDE_WORK_DIR_CMD.getCommand(),directory).readLine();
            return !normalDir.equals(parentDir);
        }catch (Exception e){
            return false;
        }
    }
}