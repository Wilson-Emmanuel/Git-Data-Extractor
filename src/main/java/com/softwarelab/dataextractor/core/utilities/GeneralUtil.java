package com.softwarelab.dataextractor.core.utilities;

import java.io.File;

/**
 * Created by Wilson
 * on Tue, 08/06/2021.
 */
public class GeneralUtil {
    public static String getProgramPath(){
        File defaultLoc  = new File(System.getProperty("user.home"),"Data_Extractor");
        boolean created = defaultLoc.exists();
        if(!created){
            created = defaultLoc.mkdir();
        }
        return created?defaultLoc.getPath():System.getProperty("user.home");
    }
}
