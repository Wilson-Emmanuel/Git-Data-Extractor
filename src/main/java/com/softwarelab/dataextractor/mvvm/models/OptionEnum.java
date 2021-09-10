package com.softwarelab.dataextractor.mvvm.models;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Wilson
 * on Thu, 19/08/2021.
 */
public enum OptionEnum {
    ALL_LIBRARIES("All Libraries","export",true),
    PROJECT_LIBRARIES("Project Libraries","export",false),
    PROJECT_UNIQUE_LIBRARIES("Project Unique Libraries","export",false),
    PROJECT_UNCLASSIFIED_UNIQUE_LIBRARIES("Project Unclassified Unique Libraries","export",false),
    //PROJECT_UNIQUE_CONTRIBUTORS("Project Contributors","export",false),
    //ALL_PROJECT_UNIQUE_CONTRIBUTORS("All Contributors","export",true),
    ALL_UNIQUE_LIBRARIES("All Unique Libraries","export",true),
    ALL_UNCLASSIFIED_UNIQUE_LIBRARIES("All Unclassified Unique Libraries","export",true),

    IMPORT_LIBRARIES("Libraries","import",true);
    //IMPORT_PROJECT_DEVELOPERS("Project Developers","import",false);

    private static final Map<String,OptionEnum> INSTANCES = new HashMap<>();
    static {
        for(OptionEnum optionEnum: values())
            INSTANCES.put(optionEnum.option,optionEnum);
    }

    private final String option;
    private final String type;
    private final boolean allProject;
    OptionEnum(String option,String type, boolean allProject){
        this.option =option;
        this.type = type;
        this.allProject  = allProject;
    }
    public boolean isAllProject(){
        return allProject;
    }
    public String getOption(){
        return option;
    }
    public String getType(){
        return type;
    }
    public static OptionEnum getOptionEnum(String option){
        return INSTANCES.getOrDefault(option,null);
    }
    public static List<String> getExportOptions(){
        return Arrays.stream(values())
                .filter(optionEnum -> optionEnum.getType().equals("export"))
                .map(OptionEnum::getOption).collect(Collectors.toList());
    }
    public static List<String> getImportOptions(){
        return Arrays.stream(values())
                .filter(optionEnum -> optionEnum.getType().equals("import"))
                .map(OptionEnum::getOption).collect(Collectors.toList());
    }
}
