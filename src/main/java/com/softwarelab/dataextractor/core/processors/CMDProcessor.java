package com.softwarelab.dataextractor.core.processors;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Wilson
 * on Mon, 19/04/2021.
 */
public class CMDProcessor {
    public static BufferedReader processCMD(String command, String directory) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder()
                .command("cmd","/c",command)
                .directory(new File(directory));
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        return new BufferedReader(new InputStreamReader(process.getInputStream()));
    }
    public static boolean isValidDir(String directory){
        try{
            String normalDir = processCMD(CMD.INSIDE_WORK_DIR_CMD.getCommand(),directory).readLine();
            if(!"true".equals(normalDir))
                return false;
            //immediate parent dir shouldn't be a working dir
            String parentDir = processCMD("cd .. && "+CMD.INSIDE_WORK_DIR_CMD.getCommand(),directory).readLine();
            return !normalDir.equals(parentDir);
        }catch (Exception e){
            return false;
        }
    }
}
