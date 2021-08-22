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
    PROJECT_LIBRARIES("Project Libraries","export"),
    PROJECT_UNIQUE_LIBRARIES("Project Unique Libraries","export"),
    PROJECT_UNIQUE_CONTRIBUTORS("Project Unique Contributors","export"),
    ALL_PROJECT_UNIQUE_LIBRARIES("All Project Unique Libraries","export"),
    IMPORT_LIBRARIES("Libraries","import"),
    IMPORT_PROJECT_DEVELOPERS("Project Developers","import");

    private static final Map<String,OptionEnum> INSTANCES = new HashMap<>();
    static {
        for(OptionEnum optionEnum: values())
            INSTANCES.put(optionEnum.option,optionEnum);
    }

    private final String option;
    private final String type;
    OptionEnum(String option,String type){
        this.option =option;
        this.type = type;
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
