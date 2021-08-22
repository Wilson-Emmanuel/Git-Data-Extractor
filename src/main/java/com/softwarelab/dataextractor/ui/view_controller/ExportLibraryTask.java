package com.softwarelab.dataextractor.ui.view_controller;

import com.softwarelab.dataextractor.core.persistence.models.PagedData;
import com.softwarelab.dataextractor.core.persistence.models.dtos.CommitModel1;
import com.softwarelab.dataextractor.core.persistence.models.dtos.ProjectModel1;
import com.softwarelab.dataextractor.core.services.usecases.CommitService1;
import javafx.concurrent.Task;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * Created by Wilson
 * on Tue, 08/06/2021.
 */
@Service(value = "exportLibrary")
@Scope("prototype")
public class ExportLibraryTask extends Task<Void> {

    @Autowired
    private CommitService1 commitService1;

    private ProjectModel1 projectModel1;
    private String exportLocation;
    private  boolean byDev;

    private final String[] columns = {"Developer","Library","Date"};
    private final String[] columns2 = {"Developer","Libraries"};

    @Override
    protected Void call() throws Exception {

        if(byDev){
            createDeveloperSheet();
        }else{
            createLibrarySheet();
        }

        return null;
    }
    private void createLibrarySheet() throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet byLibs = workbook.createSheet("Libraries");
        setByLibHeaderRow(workbook,byLibs);

        int size = 100, page = 0;
        PagedData<CommitModel1> commits = commitService1.getCommits(projectModel1.getId(),page,size);
        int total = Long.valueOf(commits.getTotalItems()).intValue(), current = 1;

        while(!commits.getItems().isEmpty()){
            for(CommitModel1 model: commits.getItems()){
                updateMessage("Processing... ("+((current/total)*100)+"%)");
                for (String cur : model.getLibraries()) {
                    Row row = byLibs.createRow(current++);
//                    row.createCell(0).setCellValue(model.getDeveloperName());
                    String email = model.getDeveloperEmail();
                    row.createCell(0).setCellValue(email == null|| email.isEmpty()?model.getDeveloperName():email);
                    row.createCell(1).setCellValue(cur);
                    row.createCell(2).setCellValue(model.getCommitDate());
                }

            }
            commits = commitService1.getCommits(projectModel1.getId(),page++,size);
        }
        // Resize all columns to fit the content size
        for (int i = 0; i < columns.length; i++) {
            byLibs.autoSizeColumn(i);
        }

        //generating the excel file
        FileOutputStream fileOut = new FileOutputStream(exportLocation+"/"+ projectModel1.getName()+"_libs.xlsx");
        workbook.write(fileOut);
        fileOut.close();
    }

    private void createDeveloperSheet() throws IOException {
        Workbook workbook = new XSSFWorkbook();
        CellStyle headerCellStyle = setHeaderFont(workbook);

        //TODO: empty collection
        List<String> devs = commitService1.getDevelopers(projectModel1.getId());

        Sheet byDevelopers = workbook.createSheet("Developers");
        Row headerRow = byDevelopers.createRow(0);

        for (int i = 0; i < columns2.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns2[i]);
            cell.setCellStyle(headerCellStyle);
        }
        int total = devs.size();
        int current = 1;
        for(String devName : devs){
            updateMessage("Processing... ("+((current/total)*100)+"%)");
            Optional<String> libs = commitService1.getDeveloperLibraries(devName, projectModel1.getId());
            if(libs.isEmpty())
                continue;

            Row row = byDevelopers.createRow(current++);
            row.createCell(0).setCellValue(devName);
            row.createCell(1).setCellValue(libs.get());
        }
        // Resize all columns to fit the content size
        for (int i = 0; i < columns.length; i++) {
            byDevelopers.autoSizeColumn(i);
        }

        //generating the excel file
        FileOutputStream fileOut = new FileOutputStream(exportLocation+"/"+ projectModel1.getName()+"_devs.xlsx");
        workbook.write(fileOut);
        fileOut.close();

    }

    public void setProject(ProjectModel1 model){
        this.projectModel1 = model;
    }
    public void setExportLocation(String loc){
        this.exportLocation = loc;
    }
    public void setByDev(boolean byDev){
        this.byDev = byDev;
    }
    private CellStyle setHeaderFont(Workbook workbook){
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 14);
        headerFont.setColor(IndexedColors.BLACK.getIndex());

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);
        return headerCellStyle;
    }
    private void setByLibHeaderRow(Workbook workbook, Sheet sheet){
        CellStyle headerCellStyle = setHeaderFont(workbook);
        Row headerRow = sheet.createRow(0);

        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerCellStyle);
        }
    }
}
