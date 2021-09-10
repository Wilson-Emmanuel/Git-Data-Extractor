package com.softwarelab.dataextractor.core.services;

import com.softwarelab.dataextractor.core.persistence.models.CommitObject;
import com.softwarelab.dataextractor.core.persistence.models.LibraryObject;
import com.softwarelab.dataextractor.core.persistence.models.PagedData;
import com.softwarelab.dataextractor.core.services.usecases.CommitService;
import com.softwarelab.dataextractor.core.services.usecases.ExportService;
import com.softwarelab.dataextractor.core.services.usecases.LibraryService;
import com.softwarelab.dataextractor.core.utilities.GeneralUtil;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Created by Wilson
 * on Thu, 02/09/2021.
 */
@Service
public class ExportServiceImpl implements ExportService {

    @Autowired
    private CommitService commitService;
    @Autowired
    private LibraryService libraryService;

    String[] headers = {"Committer Name", "Committer Email", "Committer Date", "Author Name", "Author Email", "Author Date","Library","Provider","Category","Library Frequency in Project"};
    String[] allheaders = {"Committer Name", "Committer Email", "Committer Date", "Author Name", "Author Email", "Author Date","Library","Provider","Category","Library Frequency in Project","Project"};
    String[] uniqueLibs = {"Library","Provider","Category"};


    @Override
    public void exportProjectLibrary(XSSFWorkbook workbook, Long projectId, String projectName) {

        XSSFSheet sheet = GeneralUtil.setByLibHeaderRow(workbook,projectName+" Libraries", headers);

        int page = 0;
        int size = 20;
        int rowNumber = 1;//row 0 is the header
        PagedData<CommitObject> objectPagedData = null;
        do{
             objectPagedData = commitService.getProjectCommits(projectId,page,size);
             for(CommitObject commit: objectPagedData.getItems()){

                 for(LibraryObject lib: commit.getLibraries()){
                     createCommitAndLibraryRows(sheet,commit,lib,rowNumber,false);
                     rowNumber++;
                 }
             }
             page++;
        }while (page < objectPagedData.getTotalPages());

    }

    @Override
    public void exportAllLibraries(XSSFWorkbook workbook) {
        XSSFSheet sheet = GeneralUtil.setByLibHeaderRow(workbook,"All Project Libraries", allheaders);

        int page = 0;
        int size = 50;
        int rowNumber = 1;//row 0 is the header
        PagedData<CommitObject> objectPagedData = null;
        do{
            objectPagedData = commitService.getAllProjectCommits(page,size);
            for(CommitObject commit: objectPagedData.getItems()){

                for(LibraryObject lib: commit.getLibraries()){
                    createCommitAndLibraryRows(sheet,commit,lib,rowNumber,true);
                    rowNumber++;
                }
            }
            page++;
        }while (page < objectPagedData.getTotalPages());

    }

    @Override
    public void exportProjectUniqueLibraries(XSSFWorkbook workbook, Long projectId, String projectName) {

        XSSFSheet sheet = GeneralUtil.setByLibHeaderRow(workbook,projectName+" Unique Libraries", uniqueLibs);

            int rowNumber = 1;//row 0 is the header

            Set<LibraryObject> libraryObjects = libraryService.getUniqueProjectLibraries(projectId);
            for(LibraryObject lib: libraryObjects){
                createLibraryRow(sheet,lib,rowNumber);
                rowNumber++;
            }

    }
    @Override
    public void exportProjectUnclassifiedUniqueLibraries(XSSFWorkbook workbook, Long projectId, String projectName) {

        XSSFSheet sheet = GeneralUtil.setByLibHeaderRow(workbook,projectName+" Unclassified Unique Libraries", uniqueLibs);

        int rowNumber = 1;//row 0 is the header

        Set<LibraryObject> libraryObjects = libraryService.getUnclassifiedUniqueProjectLibraries(projectId);
        for(LibraryObject lib: libraryObjects){
            createLibraryRow(sheet,lib,rowNumber);
            rowNumber++;
        }

    }

    @Override
    public void exportAllUniqueLibraries(XSSFWorkbook workbook) {
        exportUniqueLibs(workbook,"All Unique Libraries",false);
    }

    @Override
    public void exportUniqueUnclassifiedLibraries(XSSFWorkbook workbook) {
        exportUniqueLibs(workbook,"All Unclassified Unique Libraries",true);
    }
    private void exportUniqueLibs(XSSFWorkbook workbook,String sheetName, boolean unclassified) {
        XSSFSheet sheet = GeneralUtil.setByLibHeaderRow(workbook,sheetName, uniqueLibs);

        int page = 0;
        int size = 50;
        int rowNumber = 1;//row 0 is the header
        PagedData<LibraryObject> objectPagedData = null;
        do{
            if(unclassified)
                objectPagedData = libraryService.getUnclassifiedLibraries(page,size);
            else
                objectPagedData = libraryService.getAllUniqueLibraries(page,size);

            for(LibraryObject lib: objectPagedData.getItems()){
                createLibraryRow(sheet,lib,rowNumber);
                rowNumber++;
            }
            page++;
        }while (page < objectPagedData.getTotalPages());

    }


    private void createCommitAndLibraryRows(XSSFSheet sheet, CommitObject commit, LibraryObject lib, int rowNumber, boolean allProject){

        Row row = sheet.createRow(rowNumber);
        row.createCell(0).setCellValue(commit.getCommiter().getName());
        row.createCell(1).setCellValue(commit.getCommiter().getEmail());
        row.createCell(2).setCellValue(commit.getCommitDate());
        row.createCell(3).setCellValue(commit.getAuthorName());
        row.createCell(4).setCellValue(commit.getAuthorEmail());
        row.createCell(5).setCellValue(commit.getAuthorDate());
        row.createCell(6).setCellValue(lib.getName());
        row.createCell(7).setCellValue(lib.getProvider());
        row.createCell(8).setCellValue(lib.getCategory());
        row.createCell(9).setCellValue(lib.getLibraryFrequencyInProject());
        if(allProject)
            row.createCell(10).setCellValue(commit.getClassFile().getProject().getName());

    }
    private void createLibraryRow(XSSFSheet sheet, LibraryObject lib, int rowNumber){
        Row row = sheet.createRow(rowNumber);
        row.createCell(0).setCellValue(lib.getName());
        row.createCell(1).setCellValue(lib.getProvider());
        row.createCell(2).setCellValue(lib.getCategory());
    }
}
